package scrabble;

import java.util.HashMap;
import java.util.Random;

public class Lettres {
	
	HashMap<String, Integer> points;
	HashMap<String, Integer> frequence;
	Random rand = new Random();
	public Lettres (){
		points = new HashMap<>();
		frequence = new HashMap<>();
	}
	
	public void initialise(){
		
	}
	
	public int getPoint(char lettre){
		return points.get(String.valueOf(lettre));
	}
	//taille de 7 en generale sauf si il reste moins de 7 lettres dans le jeu
	//retourne null quand il y a aucun lettres restant donc fin du jeu
	public char[] tirage(){
		return null;
	}
	
	
}
