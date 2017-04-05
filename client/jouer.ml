let bindConnexion () =
  Client2.connexion "localhost" (int_of_string "2017") (Visu.txtboxUsername#text);
  (*TODO: attendre validation ou refus*)
  Visu.window#show ()

  
let bindTrouve () = ();;
(*TODO: lire le placement de interface graphique et trans en string*)
(*TODO: faire appel a la fonction /client2.ml/proto_trouve*)
let bindSort () =
  Client2.sort ();
  Visu.gmainquit ();
  (*Visu.window_conn#connect#destroy ~callback:Visu.gmainquit;
  Visu.window_conn#destroy ();*)
  (*Visu.window#connect#destroy ~callback:Visu.gmainquit;
  Visu.window#destroy ();*)
  Visu.gmainquit ()
;;
  Visu.local_bindings ();; Visu.quit_win_conn;;
  Visu.connection_win_conn#connect#clicked ~callback:bindConnexion;;
  Visu.trouve_btn#connect#clicked ~callback:bindTrouve;;
  Visu.sort_btn#connect#clicked ~callback:Client2.sort;; 


          Visu.gmainmain ();;
              


            
