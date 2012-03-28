/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Regex;

import InfixSharing.ShareableUnit;

/**
 *
 * @author heckarim
 */
public class ElementShareableUnit extends Element {

    public ShareableUnit share;

    public ElementShareableUnit(ShareableUnit share) {
        this.share = share;
        this.isShareableUnit = true;
    }

    public String getString() {
        return "[." + share.getString() + ".]";
    }

    public String toString() {
        return "[." + share.getString() + ".]";
    }
}
