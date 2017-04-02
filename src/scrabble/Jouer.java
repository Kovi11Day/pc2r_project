package scrabble;

public class Jouer  extends Thread{
	private Serveur serv;
	
	public Jouer(Serveur serv){
		this.serv = serv;
		this.start();
	}
	
	public void run (){
		serv.newSession();
	}
	

}
