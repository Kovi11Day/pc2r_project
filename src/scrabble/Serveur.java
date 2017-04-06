package scrabble;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.TimeZone;


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
	private Long condControlleurEnAttente = new Long(0);
	private Long condNbJoueurs = new Long(0);
	private char[] tirage;
	private Phase p;
	private Long connexion ;
	private Long debSession;
	private Long recherche;
	private Long soumission ;
	private Long bilan;
	//sj'ai ajouter soum car je ne sais pas pourquoi le monitor avec soumission
	//ne marchai pas ...
	private Long soum;
	private ControlleurJeu cJeu;

	
	//soi le serveur a le dico soit c chaque thread Play qui a un dico
	//j'ai choisit Serveur qui a le dico mais dit moi si c'est mieu que 
	//ca soi Play qui possede le dico
	private Dictionnaire dico;
	
	public Serveur(){
		 connexion = new Long(0);
		 debSession = new Long(0);
		 recherche = new Long(0);
		soumission = new Long(0);
		 bilan = new Long(0);
		 soum = new Long(0);
		 //finSession = new Long(0);
		this.users = new HashMap<String,DataUser>();
		this.tirage = new char[7];
		this.pool = new Lettres();
		plateau = char2DtoString(inisializePlateau(new char[15][15]));
		//this.nbJoueurs = users.size();
		
		//j'initialise mon plateau a un plateau vide
		//pareil pour tirage, mais je crois que c'est pas forcement necessaire
		
		//inisializeTirage();

		//pour la phase j'ai fait un ENUM car dans l'ennonce il nous ont donné 
		//une liste de phase
		p = Phase.DEB;
		dico = new Dictionnaire();
		initJeu();
		cJeu = new ControlleurJeu(this);
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
		//this.tirage= this.pool.piocher(7);
	}
	
	public void run (){
		
		int atPre_nbJoueurs;
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
	
	public Long getSoum(){
		return soum;
	}
	
	//chaque fois que je change de phase je remet le time a jour
	public void setPhase(Phase p){
		this.p = p;
		if(getPhase().equals(Phase.DEB)){
			debSession = System.currentTimeMillis();
			
		}
		if(getPhase().equals(Phase.SOU)){
			soumission = System.currentTimeMillis();
			
		}
		if(getPhase().equals(Phase.REC)){
			recherche = System.currentTimeMillis();
			
		}
		
		
	}

	
	public String getPlateau(){
		return plateau;
	}
	
//pour calcule le score de chacun des users 
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
	//signalement deconnexion de user
	public void signalementD(String user){
		Collection<DataUser> usersList = users.values();
		for(DataUser u:usersList){
			u.getPlay().getProtoStr().DECONNEXION(user);

		}
	}
	//signalement d'un mot trouver par user
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
		this.tirage = tirage.toCharArray();
		for (DataUser user: this.users.values()){
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
		//this.tirage = this.pool.piocher(7);
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
			u.getPlay().getProtoStr().SESSION();
			
		}
	}
	
	public void endSession(){
		Collection<DataUser> usersList = users.values();
		String bilan = scoresString();
		for(DataUser u: usersList){
			u.getPlay().getProtoStr().VAINQUEUR(bilan);
			u.getPlay().setEndPlay();
			
		}
	}

	public int getNbJoueurs(){
		return this.users.size();
	}
	public void Jeu (){
		newSession();
	}
	 
	//cherche si le mot est dans notre dico.txt
	public boolean wordInDictinary(String mot){
		//convertir les lettre en minuscule
		String lowerMot = mot.toLowerCase();
		String x = String.valueOf(lowerMot.charAt(0));
		File di = dico.getDico().get(x);
		Scanner sc = null;
		try {
			sc = new Scanner(di);
			String ligne;
			while(sc.hasNext()){
				ligne = sc.nextLine();
				if( ligne.equals(lowerMot)){
					return true;
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	public Long getCondControlleurEnAttente	(){
		return this.condControlleurEnAttente;
	}
	public Long getCondNbJoueurs(){
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
	//signaller qu'on passe a la phase de soumission au users
	public void signaleSoumission(){
		Collection<DataUser> usersList =  users.values();
		for(DataUser u: usersList){
			u.getPlay().getProtoStr().SOUMISSION();
		}
	}
	//pour recupere le temps du premier connecter --> a retiré je crois 
	public Long Connexion(){
		return connexion;
	}
	//recupere la date de demarage de session
	public Long getdebSession(){
		return debSession;
	}
	public Long getBilan(){
		return bilan;
	}
	public Long getRecherche(){
		return recherche;
	}
	/*public Long getSoumission(){
		return soumission;
	}*/
	
	//calcule du meilleur score et met a jour le plateau
	public void bilan(){
		Collection<DataUser> usersList =  users.values();
		int score = -1;
		DataUser better = null ;
		for(DataUser u: usersList){
			if(u.getScore()>score){
				score = u.getScore();
				better = u;
			}			
		}
		String mot = (String) better.getMots().toArray()[0];
		
		
		for(DataUser u: usersList){
			u.getPlay().getProtoStr().BILAN(mot, better.getPseudo(), scoresString());
		}
		this.plateau = better.getPlateauClient();
	}
	//addictionne le score avant de passé au tour suivant
	public void addScore(){
		Collection<DataUser> usersList =  users.values();
		for(DataUser u: usersList){
			u.addScore();
		}
	}
	
	//pour calculer le temps restant dans la phase concerné
	public Long temp(){
		long now = 0 ;
		
		if(p.equals(Phase.REC)){
			now = System.currentTimeMillis();
			now = now-recherche;
			now = now/1000;
			return ( cJeu.getDelay_ph_recherche()-now);
		}
		if(p.equals(Phase.SOU)){
			now = System.currentTimeMillis();
			now = now-soumission;
			now = now/1000;
			return  (cJeu.getDelay_ph_soumission()-now);
			
		}
		//pour la phase de Deb j'ai laissé 0 par defaut ....
		if(p.equals(Phase.DEB)){
			return now;
		}
		return now;
		
	}
	//quand on passe au tour suivant je reinitialise la phase de Connexion
	//des user a Deb
	public void userAjour(){
		Collection<DataUser> usersList =  users.values();
		for(DataUser u: usersList){
			u.setPhaseConexion();
		}
	}
	
	public void Sfin(){
		Collection<DataUser> usersList =  users.values();
		for(DataUser u: usersList){
			u.getPlay().getProtoStr().SFIN();
		}
	}
	
	
}
