package scrabble;

import java.io.PrintStream;

public class ProtoStr {
	private PrintStream out;
	public ProtoStr(PrintStream out){
		this.out=out;
	}
	public  void BIENVENUE (String plateau, String tirage, String scores, Phase phase, int temps){
		String s = "BIENVENUE/" + plateau + "/" + tirage + "/" + scores + "/" + phase + "/" + temps + "/\n";
		out.println(s);
		out.flush();
		
	}
	public  void CONNECTE (String user){
		String s ="CONNECTE/" + user + "/\n";
		out.println(s);
		out.flush();
	}
	
	public void SORT(String user){
		String s = "SORT/"+user+"/\n";
		out.println(s);
		out.flush();
	}
	
	public void DECONNEXION(String user){
		String s = "DECONNEXION/"+user+"/\n";
		out.println(s);
		out.flush();
	}
	public  void TOUR (String plateau, String tirage){
		String s = "TOUR/" + plateau + "/" + tirage + "/\n";
		out.println(s);
		out.flush();
	}
	
	public void REFUS(){
		String s = "REFUS\n";
		out.println(s);
		out.flush();
	}
	
	public void CONNEXION(String user){
		String s ="CONNEXION/" + user + "/\n";
		out.println(s);
		out.flush();
	}
	
	public void RVALIDE(){
		String s = "RVALIDE/\n";
		out.println(s);
		out.flush();
	}
	
	public void RINVALIDE(Raison r,String rai){
		String s = "RINVALIDE/"+r+" "+rai+"/\n";
		out.println(s);
		out.flush();
		
	}
	
	public void RATROUVE(String user){
		String s = "RATROUVE/"+user+"/\n";
		out.println(s);
		out.flush();
	}
	
	public void SESSION(){
		String s = "SESSION/\n";
		out.println(s);
		out.flush();
	}
	
	public void VAINQUEUR(String bilan){
		String s = "VAINQUEUR/+bilan"+"/\n";
		out.println(s);
		out.flush();
	}
	public static String RFIN (){
		return "RFIN/\n";
	}
	public static String RATROUVE (String user){
		return "RATROUVE/" + user + "/\n";
	}
}
