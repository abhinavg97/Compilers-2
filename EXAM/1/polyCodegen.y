%{
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
int indentLevel = 0;
bool printme = true;
%}

%token LET DECLS NUMBER LCURL RCURL EQUALS VNAME COMMA PIPE COMPARATOR COLON SEMICOLON POINTS RBRACE LBRACE

%%

/*model:	parameters statements poly_reps*/
		 /*;*/
model:
     |  parameter model
     |  statements {
          printf("int main() {\n");
        } model 
     |  poly_rep model
     ;

statements:
          | statement statements
          ;

parameter: LET VNAME DECLS NUMBER {
	printf("#define %s %s\n", $2, $4);
};

statement: LET VNAME LCURL {
	printf("#define %s(", $2);
} elements RCURL DECLS LCURL VNAME LCURL {
	printf(") (%s(", $10);
} elements RCURL EQUALS NUMBER RCURL {
	printf(") = %s)\n", $16);
};

elements: 
				|	VNAME	{ if(printme) printf("%s", $1); } 
				|	VNAME COMMA { if(printme) printf("%s, ", $1); } elements;

poly_rep: VNAME LCURL { printme = false; } elements { printme = true; } RCURL POINTS LBRACE { printme = false; } elements { printme = true; } PIPE loops RBRACE {
        printIndent();
        printf("%s()\n", $1);
        indentLevel = 0;
      };

loops: loop 
		 | loop SEMICOLON loops
		 ;

loop: NUMBER COMPARATOR VNAME COMPARATOR VNAME COLON NUMBER {
  /*TODO: FIX THE INITIALISATION*/
  printIndent();
  printf("for(%s = %s; %s %s %s; %s += %s)\n", $3, $1, $3, $4, $5, $3, $7);
  indentLevel++;
}
    | NUMBER COMPARATOR VNAME COMPARATOR NUMBER COLON NUMBER {
  /*TODO: FIX THE INITIALISATION*/
  printIndent();
  printf("for(%s = %s; %s %s %s; %s += %s)\n", $3, $1, $3, $4, $5, $3, $7);
  indentLevel++;
};

%%

void yyerror(const char *err) {
	fprintf(stderr, "error: %s", err);
	exit(-1);
}

void printIndent() {
  for (int i = 0; i < indentLevel; i++)
    printf("    ");
}

int main() {
	yyparse();
  printf("}\n");
}
