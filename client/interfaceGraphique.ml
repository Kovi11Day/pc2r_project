(*ocamlfind ocamlc -w -g -package lablgtk2 -linkpkg unix.cma str.cma interfaceGraphique.ml -o interfaceGraphique*)
let _ = GMain.init ()

(* Fenêtre principale. *)
let window = GWindow.window 
  ~width:800
  ~height:600
  ~resizable:true
  ~title:"Scrabble" ()

(*conteneur base de la fenetre (window peut contenir un seul enfant!)*)
let base = GPack.box `HORIZONTAL (*renommer jeu*)
  ~spacing:30
  ~border_width:10
  ~packing:window#add ()

  (*conteneur base de la fenetre (window peut contenir un seul enfant!)*)
(*let base = GPack.layout
  ~border_width:10
  ~packing:window#add ()*)
(* Insertion de barres de défilement. *)
  (*
let scroll = GBin.scrolled_window
  ~height:600
  ~hpolicy:`ALWAYS
  ~vpolicy:`ALWAYS
  ~packing:base#add ()
   *)
(*conteneur pour grille de placement*)
(*let hbox = GPack.hbox
             ~border_width:10
             ~packing:base#add ()*)
let bplacement = GPack.hbox   ~packing:base#pack ()
let vplacement = GPack.table
  ~columns:3
  ~rows:3
  ~row_spacings:0
  ~col_spacings:0
  ~homogeneous:true
  ~packing:bplacement#add ()

  
(*conteneur pour grille de tirage*)
let btirage = GPack.hbox   ~packing:base#pack ()
let vtirage = GPack.table
  ~columns:7
  ~rows:1
  ~row_spacings:0
  ~col_spacings:0
  ~homogeneous:true
  ~packing:btirage#add ()
  


(* Un conteneur spécialement conçu pour les boutons. Essayez de remplacer 
 * `SPREAD par `EDGE pour voir ce que ça fait... *)
(*let bbox = GPack.button_box `HORIZONTAL
  ~layout:`SPREAD
  ~packing:(base#pack ~expand:false) ()*)

let deconnexion_msg () = print_endline "deconnexion"
let envoyer_placement_msg () = print_endline "placement envoyer"
(*quand placement envoyer, grille placement et grille tirage se reinitialise
et l'ecran reste active ou grise en fonc de la pjase rec ou sou*)
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

let str_placement = strToPlacement "abcdefghijkl"
let str_tirage = strToPlacement "lettres"
(* Le conteneur GPack.table (GtkTable) est rempli : chaque case reçoit un bouton
 * contenant un symbole du tableau < symbols > défini ci-dessus. Le symbole est
 * inséré dans une étiquette (GtkLabel) pour pouvoir utiliser les balises Pango
 * (notamment <big> et </big> qui augmentent la taille du texte). *)
let afficher_table largeur str_table=
  Array.iteri (fun i sym ->
    let button = GButton.button 
      ~relief:`NONE 
      ~packing:(vplacement#attach ~left:(i mod largeur) ~top:(i / largeur)) ()
    and markup = Printf.sprintf "<big>%s</big>" sym in
    ignore (GMisc.label ~markup ~packing:button#add ())
  ) str_table

let _ =
  afficher_table 3 str_placement;
  afficher_table 7 str_tirage;
  window#connect#destroy ~callback:GMain.quit;
  window#show ();
  GMain.main ()

