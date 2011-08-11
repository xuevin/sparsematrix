package org.apache.mahout;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SparseMatrixMultiplier {
  
  private static int NUMBEROFTHREADS = Runtime.getRuntime().availableProcessors();
  private static Logger log = LoggerFactory.getLogger(SparseMatrixMultiplier.class);
  
  public static void setNumberOfThreads(int numThreads){
    NUMBEROFTHREADS=numThreads;
  }
  private SparseMatrixMultiplier() {}
  
  public static Matrix multiply(Matrix a, Matrix b) {
    return multiply(a, b, NUMBEROFTHREADS);
  }
  
  public static Matrix multiply(Matrix a, Matrix b, int threads) {
    // long time = System.currentTimeMillis();
    System.out.println("Using " + threads + " processors to multiply 2 matricies");
    log.info("Using " + threads + " processors to multiply 2 matricies");
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    
    // System.out.println("Rows: " + a.numRows() + "\tColumns: " + a.numCols());
    
    SparseMatrixThreaded targetMatrix = new SparseMatrixThreaded(a.numRows(), b.numCols());
    
    for (int row = 0; row < a.numRows(); row++) {
      RandomAccessSparseVector targetRow = new RandomAccessSparseVector(b.size()[1]);
      targetMatrix.assignRowAndSyncLater(row, targetRow);
      pool.execute(new SumOfProductsRunnable(a.getRow(row), b, targetRow));
    }
    
    // System.out.println("Finished Submitting, now waiting for threads to finish: " +
    // (System.currentTimeMillis()-time));
    pool.shutdown();
    try {
      if (pool.awaitTermination(1, TimeUnit.DAYS)) {
        // EXPERIMENTAL
        // System.out.println("Starting sync");
        // time = System.currentTimeMillis();
        targetMatrix.syncRowsToColumns();
        // System.out.println("Sync took: " + (System.currentTimeMillis()-time));
        // System.out.println("Finished Within Limits");
      } else {
        System.err.println("DID NOT FINISH!");
      }
    } catch (InterruptedException e) {
      System.err.println("DID NOT FINISH!");
      e.printStackTrace();
    }
    return targetMatrix;
  }
  
  public static Vector multiply(Matrix a, Vector vector) {
    return multiply(a, vector, NUMBEROFTHREADS);
  }
  
  public static Vector multiply(Matrix a, Vector vector, int threads) {
    // System.out.println("Using " + threads + " processors to multiply matrix by vector");
    log.info("Using " + threads + " processors to multiply 2 matricies");
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    
    // System.out.println("Rows: " + a.numRows() + "\tColumns: " + 1);
    
    final RandomAccessSparseVector targetVector = new RandomAccessSparseVector(a.numRows());
    SetItemCallback callback = new SetItemCallback() {
      
      public void setQuick(int position, double value) {
        synchronized (targetVector) {
          targetVector.setQuick(position, value);
        }
      }
    };
    
    for (int row = 0; row < a.numRows(); row++) {
      pool.execute(new DotProductRunnable(row, a.getRow(row), vector, callback));
    }
    
    pool.shutdown();
    try {
      if (pool.awaitTermination(1, TimeUnit.DAYS)) {
        // System.out.println("Finished Within Limits");
      } else {
        System.err.println("DID NOT FINISH!");
      }
    } catch (InterruptedException e) {
      System.err.println("DID NOT FINISH!");
      e.printStackTrace();
    }
    return targetVector;
  }
  
  public static Matrix multiplyByDiagonalizedVector(Matrix a, Vector diagonalVector) {
    return multiplyByDiagonalizedVector(a, diagonalVector, NUMBEROFTHREADS);
  }
  
  public static Matrix multiplyByDiagonalizedVector(Matrix a, Vector diagonalVector, int threads) {
    log.info("Multiplying matrix by a diagonal vector");
    ExecutorService pool = Executors.newFixedThreadPool(threads);
    
    SparseMatrixThreaded targetMatrix = new SparseMatrixThreaded(a.numRows(), diagonalVector.size());
    for (int row = 0; row < a.numRows(); row++) {
      RandomAccessSparseVector targetVector = new RandomAccessSparseVector(diagonalVector.size());
      targetMatrix.assignRowAndSyncLater(row, targetVector);
      pool.submit(new MultiplyByDiagonalRunnable(a.getRow(row), diagonalVector, targetVector));
      
    }
    
    pool.shutdown();
    try {
      if (pool.awaitTermination(1, TimeUnit.DAYS)) {
        // System.out.println("Finished Within Limits");
        targetMatrix.syncRowsToColumns();
      } else {
        System.err.println("DID NOT FINISH!");
      }
    } catch (InterruptedException e) {
      System.err.println("DID NOT FINISH!");
      e.printStackTrace();
    }
    return targetMatrix;
  }
}
