package org.apache.mahout;

import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.Vector;

public class SumOfProductsRunnable implements Runnable {
  
  private Vector vectorA;
  private Matrix matrixB;
  private Vector targetRow;
  
  public SumOfProductsRunnable(Vector rowA, Matrix matrixB,Vector targetRow) {
    this.vectorA = rowA;
    this.matrixB = matrixB;
    this.targetRow=targetRow;
  }
  
  public void run() {
    // long time = System.nanoTime();
    // Iterate through all the columns in B
    
    for (int i = 0; i < matrixB.size()[1]; i++) {
      double sum = vectorA.dot(matrixB.getColumn(i));
      targetRow.setQuick(i, sum);
    }
    // System.out.println((System.nanoTime() - time));
    return;
    
  }
  
}
