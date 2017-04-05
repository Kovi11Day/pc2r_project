(*ocamlfind ocamlc -w -g -thread -package lablgtk2 -linkpkg threads.cma str.cma interfaceGraphique.ml -cclib -lthreads -o interfaceGraphique*)
let _ = GMain.init ()
let gmainquit ()=
  GMain.quit ()
(*-------------window_connexion------------------*)
                   
let window_conn = GWindow.window 
  ~width:800
  ~height:600
  ~resizable:true
  ~title:"Connection" ()
let base_win_conn = GPack.table
  ~columns:3
  ~rows:3
  ~border_width:10
  ~row_spacings:20
  ~col_spacings:20
  ~homogeneous:true
  ~packing:window_conn#add()
let bbox_win_conn = GPack.button_box `HORIZONTAL
  ~layout:`SPREAD
  ~packing:(base_win_conn#attach ~left:1 ~top:2) ()
(*to be bound*)
let connection_win_conn = GButton.button
  ~label:"CONNECTION"
  ~packing: bbox_win_conn#add ()
let quit_win_conn = GButton.button
    ~label:"QUIT"
    ~packing:bbox_win_conn#add ()
let txtboxUsername = GEdit.entry
  ~text:""
  ~max_length:500
  ~packing:(base_win_conn#attach ~left:1 ~top:1) ()
(*-------------window_scrabble------------------*)  
let window = GWindow.window 
  ~width:800
  ~height:600
  ~resizable:true
  ~title:"Scrabble" ()
let base_area = GPack.table
  ~columns:2
  ~rows:2
  ~show:true
  ~border_width:10
  ~row_spacings:20
  ~col_spacings:20
  ~homogeneous:true
  ~packing:window#add ()

let comm_area = GPack.table
  ~columns:2
  ~rows:2
  ~show:true
  ~border_width:10
  ~row_spacings:20
  ~col_spacings:20
  ~homogeneous:true
  ~packing:(base_area#attach ~left:1 ~top:0) ()

let vplacement = GPack.table
  ~columns:3
  ~rows:3
  ~show:true
  ~row_spacings:0
  ~col_spacings:0
  ~homogeneous:true 
  ~packing:(base_area#attach ~left:0 ~top:0) ()
  
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
(*called by client2*)
let update_lblMsgConnxDecnnx msg =
  vMsgConnxDecnnx#set_text msg
                          
let vScores = GMisc.label
  ~text:"Scores"
  ~width:200
  ~height:200
  ~show:true
  ~packing:(comm_area#attach ~left:0 ~top:0) ()
let update_lblScores scores =
  let sc = Printf.sprintf "%s" scores in
  vScores#set_text sc

let bbox_win_scrabble = GPack.button_box `HORIZONTAL
  ~layout:`SPREAD
  ~packing:(comm_area#attach ~left:1 ~top:1) ()
(*to be bound*)
let trouve_btn = GButton.button
  ~label:"TROUVE"
  ~packing: bbox_win_scrabble#add ()
(*to be bound*)
let sort_btn = GButton.button
    ~label:"SORT"
    ~packing:bbox_win_scrabble#add ()


(*-----------------------------------------------------------------------*)
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

(*service*)
let afficher_msg_lblDeconnexion msg =
  Printf.printf "interface:deconnexion\n"; flush(stdout); 
  vMsgConnxDecnnx#set_text msg                        

let local_bindings () =
  quit_win_conn#connect#clicked ~callback:GMain.quit

(*test if can be removed*)                            
let apply () =
  connection_win_conn;
  trouve_btn;
  sort_btn;
  quit_win_conn
let gmainmain () =
  afficher_plateau 4 str_placement;
  afficher_tirage 7 str_tirage;
  window_conn#connect#destroy ~callback:GMain.quit;
  window_conn#show ();
  (*Client2.main ();*)
  (*Thread.create afficher_msgConnxDecnnx  "working yeahh!!";*)
  GMain.main ()
 ;;            
             (*end;;*)

   
