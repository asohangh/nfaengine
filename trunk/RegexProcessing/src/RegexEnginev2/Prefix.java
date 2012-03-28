/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package RegexEnginev2;

import NFA.NFA;

/**
 *
 * @author heckarim
 */
public class Prefix  extends ReEngine{
    public int prefixID;
    public NFA nfa;

    Prefix(int i, NFA nfa) {
        this.prefixID = i;
        this.nfa = nfa;
    }

    void buildPrefix() {
        this.buildEngine(nfa);
    }
}
