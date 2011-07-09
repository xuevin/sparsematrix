package org.apache.mahout;

import static org.junit.Assert.assertEquals;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.junit.Before;
import org.junit.Test;

public class SumOfProductsRunnableTest {
  
  @Before
  public void setUp() throws Exception {}
  
  @Test
  public void test() {
    
    Matrix diagonalMatrix = new SparseMatrixThreaded(4, 4);
    diagonalMatrix.setQuick(0, 0, 2);
    diagonalMatrix.setQuick(1, 1, 2);
    diagonalMatrix.setQuick(2, 2, 3);
    diagonalMatrix.setQuick(3, 3, 5);
    
    RandomAccessSparseVector vector = new RandomAccessSparseVector(4);
    vector.setQuick(0, 0);
    vector.setQuick(1, 1);
    vector.setQuick(2, 2);
    vector.setQuick(3, 2);
    
    RandomAccessSparseVector targetRow = new RandomAccessSparseVector(4);
    SumOfProductsRunnable runnable = new SumOfProductsRunnable(vector, diagonalMatrix, targetRow);
    
    runnable.run();
    
    assertEquals(0, targetRow.get(0), 0);
    assertEquals(2, targetRow.get(1), 0);
    assertEquals(6, targetRow.get(2), 0);
    assertEquals(10, targetRow.get(3), 0);
  }
  
}
