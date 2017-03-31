package scrabble;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Serveur extends Thread{
	private char[] plateau;
	private static final int port =2017 ;
	private ServerSocket ecoute;
	//DataUser est une nouvelle structure qui stock toute les donne du user
	// c'est a dire le thread, son pseudo, son score , ... on poura rajouter 
	//par la suite si necessaire
	private HashMap<String,DataUser> users ;
	private char[] tirage;
	//j'ai besoin d'une liste pour le calcule du scores
	//donc j'ai ajouter une liste de DataUser 
	private ArrayList<DataUser> usersList;
	private Phase p;
	private int temps;
	
	public Serveur(){
		this.users = new HashMap<String,DataUser>();
		this.plateau = new char[255];
		this.tirage = new char[7];
		//j'initialise mon plateau a un plateau vide
		//pareil pour tirage, mais je crois que c'est pas forcement necessaire
		inisializePlateau();
		inisializeTirage();
		usersList = new ArrayList<DataUser>();
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
		String s = ""+usersList.size()+"*";
		DataUser u ;
		for(int i=0; i < usersList.size(); i++){
			u = usersList.get(i);
			 s+=u.getPseudo()+"*";
			 s+=u.getScore();
		}
		return s;
		
	}
	//pour signaler a tout les utilisateur sauf user que qqun s'est conecter
	public void signalement(DataUser user){
		String userConnect = user.getPseudo();
		for(DataUser u: usersList){
			if(!u.getPseudo().equals(userConnect)){
				u.getPlay().stringToClient("CONNECT/"+userConnect);
			}
		}
	}
	
	public HashMap<String, DataUser> getUsers(){
		return users;
	}
	public ArrayList<DataUser> getListUsers(){
		return usersList;
	}
	public char[] getTirage(){
		return tirage;
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
