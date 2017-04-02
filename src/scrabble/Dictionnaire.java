package scrabble;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class Dictionnaire {
	
	private  static final String fichier ="dico.txt";
	private HashMap<String,File> dico;
	
	public Dictionnaire(){
		dico = new HashMap<String, File>();
		makeFiles ();
		
	}
	public void makeFiles (){
		
		Scanner sc = null;
		String ligne;
		File d = new File(fichier);
		char x = 'a';
		try {
			sc = new Scanner(d);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		String fileName = Character.toString(x);
		File f = new File("a.txt");
		FileWriter fw = null;
		try {
			fw = new FileWriter(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while( sc.hasNext() && x < 'z'){
			ligne = sc.nextLine();
			char c = ligne.charAt(0);
			if(x == fonction(c)){
				try {
					fw.write(ligne+"\n");
					fw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				dico.put(fileName, f);
				x = (char) (x+1);
				 fileName = Character.toString(x);
				 f = new File(fileName+".txt");
				try {
					fw = new FileWriter(f);
					fw.write(ligne+"\n");
					fw.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		}
	}
	public  char fonction(char c){
		if(c == 'c' || c== 'ç' || c=='C'){
			return 'c';
		}
		if( c== 'a' || c== 'à' || c== 'â' || c=='A'){
			return 'a';
		}
		if( c== 'e'|| c=='é' || c=='è' || c=='ê' || c=='E'){
			return 'e';
		}
		if( c== 'i' || c=='î'){
			return 'i';
		}
		if(c=='o' || c=='ô'){
			return 'o';
		}
		return Character.toLowerCase(c);
	}
	
	public void removeFile(){
		/*pour supprimer les fichier se terminant par .txt sauf le dico*/
		File rep = new File(".");
		File[] fi = rep.listFiles();
		  for(File x:fi){
			if((!x.getName().equals("dico.txt"))  &&
					x.getName().contains("txt")){
			new File(x.getName()).delete();
			}
		}
	}
	public HashMap<String, File> getDico(){
		return dico;
	}

}
