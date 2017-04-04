package scrabble;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashSet;

public class Play extends Thread{
	private Serveur serv;
	private Socket client;
	private PrintStream out;
	private BufferedReader in;
	private ProtoStr protocole;
	private HashSet<String> motsJoueur;
	private boolean endPlay = false;
	private DataUser user;
	private int score;
	
	public Play (Serveur serv,Socket client){
		motsJoueur = new HashSet<String>();
		 score = 0;
		this.serv = serv;
		this.client = client;
		try {
			this.out = new PrintStream(client.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader
					(client.getInputStream()));
			protocole = new ProtoStr(out);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		this.start();
	}
	
	public void run(){
		if(connexion() == false){
			try {
				//on peut faire le close au bout de 3 refus consecutif par exemple
				//pour l'instant j'ai laisse ca mais on poura modifier
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			String[] protocol;

			while(!endPlay){
				protocol = protoRecu();

				switch (protocol[0]){
					case "SORT": sort(protocol[1]); break;
					case "TROUVE": trouve(protocol[1]);break;
					default: throw new RuntimeException ("server received "
							+ "unknown protocol");
				}
			}
		}
	}
	
	// a retiré si tu aime la nouvel version de ProtoStr
	/*public void stringToClient(String s){
		System.out.println("protocol envoye:" + s);
		out.println(s);
		out.flush();
	}*/
	
	public String[] protoRecu (){
		String ligne = null;
		String[] protocol =null;
		try {
			ligne = in.readLine();
			System.out.println("protocol recu: " + ligne);
		} catch (IOException e) {e.printStackTrace();throw new RuntimeException ("prob comm btw client/server stream\n");}
		protocol = ligne.split("/");
		return protocol;
	}
	public void tour (){
		//faire un nouveau tirage
	}
	
	public void sort(String user){		
		deconnexion(serv.getUsers().get(user));
		serv.getUsers().remove(this.user.getPseudo());
		//if nb serv.getUsers().size == 0; notify thread waiting on condition
		this.endPlay = true;
	}
	
	public void trouve(String placement){/*TODO*/
		//verifier mot valide
		//si non ...
//<<<<<<< HEAD
		//si oui notifier serveur
		if(!positionLettre(placement)){
			protocole.RINVALIDE(Raison.POS,"");
		}
		else{
			char[][] pClient;
			try {
				pClient = stringToPlateau(placement);
				char[][] pServ = stringToPlateau(serv.getPlateau());
				HashSet<String> mot = wordsMake(pClient, pServ);
				if(calculeScore(mot)> score){
					this.score = calculeScore(mot) ;
					this.motsJoueur = mot;
					protocole.RVALIDE();
					serv.signalementT(user);
				}else{
					protocole.RINVALIDE(Raison.INF, "");
				}
			} catch (PlateauException e) {
				
				e.printStackTrace();
			}
			
		}
		Sync.notify(serv.getCondControlleurEnAttente());

	}
	
	public boolean connexion(){

			String[] connexion = protoRecu(); //j'ai factorise le code qui etait ici pour mettre dans la fonc protoRecu()


			if(!connexion[0].equals("CONNEXION")){
				// on ignore
			}else{
				if(connexion.length>1){
					String pseudo = connexion[1];
					if(serv.getUsers().containsKey(pseudo)){
						protocole.REFUS();
						//stringToClient("REFUS/\n");						
					}else{
						String tirage = new String(serv.getTirage());
						String plateau = new String(serv.getPlateau());
					
						DataUser u = new DataUser(this,pseudo);
						this.user = u;
						serv.getUsers().put(pseudo,u);

						protocole.BIENVENUE(plateau, tirage, serv.scoresString(), serv.getPhase(), serv.getTemps());
						serv.signalementC(u);

					     
						Sync.notify(serv.getCondNbJoueurs());

						return true;
					}
				}else{//stringToClient("REFUS/\n");
					protocole.REFUS();
				
				}
			}
		/*} catch (IOException e) {
			e.printStackTrace();*/
		//}	
		return false;	
	}
	//deconecte user et envoi le signale a tous les autres de cette deconexion
	public void deconnexion(DataUser user){
		serv.getUsers().remove(user.getPseudo());
		if (serv.getNbJoueurs() == 0)
			Sync.notify(serv.getCondControlleurEnAttente());
		serv.signalementD(user); //proto_DECONNEXION
	}
	//verifie si mot est dans le dico
	public boolean wordInDictionary(String mot){
		return  serv.wordInDictinary(mot);
		
	}
	//convertir un String en tableau 2D char
	public char[][] stringToPlateau(String plateau) throws PlateauException{
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
	//teste la bonne position des lettre
	public boolean positionLettre(String pClient){
		String pServ = serv.getPlateau();
		for(int i =0; i < pClient.length();i++){
			//si le client ajoute une lettre dans une place qui été deja prise
			//par une autre lettre alors c'est faux
			if(pServ.charAt(i)!= ' ' && pClient.charAt(i)!=pServ.charAt(i)){
				return false;
			}
		}
		return true;
	}
	
	//je suppose que le mot a bien été positionné
	//pb si le client ajoute 2 mots??
	//renvoi la liset des mots formé par user
	public HashSet<String> wordsMake(char[][] pClient,char[][] pServ) 
			throws PlateauException{
		boolean horizontal = false;
		boolean vertical = false;
		boolean firstCharFind = false;
		HashSet<String> listMot = new HashSet<String>();
		String mot="";
		int c=0;
		int l=0;
		for(int i =0; i <15;i++){
			for(int j=0; j<15;j++){
				if(pClient[j][i]!=pServ[j][i]){
					firstCharFind = true;
					c =j;
					l = i;
					break;
				}
			}
			if(firstCharFind){
				break;
			}
		}
		System.out.println(pClient[c][l]);
		if(!firstCharFind){
			throw new PlateauException();
		}
		if(pClient[c-1][l]!=' '|| pClient[c+1][l]!= ' '){
			horizontal = true;
		}
		if(pClient[c][l-1]!=' '|| pClient[c][l+1]!=' '){
			vertical=true;
		}
		
		if(vertical && !horizontal){
			listMot.add(motVertical(pClient, c, l));
		}
		if(horizontal && !vertical){
			
			listMot.add(motHorizontal(pClient, c, l));
		}
		if(horizontal && vertical){
			listMot.add(motHorizontal(pClient, c, l));
			listMot.add(motHorizontal(pClient, c, l));
			for(int i =l;i<15;i++){
				for(int j=c;j<15;j++){
					if(pClient[j][i]!=pServ[j][i]){
						mot = motVertical(pClient, j, i);
						if(mot!=""){
							listMot.add(mot);
						}
						mot = motHorizontal(pClient, j, i);
						if(mot!=""){
							listMot.add(mot);
						}
					}
				}
			}
		}
		return listMot;
	}
	//utiliser uniquement par la fonction wordsMake
	public String motVertical(char[][]pClient,int c,int l){
		String mot = "";
		for(int i = 0; i <=l;i++){
			mot+=pClient[c][i];
			if(pClient[c][i]== ' '){
				mot = "";
			}
		}

		int i=l+1;
		while(i<15 && pClient[c][i]!=' '){
			mot+=pClient[c][i];
			i++;
		}
		return mot;
	}
	//utilise uniquement par wordsMake
	public String motHorizontal(char[][] pClient,int c,int l){
		String mot ="";
		for(int j = 0; j <=c;j++){
			mot+=pClient[j][l];
			if(pClient[j][l]== ' '){
				mot = "";
			}
		}
		int j=c+1;
		while(pClient[j][l]!=' ' && j<15){
			mot+=pClient[j][l];
			j++;
		}
		return mot;
	}
	
	
	public ProtoStr getProtoStr(){
		return protocole;
	}
	
	public int calculeScore(HashSet<String> listWords){
		//TODO
		// calcule les points fait avec cette liste de mot
		return 0;
	}
	
	


}
