package pcre;

/**
 *
 * @author heckarim
 *
 * This class contain content of PCRE
 *
 * include dedicated method for parse tree;
 *
 */
public class PcreRule {

    // pcre:  /<pattern>/[<modifier>]
    private String pattern;         //Content of pcreRule
    private String modifier;		//Modifier of pcreRule

    private int index;				//index of current element;
    private int nextIndex;			//index of next element, set after call getElement


    public String testPartten;

    public PcreRule(){
            pattern = "";
            modifier = "";
            index = 0;
            nextIndex = 0;
            testPartten = "";
    }

    public PcreRule(String rule){
            pattern = "";
            modifier = "";
            index = 0;
            nextIndex=0;
            parsePcre(rule.trim());
            formatPcre();
    }

    public void setPattern(String pattern){
        this.pattern = pattern;
    }
    public String getPattern(){
        return this.pattern;
    }
    public String getModifier(){
        return this.modifier;
    }

    /**
     * Separate pcre rule into pattern and modifier.
     *
     * @param rule :    pcre rule
     *          format: /<pattern>/[<modifier>]
     * return:  true if success
     */
    public boolean parsePcre(String rule){
        int i = Refer.getIndexOBlock(rule,'/','/');
        if(i != 0){
            this.pattern = rule.substring(1, i);
            this.pattern = this.pattern.trim();

            if(i < rule.length()-1)
                    modifier = rule.substring(i+1);
            return true;
        }
        else
            return false;
    }

    /**
     * Check if the end of pattern is reached.
     * @return
     */
    public boolean isNotEnd(){
            return index < this.pattern.length();
    }

    /**
     * Get element with current index position
     * @return
     *
     */
    public Element getElement(){
            Element ret = this.getElementAt(this.index);
            this.nextIndex = ret.getReturnIndex();
            return ret;
    }

    /**
     * Get element at specific position
     * @param index
     * @return  null if index is out of range
     */
    public Element getElementAt(int index){
       if(index >= this.pattern.length())
           return null;
                return new Element(this.pattern,index);
    }

    /**
     * Current position to index of next element;
     */
    public void nextElement(){
        this.index = this.nextIndex;
    }

    /**
     * Get next element
     * @return
     */
    public Element getNextElement(){
        return getElementAt(this.nextIndex);
    }

    /**
     * turn pcre pattern into formated model, easy to parse.
     * @return pattern after format;
     * note:
     *      - with '^' insert modifier 't'.
     *      - with '$' insert modifier 'z'.
     *      - insert character of '\176' for concatenation.
     */
    private String formatPcre(){
        //This function just add char_and in to pcre pattern
        pcre.Refer.println("Call format PCRE");
        pcre.Refer.println("Before: " + this.pattern);
        int i=0;
        boolean cando=false;
        while (i < this.pattern.length()){
            char chr = this.pattern.charAt(i);
            //System.out.println(chr);
            switch(chr){
            case Refer._char_and:
            case '|':
                    cando=false;
                    i++;
                    break;
            case '*':
            case '+':
            case '?':
            case ')':
                    cando =true;
                    i++;
                    break;
            case '{':
                    int last=Refer.getIndexOBlock(this.pattern.substring(i), '{', '}');
                    cando = true;
                    i=i+last+1;
                    break;
            case '[':
                    if(cando){
                            this.insertAnd(i);
                            i=i+1;
                            cando=false;
                    }
                    last=Refer.getIndexOBlock(this.pattern.substring(i), '[', ']');
                    //System.out.println(last);
                    //return;
                    cando = true;
                    i=i+last+1;
                    break;
            case '(':
                    if(cando){
                            this.insertAnd(i);
                            i=i+2;
                            cando=false;
                    }else
                            i++;
                    break;

            case '\\':
                    if(this.pattern.charAt(i+1) == 'x' || this.pattern.charAt(i+1)=='X'){  // \xFF
                            if(cando){
                                    this.insertAnd(i);
                                    i=i+5;
                            }else
                                    i=i+4;
                            cando =true;
                    }else if(this.pattern.charAt(i+1) >='0' && this.pattern.charAt(i+1)<='9'){	// \000
                            if(cando){
                                    this.insertAnd(i);
                                    i=i+5;
                            }else
                                    i=i+4;
                            cando=true;
                    }else{	// \?
                            if(cando){
                                    this.insertAnd(i);
                                    i=i+3;
                            }else
                                    i=i+2;
                            cando =true;

                    }
                    break;
            case '^':
                String temp2 = this.pattern.substring(i+1);
                String temp1 = this.pattern.substring(0,i);
                //System.out.println("insert:"+ temp1+ "..." + temp2);
                this.pattern = temp1 + temp2;
                this.modifier += "t";
                break;
                        case '$':
                temp2 = this.pattern.substring(i+1);
                temp1 = this.pattern.substring(0,i);
                //System.out.println("insert:"+ temp1+ "..." + temp2);
                this.pattern = temp1 + temp2;
                this.modifier += "z";
                break;
            case '.':
            default://is character;
                    if(cando){
                            this.insertAnd(i);
                            i=i+2;
                    }else
                            i++;
                    cando=true;
                    break;
            }
        }
        System.out.println("After: "+this.pattern);
        return this.pattern;
    }


    /**
     * Insert and character (176) into specific position
     * @param index
     */
    private void insertAnd(int index){
        String temp2 = this.pattern.substring(index);
        String temp1 = this.pattern.substring(0,index);
        //System.out.println("insert:"+ temp1+ "..."+temp2);
        this.pattern = temp1 + Refer._char_and + temp2;
    }

    public String toString(){
        return "/" + this.pattern + "/" + this.modifier;
    }
}
