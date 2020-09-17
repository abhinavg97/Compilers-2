(*
Nested let expressions
*)
(* The parse successfully parses this file creating a hirarchy of the let expression*)


class NoFeature {	-- class can have no features. 
};


Class Main inherits IO{


	main() :Object{
		{
			
			(let a:Int <- 4, b:Int <- 6 in 
			{ 
				let c:Int <- 5,d:Int <- 9 in { 8;};
			} 
			);
		}
	};
};

