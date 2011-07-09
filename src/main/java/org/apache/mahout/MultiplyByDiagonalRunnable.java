package org.apache.mahout;

import org.apache.mahout.math.Vector;

public class MultiplyByDiagonalRunnable implements Runnable {
  private Vector diagonalVector;
  private Vector rowVector;
  private Vector targetVector;
  
  public MultiplyByDiagonalRunnable(Vector a, Vector diagonalVector, Vector targetVector) {
    this.targetVector = targetVector;
    this.diagonalVector = diagonalVector;
    this.rowVector = a;
    
  }
  
  public void run() {
    for (int i = 0; i < diagonalVector.size(); i++) {
      targetVector.setQuick(i, rowVector.getQuick(i) * (diagonalVector.get(i)));
      // targetMatrix.setQuick(row, i, a.getQuick(row, i) * (diagonalVector.get(i)));
    }
  }
  
}
