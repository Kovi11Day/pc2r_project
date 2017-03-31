(*ocamlc -o client.exe -thread -custom unix.cma threads.cma client.ml -cclib -lthreads -cclib -lunix *)

(*TODO 
-methode pour parser protocol //DONE
-recuperation des donnees entree par utilisateurs et conversion en protocol string
 *)
class client serv p =
object(self)
  val sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0
  val port_num = p
  val server = serv
                 
  (*stocker placement, tirage*)
  method start () =
    let host = Unix.gethostbyname server in
    let h_addr = host.Unix.h_addr_list.(0) in
    let sock_addr = Unix.ADDR_INET (h_addr, port_num) in
    Unix.connect sock sock_addr;
    let th = Thread.create self#serveur_A_InterfaceGraphique sock in
      self#interfaceGraphique_A_Serveur sock;
      Thread.join th;
    Unix.close sock
               
  (*prendre entree de l'utilisateur et envoyer au serveur*)
  (*a remplacer par la fonction qui lance l'interface graphique 
ici on simule l'interface graphique par le input stream du terminal*)
  method interfaceGraphique_A_Serveur s =
    while true do
      let protocol = ref "" in 
      let usr_in= (self#lecture_proto Unix.stdin) in (*read form interface graphique*)
      match usr_in with
      | "CONNEXION" ->
         let proto =  self#proto_connexion (self#lecture_proto Unix.stdin) in
         ignore (ThreadUnix.write s proto 0 (String.length proto));
      | "SORT" ->
         protocol := self#proto_sort (self#lecture_proto Unix.stdin);
      | "TROUVE" ->
         protocol := self#proto_trouve ();
         ignore (ThreadUnix.write s !protocol 0 (String.length !protocol));
      | some -> ignore (Printf.printf "ERROR in interfaceGraphique_A_Serveur\n");
    done

  (*lit continuellement sur inputstream pour voir si serveur envoie des donnees,
   parse le protocol envoyer et fait appel aux methodes adequats*)
  method serveur_A_InterfaceGraphique s =
    
    while true do
      let protocol_recu = self#lecture_proto s in
      match protocol_recu with
      | "BIENVENUE" ->
         self#proto_bienvenue ((self#lecture_proto s), (self#lecture_proto s), (self#lecture_proto s));
      | "CONNECTE" ->
         self#proto_connecte ((self#lecture_proto s));
      | "DECONNEXION" -> self#proto_connecte (self#lecture_proto s);
      | "SESSION" -> self#proto_session ();
      | "VAINQUEURE" -> self#proto_vainqueure (self#lecture_proto s);
      | "TOUR" ->
         self#proto_tour ((self#lecture_proto s), (self#lecture_proto s), (self#lecture_proto s));
      | "RVALIDE" -> self#proto_rvalide ();
      | "RINVALIDE" -> self#proto_rinvalide (self#lecture_proto s);
      | "RATROUVE" -> self#proto_ratrouve (self#lecture_proto s);
      | "RFIN" -> self#proto_rfin ();
      | "SVALIDE" -> self#proto_svalide ();
      | "SINVALIDE" -> self#proto_sinvalide (self#lecture_proto s);
      | "SFIN" -> self#proto_sfin ();
      | "BILAN" ->
         self#proto_bilan ((self#lecture_proto s), (self#lecture_proto s),
                           (self#lecture_proto s));
         (*|some -> Printf.printf "ERROR in interfaceGraphique_A_Serveur\n";*)
    done

  method useless string = ();
  method proto_bilan mot vainqueure score =
    (*TODO*)
    ();
  method proto_sfin () =
    (*TODO*)
    ();
  method proto_sinvalide raison =
    (*TODO*)
    ();
  method proto_svalide () =
    (*TODO*)
    ();
  method proto_rfin () =
    (*TODO*)
    ();
  method proto_ratrouve placement user =
    (*TODO*)
    ();
  method proto_rinvalide raison =
    (*TODO*)
    ();
  method proto_rvalide () =
    (*TODO*)
    ();
  method proto_tour placement tirage  =
    (*TODO*)
    ();
  method proto_vainqueure bilan =
    (*TODO*)
    ();
  method proto_session () =
    (*TODO*)
    ();
  method proto_deconnexion user =
    (*TODO*)
    ();
  method proto_bienvenue (placement, tirage, scores) =
    (*TODO*)
    "";
  method proto_connecte user =
    (*TODO*)
    "";
  method proto_connexion user =
    "CONNEXION/" ^ user ^ "/"
    ();
  method proto_trouve () =
    (*TODO*)
    (*transformer tableau en string et former le protocol:
     "TROUVE/" ^ placement ^ "/"
     *)
    ();
  method proto_sort user =
    "SORT/" ^ user ^ "/"
    ;
  method lecture_proto stream =
    let s = "" and r = ref "" in
    while (ThreadUnix.read stream s 0 1 > 0) && s.[0] <> '/' do
      r := !r ^ s
    done;
    !r
                                                           
end;;
