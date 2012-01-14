/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author heckarim
 */
public class icnc_rulex_gen {
    
    
    LinkedList<String> lpre = new LinkedList<String>();
   
    //ieice section
    public static String infolder = System.getProperty("user.dir")
            + File.separator + "ieice" + File.separator + "test.2" + File.separator;
    public static String outfolder = System.getProperty("user.dir")
            + File.separator + "ieice" + File.separator + "test.2" + File.separator;
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        icnc_rulex_gen test = new icnc_rulex_gen();
        test.action();
    }
    private void action() throws FileNotFoundException, IOException {
        String filein = infolder + "ieice.200.pcre";
        String fileout = outfolder + "ieice.200.rules";
        
        this.readPCREFile(filein);
        System.out.println("finish readfromfile");
        this.writeRULEXFile(fileout);
    }

    private void readPCREFile(String file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String s;
        
        while ((s = br.readLine()) != null) {
            
            if (s.startsWith("#")) {
                continue;
            }
            if (s.trim().isEmpty()) {
                continue;
            }
            
            this.lpre.add(s.trim());
            
        }
        br.close();
    }

    private void writeRULEXFile(String fileout) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileout));
        
        for(int i =0 ;i<this.lpre.size(); i++){
            String pcre = this.lpre.get(i);
            bw.write("alert tcp $EXTERNAL_NET any -> $HOME_NET any "
                    + "(msg:\"IEICE Rules "+ i +"\"; "
                    + "pcre:\""+ pcre+"\"; "
                    + "sid:"+ (1000000+i+1)+";)\n");
        }
        
        bw.flush();
        bw.close();
    }
}
