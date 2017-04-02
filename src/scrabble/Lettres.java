package scrabble;

import java.util.HashMap;
import java.util.Random;

public class Lettres {
	private HashMap<String, Integer> points;
	private HashMap<String, Integer> frequence;
	private Random rand = new Random();
	
	public Lettres (){
		initialise(poolFrancais());
	}
	
	public void initialise(String[][][] pool){
		points = new HashMap<>();
		frequence = new HashMap<>();
		int lettrePoint;
		for (int i = 0; i < pool.length; i++){
			lettrePoint = Integer.parseInt(pool[i][0][0]);
			for (int j = 1; j < pool[i].length; j++){
				points.put(pool[i][j][0], lettrePoint);
				frequence.put(pool[i][j][0], Integer.parseInt(pool[i][j][1]));
			}
		}
	}

	private static String[][][] poolFrancais(){
		/*format:
		 * {
		 * 		{ {point}, {lettre1, frequence1},... ,{lettreN, frequenceN}},..
		 * }*/
		String[][][] pool = {
			{{"1"}, {"E","15"},{"A","9"}, {"I","8"},{"N","6"},{"O","6"},{"R","6"},{"S","6"},{"T","6"},{"U","6"},{"L","5"}},
			{{"2"}, {"D","3"},{"M","3"}, {"G","2"}},
			{{"3"}, {"B","2"},{"C","2"}, {"H","2"},{"V","2"}},
			{{"8"}, {"J","1"},{"Q","1"}},
			{{"10"}, {"K","1"},{"W","1"},{"X","1"},{"Y","1"},{"Z","1"},},
		};
		return pool;
	}
	public int getPoint(char lettre){
		return points.get(String.valueOf(lettre));
	}
	public int getFrequence(char lettre){
		return frequence.get(String.valueOf(lettre));
	}
	public char[] piocher (int nb){
		String result = new String();
		int getEltAtIndex; int index;
		for (int i = 0 ;i < nb; i++){
			if (this.frequence.size() == 0)
				break;
			getEltAtIndex= rand.nextInt(this.frequence.size());
			index = 0;
			for (String elt: this.frequence.keySet()){
				if (index == getEltAtIndex){ //piocher l'element
					result = result + elt;
					//result.concat(elt);
					this.frequence.computeIfPresent(elt, (k,v)->v=v-1);
					if(this.frequence.get(elt) == 0)//cette lettre est epuiser
						this.frequence.remove(elt);
					break;
				}else
					index++;
			}
		}
		return result.toCharArray();
	}
	
	public String tirageToStr(char[] pick){
		return String.valueOf(pick);
	}
	
	private static String[][][] poolTest(){
		String[][][] pool = {
				{{"2"}, {"D","3"},{"M","3"}, {"G","2"}},
				{{"8"}, {"J","1"},{"Q","1"}}
			};
			return pool;
	}
	
	public static void main (String[] args){
		Lettres lettres = new Lettres();
		while (lettres.frequence.size() != 0)
			System.out.println(String.valueOf(lettres.piocher(20)));
	}
}
