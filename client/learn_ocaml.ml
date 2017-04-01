(*ocamlc -o learn_ocaml.exe -thread -custom unix.cma str.cma threads.cma learn_ocaml.ml -cclib -lthreads -cclib -lunix *)
let parsing_protocol proto =
  Printf.printf "protocol:%s\n" proto;
  let proto_list = Str.split (Str.regexp "/") proto in
  let proto_name = List.nth proto_list 0 and
      proto_plateau = List.nth proto_list 1 in
  Printf.printf "proto_name:%s\n" proto_name;
    Printf.printf "proto_plateau:%s\n" proto_plateau;
  flush(stdout);
;;

parsing_protocol "BIENVENUE/plateau/username";;
