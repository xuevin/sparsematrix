package org.apache.mahout;

import java.util.Iterator;
import java.util.Random;

import org.apache.mahout.math.AbstractMatrix;
import org.apache.mahout.math.CardinalityException;
import org.apache.mahout.math.IndexException;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.MatrixSlice;
import org.apache.mahout.math.MatrixView;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.Vector.Element;
import org.apache.mahout.math.function.IntObjectProcedure;
import org.apache.mahout.math.list.IntArrayList;
import org.apache.mahout.math.map.OpenIntObjectHashMap;

import com.google.common.collect.AbstractIterator;

public class SparseMatrixThreaded extends AbstractMatrix {
  private OpenIntObjectHashMap<Vector> rows;
  private OpenIntObjectHashMap<Vector> columns;
  
  public SparseMatrixThreaded() {}
  
  public SparseMatrixThreaded(int rows, int columns) {
    this(new int[] {rows, columns});
  }
  
  public SparseMatrixThreaded(int[] cardinality) {
    this.cardinality = cardinality.clone();
    this.rows = new OpenIntObjectHashMap<Vector>();
    this.columns = new OpenIntObjectHashMap<Vector>();
  }
  
  @Override
  public Matrix clone() {
    SparseMatrixThreaded clone = (SparseMatrixThreaded) super.clone();
    clone.cardinality = cardinality.clone();
    clone.rows = rows.clone();
    return clone;
  }
  
  public void setQuick(int row, int column, double value) {
    Vector r = rows.get(row);
    if (r == null) {
      r = new RandomAccessSparseVector(cardinality[COL]);
      rows.put(row, r);
    }
    r.setQuick(column, value);
    
    Vector c = columns.get(column);
    if (c == null) {
      c = new RandomAccessSparseVector(cardinality[ROW]);
      columns.put(column, c);
    }
    c.setQuick(row, value);
  }
  
  public Vector getRow(int row) {
    if (row < 0 || row >= cardinality[ROW]) {
      throw new IndexException(row, cardinality[ROW]);
    }
    Vector res = rows.get(row);
    if (res == null) {
      res = new RandomAccessSparseVector(cardinality[COL]);
    }
    return res;
  }
  
  public Vector getColumn(int column) {
    if (column < 0 || column >= cardinality[COL]) {
      throw new IndexException(column, cardinality[COL]);
    }
    Vector res = columns.get(column);
    if (res == null) {
      res = new RandomAccessSparseVector(cardinality[ROW]);
      
    }
    return res;
  }
  
  @Override
  public Matrix like(int rows, int columns) {
    return new SparseMatrixThreaded(rows, columns);
  }
  
  public Matrix assignColumn(int column, Vector other) {
    if (cardinality[COL] != other.size()) {
      throw new CardinalityException(cardinality[ROW], other.size());
    }
    if (column < 0 || column >= cardinality[COL]) {
      throw new IndexException(column, cardinality[COL]);
    }
    columns.put(column, other);
    Iterator<Vector.Element> itr = other.iterateNonZero();
    while (itr.hasNext()) {
      Vector.Element e = itr.next();
      setQuick(e.index(), column, e.get());
    }
    return this;
  }
  
  public Matrix assignRow(int row, Vector other) {
    if (cardinality[COL] != other.size()) {
      throw new CardinalityException(cardinality[COL], other.size());
    }
    if (row < 0 || row >= cardinality[ROW]) {
      throw new IndexException(row, cardinality[ROW]);
    }
    rows.put(row, other);
    Iterator<Vector.Element> itr = other.iterateNonZero();
    while (itr.hasNext()) {
      Vector.Element e = itr.next();
      setQuick(row, e.index(), e.get());
    }
    return this;
  }
  
  public synchronized Matrix assignRowAndSyncLater(int row, Vector other) {
    if (cardinality[COL] != other.size()) {
      throw new CardinalityException(cardinality[COL], other.size());
    }
    if (row < 0 || row >= cardinality[ROW]) {
      throw new IndexException(row, cardinality[ROW]);
    }
    rows.put(row, other);
    return this;
  }
  
  // TODO need test
  public void syncRowsToColumns() {
    columns = new OpenIntObjectHashMap<Vector>();
    rows.forEachPair(new IntObjectProcedure<Vector>() {
      
      public boolean apply(int row, Vector vector) {
        Iterator<Element> itr = vector.iterateNonZero();
        while (itr.hasNext()) {
          Element e = itr.next();
          Vector c = columns.get(e.index());
          if (c == null) {
            c = new RandomAccessSparseVector(cardinality[ROW]);
            columns.put(e.index(), c);
          }
          c.setQuick(row, e.get());
        }
        return true;
      }
    });
  }
  
  public int[] getNumNondefaultElements() {
    int[] result = new int[2];
    result[ROW] = rows.size();
    for (Vector vectorEntry : rows.values()) {
      result[COL] = Math.max(result[COL], vectorEntry.getNumNondefaultElements());
    }
    return result;
  }
  
  public double getQuick(int row, int column) {
    Vector r = rows.get(row);
    return r == null ? 0.0 : r.getQuick(column);
  }
  
  public Matrix like() {
    return new SparseMatrixThreaded(cardinality);
  }
  
  public Matrix viewPart(int[] offset, int[] size) {
    if (offset[ROW] < 0) {
      throw new IndexException(offset[ROW], cardinality[ROW]);
    }
    if (offset[ROW] + size[ROW] > cardinality[ROW]) {
      throw new IndexException(offset[ROW] + size[ROW], cardinality[ROW]);
    }
    if (offset[COL] < 0) {
      throw new IndexException(offset[COL], cardinality[COL]);
    }
    if (offset[COL] + size[COL] > cardinality[COL]) {
      throw new IndexException(offset[COL] + size[COL], cardinality[COL]);
    }
    return new MatrixView(this, offset, size);
  }
  
  @Override
  public Matrix times(Matrix inputMatrix) {
    return SparseMatrixMultiplier.multiply(this, inputMatrix);
  }
  
  @Override
  public Vector times(Vector inputVector) {
    return SparseMatrixMultiplier.multiply(this, inputVector);
  }
  
  public static SparseMatrixThreaded generateRandomSparseMatrix(int rows, int columns, double howfull) {
    int nonZeroItems = (int) (howfull * rows * columns);
    System.out.println("Non Zero Elements: " + nonZeroItems);
    
    SparseMatrixThreaded sparseMatrix = new SparseMatrixThreaded(rows, columns);
    
    Random random = new Random();
    while (nonZeroItems != 0) {
      int randomRow = random.nextInt(rows);
      int randomColumn = random.nextInt(columns);
      double randomValue = random.nextDouble();
      // System.out.println(randomRow + " " + randomColumn + " " + randomValue);
      sparseMatrix.setQuick(randomRow, randomColumn, randomValue);
      nonZeroItems--;
    }
    return sparseMatrix;
  }
  
  @Override
  public Iterator<MatrixSlice> iterator() {
    final IntArrayList keys = new IntArrayList(rows.size());
    rows.keys(keys);
    return new AbstractIterator<MatrixSlice>() {
      private int slice;
      
      @Override
      protected MatrixSlice computeNext() {
        if (slice >= rows.size()) {
          return endOfData();
        }
        int i = keys.get(slice);
        Vector row = rows.get(i);
        slice++;
        return new MatrixSlice(row, i);
      }
    };
  }
  
  /**
   * Transforms this vector into a diagonal matrix and performs multiplication.
   * 
   * @param vector
   * @return product
   */
  public Matrix timesDiagonaledVector(Vector vector) {
    return SparseMatrixMultiplier.multiplyByDiagonalizedVector(this, vector);
  }
}
