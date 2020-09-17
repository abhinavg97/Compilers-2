General Points
***************

A total of four passes were made throught the code, the first 2 being shallow passes , going through higher level code(i.e,classes) to eliminate obvious errors.Later 2 passes makes up the crux of the code , where we report intricate errors and annotate the AST.
Note : SELF_TYPE is not handled, so if SELF_TYPE appears in a test case , the code might output unexpected results

==============================================================================================================================================

Rough description of what is done:
**********************************
==============================================================================================================================================

First  pass 
~~~~~~~~~~~~~
This pass eliminates programs if they contain obvious (fatal) errors:

i)Basic class is redfined.
ii)Class is redefined.
iii)Class is inherited from a uninheritable class as mentioned in the cool manual.

In this pass:

Inheritance graph is built

We use the following:
idclsnm              -      Assigning each class a unique id and getting the name of teh class from the unique id
classids             -      getting the id of the class from the class name
clsnmC               -      getting the class object from the class name
graph                -      graph for storing the integers(mapped to unique classes)
parent               -      getting the parent class name from a child class name
class_attrs          -      to store the class attributes of a class 
class_attrs_order.   -      to keep the order of the attributes stored in a particular class
class_methods        -      to store the class methods of the class

After this pass we check if main class is defined.

Second  pass:
~~~~~~~~~~~~~~~

This pass helps in later stages to avoid confusion of inheriting from an undefined class.
Checking if all the parent classes are defined, this can be done only after the first pass

We check for cycles after the second pass and report all the inheritance errors using a bfs search , exitting only after traversing the entire graph.

Third pass:
~~~~~~~~~~~~

This pass checks the features(methods and attributes) of a class for their type correctness.
Errors are reported on ill defined methods and attributes of all the classes.


Attribute redifinition:
-----------------------

Attributes are checked if they are inherited or are redefined in a class.
Attributes are checked for conformance with their declaration type.
All the attributes are stored in the hasmap .Errors are appropriately reported.

Method Redifinition:
---------------------

All the methods are stored in the hasmap. 
They are checked for type safety if a parent class contain the method. (this is also done in staic dispatch and dispatch expressions)
Methods are checked if they redfined  in the same class.

Other errors are also checked which can gleaned from the comments.

Fourth Pass:
~~~~~~~~~~~~~

This pass checks for expression validity inside the methods and type annotating the AST.
A dfs is run over all the classes starting form the root node .
There was no particular reason for choosing dfs, a bfs would have also have completed our job.
Appropraite errors are reported if an expression is ill defined.

==============================================================================================================================================

Few crucial contructs :
*********************
==============================================================================================================================================
LCA
~~~~

It helps in finding whether two expressions are of the same type or not , this is used extensively , to check for type conformance.

Attribute checks:
~~~~~~~~~~~~~~~~~

If a attribute is redefined in a class or its inherited class , both the attributes are stored in hashmap and appropriate error message is printed.

Method checks:
~~~~~~~~~~~~~~~

If a method is redefined in a class , both the methods are stored in a hashmap and errors are appropriately reported.
We conform with the type safety of cool for method checking in case it is overided from an inherited class.
The formal parameters are checked if they are redefined. The type of the formal parameters are conformed with their declared type in the defined method.

Note: 
-----
The duplicates are stored for methods in order to check for expression errors within each method to have a robust error reporting mechanism.

Duplicate attributes are stored not for a special reason , but I had to do this , in order to maintain an order in which attributes are stored.

In hashmap order is not maintained .I associated attribute names to 2 hashmaps ,one with a unique integer and one with the attribute.
The details can be gleaned from the comments in the code.
Details of this can be seen in the code.

P.S : Other methods would have made the job easier , but I went with this ,as this idea struck me first.

==============================================================================================================================================

Design Decisions:
*****************
==============================================================================================================================================
The type of the expression are annotated as mentioned in the cool manual.
----------------------------------------------------------------------------------------------------------------------------------------------

Dispatch and static dispatch expression:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In dispatch , the youngest parent in which the method is defined is chosen.


In static dispatch , we check if the method is defined in the class it is called from.
Both in dispatch and static dispatch , we type check the parameters once a corresponding method is found.
The return type of the method is annotated if a method is found and object is returned , if  no method is found.

Case expression:
~~~~~~~~~~~~~~~~

We check if a branch is redifined.
The type of the branch expression is the "oldest" class from which return types of individual branches may be derived.

Assign expression:
~~~~~~~~~~~~~~~~~

We use the scopetable to check if the attribute is in scope of the assign expression .
Error is reported if is not scope.
Type conformance of left and right expressions are checked.

Object Expression:
~~~~~~~~~~~~~~~~~~

We use scopetable to check if the attribute is in scope , the type of the attribute is returned.

Let expression:
~~~~~~~~~~~~~~~

We use scope table to enter a new scope and store the attributes defined in the let expression there.
We check for type correctness of the expressions of the let expression in this new scope.
The return type of the let expression is the return type of the body of the let expression , i.e the type of the  last  let expression.

==============================================================================================================================================

An attribute can be introduced in 4 ways in cool:

1.let

2.Branch

3.Mehod fromals

4. Class formals


A scope is created whenever the above are encountered and the corresponding attributes are used when these atributes are encountered inside the scope.

==============================================================================================================================================

Good test cases:

A few basic programs have been taken from the examples , as they check thoroughly the constructs in cool.

Bad test cases:

An attempt has been made to generate a complete list of errors which the semantic analyzer can produce.

==============================================================================================================================================
