/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

/**
 *
 * @author heckarim
 *
 * This class use as mask of rule option
 */
public class OptionMask {

    int[] opMark; // 0 is don't care
    // 1 is have
    // -1 is don't have

    public OptionMask() {
        opMark = new int[References._opAll.length];
        //set as don't care
        for (int i = 0; i < opMark.length; i++) {
            opMark[i] = 0;
        }
    }

    public void SetForbid(String[] mask) {
        for (int i = 0; i < mask.length; i++) {
            for (int k = 0; k < References._opAll.length; k++) {
                if (References._opAll[k].compareToIgnoreCase(mask[i]) == 0) {
                    this.opMark[k] = -1;
                }
            }
        }
    }

    public void SetForbid(String mask) {

        for (int k = 0; k < References._opAll.length; k++) {
            if (References._opAll[k].compareToIgnoreCase(mask) == 0) {
                this.opMark[k] = -1;
            }
        }

    }

    public void SetPermit(String[] mask) {
        for (int i = 0; i < mask.length; i++) {
            for (int k = 0; k < References._opAll.length; k++) {
                if (References._opAll[k].compareToIgnoreCase(mask[i]) == 0) {
                    this.opMark[k] = 1;
                }
            }
        }
    }

    public void SetPermit(String mask) {
        for (int k = 0; k < References._opAll.length; k++) {
            if (References._opAll[k].compareToIgnoreCase(mask) == 0) {
                this.opMark[k] = 1;
            }
        }
    }

     public void SetDontCare(String mask) {
        for (int k = 0; k < References._opAll.length; k++) {
            if (References._opAll[k].compareToIgnoreCase(mask) == 0) {
                this.opMark[k] = 0;
            }
        }
    }

}
