package scrabble;

import java.util.HashSet;

public class DataUser {
	private Play puser;
	private int scoreTotale;
	private int score;
	private String pseudo;
	private HashSet<String> motsJoueur;
	private ProtoStr protocole;
	private String plateauClient;
	private Phase phaseUserInConnexion;
	
	public DataUser(Play puser,String pseudo,Phase p){
		//necessaire si un user se conecte a une autre phase que Deb 
		//il poura direct demaré a l'etape concerne au lieu
		phaseUserInConnexion =p;
		this.scoreTotale=0;
		this.puser =puser;
		this.score = 0;
		this.pseudo = pseudo;
		motsJoueur = new HashSet<String>();
	}
	public Play getPlay(){
		return puser;
	}
	
	public int getScore(){
		return score;
	}
	public void setScore(int score){
		this.score = score;
	}
	public String getPseudo(){
		return pseudo;
	}
	
	public HashSet<String> getMots(){
		return motsJoueur;
	}
	//
	public void setMots(HashSet<String> mots){
		this.motsJoueur = mots;
	}
	public void addScore(){
		scoreTotale+=score;
		score = 0;
	}
	
	public void setPlateau(String plateau){
		plateauClient = plateau;
	}
	
	public String getPlateauClient(){
		return plateauClient;
	}
	
	public Phase getPhaseConexion(){
		return phaseUserInConnexion;
	}
	
	public void setPhaseConexion(){
		phaseUserInConnexion = Phase.DEB;
	}

}
