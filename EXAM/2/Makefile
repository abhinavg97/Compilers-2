all:
	@lex tagGen.l
	@yacc -d tagGen.y
	@gcc lex.yy.c y.tab.c -o tagGen.out 
clean:
	@rm -f tagGen.tab.cc lex.yy.cc tagGen.out stack.hh tagGen.tab.hh
	
