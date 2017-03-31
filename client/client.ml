(*ocamlc -o client.exe -thread -custom unix.cma threads.cma client.ml -cclib -lthreads -cclib -lunix *)

(*TODO 
-methode pour parser protocol //DONE
-recuperation des donnees entree par utilisateurs et conversion en protocol string
 *)
  let proto_bilan mot vainqueure score =
    (*TODO*)
    ();;
  let proto_sfin () =
    (*TODO*)
    ();;
  let proto_sinvalide raison =
    (*TODO*)
    ();;
  let proto_svalide () =
    (*TODO*)
    ();;
  let proto_rfin () =
    (*TODO*)
    ();;
  let proto_ratrouve user =
    (*TODO*)
    ();;
  let proto_rinvalide raison =
    (*TODO*)
    ();;
  let proto_rvalide () =
    (*TODO*)
    ();;
  let proto_tour placement tirage  =
    (*TODO*)
    ();;
  let proto_vainqueure bilan =
    (*TODO*)
    ();;
  let proto_session () =
    (*TODO*)
    ();;
  let proto_deconnexion user =
    (*TODO*)
    ();;
  let proto_bienvenue placement tirage  scores =
    (*TODO*)
    ();;
  let proto_connecte user =
    (*TODO*)
    ();;
  let proto_connexion user =
    "CONNEXION/" ^ user ^ "/"
  ;;
  let proto_trouve () =
    (*TODO*)
    (*transformer tableau en string et former le protocol:
     "TROUVE/" ^ placement ^ "/"
     *)
    "";;
  let proto_sort user =
    "SORT/" ^ user ^ "/"
    ;;
  let lecture_proto stream =
    let s = "" and r = ref "" in
    while (ThreadUnix.read stream s 0 1 > 0) && s.[0] <> '/' do
      r := !r ^ s
    done;
    !r
  ;;

      (*prendre entree de l'utilisateur et envoyer au serveur*)
  (*a remplacer par la fonction qui lance l'interface graphique 
ici on simule l'interface graphique par le input stream du terminal*)
  let interfaceGraphique_A_Serveur s =
    while true do
      let protocol = ref "" in 
      let usr_in= (lecture_proto Unix.stdin) in (*read form interface graphique*)
      match usr_in with
      | "CONNEXION" ->
         let proto =  proto_connexion (lecture_proto Unix.stdin) in
         ignore (ThreadUnix.write s proto 0 (String.length proto));
      | "SORT" ->
         protocol := proto_sort (lecture_proto Unix.stdin);
      | "TROUVE" ->
         protocol := proto_trouve ();
         ignore (ThreadUnix.write s !protocol 0 (String.length !protocol));
      | some -> ignore (Printf.printf "ERROR in interfaceGraphique_A_Serveur\n");
    done
  ;;

  (*lit continuellement sur inputstream pour voir si serveur envoie des donnees,
   parse le protocol envoyer et fait appel aux methodes adequats*)
  let serveur_A_InterfaceGraphique s =
    while true do
      let protocol_recu = lecture_proto s in
      match protocol_recu with
      | "BIENVENUE" ->
         proto_bienvenue (lecture_proto s) (lecture_proto s) (lecture_proto s);
      | "CONNECTE" ->
         proto_connecte (lecture_proto s);
      | "DECONNEXION" -> proto_connecte (lecture_proto s);
      | "SESSION" -> proto_session ();
      | "VAINQUEURE" -> proto_vainqueure (lecture_proto s);
      | "TOUR" ->
         proto_tour (lecture_proto s) (lecture_proto s);
      | "RVALIDE" -> proto_rvalide ();
      | "RINVALIDE" -> proto_rinvalide (lecture_proto s);
      | "RATROUVE" -> proto_ratrouve (lecture_proto s);
      | "RFIN" -> proto_rfin ();
      | "SVALIDE" -> proto_svalide ();
      | "SINVALIDE" -> proto_sinvalide (lecture_proto s);
      | "SFIN" -> proto_sfin ();
      | "BILAN" ->
         proto_bilan (lecture_proto s) (lecture_proto s) (lecture_proto s);
      |some -> Printf.printf "ERROR in interfaceGraphique_A_Serveur\n";
    done
  ;;
  let creer_client serv p = 
  let sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0 and
      port_num = p and
      server = serv in
  let host = Unix.gethostbyname server in
  let h_addr = host.Unix.h_addr_list.(0) in
  let sock_addr = Unix.ADDR_INET (h_addr, port_num) in
  Unix.connect sock sock_addr;
  let th = Thread.create serveur_A_InterfaceGraphique sock in
  interfaceGraphique_A_Serveur sock;
  Thread.join th;
  Unix.close sock
;;
    
  let main () =
    let port = int_of_string Sys.argv.(2)
    and serv = (Sys.argv.(1)) in
    creer_client serv port
  ;;
 
