package scrabble;

import java.util.HashSet;

public class EssaiPlateau {
	
	public static void main (String args[]){
		char[][]pServ = new char[15][15];
		char[][] pClient = new char[15][15];
		char[][] pClient2 = new char[15][15];
		char[][] pClient3 = new char[15][15];
		setPlateauClientv2(pClient2);
		setPlateauClientv3(pClient3);
		setPlateauServ(pServ);
		setPlateauClient(pClient);
		System.out.println("Serveur -------------------");
		System.out.println(toStringPlateau(pClient2));
		//toStringPlateau(pServ);
		System.out.println("client --------------");
		System.out.println(toStringPlateau(pClient3));
		//toStringPlateau(pClient);
		//System.out.println(wordInPlateau2D(pClient2,pClient));
		try {
			System.out.println(mot(pClient3,pClient2));
		} catch (PlateauException e) {
			
			e.printStackTrace();
		}
	}
	public static void setPlateauServ(char[][] plateau2D){
			for(int i =0;i<15;i++){
				for(int j =0;j<15;j++){
					plateau2D[j][i] = ' ';
				}
			}
		plateau2D[0][0]='s';
		plateau2D[1][0]='a';
		plateau2D[2][0]='r';
		plateau2D[3][0]='r';
		plateau2D[4][0]='a';
		plateau2D[4][1]='r';
		plateau2D[4][2]='i';
		plateau2D[4][3]='e';
		plateau2D[4][4]='l';
	}
	public static void setPlateauClient(char[][] plateau2D){
			for(int i =0;i<15;i++){
				for(int j =0;j<15;j++){
					plateau2D[j][i] = ' ';
				}
			}
		plateau2D[0][0]='s';
		plateau2D[1][0]='a';
		plateau2D[2][0]='r';
		plateau2D[3][0]='r';
		plateau2D[4][0]='a';
		plateau2D[4][1]='r';
		plateau2D[4][2]='i';
		plateau2D[4][3]='e';
		plateau2D[4][4]='l';
		plateau2D[1][2]='r';
		plateau2D[1][3]='i';
		plateau2D[1][4]='r';
		plateau2D[1][5]='e';
	}
	public static void setPlateauClientv2(char[][] plateau2D){
		for(int i =0;i<15;i++){
			for(int j =0;j<15;j++){
				plateau2D[j][i] = ' ';
			}
		}
	plateau2D[0][0]='s';
	plateau2D[1][0]='a';
	plateau2D[2][0]='r';
	plateau2D[3][0]='r';
	plateau2D[4][0]='a';
	plateau2D[4][1]='r';
	plateau2D[4][2]='i';
	plateau2D[4][3]='e';
	plateau2D[4][4]='l';
	plateau2D[1][2]='r';
	plateau2D[1][3]='i';
	plateau2D[1][4]='r';
	plateau2D[1][5]='e';
	plateau2D[5][1]='i';
	plateau2D[6][1]='e';
	plateau2D[7][1]='n';
	}
	
	public static void setPlateauClientv3(char[][] plateau2D){
		for(int i =0;i<15;i++){
			for(int j =0;j<15;j++){
				plateau2D[j][i] = ' ';
			}
		}
	plateau2D[0][0]='s';
	plateau2D[1][0]='a';
	plateau2D[2][0]='r';
	plateau2D[3][0]='r';
	plateau2D[4][0]='a';
	plateau2D[4][1]='r';
	plateau2D[4][2]='i';
	plateau2D[4][3]='e';
	plateau2D[4][4]='l';
	plateau2D[1][2]='r';
	plateau2D[1][3]='i';
	plateau2D[1][4]='r';
	plateau2D[1][5]='e';
	plateau2D[5][1]='i';
	plateau2D[6][1]='e';
	plateau2D[7][1]='n';
	plateau2D[5][2]='l';
	plateau2D[5][3]='o';
	plateau2D[5][4]='t';
	}
	public static String toStringPlateau(char[][] p){
		String tab = "";
		
		for(int i =0;i<15;i++){
			for(int j =0;j<15;j++){
				tab=tab+ p[j][i]+" ";
			}
			tab+="\n";
		}
		return tab;
	}
	
	//pour trouver le mot que l'utilisateur a entre , a retiré???
	//
	public static String wordInPlateau2D(char[][] pClient,char[][] pServ){
		
		String mot = "";
		int c=0;
		int l=0;
		
		while(l<15){
			while(c<15){
				
				if(pServ[c][l]!=pClient[c][l]){
					break;
				}
				
				c ++;
			}
			if(c>14){c=0;}
			if(pServ[c][l]!=pClient[c][l]){
				break;
			}
			l++;
		}
		
		if(l==15){ return "";}
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
		
		if(mot.length()>2){
			return mot;
		}
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
	
	public static HashSet<String> mot(char[][] pClient,char[][] pServ) 
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
	
	public static String motVertical(char[][]pClient,int c,int l){
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
	
	public static String motHorizontal(char[][] pClient,int c,int l){
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

}
