package nuber.students;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import javax.swing.plaf.synth.Region;

/**
 * The core Dispatch class that instantiates and manages everything for Nuber
 * 
 * @author james
 *
 */
public class NuberDispatch{

	/**
	 * The maximum number of idle drivers that can be awaiting a booking 
	 */
	private final int MAX_DRIVERS = 999;
	
	private boolean logEvents = false;
	
	private HashMap<String, Integer> regionInfo;
	
	// added by Aoto
	
	private int max_drivers = MAX_DRIVERS;
	private Semaphore queueSemaphore = new Semaphore(max_drivers);
	
	//this is for driver. 
	protected BlockingQueue<Driver> idleDriver = new ArrayBlockingQueue<Driver>(max_drivers);
	
	//this is for enable to set the semaphore for one or more regions.
	//protected HashMap<String, Semaphore> semaphoreForEachRegions;
	
	private int bookingAwaitingDriver = 0;
	
	protected HashMap<String, NuberRegion> nuberRegionHashMap;
	
	
	/**
	 * Creates a new dispatch objects and instantiates the required regions and any other objects required.
	 * It should be able to handle a variable number of regions based on the HashMap provided.
	 * 
	 * @param regionInfo Map of region names and the max simultaneous bookings they can handle
	 * @param logEvents Whether logEvent should print out events passed to it
	 */
	public NuberDispatch(HashMap<String, Integer> regionInfo, boolean logEvents)
	{
		this.regionInfo = regionInfo;
		this.logEvents = logEvents;
		//Update max driver by its region. 
//		this.max_drivers = Collections.max(regionInfo.values());
//		this.queueSemaphore = new Semaphore(max_drivers);
		
		//this.queueSemaphore = new Semaphore(max_drivers);
		
		//this.semaphoreForEachRegions = new HashMap<>();
		//EntrySetでMapの全てのStringとIntの組み合わせを返して、一つずつ取り出すためにEntryとしてる。
		for (Map.Entry<String, Integer> entry : regionInfo.entrySet()) {
			//this.semaphoreForEachRegions.put(entry.getKey(), new Semaphore(entry.getValue()));
			this.nuberRegionHashMap.put(entry.getKey(), new NuberRegion(this, entry.getKey(), entry.getValue()))
		}
		
	}
	
	/**
	 * Adds drivers to a queue of idle driver.
	 * 
	 * Must be able to have drivers added from multiple threads.
	 * 
	 * @param The driver to add to the queue.
	 * @return Returns true if driver was added to the queue
	 */
	public boolean addDriver(Driver newDriver)
	{
		try {
			
			System.out.println("max_driver: " + max_drivers);
			//BlockingQueue queue = new ArrayBlockingQueue(MAX_DRIVERS);
			System.out.println("Available Semaphore: "+ queueSemaphore.availablePermits());
			System.out.println("Here is addDriver");
			queueSemaphore.acquire();
			System.out.println("Semaphore: "+ queueSemaphore.availablePermits());
			System.out.println("Here is addDriver");
			idleDriver.put(newDriver);
			
			System.out.println("Here is addDriver");
			//System.out.println("take: " + idleDriver.take());
			System.out.println("regionInfo" + regionInfo);
			System.out.println("name--" + idleDriver.element().name);
			
//			int await;
//			await =  getBookingsAwaitingDriver();
//			System.out.println("await: " + await);
			
			
			return true;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("error");
			return false;
		}	
	}
	
	/**
	 * Gets a driver from the front of the queue
	 *  
	 * Must be able to have drivers added from multiple threads.
	 * 
	 * @return A driver that has been removed from the queue
	 */
	public Driver getDriver()
	{
		try {
			System.out.println("Here is getDriver");
			Driver driver = idleDriver.take();
			System.out.println("Here is after driver = (Driver) idleDriver.take();" + driver);
			System.out.println("Semaphore: "+ queueSemaphore.availablePermits());
			queueSemaphore.release();
			System.out.println("Semaphore: "+ queueSemaphore.availablePermits());
			return driver;
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	
	/**
	 * Prints out the string
	 * 	    booking + ": " + message
	 * to the standard output only if the logEvents variable passed into the constructor was true
	 * 
	 * @param booking The booking that's responsible for the event occurring
	 * @param message The message to show
	 */
	public void logEvent(Booking booking, String message) {
		
		if (!logEvents) return;
		
		System.out.println(booking + ": " + message);
	}

	/**
	 * Books a given passenger into a given Nuber region.
	 * 
	 * Once a passenger is booked, the getBookingsAwaitingDriver() should be returning one higher.
	 * 
	 * If the region has been asked to shutdown, the booking should be rejected, and null returned.
	 * 
	 * @param passenger The passenger to book
	 * @param region The region to book them into
	 * @return returns a Future<BookingResult> object
	 */
	
	/**
	* 指定の乗客を指定のNuber地域に予約します。
	*
	* 乗客が予約されると、getBookingsAwaitingDriver() は1つ大きな値を返します。
	*
	* 地域がシャットダウンを求められている場合、予約は拒否され、nullが返されます。
	*
	* @param passenger 予約する乗客
	* @param region 予約する地域
	* @return Future<BookingResult> オブジェクトを返します
	*/
	
	public Future<BookingResult> bookPassenger(Passenger passenger, String region) {
		
		System.out.println("region in bookPassenger: " + region);
		
		Semaphore selectedRegionSemaphore = semaphoreForEachRegions.get(region);
		
		try {
			System.out.println("availablePermits: " + selectedRegionSemaphore.availablePermits());
			selectedRegionSemaphore.acquire();
			System.out.println("availablePermits: " + selectedRegionSemaphore.availablePermits());
			
		}catch (Exception e) {
			// TODO: handle exception
		}
		
		//Once a passenger is booked, the getBookingsAwaitingDriver() should be returning one higher.
		bookingAwaitingDriver++;
		return null;
	}

	/**
	 * Gets the number of non-completed bookings that are awaiting a driver from dispatch
	 * 
	 * Once a driver is given to a booking, the value in this counter should be reduced by one
	 * 
	 * @return Number of bookings awaiting driver, across ALL regions
	 */
	public int getBookingsAwaitingDriver()
	{
		return bookingAwaitingDriver;
	}
	
	/**
	 * Tells all regions to finish existing bookings already allocated, and stop accepting new bookings
	 */
	public void shutdown() {
	}

}
