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
	
	//voir explication dans la classe ControlleurJeu
	private Integer condControlleurEnAttente = new Integer(0);
	private Integer condNbJoueurs = new Integer(0);
	private char[] tirage;
	private Phase p;
	private int temps;
	//private int nbJoueurs;
	//soi le serveur a le dico soit c chaque thread Play qui a un dico
	//j'ai choisit Serveur qui a le dico mais dit moi si c'est mieu que 
	//ca soi Play qui possede le dico
	private Dictionnaire dico;
	
	public Serveur(){
		this.users = new HashMap<String,DataUser>();
		this.plateau = new char[255];
		this.tirage = new char[7];

		this.pool = new Lettres();
		
		//this.nbJoueurs = users.size();
		
		//j'initialise mon plateau a un plateau vide
		//pareil pour tirage, mais je crois que c'est pas forcement necessaire
		inisializePlateau();
		inisializeTirage();

		//pour la phase j'ai fait un ENUM car dans l'ennonce il nous ont donné 
		//une liste de phase
		p = Phase.DEB;
		dico = new Dictionnaire();
		temps = 0;
		new ControlleurJeu(this);
		try {
			this.ecoute = new ServerSocket(port);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.start();
	}
	public void initJeu(){
		//TODO: initialise phase..et tout autre choses
		//TODO: initialise scores
		inisializePlateau();
		inisializeTirage();
		this.pool.initialise(Lettres.poolFrancais());
	}
	
	public void run (){
		int atPre_nbJoueurs;
		while(true){
			try {
				Socket client = ecoute.accept();
				Play p = new Play(this,client);
				//Jouer j = new Jouer(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	public Phase getPhase(){
		return p;
	}
	public void setPhase(Phase p){
		this.p = p;
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
	//si faux est retourné alors c'est la fin du jeu
	public boolean nouveauTirage(){
		if (this.pool.isEmpty())
			return false;
		this.tirage = this.pool.piocher(7);
		return true;
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
	public char[] getTirage (){
		return this.tirage;
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
		return this.users.size();
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

	public Integer getCondControlleurEnAttente	(){
		return this.condControlleurEnAttente;
	}
	public Integer getCondNbJoueurs(){
		return this.condNbJoueurs;
	}
	public Lettres getPool(){
		return this.pool;
	}
}
