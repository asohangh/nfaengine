/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BRAM;

import engineRe.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Richard Le
 */
public class BRAM {

    public LinkedList<ReEngine> engineList; // list of engine
    public String[] BRam;                // string present a single entry
    //public ArrayList <String> BRam;         // Bram structure
    public LinkedList<BlockChar> blockCharList; // need for routing from BRAM to State block
    public int ID;                         // ID of this BRAM

    public BRAM(int id) {
        this.engineList = new LinkedList<ReEngine>();
        this.BRam = new String[256]; // deep = 256
        this.blockCharList = new LinkedList();
        this.ID = id;
    }

    public void addEngine(ReEngine engine, int id) {
        this.engineList.add(engine);
        this.engineList.get(this.engineList.size() - 1).id_num = id;
    }

    /**************************************
     * This function to share charBlock
     *
     ***************************************/
    public void unionCharBlocks() {
        //TO DO
        for (int i = 0; i < engineList.size(); i++) {
            engineList.get(i).reduceBlockChar();
            for (int j = 0; j < engineList.get(i).listBlockChar.size(); j++) {
                this.blockCharList.add(engineList.get(i).listBlockChar.get(j));
            }
        }
        for (int i = 0; i < this.blockCharList.size(); i++) {
            BlockChar temp = this.blockCharList.get(i);
            temp.array_id[0] = temp.engine.id_num;
            temp.listToState.add(temp.toState);
            for (int j = i + 1; j < this.blockCharList.size(); j++) {
                BlockChar walk = this.blockCharList.get(j);
                if (this.compareBlockChar(temp, walk)) {
                    temp.listToState.add(walk.toState);
                    temp.array_id[temp.listToState.size() - 1] = walk.engine.id_num;
                    this.blockCharList.remove(walk);
                }
            }
        }
    }

    public boolean compareBlockChar(BlockChar temp, BlockChar walk) {
        boolean res = false;
        if (temp.code_id == walk.code_id) {
            if (temp.value.compareTo(walk.value) == 0) {
                /*for (int k = 0; k < walk.toState.size(); k++) {
                temp.toState.add(walk.toState.get(k));
                walk.toState.get(k).acceptChar = temp;
                }*/
                res = true;
            } else {
                if (walk.engine.rule.getModifier().indexOf("i") != -1 && temp.engine.rule.getModifier().indexOf("i") != -1 && temp.value.compareToIgnoreCase(walk.value) == 0) {// neu la case insensitive
                    //chep toState tu walk vo temp;
                    /*for (int k = 0; k < walk.toState.size(); k++) {
                    temp.toState.add(walk.toState.get(k));
                    walk.toState.get(k).acceptChar = temp;
                    }*/
                    res = true;
                }
            }
        }
        return res;
    }

    public void print() {
        System.out.print("\n\tBRAM\nList Block Char:  ");
        for (int i = 0; i < this.blockCharList.size(); i++) {
            BlockChar temp = this.blockCharList.get(i);
            System.out.print(temp.value + "  ");
        }
        for (int i = 0; i < this.blockCharList.size(); i++) {
            BlockChar temp = this.blockCharList.get(i);
            System.out.println("  Block Char: " + temp.value);
            for (int j = 0; j < temp.listToState.size(); j++) {
                for (int k = 0; k < temp.listToState.get(j).size(); k++) {
                    BlockState tempState = (BlockState) temp.listToState.get(j).get(k);
                    System.out.println("Engine: " + temp.array_id[j] + " state: " + tempState.id);
                }
            }
        }
    }

    public void fillEntryValue() {
    }
}



