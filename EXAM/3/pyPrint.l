%{
#include <stdlib.h>
#include "y.tab.h"
%}

%option noyywrap

DIGIT	[0-9]
NUM	DIGIT+
INT	-?NUM



%%
\{	{ return LBRACE; }
:	{ return COLON; }
,	{ return COMMA; }
\[	{ return LBRAC; }
\(	{ return LCURL; }
\]	{ return RBRAC; }
\}	{ return RBRACE; }
\)	{ return RCURL; }

-?[0-9]+(e-?[0-9]+)? { yylval = strdup(yytext); return NUMBER; }
(-?[0-9]+)?\.[0-9]+(e-?[0-9]+)? { yylval = strdup(yytext); return NUMBER; }
[\"\'][a-zA-Z0-9_]+[\"\'] { yytext[0] = 39; yytext[strlen(yytext) - 1] = 39; yylval = strdup(yytext); return STRING; }

[  \t]	;
%%

