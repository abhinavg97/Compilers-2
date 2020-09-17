-- basic claculator to add , subtract , multiply and divide 2 integers

class Calc inherits IO{
	i : Int <- 0;
	j : Int <- 0;
	init() : Object{
		{
			out_string("Enter the two numbers : \n");
			i<-in_int();
			j<-in_int();
		}	
	};
	add() :Int {
		{
			j+i;
		}
	};

	subtract() : Int {
		i-j
	};

	mul() :Int {
		i*j
	};

	div() :Int {
		i/j
	};
};

class Main inherits IO{

	choice :Int ;
	cal : Calc<-new Calc ;
	main() : Object {
		{ --  Nested expression are presented here
			while(true)
			loop{
				out_string("\n1.Add\n2.Sub\n3.mul\n4.Div\n5.Exit\nEnter the option:");
				choice<-in_int();
				if (choice =1)
					then 
					{
						cal.init();
						out_int(cal.add());	
					}
				else if (choice=2) 
					then 
					{
						cal.init();
						out_int(cal.subtract());				
					}
				else if(choice =3)
					then 	
					{
						cal.init();  
						out_int(cal.mul());		-- demonstrates dispatch		
					}

				else if(choice =4)
					then
					{
						cal.init();
						out_int(cal.div());				
					}
				else abort()
				fi fi fi fi;
			}
			pool;
		}
	};
};

 