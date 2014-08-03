package domain;

import java.util.ArrayList;
import java.util.List;

public enum Station {

	A1(1, "A1", State.DELHI),
	A2(2, "A2", State.DELHI),
	A3(3, "A3", State.DELHI),
	A4(4, "A4", State.DELHI),
	A5(5, "A5", State.DELHI),
	A6(6, "A6", State.DELHI),
	A7(7, "A7", State.DELHI),
	A8(8, "A8", State.DELHI),
	A9(9, "A9", State.DELHI),
	A10(10, "A10", State.DELHI),
	M1(1, "M1", State.MUMBAI),
	M2(2, "M2", State.MUMBAI),
	M3(3, "M3", State.MUMBAI),
	M4(4, "M4", State.MUMBAI),
	M5(5, "M5", State.MUMBAI);
	
	private String stationName;
	private int stationnumber;
	private int swipeInFootFall;
	private int swipeOutFootFall;
	private State state;

	private Station(int stationnumber, String name, State belongsTo) {
		this.stationnumber = stationnumber;
		this.swipeInFootFall = 0;
		this.swipeOutFootFall = 0;
		this.stationName = name;
		this.state = belongsTo;
	}

	public int getStationnumber() {
		return stationnumber;
	}

	public int getSwipeInFootFall() {
		return swipeInFootFall;
	}

	public void setSwipeInFootFall() {
		this.swipeInFootFall += 1;
	}

	public int getSwipeOutFootFall() {
		return swipeOutFootFall;
	}

	public void setSwipeOutFootFall() {
		this.swipeOutFootFall += 1;
	}

	public String getStationName() {
		return stationName;
	}

	public static Station getStation(String target) {

		Station stationToReturn = null;
		
		for(Station station : Station.values()){
			if(station.getStationName().equals(target))
				stationToReturn = station;
		}
		
		return stationToReturn;
	}
	
	public State getState() {
		return state;
	}

	public static List<Station> getListOfStationByState(State state){
		List<Station> list = new ArrayList<Station>();
		for(Station station : Station.values()){
			if(station.getState().equals(state))
				list.add(station);
		}
		return list;
	}
}
