/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BRAM;

import engineRe.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import pcre.Refer;

/**
 *
 * @author Richard Le
 */
public class BRAM {

    public LinkedList<ReEngine> engineList; // list of engine
   // public char[] entries;                // string present a single entry
    public char[][] BRam;         // Bram structure
    public LinkedList<BlockChar> blockCharList; // need for routing from BRAM to State block
    public int ID;                         // ID of this BRAM
    int row = -1;
    int col = -1;
    public int width = -1;



    public BRAM(int id) {
        this.engineList = new LinkedList<ReEngine>();
        this.blockCharList = new LinkedList();
        this.ID = id;
        this.BRam = new char[256][1]; // deep = 256, width = 1
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
                if (this.engineList.get(0).compareBlockChar(temp, walk)) {
                    temp.listToState.add(walk.toState);
                    temp.array_id[temp.listToState.size() - 1] = walk.engine.id_num;
                    this.blockCharList.remove(walk);
                }
            }
        }

        /*for (int i = 0; i < engineList.size(); i++) {
        ReEngine temp = engineList.get(i);
        for (int j = 0; j < temp.listBlockChar.size(); j++) {
        BlockChar walk = temp.listBlockChar.get(j);
        for (int k = i + 1; k < engineList.size(); k++) {
        for (int h = 0; h < engineList.get(k).listBlockChar.size(); h++) {
        }
        }
        }
        }*/
        this.width = this.blockCharList.size();
    }

    public void fillEntryValue() {

        this.BRam = new char[256][this.blockCharList.size()];

        int hexValue = -1;
        for (col = 0; col < this.blockCharList.size(); col++) {
            BlockChar temp = this.blockCharList.get(col);
            switch (temp.code_id) {
            case Refer._char:
                //just single char
                this.fillSingle(temp.value, temp.engine.rule.getModifier());
                break;
            case Refer._class:
                //this is class char
                this.fillCharClass(temp.value, temp.engine.rule.getModifier());
                break;
            case Refer._neg_class:
                this.fillNegCharClass(temp.value, temp.engine.rule.getModifier());
                break;
            case Refer._ascii_hex:
                // \xFF
                hexValue = Integer.parseInt(temp.value.substring(2), 16);
                this.fillHex(hexValue);
                break;
            case Refer._class_digit:
                // \d
                this.fillDigitRange();
                break;
            case Refer._class_dot:
                // .
                this.fillDotClass(temp.engine.rule.getModifier());
                break;

            /*case Refer._char_start:
            case Refer._char_end:
                //FIXME
                hexValue = 10;
                this.buildHex(hexValue);
                break;*/

            case Refer._class_word:
                // \w
                this.fillCharRange();
                break;
            case Refer._op_between:
            case Refer._op_atleast:
            case Refer._op_exactly:
                break;
            default:
                this.fillSingle(temp.value.substring(0), temp.engine.rule.getModifier());
                break;
        }
    }



}

    private void fillSingle(String value, String modifier) {
        //throw new UnsupportedOperationException("Not yet implemented");

        int ascii = (int) value.charAt(0);
        //iniitialize
        for(int i = 0; i < 256; i++)
            BRam[i][col] = '0';

        if (modifier.contains("i")) {
            if (ascii >= 65 && ascii <= 90) {
                BRam[ascii][col] = '1';
                BRam[ascii + 32][col] = '1';
            }
            else if (ascii >= 97 && ascii <= 122) {
                BRam[ascii][col] = '1';
                BRam[ascii - 32][col] = '1';
            }
            else // not letter
                BRam[ascii][col] = '1';
        } else { // case sensitive
            BRam[ascii][col] = '1';
        }
    }

    private void fillCharClass(String value, String modifier) {
        //initialize
        for (int i = 0; i < 256; i++)
            BRam[i][col] = '0';
         // value = [abc]
            int from = 0, to = 0;
            value = value.substring(1, value.length() - 1);
            for (int i = 0; i < value.length(); i++) {
                int hex;
                if (value.charAt(i) == '\\') {
                    switch (value.charAt(i + 1)) {
                        case 'x':
                        case 'X':
                            hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
                            BRam[hex][col] = '1';
                            i = i + 3;
                            break;
                        case 'd':
                            for (int j = 48; j <= 57; j++)
                                BRam[j][col] = '1';
                            i++;
                            break;
                        case 'w':
                            //TODO
                            i++;
                            break;
                        case 's': // white space \x20 = 32
                            BRam[32][col] = '1';
                            i++;
                            break;
                        case 'n': // LF \x0A
                            BRam[10][col] = '1';
                            i++;
                            break;
                        case 'r': // CR \x0D
                            BRam[14][col] = '1';
                            i++;
                            break;
                        case 't': // tab \x09
                            BRam[9][col] = '1';
                            i++;
                            break;
                        case 'z': // range
                            from = (int) Integer.valueOf(value.substring(i + 2, i + 4), 16);
                            to = (int) Integer.valueOf(value.substring(i + 4, i + 6), 16);
                            if (modifier.contains("i")) {
                                int from1 = 0, to1 = 0;
                                if (from >= 65 && from <= 90) {
                                    from1 = from + 32;
                                } else if (from >= 97 && from <= 122) {
                                    from1 = from - 32;
                                }
                                if (to >= 65 && to <= 90) {
                                    to1 = to + 32;
                                } else if (from >= 97 && from <= 122) {
                                    to1 = to - 32;
                                }
                                if ((from >= 65 && from <= 90 && to >= 65 && to <= 90)
                                     || (from >= 97 && from <= 122 && to >= 97 && to <= 122)) {
                                     for (int j = from; j <= to; j++)
                                         BRam[j][col] = '1';
                                     for (int j = from1; j <= to1; j++)
                                         BRam[j][col] = '1';
                                } else  {// not letter
                                    for (int j = from; j <= to; j++)
                                        BRam[j][col] = '1';
                                }
                            } else { // case sensitive
                                 for (int j = from; j <= to; j++)
                                        BRam[j][col] = '1';
                            }
                            i = i + 3;
                            break;
                        default: // \?
                            hex = (int) value.charAt(i + 1);
                            BRam[hex][col] = '1';
                            i++;
                            break;

                    }
                } else {
                    //value.charAt(i) != '\\' ex: a
                    BRam[(int) value.charAt(i)][col] = '1';

                }
            }
    }

   /* this finction to fill \w*/
    private void fillCharRange() {
        //throw new UnsupportedOperationException("Not yet implemented");
        // 48 - 57 [0 - 9]
        // 65 - 90 [A - Z]
        // 97 - 122 [a - z]
        for(int i = 0; i <= 47; i++)
            BRam[i][col] = '0';
        for(int i = 48; i <= 57; i++)
            BRam[i][col] = '1';
        for(int i = 58; i <= 64; i++)
            BRam[i][col] = '0';
        for(int i = 65; i <= 90; i++)
            BRam[i][col] = '1';
        for(int i = 91; i <= 96; i++)
            BRam[i][col] = '0';
        for(int i = 97; i <= 122; i++)
            BRam[i][col] = '1';
        for(int i = 123; i <= 255; i++)
            BRam[i][col] = '0';
    }


    private void fillDigitRange() {
        //throw new UnsupportedOperationException("Not yet implemented");
        // 48 - 57 [0 - 9]
         for(int i = 0; i <= 47; i++)
            BRam[i][col] = '0';
        for(int i = 48; i <= 57; i++)
            BRam[i][col] = '1';
        for(int i = 58; i <= 255; i++)
            BRam[i][col] = '0';

    }

    private void fillNegCharClass(String value, String modifier) {
        //initialize
        for (int i = 0; i < 256; i++)
            BRam[i][col] = '1';
         // value = [abc]
            int from = 0, to = 0;
            value = value.substring(1, value.length() - 1);
            for (int i = 0; i < value.length(); i++) {
                int hex;
                if (value.charAt(i) == '\\') {
                    switch (value.charAt(i + 1)) {
                        case 'x':
                        case 'X':
                            hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
                            BRam[hex][col] = '0';
                            i = i + 3;
                            break;
                        case 'd':
                            for (int j = 48; j <= 57; j++)
                                BRam[j][col] = '0';
                            i++;
                            break;
                        case 'w':
                            //TODO
                            i++;
                            break;
                        case 's': // white space \x20 = 32
                            BRam[32][col] = '0';
                            i++;
                            break;
                        case 'n': // LF \x0A
                            BRam[10][col] = '0';
                            i++;
                            break;
                        case 'r': // CR \x0D
                            BRam[14][col] = '0';
                            i++;
                            break;
                        case 't': // tab \x09
                            BRam[9][col] = '0';
                            i++;
                            break;
                        case 'z': // range
                            from = (int) Integer.valueOf(value.substring(i + 2, i + 4), 16);
                            to = (int) Integer.valueOf(value.substring(i + 4, i + 6), 16);
                            if (modifier.contains("i")) {
                                int from1 = 0, to1 = 0;
                                if (from >= 65 && from <= 90) {
                                    from1 = from + 32;
                                } else if (from >= 97 && from <= 122) {
                                    from1 = from - 32;
                                }
                                if (to >= 65 && to <= 90) {
                                    to1 = to + 32;
                                } else if (from >= 97 && from <= 122) {
                                    to1 = to - 32;
                                }
                                if ((from >= 65 && from <= 90 && to >= 65 && to <= 90)
                                     || (from >= 97 && from <= 122 && to >= 97 && to <= 122)) {
                                     for (int j = from; j <= to; j++)
                                         BRam[j][col] = '0';
                                     for (int j = from1; j <= to1; j++)
                                         BRam[j][col] = '0';
                                } else  {// not letter
                                    for (int j = from; j <= to; j++)
                                        BRam[j][col] = '0';
                                }
                            } else { // case sensitive
                                 for (int j = from; j <= to; j++)
                                        BRam[j][col] = '0';
                            }
                            i = i + 3;
                            break;
                        default: // \?
                            BRam[(int) value.charAt(i + 1)][col] = '0';
                            i++;
                            break;

                    }
                } else {
                    //value.charAt(i) != '\\' ex: a
                    BRam[(int) value.charAt(i)][col] = '0';

                }
            }
    }


    private void fillHex(int hexValue) {
        //throw new UnsupportedOperationException("Not yet implemented");
         for(int i = 0; i < 256; i++) {
            if (i != hexValue)
                BRam[i][col] = '0';
            else
                BRam[i][col] = '1';
        }

    }

    private void fillDotClass(String modifier) {
        for(int i = 0; i < 256; i++) {
            if (!modifier.contains("s")) {
                //match all except '\n'
                if (i == 10)
                    BRam[i][col] = '0';
                else
                    BRam[i][col] = '1';
            } else
                BRam[i][col] = '1';

        }
    }

    public void printBRam() {
        System.out.println("Width: " + width);
        for(int i = 0; i < this.blockCharList.size(); i ++) {
            System.out.print(this.blockCharList.get(i).value + " ");
        }
        System.out.println();
        for (int i = 0 ; i < 256; i++) {
            if (i >= 32 && i <= 127)
                System.out.print((char) i + ": ");
            else
                System.out.print(i + ": ");
            for(int j = 0; j < width; j++ )
                System.out.print(BRam[i][j] + " ");
            System.out.println();
        }
    }

    public void buildCOE() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + System.getProperty("file.separator") + "BRAM_" + ID + ".coe" ));
            bw.write(";");
            for(int i = 0; i < this.blockCharList.size(); i ++) {
                bw.write(this.blockCharList.get(i).value + " ");
            }
            bw.write("\n");
            bw.write(";Block memory of depth=256, and width=" + width + "\n"
                    + "MEMORY_INITIALIZATION_RADIX=2;\n"
                    + "MEMORY_INITIALIZATION_VECTOR=\n");
            for (int i = 0; i < 256; i++) {
                for(int j = 0; j < width; j++) {
                    bw.write(BRam[i][j]);
                }
                if (i != 255)
                    bw.write(",\n");
                else if (i == 255)
                    bw.write(";\n");
            }

            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



