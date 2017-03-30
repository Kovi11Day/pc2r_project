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
		}
		
	}
	
	public void stringToClient(String s){
		out.println(s);
		out.flush();
	}
	
	public boolean connexion(){
		
		String ligne = null;
		try {

			ligne = in.readLine();
			
			String []connexion = ligne.split("/");
			if(!connexion[0].equals("CONNEXION")){
				// on ignore
				
				
			}else{
				
				if(connexion.length>1){
					
					String pseudo = connexion[1];
					if(serv.getUsers().containsKey(pseudo)){
						
						stringToClient("REFUS/");
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
						stringToClient(bienvenue);
						serv.signalement(u);
						System.out.println(bienvenue);
						return true;
					
					}
				}else{stringToClient("/REFUS");
				
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return false;
		
	}

}
