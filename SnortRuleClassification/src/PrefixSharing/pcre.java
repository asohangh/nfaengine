/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PrefixSharing;

import Regex.Element;
import Regex.ElementParser;
import Regex.ElementPrefix;
import Regex.Refer;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class pcre {

    LinkedList<Element> elist;
    LinkedList<RePrefix> prelist = new LinkedList<RePrefix>();
    // pcre:  /<pattern>/[<modifier>]
    private String regex;         //Content of pcreRule
    private String modifier;		//Modifier of pcreRule
    //
    private String pcrerule;
    //
    private int noChar = 0; //number of char element

    public pcre() {
        regex = "";
        modifier = "";
    }

    public pcre(String rule) {
        regex = "";
        modifier = "";
        System.out.println("pcre.java. Parse rule: " + rule);
        this.pcrerule = rule;
        parsePcre(rule.trim());
        parsePCREElement();

        //count;
        this.countNoChar();
    }

    public String getRule() {
        return pcrerule;
    }

    public void setPattern(String pattern) {
        this.regex = pattern;
    }

    public String getPattern() {
        return this.regex;
    }

    public String getModifier() {
        return this.modifier;
    }

    /**
     * Separate pcre rule into pattern and modifier.
     *
     * @param rule :    pcre rule
     *          format: /<pattern>/[<modifier>]
     * return:  true if success
     */
    public boolean parsePcre(String rule) {
        int i = Refer.getIndexOBlock(rule, '/', '/');
        if (i != 0) {
            this.regex = rule.substring(1, i);
            this.regex = this.regex.trim();

            if (i < rule.length() - 1) {
                modifier = rule.substring(i + 1);
            }
            return true;
        } else {
            return false;
        }
    }

    private void parsePCREElement() {
        ElementParser ep = new ElementParser(this.regex, this.modifier);
        ep.parseElement();
        this.elist = ep.lelement;
    }

    public RePrefix getMaxprefix(pcre pc) {
        RePrefix pre = null;
        LinkedList<Element> le = new LinkedList<Element>();
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            if (i < pc.elist.size() && e.compareto(pc.elist.get(i))) {
                le.add(e);
            } else {
                if(e.isRepetition)
                    le.removeLast();
                break;
            }
        }
        if (le.size() >= 3) {
            pre = new RePrefix(le);
            pre.lregex.add(this);
            pre.lregex.add(pc);
        }
        return pre;
    }

    public void addPrefix(RePrefix pre) {
        this.prelist.add(pre);
    }

    private void countNoChar() {
        int count = 0;
        for (int i = 0; i < this.elist.size(); i++) {
            count += this.elist.get(i).getNumAtom();
        }
        this.noChar = count;
    }

    public int getNoAtom() {
        return noChar;
    }

    void replacePrefix(RePrefix prefix) {
        // System.out.println("\nShareabelunit Replace:" + infix.getString());
        //System.out.println("\t\tbefore " + this.getString());
        ElementPrefix e = new ElementPrefix(prefix.id);
        //this.print();
        //infix.print();
        int begin = -1;
        int next = 0;
        for (int i = 0; i < this.elist.size(); i++) {
            Element e1 = this.elist.get(i);
            Element e2 = prefix.lelement.get(next);
            // System.out.println("e1: " + e1.toString() + " - e2: " + e2.toString());
            if (e1.compareto(e2)) {
                if (begin == -1) {
                    begin = i;
                }
                next++;
            } else {
                begin = -1;
                if (next >= 1) {
                    i--;
                }
                next = 0;
            }
            // System.out.println("begin, next " + begin + " , " +next);

            if (next == prefix.lelement.size()) {
                break;
            }
        }
        if (begin == -1 || next != prefix.lelement.size()) {
            //error
            System.out.println("ShareUnit: Repalce: Error: ");
        }
        for (int i = 0; i < prefix.lelement.size(); i++) {
            this.elist.remove(begin);
        }
        this.elist.add(begin, e);
        //System.out.println("\t\tafter  " + begin + " .. " + this.getString());
    }

    public String getString() {
        String s = "";
        for (int i = 0; i < this.elist.size(); i++) {
            s += this.elist.get(i).toString();
        }
        return s;
    }

    public String getRuleString() {
        String s = "";
        s = this.getString();
        s = "/" + s + "/" + this.modifier;

        return s;
    }
}
