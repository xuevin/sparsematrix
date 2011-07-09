package matrix;


import info.vincent.matrix.SparseMatrix;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class SparseMatrixTest {
	
	private SparseMatrix sparseMatrix;
	@Before
	public void setUp() throws Exception {
//		0	0	0	0	0
//		1	2	0	0	0
//		0	0	3	0	0
//		0	0	2	1	1
		
		sparseMatrix = new SparseMatrix(4, 5);
		sparseMatrix.setQuick(1, 1, 1.0);
	}
	@Test
	public void showThatSetupWorks(){
		assertEquals(sparseMatrix.getRow(1).getColumn(1),sparseMatrix.getColumn(1).getRow(1),0);
	}
	@Test
	public void testHowLongMakingDesignMatrixTakes(){
		long time = System.currentTimeMillis();
		getDesignMatrix(16,30000);
	    System.out.println("Took: " + (System.currentTimeMillis()-time));
	}
	@Test
	public void testHowLongItTakesToAccessAMatrix(){
    SparseMatrix A = getDesignMatrix(12,3000);     
    
    long time = System.currentTimeMillis();
    for(int i =0;i<A.getNumRows();i++){
      for(int j=0;j<A.getNumColumns();j++){
        try{
          A.getRow(i).getColumn(j);
        }catch (NullPointerException e) {
        }
      }
    }
    System.out.println("Took: " + (System.currentTimeMillis()-time));

    
	}
	@Test
	public void showThatMatrixMultiplicationWorks(){
		
		long time = System.currentTimeMillis();
		int numProbes =12;
		int numSamples =300;
		
		SparseMatrix A = getDesignMatrix(numProbes,numSamples);
		SparseMatrix B = getDesignMatrixTranspose(numProbes,numSamples);
		SparseMatrix C = SparseMatrix.multiply(B, A);
//		C.print();
		
		assertEquals(C.getRow(0).getColumn(0),numProbes,0);
	    System.out.println("Took: " + (System.currentTimeMillis()-time));

	}
	private SparseMatrix getDesignMatrix(int numProbes, int numSamples){
		SparseMatrix matrix = new SparseMatrix(numProbes * numSamples, (numSamples + numProbes - 1));
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
	private SparseMatrix getDesignMatrixTranspose(int numProbes, int numSamples){
	    SparseMatrix matrixTranspose = new SparseMatrix((numSamples + numProbes - 1), numProbes * numSamples);
	    int column = 0;
	    for (int i = 0; i < numSamples; i++) {
	      int offset = 0;
	      for (int j = 0; j < numProbes; j++) {
	        matrixTranspose.setQuick(i, column, 1);
	        matrixTranspose.setQuick(numSamples + offset, column, 1);
	        column++;
	        offset++;
	      }
	      for (int v = 0; v < numProbes - 1; v++) {
	        matrixTranspose.setQuick(numSamples + v, column - 1, -1);
	      }
	    }
	    return matrixTranspose;
	}
}
