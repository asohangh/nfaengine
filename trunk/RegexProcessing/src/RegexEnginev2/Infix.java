/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RegexEnginev2;

import nfa.NFA;

/**
 *
 * @author heckarim
 */
public class Infix extends ReEngine {

    public int id;
    public NFA nfa;
    public int size; //TODO  future support (ab|cd)
    public int infixID;

    Infix(int i, NFA nfa) {
        this.id = i;
        this.nfa = nfa;
    }

    void buildInfix() {
        this.buildEngine(nfa);
        this.size = this.listBlockState.size() - 2;
    }
}
