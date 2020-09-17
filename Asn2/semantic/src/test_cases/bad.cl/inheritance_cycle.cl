

class Main inherits IO{

	x :Int;

};

class Bodol1 inherits Defined1{
	x : Notdefined <- new 
	Notdefined;
	f(x : Int, z : String) : Int {
		1
	};
	y : Int;
};

class Defined1 inherits Bodol1{
	x : Notdefined <- new 
	Notdefined;
	f(x : Int, z : String) : Int {
		1
	};
	y : Int;
};
