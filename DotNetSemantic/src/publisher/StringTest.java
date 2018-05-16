/**
 * 
 */
package publisher;

/**
 * @author win10Admin
 *
 */
public class StringTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		String arg="string s1, string s2";
		System.out.println(arg);
		if (arg!=null){
			String[] para=arg.split(",");
			System.out.println(para[0]+"  "+para[1]);
			arg="";
			
			for(int s=0;s<para.length;s++){
				para[s]=para[s].trim();
				if(para[s].indexOf(" ")>0)
				para[s]=para[s].substring(0,para[s].indexOf(" "));
				System.out.println("------"+para[s]);
				arg=arg+para[s]+" ";
			}
		}
		
		System.out.println(arg);
		
		
//		String sig=".method public hidebysig newslot virtual final instance object  Convert(object 'value', class [mscorlib]System.Type targetType, object parameter, class [mscorlib]System.Globalization.CultureInfo culture) cil managed {";
//
//		System.out.println( ParseSigniture( sig));
		
	}

		private static String ParseSigniture(String sig){
		

		int pos = sig.indexOf("(");
		String name= sig.substring(0,pos);
		name=name.trim();
		String[] t=name.split(" ");
		String name1= t[3];
		for(int s=4;s<t.length;s++){

			name1=name1+" "+ t[s];
		}

		int pos2=sig.indexOf(")");
		String args=sig.substring(pos+1,pos2);
		
		if (args!=null){
			String[] para=args.split(",");
			args="";
			for(int s=0;s<para.length;s++){
				args=args+para[s]+" ";
			}
		}
		sig=name1+" "+args;
		return sig;
	}
}
