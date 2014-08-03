package domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

public class SmartCardTest {

	@Rule
	private ExpectedException excption = ExpectedException.none();
	
	private SmartCard card;
	
	@Before
	public void init(){
		this.card = new SmartCard(1234);
	}
	
	
}
