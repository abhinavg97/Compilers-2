#codegen

The project was divided into 4 blocks.

1. Constants

The global constants which is same for all the files appear here.
These include target datalayout ,target triple,@Abortdivby0,@Abortdispvoid.

We declare a few helper c method functions which can be used for IO, and for string operations , such as strlen,strcpy etc.
-------------------------------------------------------------------------------------------------------------------------------------------------------
2. Class declarations

We declare the classes with appropriate types here.
-------------------------------------------------------------------------------------------------------------------------------------------------------
3. Method declarations 

We mangle the name of the methods specific to each class here.
-------------------------------------------------------------------------------------------------------------------------------------------------------
4. String Constants

We create a counter for each unique string apperaing throughout the program, we will use this counter later for using the string.
-------------------------------------------------------------------------------------------------------------------------------------------------------
5. Method definitions

This is the main part of the program , where we define all the methods of all the classes.

We go though all the classes and one by one define all the methods.
Inside method definition , we porcess all the expressions contained in it.
Note : We are not handling dispatch,let,case expressions in our program, any occurances of such types will be ignored.

We use a global variable flag to know if the the expression encountered is an object or a literal.
We use global variables returnvalue to store the value , incase the expression is a literal ,to store register in case it is an object
.
-------------------------------------------------------------------------------------------------------------------------------------------------------
Handling of each expression:

1. neg,comp,leq,lt,eq,plus,sub,mul,divide

If all the operands are literals we directly compute the expression and set the returnval.

neg,comp
We generate LLVM IR instructions(see the code) to evaluate the expression at runtime if one of teh operands was a register.

leq,lt,eq
We generate icmp instructions of the llvm IR to do runtime evaluation incase one of the operands of the expression was a register

plus,sub,mul,divide
We use LLVM-IR instructions add,sub,mul,div to evaluate the expression at runtime .
We abort on divison by zero if the value of the second operand in div was 0.

2. int_const
We set returnval and set the flag to 1

3. string_const
We set the returnstr and set the flag to 4

4. bool_const

We set the returnval to 0 if false and 1 if true , and set the flag to 3

5. obj

We set the flag to 2 and set the returnval to the regoster allocated to the object.

 


