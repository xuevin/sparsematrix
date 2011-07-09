package info.vincent.matrix;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

public class SumOfProductsRunnable implements Runnable {
  
  private Row vectorA;
  private HashMap<Integer,Column> allColumns;
  private int row;
  private SparseMatrix productMatrix;
  
  public SumOfProductsRunnable(int row,
                               Row rowA,
                               HashMap<Integer,Column> allColumns,
                               SparseMatrix targetMatrix) {
    this.vectorA = rowA;
    this.allColumns = allColumns;
    this.row = row;
    this.productMatrix = targetMatrix;
  }
  
  public void run() {
//    long time = System.nanoTime();
    
    for (Integer position : allColumns.keySet()) {
      
      double sum = getSumOfProducts(vectorA, allColumns.get(position));
      if (sum != 0) {
        productMatrix.setQuick(row, position, sum);
      }
    }
//     System.out.println(row + "\t" + (System.nanoTime()-time));
    return;
    
  }
  
  public static double getSumOfProducts(Vector A, Vector B) {
//    long time = System.nanoTime();
    double sum = 0;
    for (Integer key : A.keySet()) {
      try {
        sum += A.get(key) * B.get(key);
      } catch (NullPointerException e) {
        // That means the either A.get(key) is zero or B.get(key) is zero
      }
    }
//    System.out.println(System.nanoTime()-time);
    return sum;
  }
  
}
