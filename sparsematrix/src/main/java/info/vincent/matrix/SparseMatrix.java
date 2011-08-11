package info.vincent.matrix;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SparseMatrix {
  private HashMap<Integer,Row> rows; // A Row is a hash that tells me the columns
  private HashMap<Integer,Column> columns; // A Column is a hash that tells me the rows
  
  private int numRows;
  private int numColumns;
  
  public SparseMatrix(int numRows, int numColumns) {
    this.numRows = numRows;
    this.numColumns = numColumns;
    this.rows = new HashMap<Integer,Row>(numRows);
    this.columns = new HashMap<Integer,Column>(numColumns);
  }
  
  public void setQuick(int row, int column, double value) {
//    long time = System.nanoTime();

    Row rowObj;
    if ((rowObj = rows.get(row)) != null) {
      rowObj.setQuick(column, value);
    } else {
      rowObj = new Row(numColumns);
      rowObj.setQuick(column, value);
      rows.put(row, rowObj);
    }
    
    Column colObj;
    if ((colObj = columns.get(column)) != null) {
      colObj.setQuick(row, value);
    } else {
      colObj = new Column(numRows);
      colObj.setQuick(row, value);
      columns.put(column, colObj);
    }
//    System.out.println(System.nanoTime()-time);
  }
  
  public Column getColumn(int i) {
    return columns.get(i);
  }
  
  public Row getRow(int i) {
    return rows.get(i);
  }
  
  public static SparseMatrix multiply(SparseMatrix A, SparseMatrix B) {
    System.out.println("Using " + Runtime.getRuntime().availableProcessors() + " processors");
    ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    
    SparseMatrix targetMatrix = new SparseMatrix(A.getNumRows(), B.getNumColumns());
    System.out.println(A.getNumRows());
    for (int row = 0; row < A.getNumRows(); row++) {
      try {
        pool.execute(new SumOfProductsRunnable(row, A.getRow(row), B.getAllColumns(), targetMatrix));
        // targetMatrix.setQuick(row, column, SumOfProductsRunnable.getSumOfProducts(A.getRow(row),
        // B.getColumn(column)));
      } catch (NullPointerException e) {
        e.printStackTrace();
      }
    }
    pool.shutdown();
    try {
      if(pool.awaitTermination(20, TimeUnit.SECONDS)){
        System.err.println("I Fishied In Time");

      }
    } catch (InterruptedException e) {
      System.err.println("I AM NOT DONE!");
      e.printStackTrace();
    }
    return targetMatrix;
  }
  
  public int getNumColumns() {
    return numColumns;
  }
  
  public int getNumRows() {
    return numRows;
  }
  
  public HashMap<Integer,Column> getAllColumns() {
    return columns;
  }

  public void print() {
    long sum = 0;
    int i = 0;
    for (int row = 0; row < getNumRows(); row++) {
      for (int column = 0; column < getNumColumns(); column++) {
        try {
          i++;
          long time = System.nanoTime();
          getRow(row).getColumn(column);
          long dif = (System.nanoTime()-time);
          sum+=dif;
          System.out.println(sum/i);
//          System.out.print(getRow(row).getColumn(column) + "\t");
          
        } catch (NullPointerException e) {
          // e.printStackTrace();
//          System.out.print(0 + "\t");
        }
      }
      System.out.println();
    }
    
  }
  
}
