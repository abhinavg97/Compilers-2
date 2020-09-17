
class Main inherits IO {

	main() : Int {
		1
	};

	method(i : Int, i : Int) : String {	-- list of formals. And the return type of the method.
			let a : Int, 
					b : Int <- (new Crash),
					c : Int <- 0
			in
			{
			fa <- (while (not i) loop	-- expressions are nested.
			{
				c <- 2;
				i <- i - 1;
				(new Object) = a;
				b= (new Object);
			}
			pool);
			c;
			}
	};

};
