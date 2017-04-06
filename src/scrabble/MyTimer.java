package scrabble;

import java.util.Calendar;
import java.util.Collection;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

//TODO: make class throw exception
public class MyTimer extends Thread{
	private Long condTimer;
	private boolean isCancelled;
	private boolean destroy;
	private boolean isBusy;
	private Long condToNotify;
	private long delay;
	
	public MyTimer(Long condToNotify) {
		this.condTimer = new Long(0);
		this.isCancelled = false;
		this.destroy = false;
		this.isBusy = false;
		this.condToNotify = condToNotify;
		this.start();
	}
	
	public void run (){
		while(!destroy){	
		try {
			//System.out.println("in idle state");
			Sync.wait(condTimer);//IDLE
			} catch (InterruptedException e) {}
			isBusy = true;
			try {
				//System.out.println("in timing state");
				//attente active avec sleep? wait(long) meilleure?
				sleep(delay); //TIMING
				isBusy=false;
				Sync.notify(condToNotify);
				delay= 0;
			} catch (InterruptedException e) {
				if (!isCancelled){
				//System.out.println("in task state: action");
					isBusy=false;
					Sync.notify(condToNotify);//TASK
				//condToNotify.notifyAll();
				}else{
				//System.out.println("in task state: action cancelled");
					isBusy = false;
					isCancelled = false; 
				}
				delay = 0;
				}
			
			}
	}
	//can only be called if thread isNotBusy
		public void activateMillis(long millis) throws MyTimerException{
			if (isBusy)
				throw new MyTimerException();
			this.delay = millis;
			Sync.notify(condTimer);
			
		}
	//can only be called if thread isNotBusy
	public void activateMins(long mins) throws MyTimerException{
		if (isBusy)
			throw new MyTimerException();
		this.delay = TimeUnit.MINUTES.toMillis(mins);
		Sync.notify(condTimer);
		
	}
	//can only be called if thread isNotBusy
	public void activateSecs(long secs) throws MyTimerException{
		if (isBusy)
			throw new MyTimerException();
		this.delay = TimeUnit.SECONDS.toMillis(secs);
		Sync.notify(condTimer);
	}
	//can only be called if thread isBusy
	public void disactivate(){
		if (isBusy){	
			this.isCancelled = true;
			this.interrupt();
		}
	}
	
	public void destroy(){
		this.destroy = true;
		disactivate();//assurer etat IDLE
		isCancelled = true; //assurer qu'il ne fait pas notify
		Sync.notify(condTimer);
	}
	public Long getCondAttente(){
		return this.condTimer;
	}
	
	public boolean isBusy(){
		return this.isBusy;
	}
	//test timer
	/*public static void main(String[] args){
		Integer action = new Integer(1);
		Calendar cal = Calendar.getInstance();
		MyTimer timer = new MyTimer(action);
		System.out.println(cal.getTime().toString());
		
		try {
			System.out.println("ACTIVATING TIMER and wait"); System.out.flush();
			timer.activateSecs(5);
			Sync.wait(action);
			System.out.println("ACTIVATING TIMER");System.out.flush();
			timer.activateSecs(5); 
			System.out.println("CANCELLING TIMER");System.out.flush();
			timer.disactivate();
			//After cancel do not start same timer immediately
			//give it some time to reinit
			sleep(2); 
			System.out.println("ACTIVATING TIMER and wait");System.out.flush();
			timer.activateSecs(5);
			Sync.wait(action);
			System.out.println("DESTROYING TIMER");System.out.flush();
			timer.destroy();
		} catch (MyTimerException e) {	timer.destroy();e.printStackTrace();}
			catch (InterruptedException e) {	timer.destroy(); e.printStackTrace();}

		}	*/

}
