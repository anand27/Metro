package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import statistics.MetroStatistics;
import domain.DelhiMetro;
import domain.Metro;
import domain.MumbaiMetro;
import domain.SmartCard;
import domain.Station;
import domain.Swipe;
import domain.Traveller;
import exception.NotEnoughBalanceException;
import exception.UnauthorizedAccessException;

public class MetroApplication {

	private Metro metro;
	
	public MetroApplication() {	}
	
	private void stop(){
		metro.shutdown();
	}
	
	private void start(){
		
		displayConsole();
		statisticsConsole();
	}

	private void statisticsConsole() {
		System.out.println("---------------------------------------------------------\n");
		
		System.out.println("Get statistics of smart cards.\n");
		for(SmartCard card : MetroStatistics.getSmartCards()){
			String cardDetails = MetroStatistics.getCardInfo(card.getCardId());
			System.out.println(cardDetails);
		}
		
		System.out.println("---------------------------------------------------------\n");
		
		System.out.println("Get statistics of IN & OUT Footfall of Stations.\n");
		for(Station station : metro.getStations()){
			int output1 = MetroStatistics.getSwipeInFootfallForStation(station);
			int output2 = MetroStatistics.getSwipeOutFootfallForStation(station);
			System.out.println(station.getStationName() + " :  FootFall-IN " + output1 + " FootFall-OUT " + output2 );
		}
		
		System.out.println("---------------------------------------------------------\n");
	}

	private void displayConsole() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		ExecutorService executor = Executors.newFixedThreadPool(1);
		String source = null;
		String destination = null;
		int topup = 0;		
		int cardNos = 0;
		String travel = null;
		String temp = null;
		Traveller user = null;
		String city = null;
		Future<Boolean> future;
		
		System.out.println("In which State are you currently travelling in ?");
		do{
		try {
			if((city=in.readLine()).equalsIgnoreCase("delhi")){
				metro = DelhiMetro.getInstance();
				break;
			}
			else if(city.equalsIgnoreCase("mumbai")){
				metro = MumbaiMetro.getInstance();
				break;
			}else{
				System.out.println("Invalid input - please try again ...");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		}while(!city.equalsIgnoreCase("delhi") && !city.equalsIgnoreCase("mumbai"));
			
		MetroStatistics.setMetroForStatistics(metro);
		
		statisticsConsole();
		
		System.out.println("---------------------------------------------------------");
		System.out.println("                 Welcome to " + metro.getname() + "                   ");
		System.out.println("---------------------------------------------------------");
		
		do{
			System.out.println("\nEnter (YES) to travel or (NO) to exit\n");
			
			try {
				
				if((travel=in.readLine()).equalsIgnoreCase("yes")){
						System.out.println("-----------------------------------------------");
						System.out.println("Kindly pick Source and Destination Station");
						System.out.println("Stations - A1, A2, A3 ... A10");
						System.out.println("Enter Source");
						source = in.readLine();
						System.out.println("Enter Destination");
						destination = in.readLine();
						System.out.println("Enter 4 digit Card number");
						while(!isNumeric(temp = in.readLine())){
							System.out.println("not a valid smart card number ... try again");
						};
						cardNos = Integer.parseInt(temp);
						user = new Traveller();
						user.setCard(getSmartCard(cardNos));
						System.out.println("your balance is -> " + user.getCard().getBalance());
						System.out.println("Enter recharge amount ");
						topup = Integer.parseInt(in.readLine());
						user.getCard().topUp(topup);
						user.setSource(source);
						user.setDestination(destination);
						future = executor.submit(new MyCallable(user, this));
						try {
							if(future.get()){
								System.out.println("your remaining balance is -> " + user.getCard().getBalance());
								System.out.println("Thank you for Travelling\n");
								System.out.println("-----------------------------------------------");
							}
							
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
				}else if(travel.equalsIgnoreCase("no")){
					in.close();
					executor.shutdownNow();
					break;
				}else{
					System.out.println("Invalid input - try again");
					travel = "TRY AGAIN";
				}
				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
		}while(travel.equalsIgnoreCase("yes") || travel.equalsIgnoreCase("TRY AGAIN"));
		
		
	}
	
	private SmartCard getSmartCard(int cardNos) throws IOException {
		return metro.issueSmartCard(cardNos);
	}

	private boolean isNumeric(String s) {  
	    return s.matches("\\d{4}");  
	}
	
	private void swipe(Swipe swipe, Station station, SmartCard smartCard) throws UnauthorizedAccessException, NotEnoughBalanceException {
		metro.swipe(swipe, station, smartCard);
	}
	
	public static void main(String[] args){
		
		MetroApplication ma = new MetroApplication();
		ma.start();
		ma.stop();
		
	}
	
	private static class MyCallable implements Callable<Boolean>{

		private Traveller user;
		private MetroApplication application;
		
		public MyCallable(Traveller user, MetroApplication metroApplication) {
			this.user = user;
			this.application = metroApplication;
		}

		@Override
		public Boolean call() throws Exception{
			
			try {
				
				System.out.println("\n^^^^^ SWIPING IN ^^^^^");
				while(!swipeCard(Swipe.IN));
				System.out.println("~~~~~ TRAVEL BEGINS ~~~~~");
				System.out.print("("+user.getSource()+")");
				for(int i=0; i<10; i++){
					System.out.print(" <----> ");
					Thread.sleep(1000);
				}
				System.out.println("("+user.getDestination()+")");
				System.out.println("~~~~~ TRAVEL ENDED ~~~~~");
				System.out.println("^^^^^ SWIPING OUT ^^^^^\n");
				while(!swipeCard(Swipe.OUT));
				
			} catch (UnauthorizedAccessException e) {
				System.out.println(e.getMessage());
				throw e;
			}
			
			return Boolean.TRUE;
		}
		
		private boolean swipeCard(Swipe swipe) throws UnauthorizedAccessException{
			
			try {
				application.swipe(swipe, swipe.equals(Swipe.IN)?Station.getStation(user.getSource()):Station.getStation(user.getDestination()), user.getCard());
				return true;
			} catch (NotEnoughBalanceException e) {
				System.out.println(e.getMessage());
				rechargeCard();
				return false;
			}
		}
		
		private void rechargeCard(){
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			try {
				System.out.println("#############  Enter Recharge Amount -->");
				int topup = Integer.parseInt(in.readLine());
				this.user.getCard().topUp(topup);
				System.out.println("/\\/\\/\\/\\/\\/\\/  Swiping your card again -->");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
			
		}
	}


}