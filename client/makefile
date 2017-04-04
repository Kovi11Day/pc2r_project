OCAMLFIND=ocamlfind
OCAMLC=ocamlc

EXEC=client2.exe
LINTER = interfaceGraphique.cmo
LCLIENT = client2.cmo
all:prog

prog: interface client
	$(OCAMLC) -o $(EXEC) $(LINTER) $(LCLIENT)
interface:
	$(OCAMLFIND) $(OCAMLC) -c -w -g -thread -package lablgtk2 -linkpkg str.cma interfaceGraphique.ml -cclib -lthreads -lunix

client:
	$(OCAMLC) -c -thread -custom unix.cma threads.cma str.cma client2.ml -cclib -lthreads -cclib -lunix 

sig:
	$(OCAMLC) -c interfaceGraphique.mli
clean:
	rm -f *.cm[iox] *~ .*~ #*#
	rm -f $(EXEC)
	rm -f $(EXEC).opt
