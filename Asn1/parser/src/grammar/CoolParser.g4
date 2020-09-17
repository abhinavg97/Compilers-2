parser grammar CoolParser;

options {
	tokenVocab = CoolLexer;
}

@header{
	import cool.AST;
	import java.util.List;
}

@members{
	String filename;
	public void setFilename(String f){
		filename = f;
	}

/*
	DO NOT EDIT THE FILE ABOVE THIS LINE
	Add member functions, variables below.
*/

}

/*
	Add Grammar rules and appropriate actions for building AST below.
*/

// Grammar for Cool , taken from cool manual and the actions are added appropriately to build an AST.

program returns [AST.program value] :
	cl=class_list EOF 
	{
		$value = new AST.program($cl.value, $cl.value.get(0).lineNo);
	}
	;

class_list returns [ArrayList<AST.class_> value]
	/* [class]+ */
	@init
	{
		$value = new ArrayList<AST.class_>();
	}
	:
	(c = class_ SEMICOLON {$value.add($c.value);})+
	;

class_ returns [AST.class_ value] :
	/* class TYPE [inherits TYPE] {[[feature]]*}	*/
	cls=CLASS type=TYPEID INHERITS parent_type=TYPEID LBRACE fl=feature_list RBRACE
	{
		$value = new AST.class_($type.getText(), filename, $parent_type.getText(), $fl.value, $cls.getLine());
	}
	| cls=CLASS type=TYPEID LBRACE fl=feature_list RBRACE
	{
		/* using Object as the parent_type.getText() value to conform with the class_ method in AST.java */
		$value = new AST.class_($type.getText(), filename, "Object", $fl.value, $cls.getLine());
	}
	;

feature_list returns [ArrayList <AST.feature> value]
	/* [feature;]* */
	@init
	{
		$value = new ArrayList<AST.feature>();
	}
	:
	(c=feature SEMICOLON {$value.add($c.value);})*
	;

feature returns [AST.feature value]:
	/* method|attribute */
	// we are not using the feature method as defined in AST.java file as it consists of attribute or method and those methods are used instead to return the appropriate value
	function=method
	{
		$value=$function.value;
	}
	| attribute=attr
	{
		$value=$attribute.value;
	}
	;

attr returns [AST.attr value]:
	/* ID: TYPE [<- expr] */
	id=OBJECTID COLON type=TYPEID ASSIGN expr=expression
	{
		$value = new AST.attr($id.getText(), $type.getText(), $expr.value, $id.getLine());
	}
	|
	id=OBJECTID COLON type=TYPEID
	{
		// creating a dummy expression from the no_expr method of AST.java to conform with the attr method of AST.java
		$value = new AST.attr($id.getText(), $type.getText(), new AST.no_expr($id.getLine()), $id.getLine());
	}
	;

method  returns [AST.method value]:
	/* ID([formal [,formal]*]) : TYPE {expr} */
	id=OBJECTID LPAREN fl=formal_list RPAREN COLON type=TYPEID LBRACE expr=expression RBRACE
	{
		$value = new AST.method($id.getText(), $fl.value, $type.getText(), $expr.value, $id.getLine());
	}
	|
	id=OBJECTID LPAREN RPAREN COLON type=TYPEID LBRACE expr=expression RBRACE
	{
		// creating a dummy form,al list to be passed to conform with the method method() given in AST.java file
		$value = new AST.method($id.getText(), new ArrayList<AST.formal>(), $type.getText(), $expr.value, $id.getLine());
	}
	;


formal_list returns [ArrayList<AST.formal> value]
	/* formal [,formal]* */
	@init
	{
		$value = new ArrayList<AST.formal>();
	}
	:
		c = formal {$value.add($c.value);} 

		(COMMA y = formal {$value.add($y.value);})*
	;

formal  returns [AST.formal value]:
	/* ID : TYPE */
	id=OBJECTID COLON type=TYPEID
	{
		$value = new AST.formal($id.getText(), $type.getText(), $id.getLine()) ;
	}
	;

expression_list returns [ArrayList<AST.expression> value]
	/* [expr [[, expr]]* ] */
	@init
	{
		$value = new ArrayList<AST.expression>();
	}
	:
		(expr=expression {$value.add($expr.value);} (COMMA  expr=expression {$value.add($expr.value);})* )?
	;



expr_list returns [ArrayList<AST.expression> value]
	@init
	{
		$value = new ArrayList<AST.expression>();
	}
	:
		(expr = expression SEMICOLON {$value.add($expr.value);})+
	;


branch_list returns [ArrayList<AST.branch> value]
	/* [branch ;]+ */
	@init
	{
		$value = new ArrayList<AST.branch>();
	}
	:
		(br = branch SEMICOLON {$value.add($br.value);})+
	;

branch returns [AST.branch value] :
	/* ID : TYPE => expr */
	id=OBJECTID COLON type=TYPEID DARROW expr=expression
	{
		$value = new AST.branch($id.getText(), $type.getText(), $expr.value, $id.getLine());
	}
	;


let_list returns [ArrayList<AST.attr> value]
	/* attr [, attr]* */
	@init
	{
		$value = new ArrayList<AST.attr>();
	}
	:

		c = attr { $value.add($c.value); }

		(COMMA y = attr {$value.add($y.value);})*
	;


expression  returns [AST.expression value]: 
			// expr[@TYPE].ID([expr [[, expr]]* ]) 	
			expr=expression DOT id=OBJECTID LPAREN  exp_list=expression_list RPAREN 
			{
				$value = new AST.dispatch($expr.value, $id.getText(), $exp_list.value, $id.getLine());
			}
			| expr=expression ATSYM type=TYPEID DOT id=OBJECTID LPAREN  exp_list=expression_list RPAREN 
			{
				$value = new AST.static_dispatch($expr.value, $type.getText(), $id.getText(), $exp_list.value, $id.getLine());
			}
			/*ID ( [expr [[, expr]]* ]) */
			| id=OBJECTID LPAREN exp_list=expression_list RPAREN
			{ // we are using object method as a dummy expression to conform with the method of dispatch in AST.java file
				$value = new AST.dispatch(new AST.object("self" , $id.getLine()) , $id.getText() , $exp_list.value , $id.getLine()); 
			}  			
			/* ~ expr */
			| st=TILDE expr=expression
			{
				$value = new AST.comp($expr.value, $st.getLine());
			}
			/* isvoid expr */
			| st=ISVOID expr=expression
			{
				$value = new AST.isvoid($expr.value, $st.getLine());
			}
			/* expr * expr */
			| exp1=expression STAR exp2=expression
			{
				$value = new AST.mul($exp1.value, $exp2.value, $exp1.value.lineNo);
			}
			/* expr / expr */
			| exp1=expression SLASH exp2=expression
			{
				$value = new AST.divide($exp1.value, $exp2.value, $exp1.value.lineNo);
			}
			/* expr + expr */
			| exp1=expression PLUS exp2=expression
			{
				$value = new AST.plus($exp1.value, $exp2.value, $exp1.value.lineNo);
			}
			/* expr - expr */
			| exp1=expression MINUS exp2=expression
			{
				$value = new AST.sub($exp1.value, $exp2.value, $exp1.value.lineNo);
			}
			/* expr < expr */
			| exp1=expression LT exp2=expression
			{
				$value = new AST.lt($exp1.value, $exp2.value, $exp1.value.lineNo);
			}
			/* expr <= expr */
			| exp1=expression LE exp2=expression
			{
				$value = new AST.leq($exp1.value, $exp2.value, $exp1.value.lineNo);
			}
			/* expr = expr */
			| exp1=expression EQUALS exp2=expression
			{
				$value = new AST.eq($exp1.value, $exp2.value, $exp1.value.lineNo);
			}
			/* not expr */
			| st=NOT expr=expression
			{
				$value = new AST.neg($expr.value, $st.getLine());
			}
 			/* ID <- expr */	
			|id=OBJECTID ASSIGN expr=expression 
			{
				$value = new AST.assign($id.getText(), $expr.value, $id.getLine());  
			}
			/* if expr then expr else expr fi*/
			| st=IF predicate=expression THEN ifbody=expression ELSE elsebody=expression FI
			{
				$value = new AST.cond($predicate.value, $ifbody.value, $elsebody.value, $st.getLine());

			} 
			/* while expr loop expr pool */
			| st=WHILE predicate=expression LOOP body=expression POOL
			{
				$value = new AST.loop($predicate.value, $body.value, $st.getLine());
			}
			// { [[expr]]* } //
			| st=LBRACE el=expr_list RBRACE
			{
				// block method as defined in AST.java file
				$value = new AST.block($el.value, $st.getLine());
			}
	
			// let ID:TYPE [[, ID : TYPE [<- expr]]]* in expr //
			| st=LET ll=let_list IN expr=expression
			{
				$value = $expr.value; 
				AST.attr current_atr;
				int i = $ll.value.size() - 1;
				while(i>=0)
				{
					current_atr = $ll.value.get(i);
					$value = new AST.let(current_atr.name, current_atr.typeid,current_atr.value, $value, $st.getLine());
					i--;
				}
			}
			// case expr of [[ID : TYPE => expr;]]+ esac //
			| st=CASE predicate=expression OF bl=branch_list ESAC
			{
				$value = new AST.typcase($predicate.value, $bl.value, $st.getLine());
			}
			/* new TYPE */
			| st=NEW type=TYPEID
			{
				$value = new AST.new_($type.getText(), $st.getLine());
			}
			/* (expr)	*/
			| LPAREN expr=expression RPAREN
			{
				$value = $expr.value;
			}
			/* ID */	
			| id=OBJECTID
			{
				$value = new AST.object($id.getText(), $id.getLine());
			}
			/* integer */
			| var=INT_CONST
			{ // java method Integer.parseInt is used here to convert string to int conforming to the function defined in AST.java file
				$value = new AST.int_const(Integer.parseInt($var.getText()), $var.getLine());
			}
			/* string */
			| var=STR_CONST
			{
				$value = new AST.string_const($var.getText(), $var.getLine());
			}
			/* true / false */
			| var=BOOL_CONST
			{
				$value = new AST.bool_const($var.getText().charAt(0)=='t', $var.getLine());
			}
			;