package scrabble;

public class Sync {
	
	public static void notify(Integer obj){
		synchronized(obj){
			obj.notify();
		}
	}
	
	public static void wait(Integer obj) throws InterruptedException{
		synchronized(obj){
			obj.wait();
		}
	}
	
}
