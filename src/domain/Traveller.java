package domain;


public class Traveller {

	private SmartCard card;
	private String source;
	private String destination;
	
	public Traveller() {
	}

	public SmartCard getCard() {
		return card;
	}

	public void setCard(SmartCard card) {
		this.card = card;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	
}
