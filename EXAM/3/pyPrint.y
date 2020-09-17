%{
#include <stdio.h>
#include <stdbool.h>
int indentLevel;
bool isRVal = false;
%}

%token LBRACE COLON COMMA LBRAC LCURL NUMBER RBRAC RBRACE RCURL STRING

%%

pyprint: value;

value:	STRING	{ printId(); printf("%s", $1); }
		 |	NUMBER	{ printId(); printf("%s", $1); }
		 |	list		
		 |	tuple		
		 |	dict		
		 ;
list:	LBRAC {
			if(!isRVal)
				printId();
			printf("[\n");
			indentLevel++;
		} value_list RBRAC { 
			indentLevel--;
			printId(); 
			printf("]");
		}
		;
tuple: LCURL {
			if(!isRVal)
				printId();
			printf("(\n");
			indentLevel++;
		} value_list RCURL { 
			indentLevel--;
			printId(); 
			printf(")");
		}
		 ;
dict:	LBRACE {
			if(!isRVal)
				printId();
			printf("{\n");
			indentLevel++;
		} pair_list RBRACE {
			indentLevel--;
			printId(); 
			printf("}");
		}
		;
value_list: value { printf("\n"); }
					|	value COMMA { printf(",\n"); } value_list
					;
pair_list: pair { printf("\n"); }
				 | pair COMMA { printf(",\n"); } pair_list
				 ;
pair: value {
			isRVal = true;
		} COLON { 
			isRVal = false;
			printf(":"); 
		} value
		;


%%
void yyerror(const char* a){
	fprintf(stderr, "%s\n", a);
	exit(-1);
}

void printId() {
	for(int i = 0; i < indentLevel; i++)
		printf("    ");
}

int main(void) {
    yyparse();
    return 0;
}
