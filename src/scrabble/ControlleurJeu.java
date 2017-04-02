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
 * -thread Serveur fait notify lorsqu'un joueur se connecte
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
	private final long DELAY_PH_RECHERCHE = TimeUnit.MINUTES.toMillis(5);
	private final long DELAY_PH_SOUMISSION = TimeUnit.MINUTES.toMillis(2);
	private final long DELAY_PH_RESULTATS = TimeUnit.SECONDS.toMillis(10);
	private final long DELAY_DEBUT_SESSION = TimeUnit.SECONDS.toMillis(20);

	//private Integer condTimer; //moniteur pour attente du timer
	private MyTimer timer;
	private Serveur serveur;
	//private Timer timer; //TODO: a enlever
	private TimerTask timerTask;
	public ControlleurJeu(Serveur serveur){
		this.serveur = serveur;
		this.timer = new MyTimer(serveur.getCondControlleurEnAttente());
		//timer = new Timer();
		this.start();
	}
	public void initTimerTask(){
		timerTask = new TimerTask(){
			public void run(){
				serveur.getCondControlleurEnAttente().notify();
			}
		};
	}
	public void debutSession (){
		//attends qu'au moins un joueur se connecte
			try {
					serveur.getCondControlleurEnAttente().wait();
			} catch (InterruptedException e) {}
		//attente 20s avant de commencer session
			
		//commencer session	
			this.serveur.newSession();
			jouer();
	}
	public void phaseRecherche(){
		serveur.tour();
	}
	public void jouer(){
		while (!serveur.getPool().isEmpty() && serveur.getUsers().size()>0){
			phaseRecherche();
		}
	}
	public void run (){
		initTimerTask();
		debutSession();
	}
}
