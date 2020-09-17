
-- inheritance , type mistach and other errors
-- full description of the erorrs can be seen upon running it through a semantic analyzer

class Main inherits IO {
	main1() : Int {
		
		1 +"gtgyhu"
	};
	a: Undefined;
	a:Int;
	a:Bool;
	main2(): Undefined {
		self
	};

	func():Int{
		1
	};

};

class Inherit inherits Main{
	a:Bool;
	multiple():Int{
		1
	};
	multiple():Object{
		self
	};

	func():Bool{
		true
	};
};

class Inherit2 inherits Inherit{
	func(a:Int):Int{
		1
	};
};

class Inherit3 inherits Inherit2{

	b:Int <- "hello";
	func(a:Bool,a:Int):Int{
		"String"
	};
	d :Int <- d@Bash.f();
	e : Int;
	f :Int <- e@Inherit.func();
	g :Inherit2 ;
	h : Int <- g@Inherit2.func(1,2);
	i :Int <- g@Inherit2.func(true);
	k :Int <- g@Inherit2.undefinedmethod();
};

class Inherit4 inherits Inherit3{

	func1(a:Bool,b:Int):Int{
		1
	};


	var1 :Int <-func1(1,2,3);
	var2 : Int <-func1(1,"string");
	var3 : Int <- nofunc();
	
	var15:Int<-(var1<new Inherit2);
	var16:Int<-(var1<=new Inherit2);

	var17:Bool<-not("string");
	func2():Int{

		{

			notdefined<-3;
			var17<-"String";

			if("string") then
				out_string("Heya!")
			else
				out_int(2)
			fi;
			while "string" loop
			{
				5;
			}
			pool;
			2;
		}

	};

};