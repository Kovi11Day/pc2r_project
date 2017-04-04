(*ocamlfind ocamlc -w -g -thread -package lablgtk2 -linkpkg unix.cma threads.cma str.cma interfaceGraphique.ml -cclib -lthreads -lunix -o interfaceGraphique*)
(*module InterfaceGraphique =
  struct*)
let _ = GMain.init ()

(* Fenêtre principale. *)
let window = GWindow.window 
  ~width:800
  ~height:600
  ~resizable:true
  ~title:"Scrabble" ()

(*conteneur base de la fenetre (window peut contenir un seul enfant!)*)
(*let base2 = GPack.vbox (*renommer jeu*)
  ~spacing:30
  ~border_width:10
  ~packing:window#add ()*)

let base_area = GPack.table
  ~columns:2
  ~rows:2
  ~border_width:10
  ~row_spacings:20
  ~col_spacings:20
  ~homogeneous:true
  ~packing:window#add()  

let comm_area = GPack.table
  ~columns:2
  ~rows:2
  ~border_width:10
  ~row_spacings:20
  ~col_spacings:20
  ~homogeneous:true
  ~packing:(base_area#attach ~left:1 ~top:0) ()

let vplacement = GPack.table
  ~columns:3
  ~rows:3
  ~row_spacings:0
  ~col_spacings:0
  ~homogeneous:true 
  ~packing:(base_area#attach ~left:0 ~top:0) ()
   (* in base_area#attach ~left:0 ~top:0 (vplacement#corerce)*)

  
(*conteneur pour grille de tirage*)

let vtirage = GPack.table
  ~columns:7
  ~rows:1
  ~row_spacings:0
  ~col_spacings:0
  ~homogeneous:true
  ~packing:(base_area#attach ~left:0 ~top:1) ()

let vMsgConnxDecnnx = GMisc.label
  ~text:"connexion info"
  ~width:100
  ~height:50
  ~show:true
  ~packing:(comm_area#attach ~left:1 ~top:0) ()

let deconnexion_msg () = print_endline "deconnexion"
let envoyer_placement_msg () = print_endline "placement envoyer"
let envoyer_placement_msg () = print_endline "envoyer"
                                             
(*
let envoyer_placement = 
  let button = GButton.button 
    ~stock:`HELP
    ~packing:bbox#add () in
  button#connect#clicked ~callback:envoyer_placement_msg;
  button

(* TODO *)
let deconnexion = 
  let button = GButton.button
    ~stock:`QUIT
    ~packing:bbox#add () in
  button#connect#clicked ~callback:GMain.quit;
  button

 *)                  
let strToPlacement str =
  let n = String.length str in
  let arr = Array.make n " " in
      for i = 0 to n-1 do
        ignore (arr.(i) <- String.make 1 str.[i])
      done;
      arr

let str_placement = strToPlacement "ab defghijkl"
let str_tirage = strToPlacement "lettres"

let afficher_plateau largeur str_table =
  Array.iteri (fun i sym ->
    let button = GButton.button 
      ~relief:`NONE 
      ~packing:( vplacement#attach ~left:(i mod largeur) ~top:(i / largeur)) ()
    and markup = Printf.sprintf "<big>%s</big>" sym in
    ignore (GMisc.label ~markup ~packing:button#add ())
  ) str_table

let afficher_tirage largeur str_table =
  Array.iteri (fun i sym ->
    let button = GButton.button 
      ~relief:`NONE 
      ~packing:( vtirage#attach ~left:(i mod largeur) ~top:(i / largeur)) ()
    and markup = Printf.sprintf "<big>%s</big>" sym in
    ignore (GMisc.label ~markup ~packing:button#add ())
    ) str_table

(*service*)
let afficher_msg_lblConnexion msg =
  Printf.printf "interface:connexion\n"; flush(stdout); 
  vMsgConnxDecnnx#set_text msg

(*service*)s
let afficher_msg_lblDeconnexion msg =
  Printf.printf "interface:deconnexion\n"; flush(stdout); 
  vMsgConnxDecnnx#set_text msg                        
                       
let _ =
  afficher_plateau 4 str_placement;
  afficher_tirage 7 str_tirage;
  window#connect#destroy ~callback:GMain.quit;
  window#show ();
  (*Thread.create afficher_msgConnxDecnnx  "working yeahh!!";*)
  GMain.main ()

             (*end;;*)

