package info.vincent.matrix;

import org.apache.mahout.SparseMatrixThreaded;
import org.apache.mahout.SparseMatrixMultiplier;

public class MainClass {
	public static void main(String args[]){
	  System.out.println("Rows, Cols, Density");
	  long time = System.currentTimeMillis();
	  int numRows = Integer.parseInt(args[0]);
	  int numColumns = Integer.parseInt(args[1]);
	  double howFull = Double.parseDouble(args[2]);
	  int threads = Integer.parseInt(args[3]);
	  
	  System.out.println("Making: " + numRows + " " + numColumns + " " + "that are " + howFull + " full");

	  time = System.currentTimeMillis();
	  SparseMatrixThreaded a = SparseMatrixThreaded.generateRandomSparseMatrix(numRows, numColumns, howFull);
	  SparseMatrixThreaded b = SparseMatrixThreaded.generateRandomSparseMatrix(numColumns, numRows, howFull);
	  System.out.println("Took this long to make the random matrices: " + (System.currentTimeMillis()-time));
	  SparseMatrixMultiplier.multiply(a, b,threads);
    System.out.println("Finished and Took: " + (System.currentTimeMillis()-time));

	  
	  
	  
	  
	  
	  
//		int numProbes = 16;
//		int numSamples = 30000;
//		
//		time = System.currentTimeMillis();
//		SparseMatrixFinal matrix = new SparseMatrixFinal(numProbes * numSamples, (numSamples + numProbes - 1));
//	    int rowPosition = 0;
//	    for (int column = 0; column < numSamples; column++) {
//	      for (int probeNumber = 0; probeNumber < numProbes; probeNumber++) {
//	        matrix.setQuick(rowPosition, column, 1);
//	        if (rowPosition != 0 && ((rowPosition + 1) % (numProbes) == 0)) {
//	          for (int i = 0; i < (numProbes - 1); i++) {
//	            matrix.setQuick(rowPosition, numSamples + i, -1);
//	          }
//	        } else {
//	          matrix.setQuick(rowPosition, (numSamples + probeNumber), 1);
//	        }
//	        rowPosition++;
//	      }
//	    }
//	    System.out.println("Took: " + (System.currentTimeMillis()-time));
//	    
//	    time = System.currentTimeMillis();
//	    SparseMatrixFinal matrixTranspose = new SparseMatrixFinal((numSamples + numProbes - 1), numProbes * numSamples);
//	    int column = 0;
//	    for (int i = 0; i < numSamples; i++) {
//	      int offset = 0;
//	      for (int j = 0; j < numProbes; j++) {
//	        matrixTranspose.setQuick(i, column, 1);
//	        matrixTranspose.setQuick(numSamples + offset, column, 1);
//	        column++;
//	        offset++;
//	      }
//	      for (int v = 0; v < numProbes - 1; v++) {
//	        matrixTranspose.setQuick(numSamples + v, column - 1, -1);
//	      }
//	    }
//	    System.out.println("Took: " + (System.currentTimeMillis()-time));
//	    
//	    time = System.currentTimeMillis();
//	    SparseMatrixMultiplier.multiply(matrixTranspose, matrix);
////	    SparseMatrix.multiply(matrixTranspose, matrix);
//	    System.out.println("Finished and Took: " + (System.currentTimeMillis()-time));
   
	}

}
