package engineRe;

import NFA.NFAEdge;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import pcre.Refer;

public class BlockChar {

    public String value;
    public int code_id; // use for buildHDL
    public int id; //use for builHDL
    public LinkedList<BlockState> toState;
    public LinkedList<LinkedList> listToState;
    BufferedWriter bw = null;
    public ReEngine engine;
    public int[] array_id;


    public BlockChar(NFAEdge edge, ReEngine engine) {
        this.engine = engine;
        this.value = edge.value;
        this.id = 0;
        this.code_id = edge.code_id;
        this.toState = new LinkedList<BlockState>();
        this.listToState = new LinkedList<LinkedList>();
        this.array_id = new int[50];
        for(int i = 0; i< this.array_id.length; i++){
            array_id[i] = -1;
        }
    }

    public BlockChar(char c, ReEngine engine) {
        this.value = "" + c;
        this.code_id = Refer._char;
        this.engine = engine;
        this.id = 0;

    }

    public boolean isConsRep() { // is constraint repetition
        return this.code_id == pcre.Refer._op_atleast
                || this.code_id == pcre.Refer._op_between
                || this.code_id == pcre.Refer._op_exactly;
    }

    public int buildHDL(BufferedWriter bufwri) {
        int ret = 0;
        this.bw = bufwri;
        int hexValue;
        switch (this.code_id) {
            case Refer._char:
                //just single char
                this.buildSingle(this.value);
                break;
            case Refer._class:
                //this is class char
                this.buildCharClass(this.value);
                break;
            case Refer._neg_class:
                this.buildNegCharClass(this.value);
                break;
            case Refer._ascii_hex:
                hexValue = Integer.parseInt(this.value.substring(2), 16);
                this.buildHex(hexValue);
                break;
            case Refer._class_digit:
                this.buildDigitRange();
                break;
            case Refer._class_dot:
                this.buildDotClass();
                break;

            case Refer._char_start:
            case Refer._char_end:
                //FIXME
                hexValue = 10;
                this.buildHex(hexValue);
                break;

            case Refer._class_word:
                this.buildCharRange();
                break;
            case Refer._op_between:
            case Refer._op_atleast:
            case Refer._op_exactly:
                break;
            default:
                this.buildSingle(this.value.substring(0));
                break;
        }
        try {
            if (bufwri == null && bw != null) {
                bw.close();
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return ret;

    }

    public void buildSingle(String c) {
        int ascii = (int) c.charAt(0);
        String ass = "8'b" + Integer.toBinaryString(ascii);
        try {
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(this.engine._outputfolder + "charBlock_" + this.engine.id_num + "_" + this.id + "_" + ascii + ".v"));
            }
            bw.write("module charBlock_" + this.engine.id_num + "_" + this.id + "(out, char);\n");
            bw.write("// Char: " + this.value + "\n");
            bw.write("\tinput [7:0] char;\n");
            bw.write("\toutput out;\n");

            if (this.engine.rule.getModifier().contains("i")) {
                int ascii_1 = 0;
                String ass1;

                if (ascii >= 65 && ascii <= 90) // A - Z
                {
                    ascii_1 = ascii + 32;
                    ass1 = "8'b" + Integer.toBinaryString(ascii_1);
                    bw.write("\tassign out = ((char == " + ass + ") || (char == " + ass1 + ")) ? 1'b1 : 1'b0;\n");
                } else if (ascii >= 97 && ascii <= 122) // a - z
                {
                    ascii_1 = ascii - 32;
                    ass1 = "8'b" + Integer.toBinaryString(ascii_1);
                    bw.write("\tassign out = ((char == " + ass + ") || (char == " + ass1 + ")) ? 1'b1 : 1'b0;\n");
                } else //not letter
                {
                    bw.write("\tassign out = (char == " + ass + ") ? 1'b1 : 1'b0;\n");
                }

            } else {
                bw.write("\tassign out = (char == " + ass + ") ? 1'b1 : 1'b0;\n");
            }
            bw.write("endmodule\n");
            ;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This function for building block char hex
     * @param hex
     * sample:
     *          module charBlock_3_63(out, char);
    input [7:0] char;
    output out;
    assign out = (char == 8'b111111) ? 1 : 0;
    endmodule

     */
    public void buildHex(int hex) {
        String ass = "8'b" + Integer.toBinaryString(hex);
        try {
            //if(bw == null)  bw = new BufferedWriter(new FileWriter(this.engine._outputfolder + "charBlock_" + this.engine.id_num + "_" + this.id + ".v"));
            bw.write("module charBlock_" + this.engine.id_num + "_" + this.id + "(out, char);\n");
            bw.write("// Hex: " + Integer.toHexString(hex) + "\n");
            bw.write("\tinput [7:0] char;\n");
            bw.write("\toutput out;\n");
            bw.write("\tassign out = (char == " + ass + ") ? 1 : 0;\n");
            bw.write("endmodule\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  This block for building block match \d : any digit 0 - 9
     * sameple:
     *
     */
    public void buildDigitRange() {
        String from = "8'b" + Integer.toBinaryString(48);
        String to = "8'b" + Integer.toBinaryString(57);
        try {
            //if(bw == null) bw = new BufferedWriter(new FileWriter(this.engine._outputfolder + "charBlock_" + this.engine.id_num + "_" + this.id + "_" + "class_digit" + ".v"));
            bw.write("module charBlock_" + this.engine.id_num + "_" + this.id + "(out, char);\n");
            bw.write("// Digit: 0-9\n");
            bw.write("\tinput [7:0] char;\n");
            bw.write("\toutput out;\n");

            bw.write("\tassign out = (char >= " + from + " && char <= " + to + ") ? 1 : 0;\n");
            bw.write("endmodule\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This block for building block that match all char 0-9 A-Z a-z
     * sameple:
     */
    public void buildCharRange() {
        String from1 = "8'b" + Integer.toBinaryString(48); // 0-9
        String to1 = "8'b" + Integer.toBinaryString(57);
        String from2 = "8'b" + Integer.toBinaryString(65); //A-Z
        String to2 = "8'b" + Integer.toBinaryString(90);
        String from3 = "8'b" + Integer.toBinaryString(97); //a-z
        String to3 = "8'b" + Integer.toBinaryString(122);
        try {
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(this.engine._outputfolder + "charBlock_" + this.engine.id_num + "_" + this.id + "_" + "class_word" + ".v"));
            }
            bw.write("module charBlock_" + this.engine.id_num + "_" + this.id + "(out, char);\n");
            bw.write("//0-9 a-z A-Z\n");
            bw.write("\tinput [7:0] char;\n");
            bw.write("\toutput out;\n");

            bw.write("\tassign out = ((char >= " + from1 + " && char <= " + to1 + ")|| "
                    + "(char >= " + from2 + " && char <= " + to2 + ") ||"
                    + "(char >= " + from3 + " && char <= " + to3 + ")) ? 1 : 0;\n");
            bw.write("endmodule\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This function for building char class something like [abcd]
     * @param s
     * sample:
     *             module charBlock_1_CharClass(out, char);
    // Char class: abcd
    input [7:0] char;
    output out;
    assign out = (0 || (char == 8'b1100001)|| (char == 8'b1100010)|| (char == 8'b1100011)|| (char == 8'b1100100)) ? 1'b1 : 1'b0;
    endmodule

     */
    public void buildCharClass(String s) {
        try {
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(this.engine._outputfolder + "charBlock_" + this.engine.id_num + "_" + this.id + "_" + "CharClass" + ".v"));
            }
            bw.write("module charBlock_" + this.engine.id_num + "_" + this.id + "(out, char);\n");
            bw.write("// Char class: " + this.value + "\n");
            bw.write("\tinput [7:0] char;\n");
            bw.write("\toutput out;\n");
            bw.write("\tassign out = (0 ");

            // s= [abc]
            int from = 0, to = 0;
            s = s.substring(1, s.length() - 1);
            for (int i = 0; i < s.length(); i++) {
                int hex;

                if (s.charAt(i) == '\\') {
                    switch (s.charAt(i + 1)) {
                        case 'x':
                        case 'X':
                            hex = Integer.parseInt(s.substring(i + 2, i + 4), 16);
                            bw.write("|| (char == 8'b" + Integer.toBinaryString(hex) + ")");
                            i = i + 3;
                            break;
                        case 'd':
                            bw.write("|| (char >= 8'b00110000 && char <= 8'b00111001)");
                            i++;
                            break;
                        case 'w':
                            //TODO
                            i++;
                            break;
                        case 's': // white space \x20
                            bw.write("|| (char == 8'b00100000)");
                            i++;
                            break;
                        case 'n': // LF \x0A
                            bw.write("|| (char == 8'b00001010)");
                            i++;
                            break;
                        case 'r': // CR \x0D
                            bw.write("|| (char == 8'b00001101)");
                            i++;
                            break;
                        case 't': // tab \x09
                            bw.write("|| (char == 8'b00001001)");
                            i++;
                            break;
                        case 'z': // range
                            from = (int) Integer.valueOf(s.substring(i + 2, i + 4), 16);
                            to = (int) Integer.valueOf(s.substring(i + 4, i + 6), 16);
                            if (this.engine.rule.getModifier().contains("i")) {
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
                                    bw.write("|| (char >= 8'b" + Integer.toBinaryString(from) + " && char <= 8'b" + Integer.toBinaryString(to) + ")|| "
                                            + "(char >= 8'b" + Integer.toBinaryString(from1) + " && char <= 8'b" + Integer.toBinaryString(to1) + ")");
                                } else // not letter
                                {
                                    bw.write("|| (char >= 8'b" + Integer.toBinaryString(from) + " && char <= 8'b" + Integer.toBinaryString(to) + ")");
                                }
                            } else {
                                bw.write("|| (char >= 8'b" + Integer.toBinaryString(from) + " && char <= 8'b" + Integer.toBinaryString(to) + ")");
                            }
                            i = i + 3;
                            break;
                        default: // \?
                            hex = (int) s.charAt(i + 1);
                            bw.write("|| (char == 8'b" + Integer.toBinaryString(hex) + ")");
                            i++;
                            break;

                    }
                } else {
                    bw.write("|| (char == 8'b" + Integer.toBinaryString((int) s.charAt(i)) + ")");

                }
            }
            bw.write(") ? 1'b1 : 1'b0;\n");
            bw.write("endmodule\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * this function for building neg class  [^abc]
     * @param s
     * sample:
     *
     */
    public void buildNegCharClass(String s) {
        try {
            if (bw == null) {
                bw = new BufferedWriter(new FileWriter(this.engine._outputfolder + "charBlock_" + this.engine.id_num + "_" + this.id + "_" + "CharClass" + ".v"));
            }
            bw.write("module charBlock_" + this.engine.id_num + "_" + this.id + "(out, char);\n");
            bw.write("// Negative class: " + this.value + "\n");
            bw.write("\tinput [7:0] char;\n");
            bw.write("\toutput out;\n");
            bw.write("\tassign out = (0 ");

            s = s.substring(2, s.length() - 1);
            for (int i = 0; i < s.length(); i++) {
                int hex;
                int from, to;
                if (s.charAt(i) == '\\') {
                    switch (s.charAt(i + 1)) {
                        case 'x':
                        case 'X':
                            hex = Integer.parseInt(s.substring(i + 2, i + 4), 16);
                            bw.write("|| (char == 8'b" + Integer.toBinaryString(hex) + ")");
                            i = i + 3;
                            break;
                        case 'd':
                            bw.write("|| (char >= 8'b00110000 && char <= 8'b00111001)");
                            i++;
                            break;
                        case 'w':
                            //TODO
                            i++;
                            break;
                        case 's': // white space \x20
                            bw.write("|| (char == 8'b00100000)");
                            i++;
                            break;
                        case 'n': // LF \x0A
                            bw.write("|| (char == 8'b00001010)");
                            i++;
                            break;
                        case 'r': // CR \x0D
                            bw.write("|| (char == 8'b00001101)");
                            i++;
                            break;
                        case 't': // tab \x09
                            bw.write("|| (char == 8'b00001001)");
                            i++;
                            break;
                        case 'z': // range
                            from = (int) Integer.valueOf(s.substring(i + 2, i + 4), 16);
                            to = (int) Integer.valueOf(s.substring(i + 4, i + 6), 16);
                            if (this.engine.rule.getModifier().contains("i")) {
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
                                    bw.write("|| (char >= 8'b" + Integer.toBinaryString(from) + " && char <= 8'b" + Integer.toBinaryString(to) + ")|| "
                                            + "(char >= 8'b" + Integer.toBinaryString(from1) + " && char <= 8'b" + Integer.toBinaryString(to1) + ")");
                                } else // not letter
                                {
                                    bw.write("|| (char >= 8'b" + Integer.toBinaryString(from) + " && char <= 8'b" + Integer.toBinaryString(to) + ")");
                                }
                            } else {
                                bw.write("|| (char >= 8'b" + Integer.toBinaryString(from) + " && char <= 8'b" + Integer.toBinaryString(to) + ")");
                            }
                            i = i + 3;
                            break;
                        default: // \?
                            hex = (int) s.charAt(i + 1);
                            bw.write("|| (char == 8'b" + Integer.toBinaryString(hex) + ")");
                            i++;
                            break;
                    }
                } else {
                    bw.write("|| (char == 8'b" + Integer.toBinaryString((int) s.charAt(i)) + ")");

                }
            }
            bw.write(") ? 1'b0 : 1'b1;\n");
            bw.write("endmodule\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * this function for building dot class . match all character
     * @param s
     * sample:
     *
     */
    public void buildDotClass() {
        try {
            //if(bw == null) bw = new BufferedWriter(new FileWriter(this.engine._outputfolder + "charBlock_" + this.engine.id_num + "_" + this.id + "_" + "dot_class" + ".v"));
            bw.write("module charBlock_" + this.engine.id_num + "_" + this.id + "(out, char);\n");
            bw.write("// Match all char, modifier: " + this.engine.rule.getModifier() + "\n");
            bw.write("\tinput [7:0] char;\n");
            bw.write("\toutput out;\n");

            if (!this.engine.rule.getModifier().contains("s")) {
                bw.write("\tassign out = (char != " + "8'b00001010" + ") ? 1'b1 : 1'b0;\n");
            } else {
                bw.write("\tassign out = 1'b1;\n");
            }

            bw.write("endmodule\n");
            // bw.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
