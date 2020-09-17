# COOL Compiler #
Readme for parser assignment

Task: 
Build an AST for valid programs written in COOL.

We divide this Readme into the following subcategories:
1.Getting started
2.Using lists
3.Conforming with the methods 
4.Precedence Rules
5.General complexities encountered
6.Test cases

-----------------------------------------------------------------------------------------------------------------------------------------
1.Getting started:

As we have done only parsing in the previous assignment, checking whether the grammar conforms with the rules given in the cool manual , we just need to modify the previous CoolParser.g4 antlr file to now store the AST (i.e if the program is lexically and gramatically correct).

We do this using the below given functions already defined to us in AST.java file.
Each rule returns a value which was of the type AST.class, where class is the nearest parent class of all the sub rules involved.
These methods return a object of the type AST.class and it is these objects that create the various nodes of the AST parse tree.  

public expression()
public feature()
public no_expr(int l)
public bool_const(boolean v, int l)
public string_const(String v, int l)
public int_const(int v, int l)
public object(String v, int l)
public comp(expression v, int l)
public eq(expression v1, expression v2, int l)
public leq(expression v1, expression v2, int l)
public lt(expression v1, expression v2, int l)
public neg(expression v, int l)
public divide(expression v1, expression v2, int l)
public mul(expression v1, expression v2, int l)
public sub(expression v1, expression v2, int l)
public plus(expression v1, expression v2, int l)
public isvoid(expression v, int l)
public new_(String t, int l)
public assign(String n, expression v1, int l)
public loop(expression v1, expression v2, int l)
public cond(expression v1, expression v2, expression v3, int l)
public let(String n, String t, expression v, expression b, int l)
public block(List<expression> v1, int l)
public dispatch(expression v1, String n, List<expression> a, int l)
public static_dispatch(expression v1, String t, String n, List<expression> a, int l)
public typcase(expression p, List<branch> b, int l)
public branch(String n, String t, expression v, int l)
public formal(String n, String t, int l)
public method(String n, List<formal> f, String t, expression b, int l)
public attr(String n, String t, expression v, int l)
public class_(String n, String f, String p, List<feature> fs, int l)
public program(List<class_> c, int l)

-----------------------------------------------------------------------------------------------------------------------------------------
2.Using lists:

We need to store the value of each node when we parse context that match +,* rules of the cool grammar.
We achieve this by using a different parsing rule(lists) to parse and store the values of each node to build the AST. 
Lists such as branch_list,expr_list,expression_list,formal_list,feature_list,class_list are used .
We embed the action within the parsing rule so that the parent rule  maps to each class when generating the AST.If the action was defined outside the list, only a link to the last child (in this case the last class) would be maintained by the parser.
-----------------------------------------------------------------------------------------------------------------------------------------
3.Conforming with the methods :

We have used appropriate dummy variables to pass in the methods mentioned above to conform with the method to handle optional entities.
We have mentioned the same in the comments in the CoolParser.g4 file , below is the complete list of methods where we have created dummy variables.

new AST.class_($type.getText(), filename, "Object", $fl.value, $cls.getLine());
new AST.attr($id.getText(), $type.getText(), new AST.no_expr($id.getLine()), $id.getLine());
new AST.method($id.getText(), new ArrayList<AST.formal>(), $type.getText(), $expr.value, $id.getLine());
new AST.dispatch(new AST.object("self" , $id.getLine()) , $id.getText() , $exp_list.value , $id.getLine()); 

We have converted the context parsed into appropriate type before passing to the method int_const() , we have used java's Integer.parseInt() method for this purpose.

token.getLine() is used to get the lineNo ,token.getText() to get the string if the parsed value is a token, and we use expression.value.lineNo if it is an expression to be passed as parameters for the above mentioned methods.
-----------------------------------------------------------------------------------------------------------------------------------------
4.Precedence Rules:

Precedence can be enforced as ANTLR guarantees to match the first rule in the grammar to expression.
The prcedence of operators as mentioned in the cool manual section 11:

	.
	@
	~
	isvoid
	* /
	+ -
	<= < =
	not
	<-

	we conform by this precedence order while writing the expression rules.
-----------------------------------------------------------------------------------------------------------------------------------------
5.General complexities encountered:

In let expression , we have to map several attributes to one . For this recursive technique is used , and what better option than loops that can be used (we have used loops!). The let expression of every next let expression becomes the body of the previous one.
-----------------------------------------------------------------------------------------------------------------------------------------
6.Test cases :

In the test cases, several cases of lexical and parsing errors are taken care of.
In lexical analysis , unterminated string constants , unescaped newlines , EOF in comments , EOF in string constants,escaped backslashes are taken care of .
In parsing , grammar errors like replacing object id with type id , missing semicolons , missing brackets and other grammar rules are taken care of.
Nested expressions, loops are included in the test cases to check whether the AST built is correct.

-----------------------------------------------------------------------------------------------------------------------------------------

