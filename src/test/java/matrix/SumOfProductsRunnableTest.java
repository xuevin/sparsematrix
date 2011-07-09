package matrix;

import static org.junit.Assert.assertEquals;
import info.vincent.matrix.Column;
import info.vincent.matrix.Row;
import info.vincent.matrix.SumOfProductsRunnable;

import org.junit.Before;
import org.junit.Test;

public class SumOfProductsRunnableTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void showThatGetSumOfProductsIsAccurate() {
		Row a = new Row(3);
		a.setQuick(0, 2);
		a.setQuick(1, 4);
		a.setQuick(2, 6);
		Column b = new Column(3);
		// b.setQuick(0, 0);
		b.setQuick(1, 2);
		// b.setQuick(2, 0);
		assertEquals(8.0, SumOfProductsRunnable.getSumOfProducts(a, b), 0);
	}

}
