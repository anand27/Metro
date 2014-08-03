package domain;

import java.util.Date;

public class MumbaiMetro extends Metro {

	private static final MumbaiMetro metro = new MumbaiMetro("Mumbai Metro", "M.R.", new Date());
	
	private MumbaiMetro(String name, String owner, Date estd) {	
		
		super(name, owner, estd, Station.getListOfStationByState(State.MUMBAI), FareStrategy.MUMBAI);
		System.out.println("Mumbai Metro operational.");
	}
	
	public static MumbaiMetro getInstance() {
		return metro;
	}
	
}
