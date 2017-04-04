(*ocamlc -o testArray.exe -thread -custom unix.cma threads.cma str.cma testArray.ml -cclib -lthreads -cclib -lunix *)

let str_recu = "abcdefghi";;

let strToPlacement2 str l c =
  Array.make_matrix l c "a";;


(*
let strToPlacement str l res =
  if str = ""
             res
     else
       strToPlacement
         (last_chars ((length str) - l))
         l
         Array.concat [
           res
           Array.init c ()
         ]
 *)

(*arr is an array with size of lst and is initially the size of lst and k =0*)
let listToArray lst arr n k=
  if k = n
  then arr
  else listToArray (List.tl lst)
                   (arr.k <- List.hd lst)
                   n
                   (k+1)
;;
                                    
    
let strToPlacement str =
  let reg = regexp "[A-Z ]" and
      n = length str in
  listToArray (Str.split reg str)
              (Array.make n " ")
              n
              k
;;


        (*
let strToPlacement str =
  let reg = Str.regexp "[A-Z ]" and
      n = String.length str in
  begin ignore(Printf.printf "length:%d\n" (List.length  (Str.split reg str)));ignore (flush(stdout));
  listToArray (Str.split reg str)
              (Array.make n " ")
              n
              0
end
         *)
  

  (*arr is an array with size of lst and is initially the size of lst and k =0*)
               (*
let rec listToArray lst arr n k=
  if k = n
  then arr
  else
    begin
    ignore((arr.(k) <- List.hd lst));
    listToArray (List.tl lst)
                   arr
                   n
                   (k+1)
    end
                *)  
  
(* Un tableau de symboles obtenus grâce à leur code UTF-8. *)
let symbols =
  Array.concat [
    (* Lettres grecques minuscules. *)
    Array.init  25 (fun i -> Glib.Utf8.from_unichar (i +  945));
    (* Divers symboles mathématiques. *)
    Array.init 256 (fun i -> Glib.Utf8.from_unichar (i + 8704));
    ]
   
