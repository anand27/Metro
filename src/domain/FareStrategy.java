package domain;

public enum FareStrategy {

	DELHI("delhi_fare_strategy.properties"),
	MUMBAI("mumbai_fare_strategy.properties");
	
	private String classpathFileName;

	private FareStrategy(String classpathFileName) {
		this.classpathFileName = classpathFileName;
	}

	public String getClasspathFileName() {
		return classpathFileName;
	}
	
	
}
