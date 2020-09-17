all:
	@lex polyCodegen.l
	@yacc -d polyCodegen.y
	@gcc lex.yy.c y.tab.c -lfl -o polyCodegen.out 
clean:
	@rm -f polyCodegen.tab.cc lex.yy.cc polyCodegen.out stack.hh polyCodegen.tab.hh
	
