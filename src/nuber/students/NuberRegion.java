package nuber.students;

import java.nio.channels.NonReadableChannelException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * A single Nuber region that operates independently of other regions, other than getting 
 * drivers from bookings from the central dispatch.
 * 
 * A region has a maxSimultaneousJobs setting that defines the maximum number of bookings 
 * that can be active with a driver at any time. For passengers booked that exceed that 
 * active count, the booking is accepted, but must wait until a position is available, and 
 * a driver is available.
 * 
 * Bookings do NOT have to be completed in FIFO order.
 * 
 * @author james
 *
 */

//* This is Japanese translation for understanding better.
//他の地域から独立して動作する単一のNuber地域。
//* 中央配車からの予約からドライバーを取得する以外は、
//* 地域には、ドライバーが同時に処理できる予約の最大数を定義するmaxSimultaneousJobs設定があります
//*。予約した乗客数がこの上限を超える場合、
//* 予約は受け付けられますが、ポジションが空くまで待機する必要があり、
//*ドライバーが利用可能になるまで待機する必要があります。
//* 予約はFIFO（先入れ先出し）順で完了する必要はありません。

public class NuberRegion {
	
	//Added by Aoto
	protected NuberDispatch dispatch;
	protected String regionName;
	protected int maxSimultaneousJobs;
	protected Semaphore jobSemaphore;
	
	//Use thread pool.
	protected ExecutorService executor;
	protected boolean isShutdown = false;
	
	/**
	 * Creates a new Nuber region
	 * 
	 * @param dispatch The central dispatch to use for obtaining drivers, and logging events
	 * @param regionName The regions name, unique for the dispatch instance
	 * @param maxSimultaneousJobs The maximum number of simultaneous bookings the region is allowed to process
	 */
	
	//works fine.
	public NuberRegion(NuberDispatch dispatch, String regionName, int maxSimultaneousJobs)
	{
		this.dispatch = dispatch;
		this.regionName = regionName;
		this.maxSimultaneousJobs = maxSimultaneousJobs;
		this.jobSemaphore = new Semaphore(maxSimultaneousJobs);
		
		//use thread pool.
		this.executor = Executors.newFixedThreadPool(maxSimultaneousJobs);
	}
	
	/**
	 * Creates a booking for given passenger, and adds the booking to the 
	 * collection of jobs to process. Once the region has a position available, and a driver is available, 
	 * the booking should commence automatically. 
	 * 
	 * If the region has been told to shutdown, this function should return null, and log a message to the 
	 * console that the booking was rejected.
	 * 
	 * @param waitingPassenger
	 * @return a Future that will provide the final BookingResult object from the completed booking
	 */
	/**
	 * This is Japanese translation for understanding better.
	* 指定の乗客の予約を作成し、処理するジョブのコレクションに追加します
	*。 地域に空席があり、ドライバーが利用可能であれば、
	* 予約は自動的に開始されます。
	* 地域にシャットダウンが指示されている場合、この関数はnullを返し、
	* 予約が拒否されたことを示すメッセージをコンソールに記録します。
	*
	* @param waitingPassenger
	* @return 完了した予約から最終的な BookingResult オブジェクトを提供する Future
	*/
	
	public Future<BookingResult> bookPassenger(Passenger waitingPassenger)
	{		
		try {	
			//* This is Japanese translation for understanding better.
			//地域にシャットダウンが指示されている場合、この関数はnullを返し、
			//* 予約が拒否されたことを示すメッセージをコンソールに記録します。
			if (isShutdown) {
				Booking booking = new Booking(dispatch, waitingPassenger);
				dispatch.logEvent(booking, "Rejected booking");
				return null;
			}
			
			//* This is Japanese translation for understanding better.
			//指定の乗客の予約を作成し、処理するジョブのコレクションに追加します
			jobSemaphore.acquire();
			
			//* This is Japanese translation for understanding better.
			//地域に空席があり、ドライバーが利用可能であれば、
			// 予約は自動的に開始されます。
			//check w9 lec around p38 if I need.
			Future<BookingResult> future = executor.submit(new Callable<BookingResult>() {
				@Override
				public BookingResult call() throws Exception {
					Booking booking = new Booking(dispatch, waitingPassenger);
					return booking.call();
				}
			});
			//semaphore release because book is done.
			jobSemaphore.release();	
			return future;			
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in bookPassenger");
		}
		return null;
	}
	
	/**
	 * Called by dispatch to tell the region to complete its existing bookings and stop accepting any new bookings
	 */
	public void shutdown()
	{
		isShutdown = true;
		executor.shutdown();
	}
		
}
