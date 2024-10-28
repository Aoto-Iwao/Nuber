package nuber.students;

public class Driver extends Person {
	
	//aoto
	private Passenger passenger;
	
	public Driver(String driverName, int maxSleep)
	{
		super(driverName, maxSleep);
	}
	
	/**
	 * Stores the provided passenger as the driver's current passenger and then
	 * sleeps the thread for between 0-maxDelay milliseconds.
	 * 
	 * @param newPassenger Passenger to collect
	 * @throws InterruptedException
	 */
//	Output was Actual delay is 9
//	the passenger is: Alex
	public void pickUpPassenger(Passenger newPassenger)throws InterruptedException
	{
		passenger = newPassenger;
		int actualDelay;
		actualDelay = randomWithRange(0, maxSleep); 
		//System.out.println("Actual delay is "+actualDelay);
		//System.out.println("the passenger is: " + passenger.name);
		Thread.sleep(actualDelay);
	}

	/**
	 * Sleeps the thread for the amount of time returned by the current 
	 * passenger's getTravelTime() function
	 * 
	 * @throws InterruptedException
	 */
	
	//Output:  travel time of: Alex -> 59
	public void driveToDestination() throws InterruptedException {
		int travelTime;
		travelTime = passenger.getTravelTime();
		System.out.println("travel time of: " + passenger.name + " -> " + travelTime);
	}
	
	//add this since enable sleep.
	private static int randomWithRange(int min, int max) {
		int range = (max - min) + 1;
		return (int) (Math.random() * range) + min;}
	
}
