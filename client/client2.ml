(*ocamlc -o client2.exe -thread -custom unix.cma threads.cma str.cma client2.ml -cclib -lthreads -cclib -lunix *)

(*TODO 
-methode pour parser protocol //DONE
-recuperation des donnees entree par utilisateurs et conversion en protocol string
 *)
(*open InterfaceGraphique;;*)
let pseudo = ref "";;
let sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0;;
let quit = ref false;;
let pret_ecoute_serv = ref false;;
(*vraiment necessaire?*)
let c_placement = ref "";;
let c_tirage = ref "";;
let c_scores = ref "";;
let c_phase = ref "";;
let c_temps = ref "";;

(*---------utils--------------*)
    let rec to_str lst str n =
    if (List.length lst) = 0
    then str
    else
      if n=0
      then to_str (List.tl lst) str 1
      else
        to_str
          (List.tl (List.tl lst))
          (str ^ (List.hd lst) ^ ":" ^ (List.hd (List.tl lst)) ^ "\n")
          1
  ;;
    
  let parse_scores scores =
    let reg = Str.regexp "*" in
    let lst = Str.split reg scores in
    to_str lst "" 0;;

    

(*------------------------------*)
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
  (*let module InterfaceGraphique = (InterfaceGraphique:INTERFACE);*)
  let proto_deconnexion user =
    Visu.update_lblMsgConnxDecnnx ("DECONNEXION:"^user);
    Printf.printf "client:DECONNEXION\n";flush(stdout);
    Printf.printf "client:user= %s\n" user ;flush(stdout)
        ;;

        let proto_bienvenue placement tirage  scores phase temps=
          Visu.update_lblScores (parse_scores scores);
    Printf.printf "BIENVENUE\n";flush(stdout);
    Printf.printf "placement= %s\n" placement ;flush(stdout);
    Printf.printf "tirage= %s\n" tirage ;flush(stdout);
    Printf.printf "scores= %s\n" scores ;flush(stdout);
    Printf.printf "phase= %s\n" phase ;flush(stdout);
    Printf.printf "temps= %s\n" temps ;flush(stdout);
    c_placement := placement;
    c_tirage := tirage;
    c_scores := scores;
    c_phase := phase;
    c_temps := temps
 
  ;;
     
  let proto_refus () =
     Printf.printf "REFUS\n"; flush(stdout);
     quit := true;
     exit 1
  ;;
  let proto_connecte user =
    (*Printf.printf "CONNECTE\n";flush(stdout);
    Printf.printf "user= %s\n" user ;flush(stdout);*)
    Printf.printf "client:CONNECTE\n";flush(stdout);
    Printf.printf "client:user= %s\n" user ;flush(stdout);
    Visu.update_lblMsgConnxDecnnx ("CONNETE:"^user);

  ;;
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
    (*attendre que connection soit fait avant d'écouter serveur*)
    while not !pret_ecoute_serv do
     ()
    done; 
    let regexpression = Str.regexp "/" in
    while not !quit do
      let protocol_recu = lecture_proto s in
      let proto_lst = Str.split regexpression protocol_recu in
      let commande_protocol = List.nth proto_lst 0 in
      Printf.printf "client listening to server loop: recu: %s\n" commande_protocol; flush(stdout);
      match commande_protocol with
      | "BIENVENUE" ->
         proto_bienvenue (List.nth proto_lst 1) (List.nth proto_lst 2) (List.nth proto_lst 3) (List.nth proto_lst 4) (List.nth proto_lst 5) ;
      | "REFUS" -> proto_refus ();
      | "CONNECTE" ->
         proto_connecte (List.nth proto_lst 1);
      | "DECONNEXION" -> proto_deconnexion (List.nth proto_lst 1);
      | "SESSION" -> proto_session ();
      | "VAINQUEURE" -> proto_vainqueure (List.nth proto_lst 1);
      | "TOUR" ->
         proto_tour (List.nth proto_lst 1) (List.nth proto_lst 2);
      | "RVALIDE" -> proto_rvalide ();
      | "RINVALIDE" -> proto_rinvalide (List.nth proto_lst 1);
      | "RATROUVE" -> proto_ratrouve (List.nth proto_lst 1);
      | "RFIN" -> proto_rfin ();
      | "SVALIDE" -> proto_svalide ();
      | "SINVALIDE" -> proto_sinvalide (List.nth proto_lst 1);
      | "SFIN" -> proto_sfin ();
      | "BILAN" ->
         proto_bilan (List.nth proto_lst 1) (List.nth proto_lst 2) (List.nth proto_lst 3);
      |some -> Printf.printf "ERROR in serveur_A_InterfaceGraphique: %s\n" commande_protocol; flush(stdout);
    done;
     Unix.close sock
  ;;

  let th = Thread.create serveur_A_InterfaceGraphique sock;;

  let send_server msg =
    let chan_out = Unix.out_channel_of_descr sock in
    ignore (output_string chan_out msg);
    flush chan_out
  ;;
    
   let sort () =
     send_server (proto_sort !pseudo);
     quit := true
   (*terminer thread interface graphique, terminer thread client*)
   ;;
  (*lit continuellement sur inputstream pour voir si serveur envoie des donnees,
   parse le protocol envoyer et fait appel aux methodes adequats*)
  let connexion serv p user =
    pseudo := user;
    let port_num = p and
      server = serv in
    let host = Unix.gethostbyname server in
    let h_addr = host.Unix.h_addr_list.(0) in
    let sock_addr = Unix.ADDR_INET (h_addr, port_num) in
    (*try*)
        Unix.connect sock sock_addr;
   (* with
    | Unix.Unix_error (e,_,_) ->
      begin
      Printf.printf "ERROR: unable to connect to server\n"; flush(stdout);
      quit := true;
      exit 1;
      end;
    |_ -> Printf.printf "ok\n";  flush(stdout);*)
   Printf.printf "ready\n"; flush(stdout);
       pret_ecoute_serv := true;
    let proto = proto_connexion user in
    send_server proto;
  ;;
    

  (*--------------------Tests----------------------------------*)

  let util1 (serv, port, pseudo) =
    (*Printf.printf "user: %s connecting\n" pseudo ; flush(stdout);*)
    connexion serv port pseudo;
    (*Printf.printf "user: %s is going to sleep\n" pseudo ; flush(stdout);*)
    Thread.delay 5.;
    (*Printf.printf "user %s is going to leave\n" pseudo; flush(stdout);*)
    sort ()
  ;;

  let util2 (serv, port, pseudo) =
    (*doit etre refuse--app termine*)
    (*Printf.printf "user: %s connecting\n" pseudo ; flush(stdout);*)
    connexion serv port pseudo;
    (*Printf.printf "user: %s is going to sleep\n" pseudo ; flush(stdout);*)
    Thread.delay 3.;
    (*Printf.printf "user %s is going to leave\n" pseudo; flush(stdout);*)
    sort ()
  ;;
    
  let main () =
    let port = int_of_string Sys.argv.(2)
    and serv = (Sys.argv.(1)) 
  and testusr = (Sys.argv.(3)) in
      match testusr with
      |"usr1" -> (util1 (serv,port,"usr1"));
      |"usr2" -> (util2 (serv,port,"usr2"))
      |some -> Printf.printf "jeu de test n'existe pas\n";flush(stdout)
  ;;

  (*main ();;*)
    
  let test () =
    Printf.printf "client2\n"; flush(stdout);;
              
 
