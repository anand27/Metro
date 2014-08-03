package domain;

import exception.MinimumBalanceException;
import exception.NotEnoughBalanceException;
import exception.UnauthorizedAccessException;

public class Traveller {

	private SmartCard card;
	
	public Traveller() {
	}

	public SmartCard getCard() {
		return card;
	}

	public void setCard(SmartCard card) {
		this.card = card;
	}

	public void travel(String source, String destination) throws UnauthorizedAccessException, MinimumBalanceException, NotEnoughBalanceException {
		Station sourceStation = Station.getStation(source);
		Station destinationStation = Station.getStation(destination);
		this.card.swipe(Swipe.IN, sourceStation);
		this.card.swipe(Swipe.OUT, destinationStation);
	}
	
}
