OCAMLFIND=ocamlfind
OCAMLC=ocamlc

EXEC=jouer
LINTER = visu.cmo
LCLIENT = client2.cmo
LJOUER = jouer.cmo
all:prog

prog: clean interface client jouer
	$(OCAMLFIND) $(OCAMLC) -o $(EXEC) -thread -package lablgtk2 -linkpkg str.cma $(LINTER) $(LCLIENT) $(LJOUER) -cclib -lthreads
interface:
	$(OCAMLFIND) $(OCAMLC) -c -w -g -thread -package lablgtk2 -linkpkg threads.cma str.cma visu.ml -cclib -lthreads

client:
	$(OCAMLFIND) $(OCAMLC) -c -w -g -thread -package lablgtk2 -linkpkg -custom threads.cma str.cma unix.cma client2.ml -cclib -lthreads -cclib -lunix 

jouer:
	$(OCAMLFIND) $(OCAMLC) -c -w -g -thread -package lablgtk2 -linkpkg threads.cma str.cma jouer.ml -cclib -lthreads

siginterface:
	$(OCAMLC) -c visu.mli

sigclient:
	$(OCAMLC) -c client2.mli
clean:
	rm -f *.cm[iox] *~ .*~ #*#
	rm -f $(EXEC)
	rm -f $(EXEC).opt
