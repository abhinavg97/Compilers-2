(*
Given a number prints the binary represtation of it
*)
(* The parse successfully parses this file *)
Class Main inherits IO{
	bin :Bin <- (new Bin);
	n :Int;
	main() :Object{
		{
			out_string("Enter the number:");
			n<-in_int();
			out_string("The binary rep is : ");
			out_int(bin.print(n));
			out_string("\n");
		}
	};
};


Class Bin inherits IO{

	s : Int <- 0;
	c : Int <- 1;
	temp : Int;
	print(n:Int) :Int
	{
		{
			temp<-n;
			while 0<temp
			loop
			{
				if ((temp/2)*2 = temp) -- to check if the number is even or odd
				then s<-s	
				else s<-s+c
				fi;
				temp<-temp/2;
				c <- c*10;
			}
			pool;
			out_string("\n");
			out_int(s);
			out_string("\n");
			s;
		}
	};
};
