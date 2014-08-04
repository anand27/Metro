package domain;

import java.io.Serializable;

public class SmartCard implements Serializable{

	private static final long serialVersionUID = -3095692286172690706L;
	
	private double balance;
	private int cardId;
	private boolean isTravelling;
	
	private double currentFare;
	private Station sourceStation;
	private Station destinationStation;
	
	private double lastFare;
	private Station lastSourceStation;
	private Station lastDestinationStation;
	
	private boolean isNew = true;
	
	public SmartCard(int cardNos) {
		this.cardId = cardNos;
		this.balance = DelhiMetro.INITIAL_FIXED_AMOUNT_FOR_SMART_CARD;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public Station getSourceStation() {
		return sourceStation;
	}

	public Station getDestinationStation() {
		return destinationStation;
	}
	
	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public void topUp(int amount){
		this.balance += amount;
	}
	
	public double getLastFare() {
		return lastFare;
	}

	public int getCardId() {
		return cardId;
	}

	public Station getLastSourceStation() {
		return lastSourceStation;
	}

	public Station getLastDestinationStation() {
		return lastDestinationStation;
	}

	public void setLastSourceStation(Station lastSourceStation) {
		this.lastSourceStation = lastSourceStation;
	}

	public void setLastDestinationStation(Station lastDestinationStation) {
		this.lastDestinationStation = lastDestinationStation;
	}

	public void setLastFare(double lastFare) {
		this.lastFare = lastFare;
	}

	public double getCurrentFare() {
		return currentFare;
	}

	public void setCurrentFare(double currentFare) {
		this.currentFare = currentFare;
	}

	public boolean isTravelling() {
		return isTravelling;
	}

	public void setTravelling(boolean isTravelling) {
		this.isTravelling = isTravelling;
	}

	public void setSourceStation(Station sourceStation) {
		this.sourceStation = sourceStation;
	}

	public void setDestinationStation(Station destinationStation) {
		this.destinationStation = destinationStation;
	}
	
	
}