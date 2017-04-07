import junit.framework.TestCase;

public class MoneyTest extends TestCase {
	
	public void testMultiplication() {
		Money five = Money.dollar(5);
		assertEquals(Money.dollar(10), five.times(2));
		assertEquals(Money.dollar(15), five.times(3));
	}
	
	public void testFrancMultiplication() {
		Money five = Money.franc(5);
		assertEquals(Money.franc(10), five.times(2));
		assertEquals(Money.franc(15), five.times(3));
	}
	
	public void testEquality() {
		assertTrue(Money.dollar(5).equals(Money.dollar(5)));
		assertFalse(Money.dollar(6).equals(Money.dollar(5)));
		
		assertTrue(Money.franc(5).equals(new Franc(5)));
		assertFalse(Money.franc(6).equals(new Franc(5)));
		
		assertFalse(Money.franc(5).equals(Money.dollar(5)));
	}

}
abstract class Money {
	protected int amount;
	
	public boolean equals(Object object) {
		Money money = (Money)object;
		return amount == money.amount && getClass().equals(money.getClass());
	}

	static Money dollar(int amount) {
		return new Dollar(amount);
	}
	static Money franc(int amount) {
		return new Franc(amount);
	}
	
	abstract Money times(int multiplier);
}

class Dollar extends Money{

	Dollar(int amount) {
		this.amount = amount;
	}

	Money times(int multiplier) {
		return new Dollar(amount * multiplier);
	}
	
	
	
}
class Franc extends Money{

	Franc(int amount) {
		this.amount = amount;
	}

	Money times(int multiplier) {
		return new Franc(amount * multiplier);
	}
	
	
}