%{
#include "y.tab.h"
%}
%option noyywrap


%%

-?[0-9]+	{
	yylval = strdup(yytext); 
	return NUMBER;
}

Let		{
	return LET;
}

[A-Za-z0-9_]+	{
	yylval = strdup(yytext);
	return VNAME;
}

\<\=?	{
	yylval = strdup(yytext);
	return COMPARATOR; 
}


:=	{ return DECLS; }
\(	{ return LCURL; }
\)	{ return RCURL; }
=		{ return EQUALS; }
,		{ return COMMA; }
\|	{ return PIPE; }
:		{ return COLON; }
;		{ return SEMICOLON; }
->	{ return POINTS; }
\{	{ return LBRACE; }
\}	{ return RBRACE; }



\/\/.*\n?	;
[ \t\n]	;
%%

