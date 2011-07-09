package org.apache.mahout;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.SparseMatrix;
import org.apache.mahout.math.Vector;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SparseMatrixFinalTest {
  
  @Before
  public void setUp() throws Exception {}
  
  @Test
  public void showThatAssignRowWorks() {
    SparseMatrixThreaded sparseMatrix = new SparseMatrixThreaded(5, 5);
    sparseMatrix.setQuick(0, 1, 0.5);
    print(sparseMatrix);
    
    Vector newVectorToAssign = new RandomAccessSparseVector(5);
    newVectorToAssign.set(0, 0.4);
    
    sparseMatrix.assignRow(0, newVectorToAssign);
    assertEquals(0.4, sparseMatrix.get(0, 0), 0);
    assertEquals(0, sparseMatrix.get(0, 1), 0);
    
    assertEquals(sparseMatrix.getRow(0), newVectorToAssign);
    
  }
  
  @Test
  public void showThatAssignColumnWorks() {
    SparseMatrixThreaded sparseMatrix = new SparseMatrixThreaded(5, 5);
    sparseMatrix.setQuick(0, 1, 0.5);
    
    Vector newVectorToAssign = new RandomAccessSparseVector(5);
    newVectorToAssign.set(0, 0.4);
    
    sparseMatrix.assignColumn(1, newVectorToAssign);
    assertEquals(0.4, sparseMatrix.get(0, 1), 0);
    assertEquals(0, sparseMatrix.get(1, 0), 0);
    
    assertEquals(sparseMatrix.getColumn(1), newVectorToAssign);
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
  @Test
  public void testSyncRowsToColumns(){
    SparseMatrixThreaded sparseMatrix = new SparseMatrixThreaded(5, 5);
    sparseMatrix.setQuick(0, 1, 0.5);
    
    //Create a new vector
    Vector newVectorToAssign = new RandomAccessSparseVector(5);
    newVectorToAssign.set(0, 0.4);
    
    assertEquals(0.5, sparseMatrix.get(0, 1),0);
    assertEquals(0.5, sparseMatrix.getRow(0).get(1),0);
    assertEquals(0.5, sparseMatrix.getColumn(1).get(0),0);
    
    sparseMatrix.assignRowAndSyncLater(0, newVectorToAssign);
    
    //Show that the columns in the matrix are still different
    assertEquals(0.4, sparseMatrix.get(0, 0),0);
    assertEquals(0, sparseMatrix.get(0, 1),0);
    assertEquals(0.4, sparseMatrix.getRow(0).get(0),0);
    assertEquals(0.5, sparseMatrix.getColumn(1).get(0),0);

    sparseMatrix.syncRowsToColumns();

    //Show that values in column are good after sync
    assertEquals(0.4, sparseMatrix.getColumn(0).get(0),0);
    assertEquals(0, sparseMatrix.getColumn(1).get(0),0);
    
    //Make sure that pointers are the same
    assertEquals(sparseMatrix.getRow(0), newVectorToAssign);
  }
  @Test
  public void showThatThisMatrixMultiplicationWorksJustLikeSparseMatrixMultiplicationDoes(){
    Matrix matrix = SparseMatrixThreaded.generateRandomSparseMatrix(10, 10, 0.3);
    Matrix matrix2 = SparseMatrixThreaded.generateRandomSparseMatrix(10, 10, 0.3);
    Matrix product = matrix.times(matrix2);
    
    Matrix sparse1 = getSparseMatrixFromSparseMatrixFinal(matrix);
    Matrix sparse2 = getSparseMatrixFromSparseMatrixFinal(matrix2);
    Matrix product2 = sparse1.times(sparse2);
    
    for(int i = 0; i<matrix.size()[0];i++){
      for(int j = 0; j<matrix.size()[1];j++){
        assertEquals(product.get(i, j), product2.get(i, j),0.000000001);
        
      }
    }
  }
  private SparseMatrix getSparseMatrixFromSparseMatrixFinal(Matrix matrix){
    SparseMatrix newMatrix = new SparseMatrix(matrix.size());
    for(int i = 0; i<matrix.size()[0];i++){
      for(int j = 0; j<matrix.size()[1];j++){
        newMatrix.setQuick(i, j, matrix.get(i, j));
      }
    }
    return newMatrix;
    
  }
  
//  @Test
  public void testSparseMatrixGenerator(){
    Matrix matrix = SparseMatrixThreaded.generateRandomSparseMatrix(10, 10, 0.3);
    print(matrix);
  }
//  public void testHowLongItTakesToCountToAMillion(){
//    long count =0;
//    long time = System.currentTimeMillis();
//    for(int i = 0;i< 1000000;i++){
//      for(int j = 0;j< 1000000;j++){
//      count++;
//      }
//    }
//    System.out.println(count);
//    System.out.println(System.currentTimeMillis()-time);
//  }
  
}
