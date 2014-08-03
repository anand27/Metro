package domain;

import java.util.Date;

public class DelhiMetro extends Metro{

	private static final DelhiMetro metro = new DelhiMetro("Delhi Metro", "D.M.R.C.", new Date());
	
	private DelhiMetro(String name, String owner, Date estd) {	
		
		super(name, owner, estd, Station.getListOfStationByState(State.DELHI), FareStrategy.DELHI);
		System.out.println("Delhi Metro operational.");
	}
	
	public static DelhiMetro getInstance() {
		return metro;
	}
	
}
