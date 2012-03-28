package test;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.*;
import java.io.*;

public class regex2nfa {

    public static double log2(double x) {
        // Math.log is base e, natural log, ln
        return Math.log(x) / Math.log(2);
    }

    public static class state {

        int id;
        LinkedList<edge> listedge;
        LinkedList<state> listS;
        boolean isStart = false;
        boolean isMark = false;
    }

    public static class edge {

        state goingState;
        state comingState;
        char type1;
        char type2;
    }

    public static class sdfa {
        int id;
        sdfa[] list = new sdfa[256];
        boolean isStart = false;
        boolean isMark = false;
        LinkedList<state> arr = new LinkedList<state>();
        LinkedList<Integer> arr2 = new LinkedList<Integer>();
    }

    public static void main(String[] args) throws IOException {
        FileInputStream fin;
        FileOutputStream fout;
        try {
            //open input file
            String infile = System.getProperty("user.dir") + File.separator + "source.txt";
            String outfile = System.getProperty("user.dir") + File.separator + "dest.txt";
            try {
                fin = new FileInputStream(infile);
            } catch (FileNotFoundException exc) {
                System.out.println("Input File Not Found");
                return;
            }
            //open output file
            try {
                fout = new FileOutputStream(outfile);
            } catch (IOException exc) {
                System.out.println("Error Opening Output");
                return;
            }
        } catch (ArrayIndexOutOfBoundsException exc) {
            System.out.println("Usage:CopyFile From To");
            return;
        }
        
        //hien thuc regex->nfa
        LinkedList<LinkedList<state>> liststate = new LinkedList<LinkedList<state>>();
        LinkedList<state> signature = new LinkedList<state>();
        LinkedList<edge> tmpListedge = new LinkedList<edge>();
        LinkedList<state> tmpListS;
        edge tmpEdge = new edge();
        state tmpState1 = new state();
        state tmpState2 = new state();
        state tmpState3 = new state();
        state tmpState4 = new state();
        state tmpState5 = new state();
        int a, b, c;  //bien luu ki tu read tu file
        int flag = 1; //khi add new signature thi bat co nay len
        int f_or = 0;
        int i = 1;   //id cua cac state tang tu 1->n
        try {
            do {
                a = fin.read();
                if (a != 13) {
                    if (a != -1) {
                        if (a != '\n') {
                            switch (a) {
                                case '0':
                                case '1':
                                case '2':
                                case '3':
                                case '4':
                                case '5':
                                case '6':
                                case '7':
                                case '8':
                                case '9':
                                case 'a':
                                case 'b':
                                case 'c':
                                case 'd':
                                case 'e':
                                case 'f':
                                case '?':
                                    b = fin.read();
                                    tmpState1 = new state();
                                    tmpState2 = new state();
                                    tmpState1.id = i++;
                                    tmpState2.id = i++;
                                    tmpListedge = new LinkedList<edge>();
                                    tmpEdge = new edge();
                                    tmpEdge.goingState = tmpState1;
                                    tmpEdge.comingState = tmpState2;
                                    tmpEdge.type1 = (char) a;
                                    tmpEdge.type2 = (char) b;
                                    tmpListedge.add(tmpEdge);
                                    tmpState1.listedge = tmpListedge;
                                    if (flag == 1) {
                                        flag = 0;
                                        tmpState1.isStart = true;
                                    } else {
                                        if (f_or != 1) {
                                            tmpState3 = new state();
                                            tmpState3 = signature.getLast();
                                            if (tmpState3.listS != null) {
                                                tmpListS = tmpState3.listS;
                                            } else {
                                                tmpListS = new LinkedList<state>();
                                            }
                                            tmpListS.addLast(tmpState1);
                                            tmpState3.listS = tmpListS;
                                        } else {
                                            f_or = 0;
                                            tmpState3 = new state();
                                            tmpState4 = new state();
                                            tmpState4 = signature.removeLast();
                                            tmpState3 = signature.getLast();
                                            //tmpListS = new LinkedList<state>();
                                            if (tmpState3.listS != null) {
                                                tmpListS = tmpState3.listS;
                                            } else {
                                                tmpListS = new LinkedList<state>();
                                            }
                                            tmpListS.addLast(tmpState1);
                                            tmpState3.listS = tmpListS;
                                            if (tmpState4.listS != null) {
                                                tmpListS = tmpState4.listS;
                                            } else {
                                                tmpListS = new LinkedList<state>();
                                            }
                                            tmpListS.addLast(tmpState1);
                                            tmpState4.listS = tmpListS;
                                            signature.addLast(tmpState4);
                                        }
                                    }
                                    signature.addLast(tmpState1);
                                    signature.addLast(tmpState2);
                                    break;
                                case '*':
                                    tmpState1 = new state();
                                    tmpState2 = new state();
                                    tmpState1.id = i++;
                                    tmpState2.id = i++;
                                    tmpListedge = new LinkedList<edge>();
                                    tmpEdge = new edge();
                                    tmpEdge.goingState = tmpState1;
                                    tmpEdge.comingState = tmpState2;
                                    tmpEdge.type1 = (char) a;
                                    tmpEdge.type2 = (char) 0;
                                    tmpListedge.addLast(tmpEdge);
                                    tmpState1.listedge = tmpListedge;
                                    tmpListS = new LinkedList<state>();
                                    tmpListS.addLast(tmpState2);
                                    tmpState1.listS = tmpListS;
                                    tmpListS = new LinkedList<state>();
                                    tmpListS.addLast(tmpState1);
                                    tmpState2.listS = tmpListS;
                                    if (flag == 1) {
                                        flag = 0;
                                        tmpState1.isStart = true;
                                    } else {
                                        tmpState3 = new state();
                                        tmpState3 = signature.getLast();
                                        if (tmpState3.listS != null) {
                                            tmpListS = tmpState3.listS;
                                        } else {
                                            tmpListS = new LinkedList<state>();
                                        }
                                        tmpListS.addLast(tmpState1);
                                        tmpState3.listS = tmpListS;
                                    }
                                    signature.addLast(tmpState1);
                                    signature.addLast(tmpState2);
                                    break;
                                case '(':
                                    f_or = 1;
                                    tmpState1 = new state();
                                    tmpState2 = new state();
                                    tmpState3 = new state();
                                    tmpState4 = new state();
                                    tmpState1.id = i++;
                                    tmpState2.id = i++;
                                    tmpState3.id = i++;
                                    tmpState4.id = i++;
                                    b = fin.read();
                                    c = fin.read();
                                    tmpListedge = new LinkedList<edge>();
                                    tmpEdge = new edge();
                                    tmpEdge.goingState = tmpState1;
                                    tmpEdge.comingState = tmpState3;
                                    tmpEdge.type1 = (char) b;
                                    tmpEdge.type2 = (char) c;
                                    tmpListedge.addLast(tmpEdge);
                                    tmpState1.listedge = tmpListedge;
                                    b = fin.read();
                                    b = fin.read();
                                    c = fin.read();
                                    tmpListedge = new LinkedList<edge>();
                                    tmpEdge = new edge();
                                    tmpEdge.goingState = tmpState2;
                                    tmpEdge.comingState = tmpState4;
                                    tmpEdge.type1 = (char) b;
                                    tmpEdge.type2 = (char) c;
                                    tmpListedge.addLast(tmpEdge);
                                    tmpState2.listedge = tmpListedge;
                                    tmpState5 = new state();
                                    tmpState5 = signature.getLast();
                                    if (tmpState5.listS != null) {
                                        tmpListS = tmpState5.listS;
                                    } else {
                                        tmpListS = new LinkedList<state>();
                                    }
                                    tmpListS.add(tmpState1);
                                    tmpListS.add(tmpState2);
                                    tmpState5.listS = tmpListS;
                                    signature.addLast(tmpState1);
                                    signature.addLast(tmpState2);
                                    signature.addLast(tmpState3);
                                    signature.addLast(tmpState4);
                                    b = fin.read();
                                    break;
                                default:
                                    break;
                            }
                        } else {   //ket thuc 1 signature
                            flag = 1;
                            tmpState1 = new state();
                            tmpState1 = signature.getLast();
                            tmpState1.isMark = true;
                            liststate.add(signature);
                            signature = new LinkedList<state>();
                        }
                    }
                }
            } while (a != -1);
        } catch (IOException exc) {
            System.out.println("File Error");
        }
        
        //System.out.println("Begin calculate");
        //hien thuc n nfa -> 1 nfa
        tmpState1 = new state();
        tmpState1.id = 0;
        tmpState1.isStart = true;
        //lay tung signature -> lay first state -> gan vao
        for (int j = 0; j < liststate.size(); j++) {
            signature = new LinkedList<state>();
            signature = liststate.get(j);
            tmpState2 = new state();
            tmpState2 = signature.getFirst();
            tmpState2.isStart = false;
            if (tmpState1.listS != null) {
                tmpListS = tmpState1.listS;
            } else {
                tmpListS = new LinkedList<state>();
            }
            tmpListS.addLast(tmpState2);
            tmpState1.listS = tmpListS;
        }
        //cac state trong liststate co 1 canh chuyen ve state.id(0)
        for (int j = 0; j < liststate.size(); j++) {
            signature = new LinkedList<state>();
            signature = liststate.get(j);
            for (int k = 0; k < signature.size(); k++) {
                tmpState2 = new state();
                tmpState2 = signature.get(k);
                if (tmpState2.listS != null) {
                    tmpListS = tmpState2.listS;
                } else {
                    tmpListS = new LinkedList<state>();
                }
                tmpListS.addLast(tmpState1);
                tmpState2.listS = tmpListS;
            }
        }
        System.out.println("Finish creating NFA");
        
        for(i=0; i<liststate.size(); i++){
            LinkedList<state> lstate = liststate.get(i);

            for(int j=0; j<lstate.size(); j++){
                state s  = lstate.get(j);
                System.out.println("State " + s.id + ": ");
                for(int k =0; k< s.listedge.size(); k++){
                    System.out.println("\tedge: " + s.listedge.get(k).toString());
                }
            }
        }

        //nfa -> dfa
        i = 0;  //bien dem id
        int r = 0;
        LinkedList<sdfa> listDFA = new LinkedList<sdfa>();
        LinkedList<sdfa> ldfa;
        sdfa tmpSdfa;
        tmpSdfa = new sdfa();
        tmpSdfa.id = i++;
        tmpSdfa.isStart = true;
        tmpSdfa.arr.addLast(tmpState1);
        listDFA.addLast(tmpSdfa);
        sdfa tmpSdfa2;
        sdfa tmpSdfa3;
        sdfa tmpSdfa4;
        for (int j = 0; j < listDFA.size(); j++) {
            //lay node DFA tu listDFA
            tmpSdfa = listDFA.get(j);
            //System.out.println(i);
            //boolean f_sao = false;
            //int[] listSao = new int[2];
            //xu ly node DFA -> xu ly cac node nfa trong tmpSdfa.arr
            for (int k = 0; k < tmpSdfa.arr.size(); k++) {
                tmpState1 = tmpSdfa.arr.get(k);
                //xu ly node NFA
                if (tmpState1.listS != null) {
                    for (int t = 0; t < tmpState1.listS.size(); t++) {
                        tmpState2 = tmpState1.listS.get(t);
                        //neu tmpState2 chua co trong tmpSdfa.arr thi add vao
                        //neu co roi thi bo wa
                        int f_tontai = 0;
                        for (int u = 0; u < tmpSdfa.arr.size(); u++) {
                            if (tmpState2 == tmpSdfa.arr.get(u)) {
                                f_tontai = 1;
                            }
                        }
                        if (f_tontai == 0) {
                            tmpSdfa.arr.addLast(tmpState2);
                        }
                    }
                }
            }
            ldfa = new LinkedList<sdfa>();
            tmpSdfa3 = new sdfa();  //luu state co input=??
            r = i;
            for (int k = 0; k < tmpSdfa.arr.size(); k++) {
                tmpState1 = tmpSdfa.arr.get(k);
                if (tmpState1.listedge != null) {
                    for (int t = 0; t < tmpState1.listedge.size(); t++) {
                        tmpState2 = tmpState1.listedge.get(t).comingState;
                        char ch1 = tmpState1.listedge.get(t).type1;
                        char ch2 = tmpState1.listedge.get(t).type2;
                        tmpSdfa2 = new sdfa();
                        if (tmpState2.isMark) {
                            tmpSdfa2.isMark = true;
                        }
                        tmpSdfa2.id = r++;
                        int addNewState = 1;
                        if ((ch1 == '?') || (ch1 == '*')) {
                            tmpSdfa2.arr.addLast(tmpState2);
                            tmpSdfa2.arr2.addLast(tmpState2.id);
                            for (int u = 0; u < 256; u++) {
                                if (tmpSdfa.list[u] == null) {
                                    tmpSdfa.list[u] = tmpSdfa2;
                                } else {
                                    tmpSdfa.list[u].arr.addLast(tmpState2);
                                    tmpSdfa.list[u].arr2.addLast(tmpState2.id);
                                }
                            }
                            tmpSdfa3 = tmpSdfa2;
                            //if(ch1=='*'){
                            //    f_sao = true;
                            //    listSao[1]=tmpState2.id;
                            //    listSao[0]=tmpState1.id-1;
                            //}
                        } else {    //truong hop: 00->ff
                            //doi 0xab -> thapfan
                            int tr = 0;
                            if ((ch1 >= '0') && (ch1 <= '9')) {
                                tr = (ch1 - 48) * 16;
                            } else {
                                tr = (ch1 - 87) * 16;
                            }
                            if ((ch2 >= '0') && (ch2 <= '9')) {
                                tr = tr + (ch2 - 48);
                            } else {
                                tr = tr + (ch2 - 87);
                            }
                            if (tmpSdfa.list[tr] == null) {
                                tmpSdfa2.arr.addLast(tmpState2);
                                tmpSdfa2.arr2.addLast(tmpState2.id);
                                tmpSdfa.list[tr] = tmpSdfa2;
                            } else {
                                if (tmpSdfa3.id == tmpSdfa.list[tr].id) {
                                    for (int u = 0; u < tmpSdfa3.arr.size(); u++) {
                                        tmpSdfa2.arr.addLast(tmpSdfa3.arr.get(u));
                                        tmpSdfa2.arr.addLast(tmpState2);
                                        tmpSdfa2.arr2.addLast(tmpSdfa3.arr2.get(u));
                                        tmpSdfa2.arr2.addLast(tmpState2.id);
                                        tmpSdfa.list[tr] = tmpSdfa2;
                                    }
                                } else {
                                    tmpSdfa.list[tr].arr.addLast(tmpState2);
                                    tmpSdfa.list[tr].arr2.addLast(tmpState2.id);
                                    addNewState = 0;
                                }
                            }
                        }
                        if (addNewState == 1) {
                            ldfa.addLast(tmpSdfa2);
                        }
                    }
                }
            }
            /*if(f_sao){
            for(int k=0;k<ldfa.size();k++){
            tmpSdfa2 = ldfa.get(k);
            boolean f_tontai = true;
            for(int m=0;m<tmpSdfa2.arr2.size();m++){
            int v = tmpSdfa2.arr2.get(m);
            if ((v!=listSao[0])&&(v!=listSao[1])) f_tontai = false;
            }
            if(f_tontai){
            tmpSdfa2 = ldfa.remove(k);
            k--;
            }
            }
            }*/
            for (int k = 0; k < ldfa.size(); k++) {
                tmpSdfa2 = ldfa.get(k);
                boolean f_tontai = false;
                int tmp = 0;
                for (int m = 0; m < listDFA.size(); m++) {
                    tmpSdfa3 = listDFA.get(m);
                    if (tmpSdfa3.arr2.size() == tmpSdfa2.arr2.size()) {
                        boolean f_tmp = true;
                        for (int n = 0; n < tmpSdfa3.arr2.size(); n++) {
                            int u1 = tmpSdfa3.arr2.get(n);
                            int u2 = tmpSdfa2.arr2.get(n);
                            if (u1 != u2) {
                                f_tmp = false;
                            }
                        }
                        if (f_tmp) {
                            f_tontai = true;
                            tmp = m;
                        }
                    }
                }
                if (f_tontai) {
                    tmpSdfa3 = listDFA.get(tmp);
                    for (int n = 0; n < 256; n++) {
                        if (tmpSdfa.list[n] == tmpSdfa2) {
                            tmpSdfa.list[n] = tmpSdfa3;
                        }
                    }
                } else {
                    tmpSdfa2.id = i++;
                    listDFA.addLast(tmpSdfa2);
                }
            }
        }
        //tmpSdfa2 = new sdfa();
        tmpSdfa2 = listDFA.get(0);
        for (int j = 0; j < listDFA.size(); j++) {
            //tmpSdfa3 = new sdfa();
            tmpSdfa3 = listDFA.get(j);
            for (int k = 0; k < 256; k++) {
                if (tmpSdfa3.list[k] == null) {
                    tmpSdfa3.list[k] = tmpSdfa2;
                }
            }
        }
        //System.out.println(listDFA.size());
        //for(int j=0;j<listDFA.size();j++){
        //    listDFA.get(j).id=j;
        //}
        //dfa->bit
        //int n=listDFA.size();
        //}
        //catch(IOException exc){
        //    System.out.println("File Error");
        //}
        fin.close();
        fout.close();
    }
}
