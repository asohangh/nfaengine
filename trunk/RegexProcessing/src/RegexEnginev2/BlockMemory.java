/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RegexEnginev2;

import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class BlockMemory {

    public LinkedList<BlockChar> lchar = new LinkedList<BlockChar>();
    // Bit area
    public char[][] BRam;         // Bram structure
    public int width = 0;

    public BlockMemory() {
        //TODO
    }

    public void insertChar(LinkedList<BlockChar> lc) {
        this.lchar.addAll(lc);
    }

    public void insertChar(BlockChar lc) {
        this.lchar.add(lc);
    }

    public void reduceBlockChar() {
        // begin to reduce.
        for (int i = 0; i < this.lchar.size(); i++) {
            BlockChar temp = this.lchar.get(i);
            for (int j = i + 1; j < this.lchar.size(); j++) {
                //System.out.println(" " + i + "." + j);
                BlockChar walk = this.lchar.get(j);
                if (temp.compareTo(walk)) {
                    //replace it in block state level.

                    for (int k = 0; k < walk.lState.size(); k++) {
                        BlockState bs = walk.lState.get(k);
                        //there is two type of block state
                        bs.replaceChar(walk, temp);
                    }
                    //replace it at engine level.
                    walk.engine.listBlockChar.remove(walk);
                    if (walk.engine.listBlockChar.indexOf(temp) == -1) {
                        walk.engine.listBlockChar.add(temp);
                    }
                    //remove from current list.
                    this.lchar.remove(j);
                    j--; //just remove one char so ...
                }
            }
        }
        //update order;
        for (int i = 0; i < this.lchar.size(); i++) {
            //System.out.print(this.blockCharList.get(i).value + " ");
            this.lchar.get(i).order = i;
        }
        this.width = this.lchar.size();
    }

    void print() {
        System.out.println("BlockMemory: size: " + this.lchar.size());
        for (int i = 0; i < this.lchar.size(); i++) {
            this.lchar.get(i).print();
        }

    }

    public void fillBRAM() {
        this.width = this.lchar.size();
        int depth = 256;
        this.BRam = new char[depth][width];

        for (int i = 0; i < this.lchar.size(); i++) {
            boolean[] arr = this.lchar.get(i).value256;
            for (int j = 0; j < 256; j++) {
                if (arr[j]) {
                    this.BRam[j][i] = '1';
                } else {
                    this.BRam[j][i] = '0';
                }
            }
        }
    }
}
