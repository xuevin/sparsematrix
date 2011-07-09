package org.apache.mahout;

import org.apache.mahout.math.Vector;

public class DotProductRunnable implements Runnable {
  private Vector rowVector;
  private Vector multiplier;
  private SetItemCallback callback;
  private int row;
  
  public DotProductRunnable(int row, Vector rowVector, Vector multiplier, SetItemCallback callback) {
    this.row = row;
    this.multiplier = multiplier;
    this.callback = callback;
    this.rowVector = rowVector;
  }
  
  public void run() {
    callback.setQuick(row, rowVector.dot(multiplier));
  }
  
}
