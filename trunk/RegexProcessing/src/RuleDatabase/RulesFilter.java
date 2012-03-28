/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RuleDatabase;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author heckarim
 */
class RulesFilter implements FileFilter {

    String filetype;

    RulesFilter(String ext) {
        this.filetype = ext;
    }

    public boolean accept(File pathname) {
        return pathname.getName().toLowerCase().endsWith(filetype);
    }

}
