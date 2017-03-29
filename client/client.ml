(*ocamlc -o client.exe -thread -custom unix.cma threads.cma client.ml -cclib -lthreads -cclib -lunix *)

(*TODO 
-methode pour parser protocol
-recuperation des donnees entree par utilisateurs et conversion en protocol string
 *)
let interfaceGraphique_to_String ()  =
(*en attente de quelque chose de l'interface graphique
 et transformation en string et ecriture dans fd ET FLUSH*)
  ;;
class client serv p =
object(s)
  val sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0
  val port_num = p
  val server = serv
                 
  method start () =
    let host = Unix.gethostbyname server in
    let h_addr = host.Unix.h_addr_list.(0) in
    Unix.connect sock sock_addr;
    Thread th1 = Thread.create s#interfaceGraphique_A_Serveur (sock, sock_addr);
    Thread th2 = Thread.create s#serveur_A_InterfaceGraphique (sock, sock_addr);
    Thread.join th1;
    Thread.join th2;
    Unix.close sock
               
  (*prendre entree de l'utilisateur et envoyer au serveur*)
  method interfaceGraphique_A_Serveur s sa =
    while true do
      let si = (my_input_line Unix.stdin ^ "\n") in (*read form interface graphique*)
      ignore (ThreadUnix.write s si 0 (String.length si));
      interfaceGraphique_to_String s
      else (Printf.printf "%s\n")
    done

  (*lit continuellement sur inputstream pour voir si serveur envoie des donnees,
   parse le protocol envoyer et fait appel aux methodes adequats*)
  method serveur_A_InterfaceGraphique s sa =
    
    while true do
   
      
    done

  method proto_connexion s sa =
    (*recuperer nom de l'utilisateur de l'interface graphique*)
    let user = (my_input_line Unix.stdin) in
    let protocol_cxn = "CONNEXION/" ^ user ^ "/" in
    ignore (ThreadUnix.write s protocol_cnx 0 (String.length protocol_cnx));
end;;
