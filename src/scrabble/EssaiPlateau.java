package scrabble;

public class EssaiPlateau {
	
	public static void main (String args[]){
		char[][]pServ = new char[15][15];
		char[][] pClient = new char[15][15];
		setPlateauServ(pServ);
		setPlateauClient(pClient);
		System.out.println("Serveur -------------------");
		System.out.println(toStringPlateau(pServ));
		//toStringPlateau(pServ);
		System.out.println("client --------------");
		System.out.println(toStringPlateau(pClient));
		//toStringPlateau(pClient);
		
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
		plateau2D[1][1]='r';
		plateau2D[1][2]='i';
		plateau2D[1][3]='r';
		plateau2D[1][4]='e';
		 
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
	public String wordInPlateau2D(char[][] pClient,char[][] pServ){
		
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
			if(pServ[c][l]!=pClient[c][l]){
				break;
			}
			l++;
		}
		
		for(int i = 0; i <=l;i++){
			mot+=pClient[c][i];
			if(pClient[c][i]== ' '){
				mot = "";
			}
		}
		int i=l;
		while(pClient[c][i]!=' '){
			mot+=pClient[c][i];
			i++;
		}
		
		if(mot != ""){
			return mot;
		}
		for(int j = 0; j <=c;j++){
			mot+=pClient[j][l];
			if(pClient[j][l]== ' '){
				mot = "";
			}
		}
		int j=c;
		while(pClient[j][l]!=' ' && j<15){
			mot+=pClient[j][l];
			j++;
		}
		
		return mot;
		
	}

}
