/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MainGui;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author heckarim
 */
public class prime {

    public static void main(String[] args) {
        prime pri = new prime();
        pri.action();
    }

    private void action() {
        int max = 9999;
        int num = 3;
        List<Integer> lpri = new LinkedList<Integer>();
        lpri.add(2);
        while(num<= max){
            boolean prem = true;
            for(int i =0; i<lpri.size(); i++){
                if(num % lpri.get(i) == 0){
                    prem = false;
                    break;
                }
            }
            if(prem){
                lpri.add(num);
                System.out.print(" - " + num);
                if(lpri.size() % 20 == 0 )
                    System.out.println("");
            }
            num = num +2;
        }
    }
}
