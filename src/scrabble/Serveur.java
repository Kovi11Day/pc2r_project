package scrabble;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Serveur extends Thread{
	private char[] plateau;
	private static final int port =2017 ;
	private ServerSocket ecoute;
	//DataUser est une nouvelle structure qui stock toute les donne du user
	// c'est a dire le thread, son pseudo, son score , ... on poura rajouter 
	//par la suite si necessaire
	private Lettres pool;
	private HashMap<String,DataUser> users ;
	private char[] tirage;
	private Phase p;
	private int temps;
	
	public Serveur(){
		this.users = new HashMap<String,DataUser>();
		this.plateau = new char[255];
		this.tirage = new char[7];
		this.pool = new Lettres();
		//j'initialise mon plateau a un plateau vide
		//pareil pour tirage, mais je crois que c'est pas forcement necessaire
		inisializePlateau();
		inisializeTirage();
		//usersList = new ArrayList<DataUser>();
		//pour la phase j'ai fait un ENUM car dans l'ennonce il nous ont donné 
		//une liste de phase
		p = Phase.DEB;
	
		temps = 0;
		try {
			this.ecoute = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.start();
	}
	
	public void run (){
		while(true){
			try {
				Socket client = ecoute.accept();
				Play p = new Play(this,client);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public Phase getPhase(){
		return p;
	}
	
	public int getTemps(){
		return temps;
	}
	
	public void stringToClient(PrintStream out,String s){
		out.println(s);
		out.flush();
	}
	
	public char[] getPlateau(){
		return plateau;
	}
	
//pour calcule le score de chacun des users et c'est la ou j'ai eu besoin
	//d'une liste, parce que sa avait l'air compliquer de parcourir un Hash
	public String scoresString(){
		String s = "" + this.users.size() + "*";
		for(DataUser u: this.users.values()){
			 s+=u.getPseudo()+"*";
			 s+=u.getScore();
		}
		return s;	
	}
	//pour signaler a tout les utilisateur sauf user que qqun s'est conecter
	public void signalement(DataUser user){
		String userConnect = user.getPseudo();
		for(DataUser u: this.users.values()){
			if(!u.getPseudo().equals(userConnect)){
				u.getPlay().stringToClient(ProtoStr.CONNECTE(userConnect));
			}
		}
	}
	
	//debut d'un nouveau tour, envoyer plateau et tirage a tout les joueurs
	public void tour (){
		String plateau = String.valueOf(this.getPlateau());
		String tirage = this.pool.tirageToStr(this.pool.piocher(7));
		for (DataUser user: this.users.values()){
			user.getPlay().stringToClient(ProtoStr.TOUR(plateau, tirage));
		}
	}
	
	public HashMap<String, DataUser> getUsers(){
		return users;
	}

	public char[] getTirage(){
		return this.pool.piocher(7);
	}
	public void inisializePlateau(){
		for(int i =0; i <plateau.length;i++){
			plateau[i]=' ';
		}
	}
	public void inisializeTirage(){
		for(int i =0; i <tirage.length;i++){
			tirage[i] =' ';
		}
	}
		
}
