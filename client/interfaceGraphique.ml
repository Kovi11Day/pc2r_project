(*ocamlfind ocamlc -w -g -package lablgtk2 -linkpkg unix.cma str.cma interfaceGraphique.ml -o interfaceGraphique*)
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

let base = GPack.table
  ~columns:2
  ~rows:2             
  ~row_spacings:20
  ~col_spacings:20
  ~homogeneous:true
  ~packing:window#add()  


let vplacement = GPack.table
  ~columns:3
  ~rows:3
  ~row_spacings:0
  ~col_spacings:0
  ~homogeneous:true 
  ~packing:(base#attach ~left:0 ~top:0) ()
   (* in base#attach ~left:0 ~top:0 (vplacement#corerce)*)

  
(*conteneur pour grille de tirage*)

let vtirage = GPack.table
  ~columns:7
  ~rows:1
  ~row_spacings:0
  ~col_spacings:0
  ~homogeneous:true
  ~packing:(base#attach ~left:0 ~top:1) ()


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
let _ =
  afficher_plateau 4 str_placement;
  afficher_tirage 7 str_tirage;
  window#connect#destroy ~callback:GMain.quit;
  window#show ();
  GMain.main ()

