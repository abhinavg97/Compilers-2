package cool;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.Map;
import cool.AST.class_;


public class Semantic{
	private boolean errorFlag = false;
	private Map<String, String> parent = new HashMap<String, String>();

	private ArrayList < ArrayList <Integer> > graph = new ArrayList < ArrayList <Integer> >();
	private	HashMap <String, AST.class_> clsnmC = new HashMap <String, AST.class_> ();
	private	HashMap <Integer, String> idclsnm = new HashMap <Integer, String>();
	private	HashMap <String, Integer> classids = new HashMap <String, Integer> ();
	private ScopeTable<AST.attr> table = new ScopeTable<AST.attr>();
	private Map<String, Map<String, List<AST.formal>>> base_classes= new HashMap<String, Map<String, List<AST.formal>>>() ;
	private Map<String,Map<AST.method,String>> class_methods = new HashMap<String,Map<AST.method,String>>() ;
	private Map<String,Map<AST.attr,String>> class_attrs = new HashMap<String,Map<AST.attr,String>>();

	private Map<String,Map<Integer,String>> class_attrs_order = new HashMap<String,Map<Integer,String>>();

	private	HashMap<String, AST.method> main_methods=new HashMap<String, AST.method>();
	private int a = 1;
	private String filename ;
	public void reportError(String filename, int lineNo, String error){
		errorFlag = true;
		System.err.println(filename+":"+lineNo+": "+error);
	}
	public boolean getErrorFlag(){
		return errorFlag;
	}

/*
	Don't change code above this line
*/

	ScopeTable<AST.attr> scopeTable = new ScopeTable<AST.attr>();
	public Semantic(AST.program program){
		//Write Semantic analyzer code here
		InheritanceGraph(program.classes);
	}

	public void get_baseclasses()
	{
		AST.no_expr noex=new AST.no_expr(1);

		//BASIC METHODS OF OBJECT
		base_classes.put("Object", new HashMap<String,List<AST.formal>>());
		base_classes.get("Object").put("abort", new LinkedList<AST.formal>());
		base_classes.get("Object").put("type_name", new LinkedList<AST.formal>());
		base_classes.get("Object").put("copy", new LinkedList<AST.formal>());

		//BASIC METHODS OF IO
		base_classes.put("IO", new HashMap<String,List<AST.formal>>());
		base_classes.get("IO").put("out_string", new LinkedList<AST.formal>());
		base_classes.get("IO").get("out_string").add(new AST.formal("a","String",1));
		base_classes.get("IO").put("out_int", new LinkedList<AST.formal>());
		base_classes.get("IO").get("out_int").add(new AST.formal("a","Int",1));
		base_classes.get("IO").put("in_int", new LinkedList<AST.formal>());
		base_classes.get("IO").put("in_string", new LinkedList<AST.formal>());

		//BASIC METHODS OF STRING
		base_classes.put("String", new HashMap<String,List<AST.formal>>());
		base_classes.get("String").put("length", new LinkedList<AST.formal>());
		base_classes.get("String").put("concat",new LinkedList<AST.formal>());
		base_classes.get("String").get("concat").add(new AST.formal("a","String",1));
		base_classes.get("String").put("substr",new LinkedList<AST.formal>());
		base_classes.get("String").get("substr").add(new AST.formal("a","Int",1));
		base_classes.get("String").get("substr").add(new AST.formal("b","Int",1));

		//BASIC METHODS OF INT, BOOL: NONE
		base_classes.put("Int", new HashMap<String,List<AST.formal>>());
		base_classes.put("Bool", new HashMap<String,List<AST.formal>>());

		
		class_methods.put("Object",new HashMap<AST.method,String>());
		class_methods.get("Object").put(new AST.method("abort",base_classes.get("Object").get("abort"),"Object", noex,1),"abort");
		class_methods.get("Object").put(new AST.method("type_name",base_classes.get("Object").get("type_name"),"String", noex,1),"type_name");
		class_methods.get("Object").put(new AST.method("copy",base_classes.get("Object").get("copy"),"Object", noex,1),"copy");

		class_methods.put("IO",new HashMap<AST.method,String>());
		class_methods.get("IO").put(new AST.method("out_string",base_classes.get("IO").get("out_string"),"IO",new AST.no_expr(1),1),"out_string" );
		class_methods.get("IO").put(new AST.method("out_int",base_classes.get("IO").get("out_int"),"IO",new AST.no_expr(1),1),"out_int");
		class_methods.get("IO").put(new AST.method("in_int",base_classes.get("IO").get("in_int"),"Int",new AST.no_expr(1),1),"in_int" );
		class_methods.get("IO").put(new AST.method("in_string",base_classes.get("IO").get("in_string"),"String",new AST.no_expr(1),1),"in_string");

		class_methods.put("String",new HashMap<AST.method,String>());
		class_methods.get("String").put(new AST.method("length",new LinkedList<AST.formal>(),"Int",new AST.no_expr(1),1),"length");
		class_methods.get("String").put(new AST.method("concat",base_classes.get("String").get("concat"),"String",new AST.no_expr(1),1),"concat");
		class_methods.get("String").put(new AST.method("substr",base_classes.get("String").get("substr"),"String",new AST.no_expr(1),1),"substr");

		class_methods.put("Int",new HashMap<AST.method,String>());
		class_methods.put("Bool",new HashMap<AST.method,String>());	
	}

	private void InheritanceGraph(List <AST.class_> classes) 
	{
		Integer n_classes=0;
		List <String> no_redef = Arrays.asList("Object", "String", "Int", "Bool", "IO");
		List <String> no_inherit = Arrays.asList("String", "Int", "Bool");

		classids.put("Object", 0);
		classids.put("Int",1);
		classids.put("String",2);
		classids.put("Bool",3);
		classids.put("IO", 4);

		class_methods.put("Object",new HashMap<AST.method,String>());
		class_methods.put("Int",new HashMap<AST.method,String>());
		class_methods.put("String",new HashMap<AST.method,String>());
		class_methods.put("Bool",new HashMap<AST.method,String>());
		class_methods.put("IO",new HashMap<AST.method,String>());
		
		class_attrs.put("Object",new HashMap<AST.attr,String>());
		class_attrs.put("Int",new HashMap<AST.attr,String>());
		class_attrs.put("String",new HashMap<AST.attr,String>());
		class_attrs.put("Bool",new HashMap<AST.attr,String>());
		class_attrs.put("IO",new HashMap<AST.attr,String>());
		
		class_attrs_order.put("Object",new HashMap<Integer,String>());
		class_attrs_order.put("Int",new HashMap<Integer,String>());
		class_attrs_order.put("String",new HashMap<Integer,String>());		
		class_attrs_order.put("Bool",new HashMap<Integer,String>());
		class_attrs_order.put("IO",new HashMap<Integer,String>());

		parent.put("Object",null);
		parent.put("Int","Object");
		parent.put("String","Object");
		parent.put("Bool","Object");
		parent.put("IO","Object");

		idclsnm.put(0, "Object");
		idclsnm.put(1, "Int");
		idclsnm.put(2, "Str");
		idclsnm.put(3, "Bool");
		idclsnm.put(4, "IO");

		graph.add(new ArrayList <Integer> (Arrays.asList(1,2,3,4)));	
		graph.add(new ArrayList <Integer> ());
		graph.add(new ArrayList <Integer> ());
		graph.add(new ArrayList <Integer> ());
		graph.add(new ArrayList <Integer> ());
		
		n_classes = n_classes + 5;

		get_baseclasses();

		for(AST.class_ c : classes)   // first shallow pass , eliminating programs containing obvious errors
		{   							// also we gather some info of classes during this shallow pass
			if(no_redef.contains(c.name)) 
			{
				reportError(c.filename, c.lineNo, "Cannot redefine class : " + c.name);
				System.exit(1);
			}
			else if(no_inherit.contains(c.parent)) 
			{
				reportError(c.filename, c.lineNo, "Class cannot inherit : " + c.parent);
				System.exit(1);
			}

			else if(classids.containsKey(c.name))
			{
				reportError(c.filename, c.lineNo, "Class "+c.name+" was previously defined");
				System.exit(1);
			}

			else
			{	
				idclsnm.put(n_classes, c.name);
				classids.put(c.name, n_classes++);		
				clsnmC.put(c.name, c);
				graph.add(new ArrayList <Integer> ());	
				parent.put(c.name, c.parent);
				class_attrs.put(c.name,new HashMap<AST.attr,String>());
				class_attrs_order.put(c.name,new HashMap<Integer,String>());
				class_methods.put(c.name,new HashMap<AST.method,String>());

			}

		}
		// Main class can be checked only after the first shallow pass (i.e , after we go through all the classes once)
		if(classids.containsKey("Main")==false)
		{
			reportError(classes.get(0).filename, 1, "Program does not contain Main class!");
			System.exit(1);
		}

		for(AST.class_ c : classes) // after the first shallow pass , now we have some info of all the classes , 
		// this is the second shallow pass where we eliminate programs where parent class is not defined
		{
			if(classids.containsKey(c.parent) == false)
			{
				reportError(c.filename, c.lineNo, "Parent class not found : " + c.parent);
				System.exit(1);
			}
			int u = classids.get(c.parent);
			int v = classids.get(c.name);
			graph.get(u).add(v);
		}

		// Checking for cycles
		// Eliminating programs if cycles are found
		boolean cycles = false;
		Boolean[] visited = new Boolean[n_classes+10];
		Arrays.fill(visited, Boolean.FALSE);



		Vector<Integer> queue = new Vector<Integer>();
		queue.add(0);

		// bfs over the graph to search for cycles , exit if classes with ill formed hierarchies are found
		while (!queue.isEmpty())
		{
			
			int u = queue.firstElement();
			queue.remove(0);

			if (!visited[u])    
				visited[u] = true;
			else
			{

				reportError(clsnmC.get(idclsnm.get(u)).filename, 1, "Class " +  idclsnm.get(u) + ", is involved in an inheritance cycle.");
				cycles = true;	
				if(queue.isEmpty()) 
				{
					for(int i = 0; i < n_classes; ++i)
						if(visited[i] == false) 
						{
							queue.add(i);
							break;
						}
				}
				continue;
			}

			for(Integer v : graph.get(u)) 	queue.add(v);
			
			if(queue.isEmpty())
			{
				for(int i = 0; i < n_classes; ++i)
					if(visited[i] == false) 
					{
						queue.add(i);
						break;
					}
			}
		}

		if(cycles) System.exit(1);
		
		// third overall pass and first deep pass , where we report errors of ill formed methods and attributes

		for (AST.class_ c: classes)
		{

			if(c.name.equals("Main"))
			{// check if main method is present
				for(AST.feature feature_: c.features)
				{
					if(feature_ instanceof AST.method) main_methods.put(((AST.method)feature_).name,(AST.method)feature_);
				}
				if((main_methods).get("main")==null) reportError(c.filename, 1,"No 'main' method in class Main.");
				else if(((main_methods.get("main")).formals).size()!=0) reportError(c.filename, 1,"'main' method in class Main should have no arguments");
			}
			// attribute and methods check

			int class_atr_n = 0;
			for(AST.feature feature_ : c.features)
			{
				// attribute check
				if(feature_ instanceof AST.attr)
				{ 
					// if the class of the attribute is undefined
					if(classids.get(((AST.attr)feature_).typeid)==null)
						reportError(c.filename,((AST.attr)feature_).lineNo,"Class "+((AST.attr)feature_).typeid+" of attribute " +((AST.attr)feature_).name+ " is undefined.");
					
					else
					{
						String class_iter = c.name;
						int okay=1;
						while(!class_iter.equals("Object"))
						{
							Map<AST.attr,String> a = class_attrs.get(class_iter);	
							for(Map.Entry<AST.attr,String> entry : a.entrySet())
							{
								if(entry.getValue().equals(((AST.attr)feature_).name))
								{
									if(class_iter.equals(c.name)) reportError(c.filename, ((AST.attr)feature_).lineNo,"Attribute "+((AST.attr)feature_).name+" is multiply defined in class.");
									else reportError(c.filename, ((AST.attr)feature_).lineNo, "Attribute "+((AST.attr)feature_).name+" is an attribute of an inherited class.");
									okay=0;
									break;
								}
							}
							if(okay==0) break;
							class_iter = parent.get(class_iter);
						}
						class_attrs_order.get(c.name).put(class_atr_n,((AST.attr)feature_).name);
						(class_attrs.get(c.name)).put((AST.attr)feature_,((AST.attr)feature_).name);
						class_atr_n++;
					}
				}

				else// method check
				{
					// if return type if method not specified
					if(classids.get(((AST.method)feature_).typeid)==null) reportError(c.filename,((AST.method)feature_).lineNo,"Undefined return type "+((AST.method)feature_).typeid+ " in method "+((AST.method)feature_).name+".");
					else
					{
						// if the method is multiply defined in the same class	
						Map<AST.method,String> a = class_methods.get(c.name);
						int okay=1;
						for(Map.Entry<AST.method,String> entry : a.entrySet())
						{
							if(entry.getValue().equals(((AST.method)feature_).name))
							{
								reportError(c.filename, ((AST.method)feature_).lineNo,"Method "+((AST.method)feature_).name+" is multiply defined.");
								okay=0;
							}
							if(okay==0) break;
						}

						if(okay==1)
						{
							String iter =c.name;
							iter=parent.get(iter);
							while(!iter.equals("Object"))
							{
								Map<AST.method,String> a1 = class_methods.get(iter);
								int okay1=1;
								for(Map.Entry<AST.method,String> entry : a1.entrySet())
								{
									if(entry.getValue().equals(((AST.method)feature_).name))
									{
										okay1=0;
										// f is the list of formals of the parent class									
										List<AST.formal> f=(entry.getKey()).formals;
										
										String method_rettype=((AST.method)feature_).typeid;

										String method_rettype_a_class = (entry.getKey()).typeid;

										if(!method_rettype.equals(method_rettype_a_class))
										{
											
											reportError(c.filename, ((AST.method)feature_).lineNo,"In redefined method "+((AST.method)feature_).name+" of class "+c.name+", return type "+method_rettype+" is different from original return type "+method_rettype_a_class+" of class "+iter+".");
											break;
										}
										else
										{
											if(f.size()!=((AST.method)feature_).formals.size())
											{
												// checking if same number of parameters as that of parent class
											
												reportError(c.filename, ((AST.method)feature_).lineNo,"In redefined method "+((AST.method)feature_).name+" of class "+c.name+", number of parameters : "+ ((AST.method)feature_).formals.size() +" are different from original type : "+ f.size()+" of class "+iter+".");
												break;
											}
											else
											{
												// checking same type of parameters are passed
												for(int i=0;i<((AST.method)feature_).formals.size();i++){
													if(((AST.method)feature_).formals.get(i).typeid.equals(f.get(i).typeid)) ;
													else 
													{
											
														reportError(c.filename, ((AST.method)feature_).lineNo,"In redefined method "+((AST.method)feature_).name+" of class "+c.name+", parameter type "+((AST.method)feature_).formals.get(i).typeid+" is different from original type "+f.get(i).typeid+" of class "+iter+".");
													}
													
												}
											}
										}
									}
									if(okay1==0) break;
								}
								// In cool type safety of methods is important , 
								// the return type and number of parameters and their types should match

								iter=parent.get(iter);
							}
						}
						// multiples definition of same formal parameter
						for(int i=0;i<((AST.method)feature_).formals.size();i++)
						{
							for(int j=i+1;j<((AST.method)feature_).formals.size();j++)
							{
								if(((AST.method)feature_).formals.get(i).name.equals(((AST.method)feature_).formals.get(j).name))
								{
								
									reportError(c.filename, ((AST.method)feature_).lineNo,"Formal parameter "+((AST.method)feature_).formals.get(i).name+" is multiply defined.");
								}
							}
						}	
					}
					class_methods.get(c.name).put((AST.method)feature_,((AST.method)feature_).name)	;
				}
			}
		}

		// we have not checked the expressions conatined in method and attribute
		// we go thorugh all the classes in dfs order , to check for expression type errors
		// We will also anotate our AST while checking for type correctness
		// after adding all the methods, start with object root node
		dfs(0);
	}

	// fourth overall pass , second deep pass, we report errors in expression mismatch
	public void dfs(int v)
	{
		String class_name = "";
		for(Map.Entry<String,Integer> e:classids.entrySet())
		{
			if(v==(e.getValue()))
			{
				class_name = e.getKey();
			}
		}
		
		Map<AST.attr,String> a = class_attrs.get(class_name);
		Map<Integer,String> b = class_attrs_order.get(class_name);
		int total_atr_no=0;
		if(v>4)
		{
			table.enterScope();
			// associating the each class name with "self"
			table.insert("self", new AST.attr("self", class_name, new AST.no_expr(clsnmC.get(class_name).lineNo), clsnmC.get(class_name).lineNo));
			// checking attribute errors
			while(total_atr_no != a.size())
			{
				for(Map.Entry<Integer,String> entry1 : b.entrySet())
				{
					if(entry1.getKey()==total_atr_no)
					{
						for(Map.Entry<AST.attr,String> entry : a.entrySet())
						{
							if(entry.getValue()==entry1.getValue())
							{
								table.insert(entry.getValue(),entry.getKey());
								String ret_type = exprType(entry.getKey().value,clsnmC.get(class_name).filename);
								String attr_type = entry.getKey().typeid;

								if(!ret_type.equals("No_Type"))
								{	
									if(lca(ret_type,attr_type)!=null)
										if(!lca(ret_type,attr_type).equals(attr_type))
											reportError(clsnmC.get(class_name).filename, entry.getKey().lineNo, "Inferred type "+ret_type+" of intialisation of attribute "+entry.getKey().name+ " does not conform to declared type "+ attr_type);
								}
							}

						}
						total_atr_no++;
					}

				}
			}
			// checking methods errors
			Map<AST.method,String> m = class_methods.get(class_name);
			for(Map.Entry<AST.method,String> entry : m.entrySet())
			{
				List<AST.formal> f=(entry.getKey()).formals;
				table.enterScope();
				for(int i=0;i<f.size();i++)
				{
					table.insert((f.get(i)).name, new AST.attr(f.get(i).name, (f.get(i)).typeid, new AST.no_expr(1), 1));
				}
				String actual_rettype=exprType((entry.getKey()).body,clsnmC.get(class_name).filename);
				String method_rettype=(entry.getKey()).typeid;

				if(lca(actual_rettype,method_rettype)!=null)
					if(!(lca(actual_rettype,method_rettype).equals(method_rettype)))
						reportError(clsnmC.get(class_name).filename, (entry.getKey()).lineNo,"Inferred return type "+actual_rettype+" of method "+entry.getKey().name+" does not conform to declared return type "+method_rettype+".");
				
				table.exitScope();
			}
		}
		// recursively traverse the graph
		if(graph.get(v)!=null){ for(int i=0;i<(graph.get(v)).size();i++) {dfs((graph.get(v)).get(i));}}
		if(v>4)
			table.exitScope();
		
	}

	// method to check if two expressions are of the same type , it returns null if they are not

	public String lca(String s1, String s2)
	{
		String s1_copy = s1; String s2_copy = s2;
		while(s1_copy!=null)
		{
			while(s2_copy!=null)
			{
				if(s1_copy.equals(s2_copy)) return s1_copy;
				s2_copy = parent.get(s2_copy); 
			}
			s2_copy=s2;
			s1_copy = parent.get(s1_copy);
		}
		return null;
	}
    

    // we report errors , and return expression types here
    // none of the error are fatal here , we try to recover and continue
    // we annotate the AST with appropriate types , 
    // Object is annotated to an expression when it cannot be given a type

	public String exprType(AST.expression expr,String filename)
	{

		// basic type of expressions
		if(expr instanceof AST.no_expr) {expr.type ="No_Type";return "No_Type";}
		else if(expr instanceof AST.bool_const) {expr.type = "Bool";return "Bool";}
		// bool is the result of comp,eq,lt,leq, isvoid
		else if(expr instanceof AST.string_const) {expr.type = "String";return "String";}
		else if(expr instanceof AST.int_const) {expr.type ="Int";return "Int";}

		else if(expr instanceof AST.static_dispatch)
		{
			
			List<AST.expression> actual_param;
			List<AST.formal> formal_param;
			int okay=0;
			if(classids.get(((AST.static_dispatch)expr).typeid)==null)
			{
				okay=1;
				reportError(filename,((AST.static_dispatch)expr).lineNo,"Static dispatch to undefined class "+((AST.static_dispatch)expr).typeid+".");
			}
			
			String caller_type=exprType(((AST.static_dispatch)expr).caller,filename);
			(((AST.static_dispatch)expr).caller).type=caller_type;

			if(!(caller_type.equals("No_type")||(((AST.static_dispatch)expr).typeid).equals("No_type")))
			{	
				if(lca(caller_type,((AST.static_dispatch)expr).typeid)!=null)
					if(!lca(caller_type,((AST.static_dispatch)expr).typeid).equals(((AST.static_dispatch)expr).typeid))
						reportError(filename,((AST.static_dispatch)expr).lineNo,"Expression type "+caller_type+" does not conform to declared static dispatch type "+((AST.static_dispatch)expr).typeid+".");
			}

			String sd_type =((AST.static_dispatch)expr).typeid;
			
			if(okay==0)
			{
				Map<AST.method,String> a = class_methods.get(sd_type);
				if(a!=null)
					for(Map.Entry<AST.method,String> entry : a.entrySet())
					{
						if(entry.getValue().equals(((AST.static_dispatch)expr).name))
						{
							okay=1;
							actual_param=((AST.static_dispatch)expr).actuals;
							formal_param=(entry.getKey()).formals;
							if(actual_param.size()!=formal_param.size())
							{
								reportError(filename,((AST.static_dispatch)expr).lineNo,"Method "+((AST.static_dispatch)expr).name+" of static dispatch invoked with wrong number of arguments.");
								break;
							}
							for(int i=0;i<actual_param.size();i++)
							{
								String actual_type=exprType(actual_param.get(i),filename);
								if(lca(actual_type,((formal_param.get(i)).typeid))!=null)
									if(!lca(actual_type,((formal_param.get(i)).typeid)).equals((formal_param.get(i)).typeid)) 
										reportError(filename,((AST.static_dispatch)expr).lineNo,"In call of method "+((AST.static_dispatch)expr).name+", type "+actual_type+" of parameter "+(formal_param.get(i)).name+" does not conform to declared type "+(formal_param.get(i)).typeid+".");

							}
							expr.type = (entry.getKey()).typeid;
							//(((AST.static_dispatch)expr).caller).type=expr.type;
							return expr.type ;
						}
					}
			}
			// okay is 1 only if the class of the static dispatch is not defined , or we found the method in the static dispatch class
			if(okay==0)
			{
				reportError(filename, ((AST.static_dispatch)expr).lineNo, "Static dispatch to undefined method " + ((AST.static_dispatch)expr).name);
			}
			// if not found
			expr.type = "Object";
			return expr.type;
		}


		else if(expr instanceof AST.dispatch)
		{
			List<AST.expression> actual_param;
			List<AST.formal> formal_param;			
			String caller_type = exprType(((AST.dispatch)expr).caller,filename);
			int okay=0;

			while(caller_type!=null)
			{
				Map<AST.method,String> a = class_methods.get(caller_type);
				if(a!=null)
					for(Map.Entry<AST.method,String> entry : a.entrySet())
					{
						// if the method is defined, we use the most recent version of the method in the clas hierarchy
						if(entry.getValue().equals(((AST.dispatch)expr).name))
						{
							okay=1;
							actual_param=((AST.dispatch)expr).actuals;
							formal_param=(entry.getKey()).formals;
							if(actual_param.size()!=formal_param.size())
							{
								reportError(filename,((AST.dispatch)expr).lineNo,"Method "+((AST.dispatch)expr).name+" called with wrong number of arguments.");
								break;
							}
							// type checking conforming with the method rules 
							for(int i=0;i<actual_param.size();i++)
							{
								String actual_type=exprType(actual_param.get(i),filename);
								if(lca(actual_type,((formal_param.get(i)).typeid))!=null)
									if(!lca(actual_type,((formal_param.get(i)).typeid)).equals((formal_param.get(i)).typeid)) 
										reportError(filename,((AST.dispatch)expr).lineNo,"In call of method "+((AST.dispatch)expr).name+", type "+actual_type+" of parameter "+(formal_param.get(i)).name+" does not conform to declared type "+(formal_param.get(i)).typeid+".");

							}
							expr.type = (entry.getKey()).typeid;
							return expr.type ;
						}
					}			
				// we check all the classes in the class hierarchy for the method
				// we check every class that caller inherits
				caller_type=parent.get(caller_type);
				if(okay==1) break;
			}
			// okay plays the same role as that of static dispatch
			if(okay==0)
			{
				reportError(filename, ((AST.dispatch)expr).lineNo, "Dispatch to undefined method " + ((AST.dispatch)expr).name);
			}
			// if not found
			expr.type = "Object";
			//(((AST.dispatch)expr).caller).type=expr.type;
			return expr.type;
		}

		



		else if(expr instanceof AST.neg)
		{			
			expr.type ="Int";
			String t1 = exprType(((AST.neg)expr).e1,filename);
			if(t1.equals("Int"))return "Int";
			else
			{
				reportError(filename,expr.lineNo,"Argument of '~' has type " + t1 + " instead of Int.");
				return "Int";
			}
		}	


		// If expression is of type object, we see if the attribute was defined
		else if(expr instanceof AST.object)
		{

			AST.attr ret_attr=table.lookUpGlobal(((AST.object)expr).name);
	
			if(ret_attr==null) 
			{
				reportError(filename,((AST.object)expr).lineNo,"Undeclared identifier "+((AST.object)expr).name);
				expr.type ="Object";
				return "Object";
			}

			else {expr.type =ret_attr.typeid; return ret_attr.typeid;}

		}


		// isvoid is evaluated to bool in cool
		else if(expr instanceof AST.isvoid) {expr.type="Bool";return "Bool";}

		// if the expr is of the type plus,sub,mul,divide, or neg ,
		// if both left and right hand types are int return int 
		// otherwise report error


		else if(expr instanceof AST.mul)
		{
			expr.type ="Int";
			String t1 = exprType(((AST.mul)expr).e1,filename);
			String t2 = exprType(((AST.mul)expr).e2,filename);

			if(t1.equals("Int") && t2.equals("Int")) return "Int";
			else
			{
				reportError(filename,expr.lineNo,"non-Int arguments: "+t1+" * "+t2);
				return "Int";
			}
		}

		else if(expr instanceof AST.divide)
		{
			expr.type ="Int";
			String t1 = exprType(((AST.divide)expr).e1,filename);
			String t2 = exprType(((AST.divide)expr).e2,filename);
			if(t1.equals("Int") && t2.equals("Int")) return "Int";
			else
			{
				reportError(filename,expr.lineNo,"non-Int arguments: "+ t1 +" / "+ t2);
				return "Int";
			}
		}

		else if(expr instanceof AST.plus)
		{
			expr.type ="Int";
			String t1 = exprType(((AST.plus)expr).e1,filename);
			String t2 = exprType(((AST.plus)expr).e2,filename);
			if(t1.equals("Int") && t2.equals("Int")) return "Int";
			else
			{
				reportError(filename,expr.lineNo,"non-Int arguments: "+t1+" + "+t2);
				return "Int";
			}
		}

		else if(expr instanceof AST.sub)
		{
			expr.type ="Int";
			String t1 = exprType(((AST.sub)expr).e1,filename);
			String t2 = exprType(((AST.sub)expr).e2,filename);
			if(t1.equals("Int") && t2.equals("Int")) return "Int";
			else
			{
				reportError(filename,expr.lineNo,"non-Int arguments: "+t1+" - "+t2);
				return "Int";
			}
		}

		// if the expression if of the type eq , if both left and right hand side of expression match , return bool
		// otherwise display error
		else if(expr instanceof AST.eq)
		{

			expr.type ="Bool";
			String t1 = exprType(((AST.eq)expr).e1,filename);
			String t2 = exprType(((AST.eq)expr).e2,filename);

			if( (t1.equals("Bool") && !t2.equals("Bool"))||(t1.equals("String")&& !t2.equals("String"))||(t1.equals("Int")&& !t2.equals("Int" ))) 
				reportError(filename,expr.lineNo,"Illegal comparison with a basic type");
			else if( (t2.equals("Bool") && !t1.equals("Bool"))||(t2.equals("String")&& !t1.equals("String"))||(t2.equals("Int")&& !t1.equals("Int" ))) 
				reportError(filename,expr.lineNo,"Illegal comparison with a basic type");
			return "Bool";

			
		}

		// if the expr is of type lt ,or leq , if both left hand and right hand types are int , return bool
		// otherwise display error
		else if(expr instanceof AST.lt)
		{
			expr.type ="Bool";
			String t1 = exprType(((AST.lt)expr).e1,filename);
			String t2 = exprType(((AST.lt)expr).e2,filename);
			if(t1.equals("Int") && t2.equals("Int")) return "Bool";
			else
			{
				reportError(filename,expr.lineNo,"non-Int arguments: "+t1+" < "+t2);
				return "Bool";
			}
		}

		else if(expr instanceof AST.leq)
		{
			expr.type ="Bool";
			String t1 = exprType(((AST.leq)expr).e1,filename);
			String t2 = exprType(((AST.leq)expr).e2,filename);
			if(t1.equals("Int") && t2.equals("Int")) return "Bool";
			else
			{
				reportError(filename,expr.lineNo,"non-Int arguments: "+t1+" <= "+t2);
				return "Bool";
			}
		}

		// if the expression is of the type comp, if the type id of the expression is bool , return otherwise display error
		else if(expr instanceof AST.comp)
		{
			expr.type ="Bool";
			String t1 = exprType(((AST.comp)expr).e1,filename);
			if(t1.equals(new String("Bool"))) return "Bool";
			else
			{
				reportError(filename, expr.lineNo,"Argument of 'not' has type " + t1+ " instead of Bool");
				return "Bool";
			}
		}

		// if the expr is of the type assign 
		// if the assigned variable is not declared or types does not match 
		// report error
		else if(expr instanceof AST.assign)
		{
			
			AST.attr ret_attr=table.lookUpGlobal(((AST.assign)expr).name);
			if(ret_attr==null)
			{
				// giving type Object , since the expression cannot be assigned a type
				expr.type = "Object";
				reportError(filename,((AST.assign)expr).lineNo,"Assignment to undeclared variable "+((AST.assign)expr).name+".");
				
			}
			else
			{
				String t1 = exprType(((AST.assign)expr).e1,filename);

				if (lca(ret_attr.typeid,t1 )!=null)
					if(!((ret_attr.typeid).equals(lca(ret_attr.typeid, t1))))
						reportError(filename,((AST.assign)expr).lineNo,"Type "+ t1+" of assigned expression does not conform to declared type "+ret_attr.typeid+" of identifier "+ret_attr.name+".");
				expr.type = t1;
			}
			return expr.type;
		}

		// for cond and loop,if the condition does not evaluate to bool , report error

		else if(expr instanceof AST.cond)
		{
			
			if(!exprType(((AST.cond)expr).predicate,filename).equals("Bool"))
			{
				reportError(filename,expr.lineNo,"Predicate of 'if' does not have type Bool.");
			}
			return expr.type = lca(exprType(((AST.cond)expr).ifbody,filename), exprType(((AST.cond)expr).elsebody,filename));
		}

		else if(expr instanceof AST.loop)
		{

			if(!exprType(((AST.loop)expr).predicate,filename).equals("Bool"))
			{
				reportError(filename, expr.lineNo,"Predicate of 'while' does not have type Bool.");
			}
			exprType(((AST.loop)expr).body,filename);
			return expr.type = "Object";
		}
		// a block is a list of expressions 
		// check if all the expressions are valid and return the final expression value
		// note : we check the validity of all the expressions here and generate aprropriate errors 
		// on type errors
		else if(expr instanceof AST.block)
		{
			
			List<AST.expression> exp=((AST.block)expr).l1;
			for(int i=0;i<exp.size();i++) 
				exp.get(i).type = exprType(exp.get(i),filename);
			
			expr.type = exprType(exp.get(exp.size()-1),filename);
		
			return expr.type;			
		}

		// object names can be introduced through let expressions ,
		// we enter them in the scopetable

		else if(expr instanceof AST.let)
		{
			table.enterScope();
			// if the type of identifier is not defined , report error
			if(classids.get(((AST.let)expr).typeid)==null)
				reportError(filename,((AST.let)expr).lineNo,"Class "+((AST.let)expr).typeid+" of let-bound identifier "+((AST.let)expr).name+" is undefined.");
			
			// insert attribute into the scope table
			table.insert(((AST.let)expr).name,new AST.attr(((AST.let)expr).name,((AST.let)expr).typeid,new AST.no_expr(1),1));
		
			String t1 = exprType(((AST.let)expr).value,filename);
			if(!(t1.equals("No_Type")))
			{
				// if the type id of attribute does not match with the declared type ,
				// report error

				if(lca(t1,((AST.let)expr).typeid)!=null)
					if(!lca(t1,((AST.let)expr).typeid).equals(((AST.let)expr).typeid))
						reportError(filename,((AST.let)expr).lineNo,"Inferred type "+t1+" of initialization of "+((AST.let)expr).name+" does not conform to identifier's declared type "+((AST.let)expr).typeid+".");
			}

			expr.type = exprType(((AST.let)expr).body,filename);
			table.exitScope();

			return expr.type;
		}

		// If the expression is of case type, check if all braches are valid and distinct
		else if(expr instanceof AST.typcase)
		{
			String branch_predicate=exprType(((AST.typcase)expr).predicate,filename);
			// no branch can be defined twice
			for(int i=0;i<((AST.typcase)expr).branches.size();i++)
			{
				for(int j=i+1;j<((AST.typcase)expr).branches.size();j++)
				{
					if((((AST.typcase)expr).branches.get(j)).type.equals((((AST.typcase)expr).branches.get(i)).type)) reportError(filename,1,"Duplicate branch "+(((AST.typcase)expr).branches.get(i)).type+" in case statement.");
				}
			}
			int i=-1;
			branch_predicate=((AST.typcase)expr).branches.get(0).type;
			for(AST.branch ithbranch : ((AST.typcase)expr).branches ) 
			{
				table.enterScope();
				i++;
				// if the brach type is null , report error
				if(classids.get(ithbranch.type)==null)
					reportError(filename,1,"Class "+(((AST.typcase)expr).branches.get(i)).type+" of case branch is undefined.");
				// inserting name and type of brach into the scope table
				// we insert the branches into the scope table as object name can be introduced here
				table.insert((((AST.typcase)expr).branches.get(i)).name,new AST.attr((((AST.typcase)expr).branches.get(i)).name,(((AST.typcase)expr).branches.get(i)).type,new AST.no_expr(1),1));
				// evaluating type of the expression of the ith branch
				String t1 = exprType((((AST.typcase)expr).branches.get(i)).value,filename);
				branch_predicate=lca(branch_predicate,t1);
				(((AST.typcase)expr).branches.get(i)).type = t1;
				table.exitScope();
								
			}
			// returning the "oldest" class from which all the return values of the brach inherit
			return expr.type = branch_predicate;
		}

		// If the class was not defined for which new is used , report error
		else if(expr instanceof AST.new_)
		{
			if(classids.get(((AST.new_)expr).typeid)==null) 
			{
				expr.type = "Object";
				reportError(filename,expr.lineNo,"'new' used with undefined class "+((AST.new_)expr).typeid+".");
			}
			else return expr.type = ((AST.new_)expr).typeid;
		}

		return "";
	}
}

