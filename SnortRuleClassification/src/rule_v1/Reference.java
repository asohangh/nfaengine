
package rule_v1;

import java.util.LinkedList;



/**
 *
 * @author heckarim
 */
public class Reference {
	
	
	/**
	 * 
	 * @param s1
	 * @param s2
	 * @param percent
	 * @return true if s1 more than percent like s2, from begin to end
	 */
	public static boolean compareByPercent(String s1,String s2, int percent){
		int count=0;
		int minlength=(s1.length()<s2.length())?s1.length():s2.length();
		for(int i=0;i<minlength;i++){
			if(s1.charAt(i)==s2.charAt(i))
				count++;
		}
		if((count*100)/s1.length() >= percent)
			return true;
		
		return false;
	}
	
	
	/**
	 * 
	 * @param rule
	 * @return true if pcre is simple 
	 */
	public static boolean isSimplyPcre(RulePcre rule){
		
		//Remove contraint repetion rule
		if(rule.pcre.indexOf("{")>=0)// co the co chua contraint repetiton
			return false;
		
		//Remove back reference rule
		int  index = rule.pcre.indexOf("\\");
		char chr = rule.pcre.charAt(index+1);
		if(chr>'0' && chr<'7')
			return false;
		//Remove '^' or '$' rule
		if(rule.pcre.startsWith("^") || rule.pcre.indexOf('$')>=0)
				return false;
		
		
		return true;
	}


    /**
     *
     * @param rule
     * @param mark
     * @return arrray of Strings split from rule by mark
     * note: ignore blank and "" string
     */
	public static String[] splitByChar (String rule,char mark){
		LinkedList<String> temp =new LinkedList<String>();
		String s="";
		for(int i=0;i<rule.length();i++){
			char chr=rule.charAt(i);
			if(chr==mark){
				if(i>0 && rule.charAt(i-1)=='\\') // mark is part of content
					s+=chr;
				else {
					// mark not is the part of pcre so it begin new part of rule
					if(s.compareTo("") != 0){
						temp.add(s.trim());
						s="";
					}
				}	
			}else
				s+=chr;
		}
		// sill something in s
		if(s.trim().compareTo("") != 0)
			temp.add(s.trim());
		
		if(temp.size()==0)
			return new String[0];
		else{
			return temp.toArray(new String[0]);
		}
	}

    /**
	 * This function will get the index of the end of block String
	 * @param s			:Sring, must begin with bmark;
	 * @param bmark		:begin mark;
	 * @param emark		:end 	mark;
	 * @return			: index of the end of block String
	 * note: 	- BackSlash \
	 * 			- Block in Block problem
	 */
	public static int getIndexOBlock (String s,char bmark,char emark){

		if(s==null || s.length()<2 ||  s.charAt(0)!=bmark)
			return 0;

		int count=1;
		int i;
		for(i=1 ; i < s.length(); i++){
			char chr = s.charAt(i);
			if(chr == emark && s.charAt(i-1) != '\\'){
				count--;
				if(count == 0)
					break;
			}else if(chr == bmark && s.charAt(i-1) != '\\')
				count++;
		}
		return i;
	}

}