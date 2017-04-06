package scrabble;

public class Sync {
	
	public static void notify(Long obj){
		synchronized(obj){
			obj.notify();
		}
	}
	
	public static void wait(Long obj) throws InterruptedException{
		synchronized(obj){
			obj.wait();
		}
	}
	public static void notifyAll(Long obj){
		synchronized(obj){
			obj.notifyAll();
		}
	}
	
}
