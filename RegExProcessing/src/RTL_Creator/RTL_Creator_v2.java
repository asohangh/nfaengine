/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RTL_Creator;

import BRAM.BRAM;
import RegexEnginev2.Infix;
import RegexEnginev2.ReEngineGroup;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class RTL_Creator_v2 {

    public LinkedList<ReEngineGroup> lsGroup= new LinkedList<ReEngineGroup>();
    public boolean no_CRB;

    public void createGroup(int id) {
        ReEngineGroup group = new ReEngineGroup(id);
        this.lsGroup.add(group);
    }

    public void addPrefix(int id, LinkedList<String> list) {
        ReEngineGroup group = lsGroup.get(id);
        group.addPrefix(list);
    }

    public void addInfix(int id, LinkedList<String> list) {
        ReEngineGroup group = lsGroup.get(id);
        group.addInfix(list);
    }

    public void addEngine(int id, LinkedList<String> list) {
        ReEngineGroup group = lsGroup.get(id);
        group.addEngine(list);
    }

    public void print(int id){
        ReEngineGroup group = lsGroup.get(id);
        group.print();
    }

    void reduceChar(int id) {
        this.lsGroup.get(id).memory.reduceBlockChar();
    }

    public Infix getInfix(int gid, int id){
        ReEngineGroup g = this.lsGroup.get(gid);
        return g.getInfix(id);
    }

    public String printCharSize() {
        for(int i =0; i<this.lsGroup.size(); i++){
            System.out.println("Group " + i + " : ");
            System.out.println("\t #char : " + this.lsGroup.get(i).getNoChar());
            System.out.println("\t #char reduce : " + this.lsGroup.get(i).memory.width);
        }
        return null;
    }
}
