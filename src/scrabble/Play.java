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
	public Play (Serveur serv,Socket client){
		this.serv = serv;
		this.client = client;
		try {
			this.out = new PrintStream(client.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
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
			while(true){
				protocol = protoRecu(); System.out.println("protocol recu:"+ protocol[0]);
				switch (protocol[0]){
				case "SORT": sort(); break;
				case "TROUVE": trouve(protocol[1]);break;
				default: throw new RuntimeException ("server received unknown protocol");
			}
			}
		}
	}
	
	public void stringToClient(String s){
		out.println(s);
		out.flush();
	}
	
	public String[] protoRecu (){
		String ligne = null;
		try {
			ligne = in.readLine();
		} catch (IOException e) {e.printStackTrace();}
		String[] protocol = ligne.split("/");
		return protocol;
	}
	public void tour (){
		//faire un nouveau tirage
	}
	
	public void sort(){
		System.out.println("serveur recu SORT");
	}
	
	public void trouve(String placement){/*TODO*/}
	public boolean connexion(){
			String[] connexion = protoRecu(); //j'ai factorise le code qui etait ici pour mettre dans la fonc protoRecu()
			System.out.println("recu par client:" + connexion[0]);

			if(!connexion[0].equals("CONNEXION")){
				// on ignore
			}else{
				if(connexion.length>1){
					String pseudo = connexion[1];
					if(serv.getUsers().containsKey(pseudo)){
						stringToClient("REFUS/\n");
						System.out.println("REFUS/");
						
					}else{
						
						String tirage = new String(serv.getTirage());
						String plateau = new String(serv.getPlateau());
						String bienvenue = "BIENVENUE/"+plateau+"/"+
								tirage+"/"+serv.scoresString()+"/"+
								serv.getPhase()+"/"+serv.getTemps();
					
					
						DataUser u = new DataUser(this,pseudo);
						serv.getUsers().put(pseudo,u);
						serv.getListUsers().add(u);
						stringToClient(bienvenue+"\n");
						serv.signalement(u);
						System.out.println("message envoyer au client:" + bienvenue);
						return true;
					
					}
				}else{stringToClient("/REFUS\n");
				
				}
			}
		/*} catch (IOException e) {
			e.printStackTrace();*/
		//}	
		return false;	
	}

}
