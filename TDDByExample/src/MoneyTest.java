import junit.framework.TestCase;

public class MoneyTest extends TestCase {
	
	public void testMultiplication() {
		Dollar five = new Dollar(5);
		assertEquals(new Dollar(10), five.times(2));
		assertEquals(new Dollar(15), five.times(3));
	}
	
	public void testEquality() {
		assertTrue(new Dollar(5).equals(new Dollar(5)));
		assertFalse(new Dollar(6).equals(new Dollar(5)));
	}

}


class Dollar {

	private int amount;

	Dollar(int amount) {
		this.amount = amount;
	}

	Dollar times(int multiplier) {
		return new Dollar(amount * multiplier);
	}
	
	public boolean equals(Object object) {
		Dollar dollar = (Dollar)object;
		return amount == dollar.amount;
	}
	
}