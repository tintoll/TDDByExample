import java.util.Hashtable;

import javax.swing.text.FieldView;

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
		assertFalse(Money.franc(5).equals(Money.dollar(5)));
	}
	
	public void testCurrency() {
		assertEquals("USD", Money.dollar(1).currency());
		assertEquals("CHF", Money.franc(1).currency());
	}
	
	public void testSimpleAddition() {
		
		Money five = Money.dollar(5);
		Expression sum = five.plus(five);
		Bank bank = new Bank();
		Money reduced = bank.reduce(sum,"USD");
		assertEquals(Money.dollar(10), reduced);
	}
	
	public void testPlusReturnSum() {
		Money five = Money.dollar(5);
		Expression result = five.plus(five);
		Sum sum = (Sum)result;
		assertEquals(five, sum.augend);
		assertEquals(five, sum.addend);
	}
	
	public void testReduceSum() {
		Expression sum = new Sum(Money.dollar(3), Money.dollar(4));
		Bank bank = new Bank();
		Money result = bank.reduce(sum, "USD");
		assertEquals(Money.dollar(7), result);
	}
	
	public void testReduceMoney() {
		Bank bank = new Bank();
		Money result = bank.reduce(Money.dollar(1), "USD");
		assertEquals(Money.dollar(1), result);
	}
	
	public void testReduceMoneyDifferentCurrency() {
		Bank bank = new Bank();
		bank.addRate("CHF","USD",2);
		Money result = bank.reduce(Money.franc(2), "USD");
		assertEquals(Money.dollar(1), result);
	}
	
	public void testIdentityRate() {
		assertEquals(1, new Bank().rate("USD", "USD"));
	}
	
	public void testMixedAddition() {
		Expression fiveBucks = Money.dollar(5);
		Expression tenFrancs = Money.franc(10);
		Bank bank = new Bank();
		bank.addRate("CHF", "USD", 2);
		Money result = bank.reduce(fiveBucks.plus(tenFrancs), "USD");
		assertEquals(Money.dollar(10), result);
	}
	
	public void testSumPlusMoney() {
		Expression fiveBucks  = Money.dollar(5);
		Expression tenFrancs  = Money.franc(10);
		Bank bank = new Bank();
		bank.addRate("CHF", "USD", 2);
		Expression sum= new Sum(fiveBucks, tenFrancs).plus(fiveBucks);
		Money result = bank.reduce(sum, "USD");
		assertEquals(Money.dollar(15), result);
	}
	
	public void testSumTimes() {
		Expression fiveBucks  = Money.dollar(5);
		Expression tenFrancs  = Money.franc(10);
		Bank bank = new Bank();
		bank.addRate("CHF", "USD", 2);
		Expression sum= new Sum(fiveBucks, tenFrancs).times(2);
		Money result = bank.reduce(sum, "USD");
		assertEquals(Money.dollar(20), result);
	}
//	public void testSameCurrencyReturnsMoney() {
//		Expression sum = Money.dollar(1).plus(Money.dollar(1));
//		assertTrue(sum instanceof Money);
//	}

}
class Money implements Expression{
	protected int amount;
	protected String currency;
	
	public boolean equals(Object object) {
		Money money = (Money)object;
		return amount == money.amount && currency().equals(money.currency());
	}
	public Expression plus(Expression addend) {
		return new Sum(this, addend);
	}
	public Money(int amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	static Money dollar(int amount) {
		return new Money(amount, "USD");
	}
	static Money franc(int amount) {
		return new Money(amount, "CHF");
	}

	public Expression times(int multiplier) {
		return new Money(amount * multiplier, currency);
	}
	
	String currency() {
		return currency;
	}
	
	public Money reduce(Bank bank,String to) {
		int rate = bank.rate(currency, to);
		return new Money(amount / rate, to);
	}
	public String toString() {
		return amount + " "+currency;
	}
}

interface Expression {
	Money reduce(Bank bank, String to);
	Expression plus(Expression expression);
	Expression times(int multiplier);
}
class Bank {
	private Hashtable rates = new Hashtable<>();
	public Money reduce(Expression source, String to) {
		return source.reduce(this, to);
	}
	
	int rate(String from, String to) {
		if(from.equals(to)) return 1;
		Integer rate = (Integer)rates.get(new Pair(from, to));
		return rate.intValue();
	}

	public void addRate(String from, String to, int rate) {
		rates.put(new Pair(from, to), new Integer(rate));
	}
	private class Pair {
		private String from;
		private String to;
		Pair(String from, String to){
			this.from = from;
			this.to = to;
		}
		
		public boolean equals(Object obj) {
			Pair pair = (Pair)obj;
			return from.equals(pair.from) && to.equals(pair.to);
		}

		public int hashCode() {
			return 0;
		}
	}
	
}

class Sum implements Expression{
	Expression augend;
	Expression addend;
	
	public Sum(Expression augend, Expression addend) {
		this.augend = augend;
		this.addend = addend;
	}

	public Expression times(int multiplier) {
		return new Sum(augend.times(multiplier), addend.times(multiplier));
	}

	public Money reduce(Bank bank,String to) {
		int amount = augend.reduce(bank, to).amount + addend.reduce(bank, to).amount;
		return new Money(amount, to);
	}

	@Override
	public Expression plus(Expression expression) {
		return new Sum(this, addend);
	}
	
}