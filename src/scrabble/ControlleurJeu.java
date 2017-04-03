package scrabble;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/*Dès que le thread Controlleur doit faire une attente il fait wait sur  
 * l'objet condControlleurEnAttente (dans classe Serveur) et soit le thread du timer, soit le thread du Serveur soit le
 * remarque: le fait que condControlleurEnAttente est un Integern'est pas important , ça pourrait être
 * n'importe quel Objet java, l'importance c'est d'avoir un objet avec un moniteur
 * thread d'un Play va le reveiller:
 * EN TOUT DEBUT:
 * +thead Controlleur attends tant qu'il n'y a pas de jouueur connecté
 * -thread Play fait notify lorsqu'un joueur est connecte (protocol CONNECTE envoyé)
 * PHASE RECHERCHE: 
 * +thread Controlleur lance timer de 5min et attends
 * -thread serveur fait notify au cas ou tout les utilisateurs se sont déconnecté auquel cas le Controlleur
 * 	re initialise le plateau et les Lettres et vide le tirage et repase en phase debut se remet en attente 
 * -le Timer fait notify si c'est la fin de la phase recherche
 * -un thread Play fait notify dès qu'il a recu un placement de d'un joueur et dès qu'il l'a bien valider
 * PHASE DE SOUMISSION
 * +thread Controlleur lance timer de 2min et attends
 * -thread serveur fait notify au cas ou tout les utilisateurs se sont déconnecté
 * -thread timer fait notify dès que les 2min sont ecrouler
 * PHASE DE RESULTATS
 * +thread Controlleur envoie BILAN et lance timer de 10s 
 * -serveur fait notify si tout les joueurs sont partit
 * -timer fait notify, quand le temps c'est ecrouler
 * */
public class ControlleurJeu extends Thread{
	private final long DELAY_PH_RECHERCHE = 5; //mins
	private final long DELAY_PH_SOUMISSION = 2;//mins
	private final long DELAY_PH_RESULTATS = 10; //secs
	private final long DELAY_DEBUT_SESSION = 20; //secs
	
	private MyTimer timer;
	private Serveur serveur;
	public ControlleurJeu(Serveur serveur){
		this.serveur = serveur;
		this.timer = new MyTimer(serveur.getCondControlleurEnAttente());
		this.start();
	}

	public void run (){
		//phase debut
		lanceJeu();
	}
	
	public void lanceJeu(){
		boolean timerExpirer;
		while(true){
			switch(serveur.getPhase()){
			case DEB:
				//attends qu'au moins un joueur se connecte
				try {
					//notify correspondant dans: Play.connexion()
					Sync.wait(serveur.getCondNbJoueurs());
				} catch (InterruptedException e) {}
				//attente 20s avant de commencer session
				try {
					this.timer.activateSecs(DELAY_DEBUT_SESSION);
					//notify: timer, Play.deconnexion()
					Sync.wait(serveur.getCondControlleurEnAttente());
				} catch (MyTimerException e) {e.printStackTrace();}
					catch (InterruptedException e) {}
				this.timer.disactivate(); 
				if (serveur.getNbJoueurs() == 0){
					serveur.initJeu();
					serveur.setPhase(Phase.DEB);
				}else{
					serveur.newSession(); //proto_SESSION
					serveur.setPhase(Phase.REC);
				}
				break;
			case REC:
				serveur.tour(); //proto_TOUR
				try {
					this.timer.activateMins(DELAY_PH_RECHERCHE);
					//notify: timer, Play.deconnexion(), Play.trouve()
					Sync.wait(serveur.getCondControlleurEnAttente());
				} catch (MyTimerException e) {e.printStackTrace();}
					catch (InterruptedException e) {}
					timerExpirer = !timer.isBusy();
					timer.disactivate(); 
				if (serveur.getNbJoueurs() == 0){
					serveur.initJeu();
					serveur.setPhase(Phase.DEB);
				}else if (timerExpirer){
					if (!serveur.nouveauTirage()){//Si plus de lettres restant
						serveur.endSession();//proto_VAINQUEURE
						serveur.initJeu();
						serveur.setPhase(Phase.DEB);
					}else{
					//TODO: appel fonc proto_RFIN() ou equivalent
						serveur.setPhase(Phase.REC);
					}
				}else{
					//TODO: appel fonc proto_RATROUVE(user) ou equivalent
					serveur.setPhase(Phase.SOU);
				}
				break;
			case SOU:
				try {
					this.timer.activateMins(this.DELAY_PH_SOUMISSION);
					//notify: timer, Play.deconnexion()
					Sync.wait(serveur.getCondControlleurEnAttente());
				} catch (MyTimerException e) {e.printStackTrace();}
					catch (InterruptedException e) {}
				this.timer.disactivate(); 
				if (serveur.getNbJoueurs() == 0){
					serveur.initJeu();
					serveur.setPhase(Phase.DEB);
				}else{
					//TODO: appel fonc proto_SFIN() ou equivalent
					serveur.setPhase(Phase.RES);
				}
				break;
			case RES:
				//TODO: appel fonc proto_BILAN(mot,vainqueures, scores) ou equivalent
				try {
					this.timer.activateSecs(this.DELAY_PH_RESULTATS);
					//notify: timer, Play.deconnexion()
					Sync.wait(serveur.getCondControlleurEnAttente());
				} catch (MyTimerException e) {e.printStackTrace();}
					catch (InterruptedException e) {}
				this.timer.disactivate(); 
				if (serveur.getNbJoueurs() == 0){
					serveur.initJeu();
					serveur.setPhase(Phase.DEB);
				}else{
					serveur.setPhase(Phase.REC);
				}
				break;
			}
		}
	}


	public static void main (String[] args) {
		//test case with enum
		Phase p = Phase.DEB;
		for(int i = 0 ; i < 8 ; i++){
		switch(p){
		case DEB: System.out.println(p.toString()); p = Phase.REC; break;
		case REC: System.out.println(p.toString()); p = Phase.SOU; break;
		case SOU: System.out.println(p.toString()); p = Phase.RES; break;
		case RES: System.out.println(p.toString()); p = Phase.DEB; break;
		}
		}
	}
}
