package org.apache.mahout;

import static org.junit.Assert.*;

import java.util.Random;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.SparseMatrix;
import org.apache.mahout.math.Vector;
import org.junit.Before;
import org.junit.Test;

public class SparseMatrixMultiplierTest {
  
  @Before
  public void setUp() throws Exception {}
  
  @Test
  public void testHowLongItTakesToMakeDesignMatrix() {
    long time = System.currentTimeMillis();
    Matrix designMatrix = getDesignMatrix(12, 30000);
    System.out.println(System.currentTimeMillis() - time);
  }  
  public void testHowLongItTakesToAccessDesignMatrix() {
    long time = System.currentTimeMillis();
    Matrix designMatrix = getDesignMatrix(12, 3000);
    int rows = designMatrix.size()[0];
    int col = designMatrix.size()[1];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < col; j++) {
        designMatrix.getQuick(i, j);
      }
    }
    System.out.println(System.currentTimeMillis() - time);
  }
  
  @Test
  public void showThatProductOfMatrixMultiplicationIsCorrect() {
    long time = System.currentTimeMillis();
    int numProbes = 16;
    int numSamples = 30;
    Matrix A = getDesignMatrix(numProbes, numSamples);
    Matrix B = getDesignMatrixTranspose(numProbes, numSamples);
    Matrix productMatrix = SparseMatrixMultiplier.multiply(B, A);
//    print(productMatrix);
    assertEquals(numProbes,productMatrix.get(0, 0),0);
    System.out.println(System.currentTimeMillis() - time);
    
  }
  
  private Matrix getDesignMatrix(int numProbes, int numSamples) {
    SparseMatrixThreaded matrix = new SparseMatrixThreaded(numProbes * numSamples, (numSamples + numProbes - 1));
    
    int rowPosition = 0;
    for (int column = 0; column < numSamples; column++) {
      for (int probeNumber = 0; probeNumber < numProbes; probeNumber++) {
        matrix.setQuick(rowPosition, column, 1);
        if (rowPosition != 0 && ((rowPosition + 1) % (numProbes) == 0)) {
          for (int i = 0; i < (numProbes - 1); i++) {
            matrix.setQuick(rowPosition, numSamples + i, -1);
          }
        } else {
          matrix.setQuick(rowPosition, (numSamples + probeNumber), 1);
        }
        rowPosition++;
      }
    }
    return matrix;
  }
  
  private Matrix getDesignMatrixTranspose(int numProbes, int numSamples) {
    SparseMatrixThreaded sparseMatrix = new SparseMatrixThreaded((numSamples + numProbes - 1), numProbes
                                                                                         * numSamples);
    int column = 0;
    for (int i = 0; i < numSamples; i++) {
      int offset = 0;
      for (int j = 0; j < numProbes; j++) {
        sparseMatrix.setQuick(i, column, 1);
        if (j != numProbes - 1) {
          sparseMatrix.setQuick(numSamples + offset, column, 1);
        }
        column++;
        offset++;
      }
      for (int v = 0; v < numProbes - 1; v++) {
        sparseMatrix.setQuick(numSamples + v, column - 1, -1);
      }
    }
    return sparseMatrix;
  }
  
  @Test
  public void testGenerateSparseMatrix() {
    int rows = 10000;
    int columns = 100;
    double density = 0.3;
    Matrix a = SparseMatrixThreaded.generateRandomSparseMatrix(rows, columns, density);
    Matrix b = SparseMatrixThreaded.generateRandomSparseMatrix(columns, rows, density);
    long time = System.currentTimeMillis();
//    SparseMatrixMultiplier.multiply(a, b);
    System.out.println("Took " + (System.currentTimeMillis() - time));
  }
  
  @Test
  public void testMultiplyWithVector() {
    SparseMatrixThreaded matrix = SparseMatrixThreaded.generateRandomSparseMatrix(4, 4, 0.5);
    RandomAccessSparseVector vector = new RandomAccessSparseVector(4);
    vector.setQuick(0, 1);
    print(matrix);
    print(vector);
    Vector product =matrix.times(vector);
    for(int i = 0;i<product.size();i++){
      assertEquals(matrix.get(i, 0),product.get(i),0);
    }
  }
  
  @Test
  public void testMultiplyByDiagonalizedIdentityVector() {
    SparseMatrixThreaded matrix = SparseMatrixThreaded.generateRandomSparseMatrix(4, 4, 0.5);
    print(matrix);
    RandomAccessSparseVector vector = new RandomAccessSparseVector(4);
    
    vector.set(0, 1);
    vector.set(1, 1);
    vector.set(2, 1);
    vector.set(3, 1);
    
    Matrix product = matrix.timesDiagonaledVector(vector);
    for (int i = 0; i < matrix.size()[0]; i++) {
      for (int j = 0; j < matrix.size()[1]; j++) {
        assertEquals(matrix.getQuick(i, j), product.getQuick(i, j), 0);
      }
    }
    
  }
  
  private void print(Matrix matrix) {
    for (int i = 0; i < matrix.size()[0]; i++) {
      for (int j = 0; j < matrix.size()[1]; j++) {
        System.out.print(matrix.getQuick(i, j) + "\t");
      }
      System.out.println();
    }
  }
  
  private void print(Vector vector) {
    for (int i = 0; i < vector.size(); i++) {
      System.out.print(vector.getQuick(i) + "\t");
    }
    System.out.println();
  }
}
