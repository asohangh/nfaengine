/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rule;

/**
 *
 * @author heckarim
 */
public class Counter {

    int allrep;
    int exactlyrep;
    int atleastrep;
    int betweenrep;
    int exactlylist[] = new int[3000];
    int atleastlist[] = new int[3000];

    Counter() {
        this.atleastrep = 0;
        this.betweenrep = 0;
        this.exactlyrep = 0;
        this.allrep = 0;
    }

    public void calAllrep() {
        this.allrep = this.exactlyrep + this.atleastrep + this.betweenrep;
    }

    public void clear() {
        this.allrep = this.exactlyrep = this.atleastrep = this.betweenrep = this.atleastrep = 0;
        for (int i = 0; i < 3000; i++) {
            this.atleastlist[i] = this.exactlylist[i] = 0;
        }
    }

    public Counter add(Counter o) {
        Counter res = new Counter();
        res.allrep = this.allrep + o.allrep;
        res.atleastrep = this.atleastrep + o.atleastrep;
        res.exactlyrep = this.exactlyrep + o.exactlyrep;
        res.betweenrep = this.betweenrep + o.betweenrep;
        for (int i = 0; i < 3000; i++) {
            res.atleastlist[i] = this.atleastlist[i] + o.atleastlist[i];
            res.exactlylist[i] = this.exactlylist[i] + o.exactlylist[i];
        }
        return res;
    }
}
