(*ocamlc -o client.exe -thread -custom unix.cma threads.cma str.cma client.ml -cclib -lthreads -cclib -lunix *)

(*TODO 
-methode pour parser protocol //DONE
-recuperation des donnees entree par utilisateurs et conversion en protocol string
 *)
let pseudo = ref "";;
let sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0;;

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
    Printf.printf "%s%s%s\n" placement tirage scores;
    flush(stdout);
    ;;
  let proto_connecte user =
    (*TODO*)
    ();;
  let proto_connexion user =
    "CONNEXION/" ^ user ^ "/\n"
  ;;
  let proto_trouve () =
    (*TODO*)
    (*transformer tableau en string et former le protocol:
     "TROUVE/" ^ placement ^ "/"
     *)
    ""
  ;;
  let proto_sort user =
    "SORT/" ^ user ^ "/\n"
    ;;

  let lecture_proto stream =
    let data_recv = Bytes.create 10000 in
    let len = Unix.recv stream data_recv 0 10000 [] in
    String.sub data_recv 0 len;
  ;;

  (*fonc appele par l'interface graphique quand l'utilisateur entre son pseudo et clique sur le bouton de connexion*)

  let serveur_A_InterfaceGraphique s =
    let regexpression = Str.regexp "/" in
    while true do
      let protocol_recu = lecture_proto s in
      let proto_lst = Str.split regexpression protocol_recu in
      let commande_protocol = List.nth proto_lst 0 in
      match commande_protocol with
      | "BIENVENUE" ->
         proto_bienvenue (List.nth proto_lst 0) (List.nth proto_lst 1) (List.nth proto_lst 2);
      | "CONNECTE" ->
         proto_connecte (List.nth proto_lst 0);
      | "DECONNEXION" -> proto_connecte (List.nth proto_lst 0);
      | "SESSION" -> proto_session ();
      | "VAINQUEURE" -> proto_vainqueure (List.nth proto_lst 0);
      | "TOUR" ->
         proto_tour (List.nth proto_lst 0) (List.nth proto_lst 1);
      | "RVALIDE" -> proto_rvalide ();
      | "RINVALIDE" -> proto_rinvalide (List.nth proto_lst 0);
      | "RATROUVE" -> proto_ratrouve (List.nth proto_lst 0);
      | "RFIN" -> proto_rfin ();
      | "SVALIDE" -> proto_svalide ();
      | "SINVALIDE" -> proto_sinvalide (List.nth proto_lst 0);
      | "SFIN" -> proto_sfin ();
      | "BILAN" ->
         proto_bilan (List.nth proto_lst 0) (List.nth proto_lst 1) (List.nth proto_lst 2);
      |some -> Printf.printf "ERROR in interfaceGraphique_A_Serveur\n";
    done
  ;;
let th = Thread.create serveur_A_InterfaceGraphique sock;;
  let send_server msg =
    let chan_out = Unix.out_channel_of_descr sock in
    ignore (output_string chan_out msg);
    flush chan_out
  ;;
  let sort () =
    send_server (proto_sort !pseudo);
    Unix.close sock;
  (*terminer thread interface graphique, terminer thread threand client*)
  ;;
    
  (*lit continuellement sur inputstream pour voir si serveur envoie des donnees,
   parse le protocol envoyer et fait appel aux methodes adequats*)
  let connexion serv p user =
    (*mettre a jour champ globale pseudo*)
    pseudo := user;
    (*connexion au serveur*)
    let port_num = p and
      server = serv in
    let host = Unix.gethostbyname server in
    let h_addr = host.Unix.h_addr_list.(0) in
    let sock_addr = Unix.ADDR_INET (h_addr, port_num) in
    Unix.connect sock sock_addr;
    (*(serveur_A_InterfaceGraphique sock);*)
    (*let th = Thread.create serveur_A_InterfaceGraphique sock in*)
    (*envoie de la protocol connexion au serveur*)
    let proto = proto_connexion user in
    send_server proto;
    Printf.printf "in sentmsgTOserver\n"; flush(stdout);
    (*Thread.join th;
    Unix.close sock*)
  ;;
    
  let main () =
    let port = int_of_string Sys.argv.(2)
    and serv = (Sys.argv.(1)) in
    connexion serv port "kovila";
    Printf.printf "user: %s is going to sleep\n" !pseudo ; flush(stdout);
    Unix.sleep 5;
    Printf.printf "user %s is going to leave\n" !pseudo; flush(stdout);
    sort ()
  ;;

    main ();;
    

              
 
