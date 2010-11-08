/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package HDL_Bram;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class RuleUnit {
    public boolean isPrefix;
    public LinkedList<String> lpcre;
    public RuleUnit(){
        lpcre = new LinkedList<String>();
    }

    public void insertPcre(String pcre){
        this.lpcre.add(pcre);
    }
    public String getPrefix(){
        if(this.isPrefix){
            return lpcre.getFirst();
        }
        return null;
    }
    public LinkedList<String> getSubfix(){
        LinkedList<String> ret = new LinkedList<String>();
        if(this.isPrefix){
            for(int i =1; i<this.lpcre.size(); i++){
                ret.add(this.lpcre.get(i));
            }
        }
        if(ret.isEmpty())
            return null;
        else
            return ret;
    }
    public String getPcre(){
        if(this.isPrefix)
            return null;
        else
            return this.lpcre.getFirst();
    }

}
