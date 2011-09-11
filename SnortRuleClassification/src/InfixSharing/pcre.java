/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package InfixSharing;

import Regex.*;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class pcre {

    LinkedList<Element> elist;
    LinkedList<ShareableUnit> ilist = new LinkedList<ShareableUnit>();
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

        this.pcrerule = rule;
        parsePcre(rule.trim());
        parsePCREElement();

        //count;
        this.countNoChar();
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

    public LinkedList<ShareableUnit> getshareablepattern() {
        LinkedList<ShareableUnit> lsShareable = new LinkedList<ShareableUnit>();
        LinkedList<Element> le = new LinkedList<Element>();
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            if (e.isAtom && !e.isPrefix) { //TODO   not prefix
                le.add(e);
            } else {
                if (e.isRepetition) {
                    if (le.size() >= 3) {
                        le.removeLast();
                    }
                }
                if (le.size() >= 6) { //ok to ad
                    ShareableUnit in = new ShareableUnit(le);
                    lsShareable.add(in);
                    le = new LinkedList<Element>();
                } else {
                    le.removeAll(le);
                }
            }
        }
        if (le.size() >= 6) { //ok to ad
            ShareableUnit in = new ShareableUnit(le);
            lsShareable.add(in);
        }
        for (int i = 0; i < lsShareable.size(); i++) {
            lsShareable.get(i).regex = this;
        }
        for (int i = 0; i < this.elist.size(); i++) {
            Element e = this.elist.get(i);
            if (e.isParentheis) {
                lsShareable.addAll(((ElementParenthesis) e).getshareablepattern());
            }
        }
        this.ilist = lsShareable;
        for (int i = 0; i < this.ilist.size(); i++) {
            this.ilist.get(i).regex = this;
        }
         //replace shareable unit
        for (int i = 0; i < lsShareable.size(); i++) {
            this.replaceElement2shareable(lsShareable.get(i));
        }

        return lsShareable;
    }

    private void replaceElement2shareable(ShareableUnit get) {
        ElementShareableUnit e = new ElementShareableUnit(get);
        int begin = -1;
        for (int i = 0; i <= (this.elist.size() - get.lelement.size()); i++) {
            Element e1 = this.elist.get(i);
            Element e2 = get.lelement.getFirst();

            if (e1 == e2) {
                begin = i;
            }
        }
        if (begin == -1) {
            //error
            System.out.println("PCRE: Repalce: Error: ");
        } else {

            for (int i = 0; i < get.lelement.size(); i++) {
                this.elist.remove(begin);
            }
            this.elist.add(begin, e);
        }
        //replace inside parenthesis
        for (int i = 0; i < this.elist.size(); i++) {
            Element element = this.elist.get(i);
            if (element.isParentheis) {
                ElementParenthesis par = (ElementParenthesis) element;
                par.replaceElement2shareable(get);
            }

        }
    }
}
