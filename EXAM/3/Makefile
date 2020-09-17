all:
	@lex pyPrint.l
	@yacc -d pyPrint.y
	@gcc lex.yy.c y.tab.c -o pyPrint.out 
clean:
	@rm -f pyPrint.tab.cc lex.yy.cc pyPrint.out stack.hh pyPrint.tab.hh
	
