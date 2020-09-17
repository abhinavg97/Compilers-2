class Class1 {
};

class Class2 inherits Class1{
	func(ob : Class2) : Int 
	{
		1
	};
};

class Main inherits IO 
{
	main() : Object 
	{
		{
			out_int(prod(in_int()),2);
			out_string("\n");
			case (new Class1) of
				l : Class1 => (new Class2); 
				f : Class2 => ((new Class2).func(f));
			esac;
		}
	};

};
