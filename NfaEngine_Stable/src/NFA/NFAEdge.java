/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor'  */

package NFA;

import pcre.PcreRule;
import pcre.Refer;

/**
 *
 * @author Hoang Long Le
 */
public class NFAEdge {
       public  NFAState dest = null;
       //public  String accept = "";
       public boolean isEpsilon = true;
       public  NFAEdge nextEdge = null;

       public int code_id;
       public String value="null";

       public boolean onChar[] = null;
       //public String modify;
       public PcreRule rule = null;


       public  NFAEdge(PcreRule rule){
           this.rule = rule;
       }



       /**
        * 
        */
       public void convert2Array(){
          // this.modify = modify;
           this.onChar = new boolean[256];
           for(int i =0; i<256; i++)
               this.onChar[i]=false;

            switch(this.code_id){
                case Refer._char:
                    //just single char
                    this.convertSingle(this.value);
                    break;
                case Refer._class:
                    //this is class char
                    if (!this.value.contains("-")) // notchar class with range
                        this.convertCharClass(this.value);
                    break;
                case Refer._neg_class:
                    if (!this.value.contains("-")) // notchar class with range
                        this.convertNegCharClass(this.value);
                    break;
                case Refer._ascii_hex:
                    this.convertHex(this.value.substring(2));
                    break;
                case Refer._class_digit:
                    this.convertDigitClass();
                    break;
                case Refer._class_dot:
                    this.convertDotClass();
                    break;
                case Refer._class_word:
                    this.convertWordClass();
                    break;
                default:
                    this.convertSingle(this.value.substring(0));
                    break;
            }
       }

       public void convertSingle(String s){

           if(this.rule.getModifier().indexOf("i") != -1) // case insensitive
           {
               char tmpu = s.toUpperCase().charAt(0);
               char tmpl = s.toLowerCase().charAt(0);
               this.onChar[(int)tmpu] = true;
               this.onChar[(int)tmpl] = true;
           }
           else{
               char tmp = s.charAt(0);
               this.onChar[(int)tmp] = true;
           }
       }
       public void convertCharClass(String s){
            for (int i = 0; i < s.length(); i ++)
            {
                String hex;
                if (s.charAt(i) == '\\')
                {
                    switch (s.charAt(i + 1))
                    {
                        case 'x':
                        case 'X':
                            hex = s.substring(i + 2 , i + 4);
                            //System.out.println("..."+hex);
                            i = i + 3;
                            this.convertHex(hex);
                            break;
                        case 'd':

                            this.convertDigitClass();
                            i++;
                            break;
                        case 'w':
                            this.convertWordClass();
                            i++;
                            break;
                        case 's': // white space \x20

                            this.convertHex("20");
                            i++;
                            break;
                        case 'n': // LF \x0A

                            this.convertHex("0A");
                            i++;
                            break;
                       case 'r': // CR \x0D

                            this.convertHex("0D");
                            i++;
                            break;
                       case 't': // tab \x09

                            this.convertHex("09");
                            i++;
                            break;
                        case 'z': //range \zAZ
                            hex = s.substring(i + 2 , i + 4);
                            //System.out.println("..."+hex);
                            i = i + 3;;
                            this.convertRange(hex);
                            break;
                        default: // \?
                            hex = Integer.toHexString((int) s.charAt(i + 1));
                            this.convertHex(hex);
                            i++;
                            break;

                    }
                }
                else{

                    this.convertSingle("" + s.charAt(i));
                }
           }
       }


       public void convertRange(String s){
           int begin = (int) s.charAt(0);
           int end = (int) s.charAt(1);

           for (int i=begin; i<=end; i++)
               this.onChar[i] = true;
       }
       public void convertNegCharClass(String s){
            this.convertCharClass(s);
            for(int i=0; i < 256; i++)
                this.onChar[i] = !this.onChar[i];
       }
       public void convertHex(String s){
           int hex = Integer.parseInt(s, 16);
           this.onChar[hex] = true;
       }
       public void convertDotClass(){
           for(int i = 0; i < 256; i++)
               this.onChar[i] = true;
           if(this.rule.getModifier().indexOf("s") == -1) // not have s, dot not include \x10
           {
               this.onChar[10] = false;
           }
       }
       public void convertWordClass(){
           for(int i = 0; i<256; i++){
               if((i>=48 && i<=57) || (i>=65 && i<=90) ||(i>=97 && i<=122))
                   this.onChar[i] = true;
           }
       }
       public void convertDigitClass(){
            for(int i =0; i<256; i++){
                if(i>=48 && i<=57)
                    this.onChar[i] = true;
            }
       }
}
