%{
#include <stdio.h>
int indentLevel = 0;
%}

/*
	ASSUMPTIONS: BODY HAS SEVERAL DIVS. NO DIRECT P ALLOWED
*/

%token BODYTAG BTAG DIVTAG HEADTAG HTMLTAG ITAG PARATAG STRING TITLETAG UTAG

%%

html: HTMLTAG {
			printf("<html>\n");
			indentLevel++;
		} head {
			indentLevel--;
			printf("</html>\n");
		} 
	;
head: HEADTAG {
			printId();
			printf("<head>\n");
			indentLevel++;
		} title	body {
			indentLevel--;
			printId();
			printf("</head>\n");
		}
	;
title: TITLETAG {
			printId();
			printf("<title>\n");
		} words {
			printf("</title>\n");
		}
		 ;
words: 
		 |	STRING words
		 ;

body: BODYTAG divs
		;
divs: 
		| div divs
		;
div: DIVTAG paras
	 ;
paras:
		 | para paras
		 ;
para:	PARATAG paracontent
		;
paracontent:
					 |	words spword paracontent
					 ;
spword:	UTAG STRING 
			|	ITAG STRING 
			| BTAG STRING 
			;



%%
void yyerror(const char* a){
	fprintf(stderr, "%s\n", a);
	exit(-1);
}

void printId() {
	for (int i = 0; i < indentLevel; i++)
		printf("  ");
}

int main(void) {
    yyparse();
    return 0;
}
