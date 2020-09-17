%{
#include <stdlib.h>
#include "y.tab.h"
%}

%option noyywrap

%%
"<"[hH][tT][mM][lL]">"	{ return HTMLTAG; }
"<"[bB][oO][dD][yY]">"	{ return BODYTAG; }
"<"[bB]">"							{ return BTAG; }
"<"[dD][iI][vV]">"			{ return DIVTAG; }
"<"[hH][eE][aA][dD]">"	{ return HEADTAG; }
"<"[iI]">"							{ return ITAG; }
"<"[pP]">"							{ return PARATAG; }
"<"[tT][iI][tT][lL][eE]	{ return TITLETAG; }
"<"[uU]">"							{ return UTAG; }

[\s]+									{ return STRING; }
.										;
%%

