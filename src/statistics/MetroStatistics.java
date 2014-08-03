package statistics;

import java.util.Collection;

import domain.Metro;
import domain.SmartCard;
import domain.Station;

public class MetroStatistics {

	private static Metro metro;
	
	public static int getSwipeInFootfallForStation(Station station){
		return station.getSwipeInFootFall();
	}
	
	public static int getSwipeOutFootfallForStation(Station station){
		return station.getSwipeOutFootFall();
	}
	
	public static String getCardInfo(int cardId){
		SmartCard smartCard = metro.getMapOfSmartCards().get(cardId);
		/*if(smartCard==null)
			throw new Exception("invalid card id");*/
		
		return "Card " + smartCard.getCardId() + " used last to travel from station " +
				smartCard.getLastSourceStation() + " to station " +
				smartCard.getLastDestinationStation() + ". Fare is Rs " +
				smartCard.getLastFare() + " and balance on the card is Rs " +
				smartCard.getBalance();
	
	}

	public static Collection<SmartCard> getSmartCards() {
		return metro.getMapOfSmartCards().values();
	}

	public static void setMetroForStatistics(Metro metro){
		MetroStatistics.metro = metro;
	}
}
