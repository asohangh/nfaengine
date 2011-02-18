/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.AuthProvider;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;

/**
 *
 * @author heckarim
 */
public class References extends BaseClass{

    public final static String _allModified = "nocase; rawbytes; depth; offset; distance; within; http_client_body; http_cookie; http_raw_cookie; http_header; http_raw_header; http_method; http_uri; http_raw_uri; http_stat_code; http_stat_msg; http_encode; fast_pattern";
    public final static String[] _opContentModifier = _allModified.split("; ");
    public final static String _alloption = "msg; reference; gid; sid; rev; classtype; priority; metadata; content; nocase; rawbytes; depth; offset; distance; within; http_client_body; http_cookie; http_raw_cookie; http_header; http_raw_header; http_method; http_uri; http_raw_uri; http_stat_code; http_stat_msg; http_encode; fast_pattern; uricontent; urilen; isdataat; file_data; byte_test; byte_jump; ftpbounce; asn1; cvs; dce_iface; dce_opnum; dce_stub_data; pcre";
    public final static String[] _opAll = _alloption.split("; ");
    //general option
    public final static String _allGeneralOption = "msg; reference; gid; sid; rev; classtype; priority; metadata";
    public final static String[] _opGeneral = _allGeneralOption.split("; ");
    //PCRE modifier
    public final static String _allPcreModifier = "i; s; m; x; A; E; G; R; U; I; P; H; D; M; C; K; S; Y; B; O";
    public final static String[] _pcreModifier = _allPcreModifier.split("; ");
    public final static String _simplePcreModifier = "i; s; m";
    public final static String[] _simplepcreModifer = _simplePcreModifier.split("; ");
    public final static String _complexPcreModifier = "A; E; G; R; U; I; P; H; D; M; C; K; S; Y; B; O";
    public final static String[] _complexpcreModifer = _complexPcreModifier.split("; ");


    //
    public Hashtable hashOpAll; //hash position for all option in _opAll; //this for furture using.

    References() {
        //create hasttable
        this.hashOpAll = new Hashtable(References._opAll.length);
        for (int i = 0; i < References._opAll.length; i++) {
            this.hashOpAll.put(i, References._opAll[i].toLowerCase());
        }
    }

    /**
     *
     *
     * @param s1
     * @param s2
     * @param percent
     * @return true if s1 more than percent like s2, from begin to end
     */
    public static boolean compareByPercent(String s1, String s2, int percent) {
        int count = 0;
        int minlength = (s1.length() < s2.length()) ? s1.length() : s2.length();
        for (int i = 0; i < minlength; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                count++;
            }
        }
        if ((count * 100) / s1.length() >= percent) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param rule
     * @return true if pcre is simple
     */
    /* public static boolean isSimplyPcre(RulePcre rule) {

    //Remove contraint repetion rule
    if (rule.pcre.indexOf("{") >= 0)// co the co chua contraint repetiton
    {
    return false;
    }

    //Remove back reference rule
    int index = rule.pcre.indexOf("\\");
    char chr = rule.pcre.charAt(index + 1);
    if (chr > '0' && chr < '7') {
    return false;
    }
    //Remove '^' or '$' rule
    if (rule.pcre.startsWith("^") || rule.pcre.indexOf('$') >= 0) {
    return false;
    }


    return true;
    }
     */
    /**
     *
     * @param rule
     * @param mark
     * @return arrray of Strings split from rule by mark
     * note: ignore blank and "" string
     */
    public static String[] splitByChar(String rule, char mark) {
        LinkedList<String> temp = new LinkedList<String>();
        String s = "";
        for (int i = 0; i < rule.length(); i++) {
            char chr = rule.charAt(i);
            if (chr == mark) {
                if (i > 0 && rule.charAt(i - 1) == '\\') // mark is part of content
                {
                    s += chr;
                } else {
                    // mark not is the part of pcre so it begin new part of rule
                    if (s.compareTo("") != 0) {
                        temp.add(s.trim());
                        s = "";
                    }
                }
            } else {
                s += chr;
            }
        }
        // sill something in s
        if (s.trim().compareTo("") != 0) {
            temp.add(s.trim());
        }

        if (temp.size() == 0) {
            return new String[0];
        } else {
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
    public static int getIndexOBlock(String s, char bmark, char emark) {

        if (s == null || s.length() < 2 || s.charAt(0) != bmark) {
            return 0;
        }

        int count = 1;
        int i;
        for (i = 1; i < s.length(); i++) {
            char chr = s.charAt(i);
            if (chr == emark && s.charAt(i - 1) != '\\') {
                count--;
                if (count == 0) {
                    break;
                }
            } else if (chr == bmark && s.charAt(i - 1) != '\\') {
                count++;
            }
        }
        return i;
    }

    static boolean CheckHaveSpecialPCREModifier(String modifi) {
        //_SpecialPCREModifier =

        return true;

    }

    /**
     *
     * @param option
     * @return
     */
    static boolean CheckContentModifier(String option) {
        for (int i = 0; i < _opContentModifier.length; i++) {
            if (option.compareToIgnoreCase(_opContentModifier[i]) == 0) {
                return true;

            }
        }
        return false;
    }

    /**
     *
     * @param rule
     * @return true if pcre is simple
     */
    public static boolean isSimplyPcre(PCRE rule) {



        //Remove contraint repetion rule
        if (rule.regex.indexOf("{") >= 0)// co the co chua contraint repetiton
        {
            return false;
        }
        //(?
        if (rule.regex.indexOf("(?") >= 0) {
            return false;
        }
        //(|
        if (rule.regex.indexOf("(|") >= 0) {
            return false;
        }
        //Remove back reference rule
        int index = rule.regex.indexOf("\\");
        char chr = rule.regex.charAt(index + 1);
        if (chr > '0' && chr < '7') {
            return false;
        }
        //Remove back reference rule
        for (int i = 0; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '\\') {
                chr = rule.regex.charAt(i + 1);
                if (chr > '0' && chr < '7') {
                    return false;
                }
            }
        }
        //Remove '^' or '$' rule
        if (rule.regex.startsWith("^") || rule.regex.indexOf('$') >= 0) {
            return false;
        }
        return true;
    }


    public static boolean isConstraintPCRE(PCRE pcre){
        return References.isHaveConstraint(pcre.regex);
    }

    /**
     * 
     * @param s
     * @return  true if contain {
     */
    public static boolean isHaveConstraint(String s){
        for (int i =1; i<s.length()-1;i ++){
            char ch = s.charAt(i);
            if (ch == '{') {
                char ch0 = s.charAt(i - 1);
                if (ch0 != '\\') {
                    return true;
                } else {
                    continue;
                }
            }
        }
        return false;
    }
    /**
     * 
     * @param s
     * @param c char
     * @return  last position of char inside string
     *    before this char don't have any \\
     */
    public static int getPreviousPosition(String s, char c){
        for(int i = s.length() -1 ; i>=0; i--){
            if(s.charAt(i) == c){
                if(i ==0 || s.charAt(i-1) != '\\')
                    return i;
                else
                    continue;
            }
        }
        return -1;
    }

    /**
     *
     * enhancement of issimple
     * @param rule
     * @return true if pcre is supportable
     */
    public static boolean isSupportablePCRE(PCRE rule) {
        
        /*
        //Remove contraint repetion rule
        for (int i = 1; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '{') {
                char ch0 = rule.regex.charAt(i - 1);
                if (ch0 != '\\') {
                    ch0 = rule.regex.charAt(i + 1);
                    if (ch0 >= '0' && ch0 <= '9') {
                        return false;
                    }
                } else {
                    continue;
                }
            }
        }*
         */
        //Remove rule contraint inside constraint
        if(References.isHaveConstraint(rule.regex)){
            for(int i =rule.regex.length()-1; i>=1;i--){
                char ch = rule.regex.charAt(i);
                if(ch == '}' && rule.regex.charAt(i-1) != '\\'){
                    int pi = References.getPreviousPosition(rule.regex.substring(0, i-1), '{');
                    if(pi == -1) // there are error in rule
                        return false;
                    //if before it is (
                    ch = rule.regex.charAt(pi -1);
                    pi = pi-1;
                    if(ch != ')' || pi==0 || rule.regex.charAt(pi-1) == '\\')
                        continue;
                    else{
                        int ppi = References.getPreviousPosition(rule.regex.substring(0,pi), '(');
                        //(s){..}
                        String s = rule.regex.substring(ppi, pi);
                        if(References.isHaveConstraint(s))
                            return false;
                    }
                }
            }
        }
        // reverse rule
        if(rule.isReverse)
            return false;
        //(?
        if (rule.regex.indexOf("(?") >= 0 || rule.regex.indexOf("?)") >= 0) {
            return false;
        }
        //(| |)
        if (rule.regex.indexOf("(|") >= 0 || rule.regex.indexOf("|)") >=0) {
            return false;
        }
        // /| or /^|
        if (rule.regex.startsWith("|") || rule.regex.startsWith("^|")) {
            return false;
        }

        //Remove back reference rule
        for (int i = 0; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '\\') {
                char chr = rule.regex.charAt(i + 1);
                if (chr > '0' && chr < '7') {
                    return false;
                }
            }
        }
        
        //rule with '^' but dont at beginning
        for (int i = 1; i < rule.regex.length() - 1; i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '^') {
                char ch0 = rule.regex.charAt(i - 1);
                if (ch0 != '\\' && ch0 != '[') {
                    return false;
                } else {
                    continue;
                }
            }
        }
        //'$' rule
        for (int i = 1; i < rule.regex.length(); i++) {
            char ch = rule.regex.charAt(i);
            if (ch == '$') {
                char ch0 = rule.regex.charAt(i - 1);
                if (ch0 != '\\') {
                    return false;
                } else {
                    continue;
                }
            }
        }
        // dont containt complex modifier
        for(int i=1; i< References._complexpcreModifer.length; i++){
            String s = References._complexpcreModifer[i];
            if(rule.modify.indexOf(s) != -1)
                return false;
        }
        return true;
    }



    /*
     * this function will write PCRE from LinkedList rulecomponent to file,
     * @param rb
     * @param filename
     */
    public static void WritePcreToFile(LinkedList<PCRE> lrc, String filename) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
            for (int i = 0; i < lrc.size(); i++) {
                PCRE pcre = (PCRE) lrc.get(i);
                bw.write(pcre.toString() + "\n");
                /*
                if(pcre.isReverse)
                bw.write("pcre:!\"/" + pcre.regex + "/" + pcre.modify  + "\"\n");
                else
                bw.write("pcre:\"/" + pcre.regex + "/" + pcre.modify  + "\"\n");

                 */
            }

            bw.flush();
            bw.close();

        } catch (IOException ex) {
            Logger.getLogger(References.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * this function will write PCRE from LinkedList rulecomponent to file,
     * @param rb
     * @param filename
     */
    public static void WritePcreToFileRuleComponent(LinkedList<RuleComponent> lrc, String filename) {
        LinkedList<PCRE> lpcre = new LinkedList<PCRE>();
        for (int i = 0; i < lrc.size(); i++) {
            if (lrc.get(i).isHavePCRE) {
                PCRE pcre = (PCRE) lrc.get(i).getOpPcre();
                lpcre.add(pcre);
            }
        }
        References.WritePcreToFile(lpcre, filename);
    }


    /*
     * This function will read pcre from a file which is output by WritePcreToFile function.
     */
    public static LinkedList<PCRE> ReadPcreFromFile(String filename) {
        LinkedList<PCRE> ret = new LinkedList<PCRE>();
        String s;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(filename));
            while ((s = br.readLine()) != null) {
                if (s.startsWith("#")) {
                    continue;
                }
                /*int index = Integer.parseInt(s.split(".")[0]);
                s = s.replaceFirst(index + ".", "").trim();
                 *
                 */
                if (s.startsWith("!")) {
                    s = s.substring(1);
                    s = "pcre:!\"" + s + "\"";
                } else {
                    s = "pcre:\"" + s + "\"";
                }
                PCRE temp = new PCRE(s.trim(), null);
                ret.add(temp);
            }
            br.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(References.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return ret;
    }

    /**
     *
     */
    public static LinkedList<PCRE> doReducePcre(LinkedList<PCRE> lpcre) {
        LinkedList<PCRE> rpcre = new LinkedList<PCRE>();
        for (int i = 0; i < lpcre.size(); i++) {
            boolean same = false;
            PCRE temp = lpcre.get(i);
            for (int j = 0; j < rpcre.size(); j++) {
                if (temp.CompareTo(rpcre.get(j))) {
                    same = true;
                    break;
                }
            }
            if (!same) {
                rpcre.add(temp);
            }
        }
        return rpcre;
    }

           /**
     * Count the number of element seperate by and operator
     * @return number of element
     * note:
     *      - come from formatPCRE
     *      - need modify to enhance
     *      
     */
    public static int countPcreElement(PCRE pcre) {
        //This function just add char_and in to pcre pattern
        int i = 0;
        int count =0;
        boolean cando = false;
        while (i < pcre.regex.length()) {
            char chr = pcre.regex.charAt(i);
            //System.out.println(chr);
            switch (chr) {
                case Refer._char_and:
                case '|':
                    cando = false;
                    i++;
                    break;
                case '*':
                case '+':
                case '?':
                case ')':
                    cando = true;
                    i++;
                    break;
                case '{':
                    int last = Refer.getIndexOBlock(pcre.regex.substring(i), '{', '}');
                    cando = true;
                    i = i + last + 1;
                    break;
                case '[':
                    if (cando) {
                        count++;
                        i = i + 1;
                        cando = false;
                    }
                    last = Refer.getIndexOBlock(pcre.regex.substring(i), '[', ']');
                    //System.out.println(last);
                    //return;
                    cando = true;
                    i = i + last + 1;
                    break;
                case '(':
                    if (cando) {
                        count++;
                        i = i + 2;
                        cando = false;
                    } else {
                        i++;
                    }
                    break;
                case '\\':
                    if (pcre.regex.charAt(i + 1) == 'x' || pcre.regex.charAt(i + 1) == 'X') {  // \xFF
                        if (cando) {
                            count++;
                            i = i + 5;
                        } else {
                            i = i + 4;
                        }
                        cando = true;
                    } else if (pcre.regex.charAt(i + 1) >= '0' && pcre.regex.charAt(i + 1) <= '9') {	// \000
                        if (cando) {
                            count++;
                            i = i + 5;
                        } else {
                            i = i + 4;
                        }
                        cando = true;
                    } else {	// \?
                        if (cando) {
                            count++;
                            i = i + 3;
                        } else {
                            i = i + 2;
                        }
                        cando = true;

                    }
                    break;
                case '.':
                default://is character;
                    if (cando) {
                        count++;
                        i = i + 2;
                    } else {
                        i++;
                    }
                    cando = true;
                    break;
            }
        }
        //System.out.println("After: " + pcre.regex);
        return count;
    }
     /**
     * Insert and character (176) into specific position
     * @param index
     */
    public static void insertAnd(PCRE pcre, int index) {
        //String temp2 = pcre.regex.substring(index);
        //String temp1 = pcre.regex.substring(0, index);
        //System.out.println("insert:"+ temp1+ "..."+temp2);
        //pcre.regex = temp1 + Refer._char_and + temp2;
    }
}
