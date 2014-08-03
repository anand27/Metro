package domain;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import exception.MinimumBalanceException;
import exception.NotEnoughBalanceException;
import exception.UnauthorizedAccessException;

public abstract class Metro {

	private String name;
	private String owner;
	private Date established;
	private List<Station> stations;
	private PropertiesConfiguration config;
	private Map<Integer, SmartCard> mapOfSmartCards;
	//private Properties propForMetroCardsList;
	//private OutputStream output;
	private Properties propForFareStrategy;
	private InputStream input;
	private int numberOfCardsIssued;
	private File file;
	private String FARE_STRATEGY;
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy - dd hh:mm:ss a");
	public static final double INITIAL_FIXED_AMOUNT_FOR_SMART_CARD = 5.5;
	
	public Metro(String name, String owner, Date estd, List<Station> list, FareStrategy strategy) {
		
		this.name = name;
		this.owner = owner;
		this.established = estd;
		this.stations = list;
		this.mapOfSmartCards = new HashMap<Integer, SmartCard>();
		this.propForFareStrategy = new Properties();
		this.input = null;
		this.numberOfCardsIssued = 0;
		this.file = null;
		this.FARE_STRATEGY = strategy.getClasspathFileName();
		
		try {
			this.config = new PropertiesConfiguration("metrocardIdList.properties");
			loadMetroFareStrategiesFromClasspath();
			loadSmartCadeDataFromDiskToLocalCacheMap();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		
		System.out.println("Initializing "+ this.name + " - " + this.owner + " - " + this.established + " .... ");
	}
	
	public List<Station> getStations() {
		return stations;
	}

	private void loadSmartCadeDataFromDiskToLocalCacheMap() throws IOException,
			FileNotFoundException, ClassNotFoundException {
		this.file = new File("smartcard.cache");
		//FileInputStream in;
		if (!file.exists()) {
			file.createNewFile();
		} else if (file.length() > 0){
		//}else if((in = new FileInputStream(file)).available() > 0){
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
			SmartCard card = null;
			boolean doneReadingInput = false;
			while(!doneReadingInput){
				try{
				Object obj = ois.readObject();
				card = (SmartCard)obj;
				++numberOfCardsIssued;
				mapOfSmartCards.put(card.getCardId(), card);
				} catch(EOFException e){
					doneReadingInput = true;
				}
			}
			ois.close();
		}
	}

	private void loadMetroFareStrategiesFromClasspath() throws IOException {
		this.input = this.getClass().getClassLoader().getResourceAsStream(FARE_STRATEGY);
		propForFareStrategy.load(input);
		input.close();
	}
	
	public SmartCard issueSmartCard(int cardNos) throws IOException{
		SmartCard card = this.mapOfSmartCards.get(cardNos);
		if(card == null){
			card = new SmartCard(cardNos);
		}
		return card;
	}

	private void dumpCreatedCardIdToRootPath(SmartCard card) {
		if(card.isNew()){
			try {
				config.getLayout().setHeaderComment("SMART CARD ID CREATED BY METRO CORPORATION OF INDIA");
				config.addProperty("Card"+(++numberOfCardsIssued), card.getCardId());
				config.getLayout().setFooterComment("\nUPDATED ON -> " + dateFormat.format(new Date()));
				config.save();
				card.setNew(false);
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * OVERWRITES DATA , DOES NOT APPEND
		 * output = new FileOutputStream("metrocardIdList.properties");
		this.propForMetroCardsList.setProperty(new String("Card"+(++numberOfCardsIssued)), String.valueOf(card.getCardId()));
		propForMetroCardsList.store(this.output, "CARD ENTRY");
		output.close();*/
	}

	private double getAmountFactor() {
		
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		if(dayOfWeek == 1 || dayOfWeek == 7) {
			return Double.parseDouble(propForFareStrategy.getProperty("weekend"));
		} else {
			return Double.parseDouble(propForFareStrategy.getProperty("weekday"));
		}
	}
	
	private void dumpSmartCardDataFromLocalCacheMapToDisk() throws FileNotFoundException, IOException{
		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		if(this.mapOfSmartCards.size() > 0){
			for(SmartCard card : this.mapOfSmartCards.values()){
				oos.writeObject(card);
			}
		}
		oos.flush();
		oos.close();
	}

	public void shutdown()  {
		try {
			dumpSmartCardDataFromLocalCacheMapToDisk();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<Integer, SmartCard> getMapOfSmartCards() {
		return mapOfSmartCards;
	}
	
	public void swipe(Swipe swipe, Station station, SmartCard smartCard) throws UnauthorizedAccessException, MinimumBalanceException, NotEnoughBalanceException {

		switch(swipe){
		
		case IN:
					synchronized (this) {
						checkForUnauthorizedEntry(smartCard);
						smartCard.setSourceStation(station);
						checkForValidBalanceBeforeJourney(smartCard);
						updateSourceStationFootfall(smartCard);
						updateJourneyStartDetails(smartCard);
						break;
					}
		
		case OUT:
					synchronized (this) {
						checkForUnauthorizedExit(smartCard);
						smartCard.setDestinationStation(station);
						checkForValidBalanceAfterJourney(smartCard);
						updateDestinationStationFootfall(smartCard);
						updateJourneyEndDetails(smartCard);
						updateSystemRecordsForThisCard(smartCard);
						break;
					}
		}
	}
	
	private void updateSystemRecordsForThisCard(SmartCard smartCard) {
		dumpCreatedCardIdToRootPath(smartCard);
		this.mapOfSmartCards.put(smartCard.getCardId(), smartCard);
	}
	
	private void checkForUnauthorizedEntry(SmartCard smartCard) throws UnauthorizedAccessException {
		if(smartCard.isTravelling()==true)
			throw new UnauthorizedAccessException("Invalid entry/exit. Kindly check with authorities");
	}
	
	private void checkForUnauthorizedExit(SmartCard smartCard) throws UnauthorizedAccessException {
		if(smartCard.isTravelling()==false)
			throw new UnauthorizedAccessException("Invalid entry/exit. Kindly check with authorities");
	}
	
	private void updateJourneyStartDetails(SmartCard smartCard) {
		smartCard.setTravelling(true);
	}
	
	private void updateSourceStationFootfall(SmartCard smartCard) {
		smartCard.getSourceStation().setSwipeInFootFall();
	}

	private void updateDestinationStationFootfall(SmartCard smartCard) {
		smartCard.getDestinationStation().setSwipeOutFootFall();
	}
	
	private void checkForValidBalanceBeforeJourney(SmartCard smartCard) throws MinimumBalanceException, NotEnoughBalanceException {
		if(smartCard.getBalance() < INITIAL_FIXED_AMOUNT_FOR_SMART_CARD)
			throw new MinimumBalanceException("Not enough balance - your account balance : Rs. " +
					smartCard.getBalance() + " is less than Minimum balance of Rs. " + 
					INITIAL_FIXED_AMOUNT_FOR_SMART_CARD + " - kindly recharge your card");
	}
	
	private void checkForValidBalanceAfterJourney(SmartCard smartCard) throws MinimumBalanceException, NotEnoughBalanceException {
		
		double currentFare = computeFareToBeDeducted(smartCard.getSourceStation(), smartCard.getDestinationStation());
		
		if(smartCard.getBalance() < currentFare)
			throw new NotEnoughBalanceException("Not enough balance - your account balance : Rs. " +
					smartCard.getBalance() + " is less than fare of your travel Rs. " + 
					currentFare + " - kindly recharge your card");
		
		smartCard.setCurrentFare(currentFare);
	}
	
	private double computeFareToBeDeducted(Station stationTravellingFrom, Station stationTravellingTo) {
		
		int stationsTravelled = Math.abs(stationTravellingTo.getStationnumber() - stationTravellingFrom.getStationnumber());
		double amountFactor = getAmountFactor();
		double fare = amountFactor * stationsTravelled; 
		return fare;
	}
	
	private void updateJourneyEndDetails(SmartCard smartCard) {

		smartCard.setBalance(smartCard.getBalance()-smartCard.getCurrentFare());
		smartCard.setLastFare(smartCard.getCurrentFare());
		smartCard.setLastSourceStation(smartCard.getSourceStation());
		smartCard.setLastDestinationStation(smartCard.getDestinationStation());
		
		smartCard.setCurrentFare(0.0);
		smartCard.setSourceStation(null);
		smartCard.setDestinationStation(null);
		smartCard.setTravelling(false);
		
	}

	public String getname() {
		return this.name;
	}

	
}
