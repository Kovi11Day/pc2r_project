(*ocamlfind ocamlc -w -g -package lablgtk2 -linkpkg sampleInterface.ml -o sampleInterface*)
let _ = GMain.init ()

(* Fenêtre principale (non redimensionnable). *)
let window = GWindow.window 
  ~width:300 
  ~resizable:false
  ~title:"Tuto SdZ Exemple 2" ()

(* Conteneur principal, pour pouvoir insérer plusieurs widgets. En effet, les
 * fenêtres (GtkWindow) ne peuvent contenir qu'un seul enfant. *)
let vbox = GPack.vbox 
  ~spacing:10
  ~border_width:10
  ~packing:window#add ()

(* Insertion de barres de défilement. *)
let scroll = GBin.scrolled_window
  ~height:200
  ~hpolicy:`ALWAYS
  ~vpolicy:`ALWAYS
  ~packing:vbox#add ()

(* Conteneur en forme de tableau. La méthode add_with_viewport permet d'insérer
 * un conteneur tel que GPack.table (GtkTable) en le faisant défiler à l'aide 
 * des barres de défilement de GBin.scrolled_window (GtkScrolledWindow). *)
let table = GPack.table
  ~row_spacings:5
  ~col_spacings:5
  ~homogeneous:true
  ~packing:scroll#add_with_viewport ()

(* Un conteneur spécialement conçu pour les boutons. Essayez de remplacer 
 * `SPREAD par `EDGE pour voir ce que ça fait... *)
let bbox = GPack.button_box `HORIZONTAL
  ~layout:`SPREAD
  ~packing:(vbox#pack ~expand:false) ()

let help_message () = print_endline "Cliquez sur \"Quitter\" pour quitter"

(* Un bouton pour obtenir de l'aide. *)
let help = 
  let button = GButton.button 
    ~stock:`HELP
    ~packing:bbox#add () in
  button#connect#clicked ~callback:help_message;
  button

(* Un bouton pour quitter. *)
let quit = 
  let button = GButton.button
    ~stock:`QUIT
    ~packing:bbox#add () in
  button#connect#clicked ~callback:GMain.quit;
  button

(* Un tableau de symboles obtenus grâce à leur code UTF-8. *)
let symbols =
  Array.concat [
    (* Lettres grecques minuscules. *)
    Array.init  25 (fun i -> Glib.Utf8.from_unichar (i +  945));
    (* Divers symboles mathématiques. *)
    Array.init 256 (fun i -> Glib.Utf8.from_unichar (i + 8704));
  ]

(* Le conteneur GPack.table (GtkTable) est rempli : chaque case reçoit un bouton
 * contenant un symbole du tableau < symbols > défini ci-dessus. Le symbole est
 * inséré dans une étiquette (GtkLabel) pour pouvoir utiliser les balises Pango
 * (notamment <big> et </big> qui augmentent la taille du texte). *)
let init_table () =
  Array.iteri (fun i sym ->
    let button = GButton.button 
      ~relief:`NONE 
      ~packing:(table#attach ~left:(i mod 10) ~top:(i / 10)) ()
    and markup = Printf.sprintf "<big>%s</big>" sym in
    ignore (GMisc.label ~markup ~packing:button#add ())
  ) symbols

let _ =
  init_table ();
  window#connect#destroy ~callback:GMain.quit;
  window#show ();
  GMain.main ()

