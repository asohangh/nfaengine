/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snort_rule;

/**
 *
 * @author heckarim
 *
 *         //pcre:[!]"(/<regex>/|m<delim><regex><delim>)[ismxAEGRUBPHMCOIDKYS]";
 *
 * This class is for handling PCRE option in snort rule, there are some desires:
 *  +, Store infor about component of PCRE.
 *  +, analyze PCRE:
 *      +, modifier
 */
public class PCRE extends RuleOption {

    public String regex;
    public String modify;
    public boolean isReverse; // operator "!" of PCRE.
    // String sid;

    PCRE(String sop, RuleComponent rule) {
        super(sop,rule);
        this.isPCRE = true;
        regex = "";
        modify = "";
        this.isReverse = false;
        this.ParsePcre(sop);
    }

    public void ParsePcre(String rule) {
        //pcre:[!]"(/<regex>/|m<delim><regex><delim>)[ismxAEGRUBPHMCOIDKYS]";

        String temp = null;
        if (rule.startsWith("pcre:")) {
            temp = (rule.replaceAll("pcre:", ""));
            temp = temp.trim();
            if (temp.startsWith("!")) {
                this.isReverse = true;
                temp = temp.substring(2, temp.length() - 1);
            }else
                temp = temp.substring(1, temp.length() - 1);
            //bo " va "
            temp = temp.trim(); // something like /<pcre content>/[<modifier>]
            //split into pcre and modify
            //get modifier by read from the end, so it will be revese
            int i;
            String s = "";
            for(i = temp.length() -1 ; i>1; i--){
                if(temp.charAt(i) != '/'){
                    s += temp.charAt(i);
                }else
                    break;
            }

            this.regex = temp.substring(1,i);
            //flip s to modify
            for(i=s.length()-1; i>=0; i--){
                this.modify += s.charAt(i);
            }
            
        }
    }

    public boolean CompareTo(PCRE pcre){
        boolean ret = false;
        if(CompareModifier(pcre)){
            if(this.regex.compareToIgnoreCase(pcre.regex) == 0)
                ret = true;
        }
        return ret;
    }
    
    public boolean CompareModifier(PCRE pcre){
        boolean ret = true;
        for(int i =0 ;i<modify.length(); i++){
            if(!pcre.modify.contains(""+modify.charAt(i))){
                ret =false;
                break;
            }
        }
        return ret;
    }

    public int CountPrefix(PCRE pcre){
        int ret = 0;
        if(modify.compareToIgnoreCase(pcre.modify)!=0/*!this.CompareModifier(pcre)*/){
            ret = 0;
        }else{

            int len = (regex.length()<pcre.regex.length())?regex.length():pcre.regex.length();
            for(int i =0; i<len; i++){
                if(regex.charAt(i) == pcre.regex.charAt(i))
                    ret++;
                else
                    break;
            }
        }
        return ret;
    }

    public String GetPrefix(PCRE pcre){
        String ret = "";
        if(false){//modify.compareToIgnoreCase(pcre.modify)!=0/*!this.CompareModifier(pcre)*/){
            ret = "";
        }else{

            int len = (regex.length()<pcre.regex.length())?regex.length():pcre.regex.length();
            for(int i =0; i<len; i++){
                if(regex.charAt(i) == pcre.regex.charAt(i))
                    ret += regex.charAt(i);
                else
                    break;
            }
        }
        return ret;
    }
    @Override
    public String toString() {
        if (this.isReverse) {
            return "!/" + regex + "/" + modify;
        } else {
            return "/" + regex + "/" + modify;
        }
    }
}
