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
	private static final int port =2017 ;
	private ServerSocket ecoute;
	
	//DataUser est une nouvelle structure qui stock toute les donne du user
	// c'est a dire le thread, son pseudo, son score , ... on poura rajouter 
	//par la suite si necessaire
	private Lettres pool;
	private HashMap<String,DataUser> users ;
	//le seul plateau que l'on a est un String
	//j'ai fait les fonction pour convertir string en char[][] et inversement
	private String plateau;
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
		this.tirage = new char[7];
		this.pool = new Lettres();
		plateau = char2DtoString(inisializePlateau(new char[15][15]));
		//this.nbJoueurs = users.size();
		
		//j'initialise mon plateau a un plateau vide
		//pareil pour tirage, mais je crois que c'est pas forcement necessaire
		
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
	
	public String getPlateau(){
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
			s+= u.getPseudo()+"*"+u.getScore()+"*";
		
		}
		return s;	
	}
	//pour signaler a tout les utilisateur sauf user que qqun s'est conecter
	public void signalementC(DataUser user){
		String userConnect = user.getPseudo();
		Collection<DataUser> usersList =  users.values();
		for(DataUser u: usersList){
			if(!u.getPseudo().equals(userConnect))
				//u.getPlay().stringToClient("CONNECTE/"+userConnect);
				u.getPlay().getProtoStr().CONNECTE(userConnect);
		}
	}
	
	public void signalementD(DataUser user){
		String userDisconect = user.getPseudo();
		Collection<DataUser> usersList = users.values();
		for(DataUser u:usersList){
			//u.getPlay().stringToClient("DECONNEXION/"+userDisconect);
			u.getPlay().getProtoStr().DECONNEXION(userDisconect);
		/*
		for(DataUser u: this.users.values()){
			if(!u.getPseudo().equals(userConnect)){
				u.getPlay().stringToClient(ProtoStr.CONNECTE(userConnect));
			}
			*/
		}
	}
	
	public void signalementT(DataUser user){
		Collection<DataUser> usersList = users.values();
		for(DataUser u:usersList){
			if(user.getPseudo() != u.getPseudo()){
				u.getPlay().getProtoStr().RATROUVE(user.getPseudo());
			}
		}
	}
	
	//debut d'un nouveau tour, envoyer plateau et tirage a tout les joueurs
	public void tour (){
		String plateau = String.valueOf(this.getPlateau());
		String tirage = this.pool.tirageToStr(this.pool.piocher(7));
		for (DataUser user: this.users.values()){
			//user.getPlay().stringToClient(ProtoStr.TOUR(plateau, tirage));
			user.getPlay().getProtoStr().TOUR(plateau, tirage);
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
	public char[][] inisializePlateau(char[][] plateau){
		for(int i =0; i <plateau.length;i++){
			for(int j =0; j<plateau[i].length;j++){
				plateau[j][i]=' ';
			}
		}
		return plateau;
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
			//u.getPlay().stringToClient("SESSION/");
			u.getPlay().getProtoStr().SESSION();
			
		}
	}
	
	public void endSession(){
		Collection<DataUser> usersList = users.values();
		String bilan = scoresString();
		for(DataUser u: usersList){
			//u.getPlay().stringToClient("VAINQUEUR/"+bilan);
			u.getPlay().getProtoStr().VAINQUEUR(bilan);
			
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
	
	public void setPlateau(String newPlateau){
		this.plateau = newPlateau;
	}
	
	public char[][] stringToChar2D(String plateau) throws PlateauException{
			if(plateau.length()!=225){
				throw new PlateauException();
			}
			char[][] pClient = new char[15][15];
			for(int i =0; i <15;i++){
				for(int j =0; j< 15;j++){
					pClient[j][i]=plateau.charAt(i*15+j);
				}
			}
			
			return pClient;
	
	}
	
	public String char2DtoString(char[][] plateau){
		String plateauString = "";
		for(int i =0;i <plateau.length;i++){
			for(int j =0; j< plateau[i].length;j++){
				plateauString+= plateau[j][i];
			}
		}
		return plateauString;
	}
	
	
}
