/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor'  */
package NFA;

import PCRE.Refer;

/**
 *
 * @author Hoang Long Le & Heckarim
 */
public class NFAEdge {
    // two node of the edge

    public NFAState srcState = null;
    public NFAState dstState = null;
    // Indicate value of edge.
    public boolean isEpsilon = true;        // is epsilon ?
    public String value = null;             // accept character
    //TODO
    public String modifier = "abc"; //modifier is together with value.
    public boolean[] value256 = new boolean[256]; // store value position on 256 characters table.
    public int id = -1;                          // PCRE.refer
    // include for manage constraint repetiton operator.
    public boolean isConRep = false;

    NFAEdge() {
        //this.modifier = modifier;
        for (int i = 0; i < 256; i++) {
            value256[i] = false;
        }
        //todo 
    }


    /**
     *  conver "value" to 256 bit.
     */
    public void converto256() {
        if (this.isEpsilon) {
            return;
        }
        this.fillEntryValue();
/*
        System.out.println("Converto256 ");
        System.out.println("value : " + this.value);

        char chr = 'a';
        for (chr = 'a'; chr < 'e'; chr++) {
            if (value256[chr]) {
                System.out.println("    " + chr + " ok");
            } else {
                System.out.println("    " + chr + " no ");
            }
        }
*/
    }

    public void fillEntryValue() {
       // System.out.println("fillEntry NFAEdge");
        //System.out.println("id: " + this.id + " value " + this.value + " modifier " + this.modifier);
        int hexValue = -1;
        switch (this.id) {
            case Refer._char:
                //just single char
                this.fillSingle(this.value, this.modifier);
                break;
            case Refer._class:
                //this is class char
                this.fillCharClass(this.value, this.modifier);
                break;
            case Refer._neg_class:
                this.fillNegCharClass(this.value, this.modifier);
                break;
            case Refer._ascii_hex:
                // \xFF
                hexValue = Integer.parseInt(this.value.substring(2), 16);
                this.fillHex(hexValue);
                break;
            case Refer._class_digit:
                // \d
                this.fillDigitRange();
                break;
            case Refer._class_dot:
                // .
                this.fillDotClass(this.modifier);
                break;

            /*case Refer._char_start:
            case Refer._char_end:
            //FIXME
            hexValue = 10;
            this.buildHex(hexValue);
            break;*/
            case Refer._class_notspace:
                this.fillCharRangeNoSpace();
            case Refer._class_word:
                // \w
                this.fillCharRange();
                break;
            case Refer._op_between:
            case Refer._op_atleast:
            case Refer._op_exactly:
                break;
            default:
                this.fillSingle(this.value.substring(0), this.modifier);
                break;

        }



    }

    private void fillSingle(String value, String modifier) {
        //throw new UnsupportedOperationException("Not yet implemented");

        int ascii = (int) value.charAt(0);

        if (modifier.contains("i")) {
            if (ascii >= 65 && ascii <= 90) {
                value256[ascii] = true;
                value256[ascii + 32] = true;
            } else if (ascii >= 97 && ascii <= 122) {
                value256[ascii] = true;
                value256[ascii - 32] = true;
            } else // not letter
            {
                value256[ascii] = true;
            }
        } else { // case sensitive
            value256[ascii] = true;
        }
    }

    private void fillCharClass(String value, String modifier) {

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
                        value256[hex] = true;
                        i = i + 3;
                        break;
                    case 'd':
                        for (int j = 48; j <= 57; j++) {
                            value256[j] = true;
                        }
                        i++;
                        break;
                    case 'w':
                        //TODO
                        i++;
                        break;
                    case 's': // white space \x20 = 32
                        value256[32] = true;
                        i++;
                        break;
                    case 'n': // LF \x0A
                        value256[10] = true;
                        i++;
                        break;
                    case 'r': // CR \x0D
                        value256[14] = true;
                        i++;
                        break;
                    case 't': // tab \x09
                        value256[9] = true;
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
                                for (int j = from; j <= to; j++) {
                                    value256[j] = true;
                                }
                                for (int j = from1; j <= to1; j++) {
                                    value256[j] = true;
                                }
                            } else {// not letter
                                for (int j = from; j <= to; j++) {
                                    value256[j] = true;
                                }
                            }
                        } else { // case sensitive
                            for (int j = from; j <= to; j++) {
                                value256[j] = true;
                            }
                        }
                        i = i + 3;
                        break;
                    default: // \?
                        hex = (int) value.charAt(i + 1);
                        value256[hex] = true;
                        i++;
                        break;

                }
            } else {
                //value.charAt(i) != '\\' ex: a
                value256[(int) value.charAt(i)] = true;

            }
        }
    }

    /* this finction to fill \w*/
    private void fillCharRange() {
        //throw new UnsupportedOperationException("Not yet implemented");
        // 48 - 57 [0 - 9]
        // 65 - 90 [A - Z]
        // 97 - 122 [a - z]
        for (int i = 0; i <= 47; i++) {
            value256[i] = false;
        }
        for (int i = 48; i <= 57; i++) {
            value256[i] = true;
        }
        for (int i = 58; i <= 64; i++) {
            value256[i] = false;
        }
        for (int i = 65; i <= 90; i++) {
            value256[i] = true;
        }
        for (int i = 91; i <= 96; i++) {
            value256[i] = false;
        }
        for (int i = 97; i <= 122; i++) {
            value256[i] = true;
        }
        for (int i = 123; i <= 255; i++) {
            value256[i] = false;
        }
    }

    private void fillCharRangeNoSpace() {
        for (int i = 0; i <= 255; i++) {
            value256[i] = true;
        }
        char ch = ' ';
        value256[(int) ch] = false;
    }

    private void fillDigitRange() {
        //throw new UnsupportedOperationException("Not yet implemented");
        // 48 - 57 [0 - 9]
        for (int i = 0; i <= 47; i++) {
            value256[i] = false;
        }
        for (int i = 48; i <= 57; i++) {
            value256[i] = true;
        }
        for (int i = 58; i <= 255; i++) {
            value256[i] = false;
        }

    }

    private void fillNegCharClass(String value, String modifier) {
        //initialize
        for (int i = 0; i < 256; i++) {
            value256[i] = true;
        }
        // value = [^abc]
        int from = 0, to = 0;
        System.out.println("value: " + value);
        value = value.substring(2, value.length() - 1);
        System.out.println("value after: " + value);
        for (int i = 0; i < value.length(); i++) {
            int hex;
            if (value.charAt(i) == '\\') {
                switch (value.charAt(i + 1)) {
                    case 'x':
                    case 'X':
                        hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
                        value256[hex] = false;
                        i = i + 3;
                        break;
                    case 'd':
                        for (int j = 48; j <= 57; j++) {
                            value256[j] = false;
                        }
                        i++;
                        break;
                    case 'w':
                        //TODO
                        i++;
                        break;
                    case 's': // white space \x20 = 32
                        value256[32] = false;
                        i++;
                        break;
                    case 'n': // LF \x0A
                        value256[10] = false;
                        i++;
                        break;
                    case 'r': // CR \x0D
                        value256[14] = false;
                        i++;
                        break;
                    case 't': // tab \x09
                        value256[9] = false;
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
                                for (int j = from; j <= to; j++) {
                                    value256[j] = false;
                                }
                                for (int j = from1; j <= to1; j++) {
                                    value256[j] = false;
                                }
                            } else {// not letter
                                for (int j = from; j <= to; j++) {
                                    value256[j] = false;
                                }
                            }
                        } else { // case sensitive
                            for (int j = from; j <= to; j++) {
                                value256[j] = false;
                            }
                        }
                        i = i + 3;
                        break;
                    default: // \?
                        value256[(int) value.charAt(i + 1)] = false;
                        i++;
                        break;

                }
            } else {
                //value.charAt(i) != '\\' ex: a
                value256[(int) value.charAt(i)] = false;

            }
        }
    }

    private void fillHex(int hexValue) {
        //throw new UnsupportedOperationException("Not yet implemented");
        for (int i = 0; i < 256; i++) {
            if (i != hexValue) {
                value256[i] = false;
            } else {
                value256[i] = true;
            }
        }

    }

    private void fillDotClass(String modifier) {
        for (int i = 0; i < 256; i++) {
            if (!modifier.contains("s")) {
                //match all except '\n'
                if (i == 10) {
                    value256[i] = false;
                } else {
                    value256[i] = true;
                }
            } else {
                value256[i] = true;
            }

        }
    }
}
