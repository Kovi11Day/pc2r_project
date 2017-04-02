package scrabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Play extends Thread{
	private Serveur serv;
	private Socket client;
	private PrintStream out;
	private BufferedReader in;

	private boolean endPlay = false;
	DataUser user;
	public Play (Serveur serv,Socket client){
		this.serv = serv;
		this.client = client;
		try {
			this.out = new PrintStream(client.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader
					(client.getInputStream()));
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		this.start();
	}
	
	public void run(){
		if(connexion() == false){
			try {
				//on peut faire le close au bout de 3 refus consecutif par exemple
				//pour l'instant j'ai laisse ca mais on poura modifier
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			String[] protocol;

			while(!endPlay){
				protocol = protoRecu();

				switch (protocol[0]){
					case "SORT": sort(protocol[1]); break;
					case "TROUVE": trouve(protocol[1]);break;
					default: throw new RuntimeException ("server received "
							+ "unknown protocol");
				}
			}
		}
	}
	
	public void stringToClient(String s){
		System.out.println("protocol envoye:" + s);
		out.println(s);
		out.flush();
	}
	
	public String[] protoRecu (){
		String ligne = null;
		String[] protocol =null;
		try {
			ligne = in.readLine();
			System.out.println("protocol recu: " + ligne);
		} catch (IOException e) {e.printStackTrace();throw new RuntimeException ("prob comm btw client/server stream\n");}
		protocol = ligne.split("/");
		return protocol;
	}
	public void tour (){
		//faire un nouveau tirage
	}
	
	public void sort(String user){		
		deconnexion(serv.getUsers().get(user));
		serv.getUsers().remove(this.user.getPseudo());
		//serv.getListUsers().remove(this.user);
		this.endPlay = true;
	}
	
	public void trouve(String placement){/*TODO*/}
	
	public boolean connexion(){

			String[] connexion = protoRecu(); //j'ai factorise le code qui etait ici pour mettre dans la fonc protoRecu()


			if(!connexion[0].equals("CONNEXION")){
				// on ignore
			}else{
				if(connexion.length>1){
					String pseudo = connexion[1];
					if(serv.getUsers().containsKey(pseudo)){
						stringToClient("REFUS/\n");						
					}else{
						
						String tirage = new String(serv.getTirage());
						String plateau = new String(serv.getPlateau());
					
						DataUser u = new DataUser(this,pseudo);
						this.user = u;
						serv.getUsers().put(pseudo,u);
						stringToClient(ProtoStr.BIENVENUE(plateau, tirage, serv.scoresString(), serv.getPhase(), serv.getTemps()));
						serv.signalementC(u);
						return true;
					}
				}else{stringToClient("REFUS/\n");
				
				}
			}
		/*} catch (IOException e) {
			e.printStackTrace();*/
		//}	
		return false;	
	}
	
	public void deconnexion(DataUser user){
		serv.getUsers().remove(user.getPseudo());
		serv.signalementD(user);
		
	}
	
	public boolean wordInDictionary(String mot){
		return  serv.wordInDictinary(mot);
		
	}
	


}
