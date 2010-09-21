/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snort_rule;

import java.util.Hashtable;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 * this class in charge of
 *          +, storing information of option in RuleComponent.
 *
 */
public class RuleStatus {
    //String alloption = "content; nocase; rawbytes; depth; offset; distance; within; http_client_body; http_cookie; http_raw_cookie; http_header; http_raw_header; http_method; http_uri; http_raw_uri; http_stat_code; http_stat_msg; http_encode; fast_pattern; uricontent; urilen; isdataat; file_data; byte_test; byte_jump; ftpbounce; asn1; cvs; dce_iface; dce_opnum; dce_stub_data";
    //String[] option = alloption.split("; ");

    LinkedList<OptionStatus> lstOpStatus; //trade off between memory and speed , i choose memory.

   public RuleStatus() {
        this.lstOpStatus = new LinkedList<OptionStatus>();
    }

    /**
     *
     * @param opname
     * every time a option is appeared,this will be called.
     */
    public void SetOption(String opname) {
        //check if option existed in list.
        int i;
        for (i = 0; i < lstOpStatus.size(); i++) {
            if (lstOpStatus.get(i).opName.compareToIgnoreCase(opname) == 0) {
                break;
            }
        }
        if (i == lstOpStatus.size()) {
            //don't have this option
            OptionStatus temp = new OptionStatus(opname);
            this.lstOpStatus.add(temp);
        } else {
            //just increa count
            lstOpStatus.get(i).count++;
        }
    }

    public boolean isHaveOption(String opname) {
        int i;
        for (i = 0; i < lstOpStatus.size(); i++) {
            if (lstOpStatus.get(i).opName.compareToIgnoreCase(opname) == 0) {
                break;
            }
        }
        if (i == lstOpStatus.size()) {
            //don't have this option
            return false;
        } else {
            return true;
        }
    }

    public OptionStatus GetOption(String opname) {
        int i;
        for (i = 0; i < lstOpStatus.size(); i++) {
            if (lstOpStatus.get(i).opName.compareToIgnoreCase(opname) == 0) {
                return lstOpStatus.get(i);
            }
        }
        return null;
    }

    public boolean CompareMask(OptionMask mask){
        for(int i = 0; i<mask.opMark.length; i++){
            int opstatus = mask.opMark[i];

            if(opstatus == 0)//don'tcare
                continue;
            else if(this.isHaveOption(References._opAll[i])){
                if(opstatus == -1)
                    return false;
                
            }else{
                if(opstatus == 1)
                    return false;
            }
        }
        return true;
    }

    /*
     * What is this class for ??
     *      +, need to know how many option is existed in rule.
     *      +, it also a counter for further statistic.
     */
    public class OptionStatus {

        String opName; // todo in future it will be change to int, save memory and time to compare
        //boolean isExist = false;
        int count = 0;
        ; //number of this status;

        private OptionStatus(String opname) {
            this.opName = opname;
            //this.isExist = true;
            this.count = 1;
        }
    }
}
