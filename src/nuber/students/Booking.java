package nuber.students;

import java.util.concurrent.Callable;

/**
 * 
 * Booking represents the overall "job" for a passenger getting to their destination.
 * 
 * It begins with a passenger, and when the booking is commenced by the region 
 * responsible for it, an available driver is allocated from dispatch. If no driver is 
 * available, the booking must wait until one is. When the passenger arrives at the destination,
 * a BookingResult object is provided with the overall information for the booking.
 * 
 * The Booking must track how long it takes, from the instant it is created, to when the 
 * passenger arrives at their destination. This should be done using Date class' getTime().
 * 
 * Booking's should have a globally unique, sequential ID, allocated on their creation. 
 * This should be multi-thread friendly, allowing bookings to be created from different threads.
 * 
 * @author james
 *
 */
public class Booking implements Callable<BookingResult>{
	
	//aoto
	protected NuberDispatch dispatch;
	protected Passenger passenger;
	protected Driver availableDriver;
	protected int jobID = 1;
	
	//use static int since if this is not static, bookingID/jobID is initialized 
	//every single time when the instance called.
	private static int bookingId = 1;	
	/**
	 * Creates a new booking for a given Nuber dispatch and passenger, noting that no
	 * driver is provided as it will depend on whether one is available when the region 
	 * can begin processing this booking.
	 * 
	 * @param dispatch
	 * @param passenger
	 */
	/** This is Japanese translation for understanding better.
	* 指定されたNuberの派遣と乗客に対して、新しい予約を作成します。
	* ドライバーは提供されません。これは、地域がこの予約の処理を開始できるかどうかによって、
	* 利用可能なドライバーがいるかが決まるためです。
	*/
	
	//works fine.
	//Booking class: nuber.students.NuberDispatch@5fdef03a: nuber.students.Passenger@3b22cdd0
	//Booking class: nuber.students.NuberDispatch@5fdef03a: nuber.students.Passenger@3b22cdd0
	public Booking(NuberDispatch dispatch, Passenger passenger)
	{
		this.dispatch = dispatch;
		this.passenger = passenger;	
		this.jobID = incrementalID();
		
		//System.out.println(this.toString() + ": Creating booking");
		dispatch.logEvent(this, "Creating booking");
	}
	/**
	 * At some point, the Nuber Region responsible for the booking can start it (has free spot),
	 * and calls the Booking.call() function, which:
	 * 1.	Asks Dispatch for an available driver
	 * 2.	If no driver is currently available, the booking must wait until one is available. 
	 * 3.	Once it has a driver, it must call the Driver.pickUpPassenger() function, with the 
	 * 			thread pausing whilst as function is called.
	 * 4.	It must then call the Driver.driveToDestination() function, with the thread pausing 
	 * 			whilst as function is called.
	 * 5.	Once at the destination, the time is recorded, so we know the total trip duration. 
	 * 6.	The driver, now free, is added back into Dispatch�s list of available drivers. 
	 * 7.	The call() function the returns a BookingResult object, passing in the appropriate 
	 * 			information required in the BookingResult constructor.
	 *
	 * @return A BookingResult containing the final information about the booking 
	 * @throws InterruptedException 
	 */
	/**This is Japanese translation for understanding better.
	* ある時点で、予約を担当するNuber Regionが予約を開始（空きがある）し、
	* Booking.call() 関数を呼び出します。
	* 1. Dispatchに利用可能なドライバーを問い合わせます
	* 2. 現在利用可能なドライバーがいない場合、予約は利用可能になるまで待機します。
	* 3. ドライバーが確保できたら、Driver.pickUpPassenger() 関数を呼び出します。
	スレッドは、関数が呼び出されている間、一時停止します。
	* 4. 次に、Driver.driveToDestination() 関数を呼び出し、スレッドは
	関数が呼び出されている間、一時停止します。
	* 5. 目的地に到着すると、時間が記録され、合計の移動時間がわかります。
	* 6. ドライバーは、これで解放されたので、Dispatch の利用可能なドライバーのリストに戻されます。
	* 7. call() 関数は、BookingResult オブジェクトを返します。
	* BookingResult コンストラクタに必要な適切な情報を渡します。
	*
	* @return 予約に関する最終情報を含む BookingResult
	* @throws InterruptedException
	*/
	public BookingResult call() throws InterruptedException {
		
		dispatch.logEvent(this, "Starting booking, getting driver");
		//This is Japanese translation for understanding better.
		//1. Dispatchに利用可能なドライバーを問い合わせます
		//2. 現在利用可能なドライバーがいない場合、予約は利用可能になるまで待機します。
		//If no driver is currently available, the booking must wait
		availableDriver = dispatch.getDriver(); //works fine.
		
		//works fine.
		dispatch.decrementalBookingAwaitingDriver();
		dispatch.logEvent(this, "Starting, on way to passenger");
		
		//This is Japanese translation for understanding better.
//		3. ドライバーが確保できたら、Driver.pickUpPassenger() 関数を呼び出します。
//		スレッドは、関数が呼び出されている間、一時停止します。
		//Once it has a driver, it must call the Driver.pickUpPassenger() function, with the 
		//thread pausing whilst as function is called.
		//works fine.
		availableDriver.pickUpPassenger(passenger);
		dispatch.logEvent(this, "Collected passenger, on way to destination");
		
		//This is Japanese translation for understanding better.
//		4. 次に、Driver.driveToDestination() 関数を呼び出し、スレッドは
//		関数が呼び出されている間、一時停止します。
		//call the Driver.driveToDestination() function, with the thread pausing 
		//whilst as function is called.
		availableDriver.driveToDestination();

		//This is Japanese translation for understanding better.
		//Once at the destination, the time is recorded, so we know the total trip duration. 
		//* 5. 目的地に到着すると、時間が記録され、合計の移動時間がわかります。
		//works well.
		long tripDuration;
		tripDuration = passenger.getTravelTime();
		
		//This is Japanese translation for understanding better.
		//6. ドライバーは、これで解放されたので、Dispatch の利用可能なドライバーのリストに戻されます。
		//works fine.
		Boolean boolAddDriver = dispatch.addDriver(availableDriver);
		dispatch.logEvent(this, "At destination, driver is now free");
		
		//This is Japanese translation for understanding better.
		//7. call() 関数は、BookingResult オブジェクトを返します。 BookingResult コンストラクタに必要な適切な情報を渡します。
		//(int jobID, Passenger passenger, Driver driver, long tripDuration)
		//System.out.println("Thread current name: "+ Thread.currentThread().getName());
		//works fine.
		BookingResult bookingResult = new BookingResult(jobID,passenger,availableDriver,tripDuration); 
		
		incrementalID();
		return bookingResult;
	}
	
	private static synchronized int incrementalID() {
		return bookingId++;
	}
	
	/***
	 * Should return the:
	 * - booking ID, 
	 * - followed by a colon, 
	 * - followed by the driver's name (if the driver is null, it should show the word "null")
	 * - followed by a colon, 
	 * - followed by the passenger's name (if the passenger is null, it should show the word "null")
	 * 
	 * @return The compiled string
	 */
	@Override
	public String toString()
	{
		String driverNameString;
		String passengerNameString;
		
		if (availableDriver == null) {
			driverNameString = "null";
		}else {
			driverNameString = availableDriver.name;
		}
		
		if (passenger == null) {
			passengerNameString ="null";
		}else {
			passengerNameString = passenger.name;
		}
		
		String bookingID = Integer.toString(jobID);
		
		String toString = bookingID + ":" + driverNameString + ":" + passengerNameString;
		return toString;
	}

}
