package scrabble;


import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import java.util.ArrayList;


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
	private int nbJoueurs;
	//soi le serveur a le dico soit c chaque thread Play qui a un dico
	//j'ai choisit Serveur qui a le dico mais dit moi si c'est mieu que 
	//ca soi Play qui possede le dico
	private Dictionnaire dico;
	
	public Serveur(){
		this.users = new HashMap<String,DataUser>();
		this.plateau = new char[255];
		this.tirage = new char[7];

		this.pool = new Lettres();

		this.nbJoueurs = users.size();

		//j'initialise mon plateau a un plateau vide
		//pareil pour tirage, mais je crois que c'est pas forcement necessaire
		inisializePlateau();
		inisializeTirage();

		//pour la phase j'ai fait un ENUM car dans l'ennonce il nous ont donné 
		//une liste de phase
		p = Phase.DEB;
		dico = new Dictionnaire();
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
				//j'ai beaucoup hesite je crois qu'il faut un autre thread qui 
				//va lancer le jeu avec le session les tour ect
				if(nbJoueurs ==1){
					Jouer j = new Jouer(this);
					
				}
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

// redondant? a retire peut etre parce que dans Play j'ai la meme 	
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
		Collection<DataUser> usersList =  users.values();
		String s = ""+usersList.size()+"*";
		DataUser u ;
		Iterator<DataUser> i = usersList.iterator();
		while(i.hasNext()){
			u = i.next();
			s+= u.getPseudo()+"*"+u.getScore();
		/*
		String s = "" + this.users.size() + "*";
		for(DataUser u: this.users.values()){
			 s+=u.getPseudo()+"*";
			 s+=u.getScore();
			 */
		}
		return s;	
	}
	//pour signaler a tout les utilisateur sauf user que qqun s'est conecter
	public void signalementC(DataUser user){
		String userConnect = user.getPseudo();
		Collection<DataUser> usersList =  users.values();
		for(DataUser u: usersList){
			if(!u.getPseudo().equals(userConnect))
				u.getPlay().stringToClient("CONNECTE/"+userConnect);
		}
	}
	
	public void signalementD(DataUser user){
		String userDisconect = user.getPseudo();
		Collection<DataUser> usersList = users.values();
		for(DataUser u:usersList){
			u.getPlay().stringToClient("DECONNEXION/"+userDisconect);
		/*
		for(DataUser u: this.users.values()){
			if(!u.getPseudo().equals(userConnect)){
				u.getPlay().stringToClient(ProtoStr.CONNECTE(userConnect));
			}
			*/
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

	public void newSession(){
		Collection<DataUser> usersList = users.values();
		for(DataUser u: usersList){
			u.getPlay().stringToClient("SESSION/");
		}
		
	}
	
	public void endSession(){
		Collection<DataUser> usersList = users.values();
		String bilan = scoresString();
		for(DataUser u: usersList){
			u.getPlay().stringToClient("VAINQUEUR/"+bilan);
		}
	}
	
	public int getNbJoueurs(){
		return nbJoueurs;
	}
	
	public void Jeu (){
		newSession();
		
	}
	
	public boolean wordInDictinary(String mot){
		String x = Character.toString(mot.charAt(0));
		File di = dico.getDico().get(x);
		Scanner sc = null;
		try {
			sc = new Scanner(di);
			String ligne;
			while(sc.hasNext()){
				ligne = sc.nextLine();
				if( ligne.equals(mot)){
					return true;
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

		
}
