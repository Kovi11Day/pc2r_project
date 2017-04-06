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
	private boolean endPlay = false;
	private DataUser user;
	//private boolean trouveRecherche;
	
	public Play (Serveur serv,Socket client){
		//this.trouveRecherche = false;
		this.serv = serv;
		this.client = client;
		try {
			this.out = new PrintStream(client.getOutputStream());
			this.in = new BufferedReader(new InputStreamReader
					(client.getInputStream()));
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		protocole = new ProtoStr(out);
		this.start();
	}
	
	public void run(){
		if(connexion() == false){
			try {
				//on peut faire le close au bout de 3 refus consecutif par exemple
				//pour l'instant j'ai laisse ca mais on poura modifier
				client.close();
			} catch (IOException e) {}
		}else{
			String[] protocol;
			
			while(!endPlay){
				try {
					//si un user s'est connecter et que le jeu est a la phase Rec ou
					//Sou alors pas la peine qu'il fasse ces intructions
					if(user.getPhaseConexion().equals(Phase.DEB)){
					//attend de recevoir la notification de recherche
						System.out.println("play attend la phase de recherche");
						Sync.wait(serv.getRecherche());
						System.out.println("play: fin de  l'attente phase de recherche");
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				while(true){
					System.out.println("play: pret a  lire protocole recherche");
					protocol = protoRecu();
					System.out.println("play a lu le protocole recherche");
					switch (protocol[0]){
					case "SORT": sort(protocol[1]);break;
					//c'est play qui modifie la phase donc mutex pour garder la coherence
					case "TROUVE": synchronized (serv.getPhase()) {
						System.out.println("play appelle trouverRech");
							trouverRech(protocol[1]);//synchro ici
							System.out.println("fin de l'apelle a trouver trouverRech");
						
						};break;
					default: 
					
					}
					if(serv.getPhase()!=Phase.REC){
						break;
					}
				
				}
				
				
					if(user.getPhaseConexion().equals(Phase.REC)||
							user.getPhaseConexion().equals(Phase.DEB)){
					
						System.out.println("play :attend le signale de soumission");
						try {
							Sync.wait(serv.getSoum());
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("play:signale recu de soumission");
					}
				
			
			
				//phase de soumission
				while(true){
					//la j'ai essayer de tester regulierement si la phase a 
					//changer c vrai qu'il ya beaucoup de redondance, mais ej n'ai pas
					//trouve d'autre moyen....
					System.out.println("play est dans la phase de soumission");
					synchronized (serv.getPhase()) {
						if(!serv.getPhase().equals(Phase.SOU)){break;}
						
					}
					protocol = protoRecu();
					synchronized (serv.getPhase()) {
						if(!serv.getPhase().equals(Phase.SOU)){break;}
						
					}
					switch (protocol[0]){
						case "SORT": sort(protocol[1]);break;
						case "TROUVE": trouverSoum(protocol[1]);break;
							
						}
					synchronized (serv.getPhase()) {
						if(!serv.getPhase().equals(Phase.SOU)){break;}
					}
				}
			
				//phase de soumission fini	
				try {
					System.out.println("play: dans la phase bilan");
					System.out.println("play: attend un signal de fin de bilan");
					Sync.wait(serv.getBilan());
					System.out.println("play a recu le signal de bilan");
				} catch (InterruptedException e) {
					
					e.printStackTrace();
		
				}
			}
		}
			
			
	}
		
	
	
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
	
	//deconecte user et envoi le signale a tous les autres de cette deconexion
	public void sort(String user){	
		serv.getUsers().remove(user);
		if (serv.getNbJoueurs() == 0)
			Sync.notify(serv.getCondControlleurEnAttente());
		serv.signalementD(user); //proto_DECONNEXION
		//if nb serv.getUsers().size == 0; notify thread waiting on condition
		this.endPlay = true;
	}
	
	public void trouverRech(String placement){/*TODO*/
		//verifier mot valide
		//si non ...
		
		char[][] pClient;
		try {
			pClient = stringToPlateau(placement);
			char[][] pServ = stringToPlateau(serv.getPlateau());
			HashSet<String> mots = wordsMake(pClient, pServ);
			boolean inDico = true;
			for(String s:mots){
				if(!wordInDictionary(s)){
					inDico = false;
				}
			}
			if(inDico == false){
				protocole.RINVALIDE(Raison.DIC, "");
			}
			else{
				user.setMots(mots);
				user.setScore(calculeScore(mots)) ;
				user.setPlateau(placement);
				protocole.RVALIDE();
				serv.signalementT(user);
				System.out.println("trouverRech change la phase de rech a soum");
				serv.setPhase(Phase.SOU);
				System.out.println("play :notify le controleur pour la recherche");
				Sync.notify(serv.getCondControlleurEnAttente());
				System.out.println("play:fin notify  du le controleur pour la recherche");
			}
			} catch (PlateauException e) {
				
				e.printStackTrace();
			}
			
		}	

	
	public void trouverSoum(String placement){
		if(!positionLettre(placement)){
			protocole.RINVALIDE(Raison.POS,"");
		}
		else{
			char[][] pClient;
			try {
				pClient = stringToPlateau(placement);
				char[][] pServ = stringToPlateau(serv.getPlateau());
				HashSet<String> mots = wordsMake(pClient, pServ);
				boolean inDico = true;
				for(String s:mots){
					if(!wordInDictionary(s)){
						inDico = false;
					}
				}
				if(inDico == false){
					protocole.RINVALIDE(Raison.DIC, "");
				}
				else{
					
					int score = calculeScore(mots) ;
					if(score> user.getScore()){
						protocole.RVALIDE();
						user.setScore(calculeScore(mots)) ;
						user.setMots(mots);
						protocole.RVALIDE();
						user.setPlateau(placement);
					
					}else{
						protocole.RINVALIDE(Raison.INF, "");
					}
				}
			} catch (PlateauException e) {
				
				e.printStackTrace();
			}
			
		}	
		
	}
	
	public boolean connexion(){

			String[] connexion = protoRecu(); //j'ai factorise le code qui etait ici pour mettre dans la fonc protoRecu()


			if(!connexion[0].equals("CONNEXION")){
				// on ignore
			}else{
				if(connexion.length>1){
					String pseudo = connexion[1];
					synchronized (serv.getUsers()) {
						if(serv.getUsers().containsKey(pseudo)){
							protocole.REFUS();					
						}else{
							String tirage = new String(serv.getTirage());
							String plateau = new String(serv.getPlateau());
							DataUser u = new DataUser(this,pseudo,serv.getPhase());	
							this.user = u;
							serv.getUsers().put(pseudo,u);
							String temps = String.valueOf(serv.temp());
							protocole.BIENVENUE(plateau, tirage, serv.scoresString(), 
							serv.getPhase(), temps);
							serv.signalementC(u);

							if(serv.getNbJoueurs()==1){
								Sync.notify(serv.getCondNbJoueurs());
								System.out.println("si nbjoueur=1 alors play envoi notify"
										+ "au controlleur");
							}
						}
							try {
								
								if(serv.getPhase().equals(Phase.DEB)){
									System.out.println("play wait le signal debSession");
									Sync.wait(serv.getdebSession());
									System.out.println("play a recu signal debSession");
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						return true;
					
				}else{
					protocole.REFUS();
				
				}
			}
		return false;	
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
		if(!firstCharFind){
			throw new PlateauException();
		}if(c==14 && pClient[c-1][l]!=' '){
				horizontal = true;
		}
		if(c==0 && pClient[c+1][l]!=' '){
			horizontal=true;
		}
		if(c>0 && c<14 && (pClient[c-1][l]!=' '|| pClient[c+1][l]!= ' ')){
			horizontal = true;
		}
		if(l==14 && pClient[c][l-1]!=' '){
			vertical = true;
		}
		if(l==0 && pClient[c][l+1]!=' '){
			vertical=true;
		}
		if(l>0 && l<14 && (pClient[c][l-1]!=' '|| pClient[c][l+1]!=' ')){
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
		while(j<15 && pClient[j][l]!=' '){
			mot+=pClient[j][l];
			j++;
		}
		return mot;
	}
	
	
	public ProtoStr getProtoStr(){
		return protocole;
	}
	
	public int calculeScore(HashSet<String> listWords){
		int score = 0;
		for(String s: listWords){
			score += serv.getPool().calculerScore(s);
		}
		return score;
	}
	
	public void setEndPlay(){
		endPlay = true;
	}
	
	


}
