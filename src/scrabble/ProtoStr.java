package scrabble;

public class ProtoStr {
	public static String BIENVENUE (String plateau, String tirage, String scores, Phase phase, int temps){
		return "BIENVENUE/" + plateau + "/" + tirage + "/" + scores + "/" + phase + "/" + temps + "/\n";
	}
	public static String CONNECTE (String user){
		return "CONNECTE/" + user + "/\n";
	}
}
