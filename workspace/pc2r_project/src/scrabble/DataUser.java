package scrabble;

public class DataUser {
	private Play puser;
	private int score;
	private String pseudo;
	
	public DataUser(Play puser,String pseudo){
		this.puser =puser;
		this.score = 0;
		this.pseudo = pseudo;
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

}
