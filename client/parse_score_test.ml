(*ocamlc -o parse_score_test.exe -thread -custom unix.cma threads.cma str.cma parse_score_test.ml -cclib -lthreads -cclib -lunix *)

let parse2 all_scores =
  let result = ref "" in
  let reg_n = Str.regexp "[0-9]+\*" and
      reg_split = Str.regexp "*" and
      score = Str.regexp "[0-9a-zA-Z _]+\*[0-9a-zA-Z _]+\*"
  in
  Str.search_forward reg_n all_scores 0;
  let read = Str.matched_string all_scores in
  let n =
    int_of_string
      (List.hd 
         (Str.split reg_split read))
                  
  in
  let len mot = String.length mot in
  let l = len read in
  let p = ref (1 + l) in
  for i = 1 to n do
    Str.search_forward score all_scores !p;
    let cur = Str.matched_string all_scores in
    p := !p + (len cur);
    let node = Str.split reg_split cur in
    result:=(!result ^ (List.hd node) ^ ":" ^ (List.hd(List.tl node)) ^ "\n")
  done;
    !result
;;


let s =  "230*a*2*b*3*hello*234";;
  let k ="2*lily*0koko*0";;


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
    
    

    Printf.printf "%s" (parse_scores k); flush(stdout);;
