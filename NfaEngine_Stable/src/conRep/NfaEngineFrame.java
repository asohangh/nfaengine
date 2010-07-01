/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NfaEngine_FRame.java
 *
 * Created on Jul 1, 2010, 2:46:32 PM
 */

package conRep;

import NFA.NFA;
import engineRe.BlockChar;
import engineRe.BlockState;
import engineRe.ReEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import pcre.ParseTree;

/**
 *
 * @author heckarim
 */
public class NfaEngineFrame extends javax.swing.JFrame {

    public String GenDir = System.getProperty("user.dir") + File.separator;// + "GeneratedFile";
    public Document doc;
    /** Creates new form NfaEngine_FRame */
    public NfaEngineFrame() {
        NfaEngineFrame.setDefaultLookAndFeelDecorated(false);
        initComponents();
        this.doc = this.jtxtAPcre.getDocument();
        this.jtxtfDir.setText(this.GenDir);
        this.setBounds(0, 0, 540, 415);
    }

    private void doGenerateFile() {
        LinkedList<LinkedList> listBram = new LinkedList<LinkedList>();
        LinkedList<String> pcreList = new LinkedList<String>();
        String temp;
        int id = -1;
        try {
            temp = this.doc.getText(0, this.doc.getLength());
            String []split =temp.split("\n");
            for(int i=0; i<split.length; i++){
                if(split[i].trim().compareTo("") != 0)
                    pcreList.add(split[i].trim());
            }
            System.out.println("Pcre list: ");
            for(int i=0; i<pcreList.size(); i++){

               System.out.println(pcreList.get(i));
            }
            System.out.println("ok read list");
        } catch (BadLocationException ex) {
            //no thing
        }

        LinkedList<String> bpcre = new LinkedList<String>();
        for(int i = 0; i<pcreList.size(); i++){
            temp = pcreList.get(i);
            if(temp.isEmpty())
                continue;
            if(temp.trim().startsWith("#bram")){
                if(!bpcre.isEmpty())
                    listBram.add(bpcre);
                bpcre = new LinkedList<String>();
            }else{
                bpcre.add(temp.trim());
            }
        }
        if(!bpcre.isEmpty()){
            listBram.add(bpcre);
        }
        
        System.out.println("Finish parse pcrefile!! ");
        for(int i =0; i<listBram.size(); i++){
            LinkedList<String> listPcre = listBram.get(i);
            System.out.println("BRAM " + i + " : ");
            for(int j =0; j<listPcre.size(); j++){
                System.out.println(listPcre.get(j));
            }
        }
        LinkedList<BRAM> bramlist = new LinkedList<BRAM>();
        for(int i =0; i< listBram.size(); i++){
            System.out.println("Bramxxx " + i);
            LinkedList<String> listPcre = listBram.get(i);
            BRAM br = this.createHDLFromListPCRE(listPcre, i);
            bramlist.add(br);
        }

        String folders = this.GenDir + File.separator;
        //Main.outputResult(bramlist);
        Main.createTopEngineTogether(folders, bramlist);
    }

    public BRAM createHDLFromListPCRE(LinkedList<String> rule, int index){
        BRAM bRam = new BRAM(index);
        bRam._outputFolder = this.GenDir;
        LinkedList<ReEngine> engineList = new LinkedList<ReEngine>();
        //String folders = System.getProperty("user.dir") + System.getProperty("file.separator") + "test" + System.getProperty("file.separator");
        String folders = bRam._outputFolder + File.separator;
        bRam.noBlockState = bRam.noPCRE = bRam.noCRB = bRam.noNFA = 0;
        bRam.noPCRE = rule.size();
        for (int i = 0; i < rule.size(); i++) {
            ParseTree tree = new ParseTree(rule.get(i));
            System.out.println("pcre is: " + tree.rule.getPattern() + " -------- " + tree.rule.getModifier());
            NFA nfa = new NFA();
            nfa.tree2NFA(tree);
            nfa.updateID();
            nfa.deleteRedundantState();
            bRam.noNFA += nfa.getSize();

            ReEngine engine = new ReEngine();
            engine.createEngine(nfa);
            engine.id_ram = index;
            bRam.noBlockState += engine.listBlockState.size();
            //count crb block
            int ncrb = 0;
            for(int k =0; k< engine.listBlockState.size(); k++){
                if(engine.listBlockState.get(k).isContraint)
                    ncrb++;
            }
            bRam.noCRB += ncrb;

            bRam.addEngine(engine, i);
            engineList.add(engine);
            engine.buildHDL();
            engine.print();
        }
        bRam.unionCharBlocks();
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            BlockChar temp = bRam.blockCharList.get(i);
            System.out.print(temp.value + " ");
        }
        System.out.println("Width: " + bRam.blockCharList.size());
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            BlockChar temp = bRam.blockCharList.get(i);
            System.out.println(temp.value);
            for (int j = 0; j < temp.listToState.size(); j++) {
                for (int k = 0; k < temp.listToState.get(j).size(); k++) {
                    BlockState tempState = (BlockState) temp.listToState.get(j).get(k);
                    System.out.println("Engine: " + temp.array_id[j] + " state: " + tempState.id);
                }
            }
        }
        bRam.fillEntryValue();
        bRam.buildNecessaryFiles();
        System.out.println("\n\nResult: " + bRam.blockCharList.size());
        for (int i = 0; i < bRam.blockCharList.size(); i++) {
            //System.out.print(this.blockCharList.get(i).value + " ");
            bRam.blockCharList.get(i).id = i;
            System.out.print(bRam.blockCharList.get(i).value + "[" + bRam.blockCharList.get(i).id +"] ");
        }
        return bRam;
    }

    private void doLoadFile(File file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while((s = br.readLine()) != null){
                    //this.doc.in
                    this.doc.insertString(this.doc.getLength(), s + "\n", null);
            }
            br.close();

        } catch (IOException ex) {
            Logger.getLogger(NfaEngineFrame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLocationException ex) {
            Logger.getLogger(NfaEngineFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtxtAPcre = new javax.swing.JTextArea();
        jtxtfDir = new javax.swing.JTextField();
        jbtnChan = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jBtnLoad = new javax.swing.JButton();
        jbtnGen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(null);

        jtxtAPcre.setColumns(20);
        jtxtAPcre.setRows(5);
        jScrollPane1.setViewportView(jtxtAPcre);

        getContentPane().add(jScrollPane1);
        jScrollPane1.setBounds(20, 90, 470, 220);
        getContentPane().add(jtxtfDir);
        jtxtfDir.setBounds(20, 40, 380, 20);

        jbtnChan.setText("Change");
        jbtnChan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnChanActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnChan);
        jbtnChan.setBounds(410, 40, 90, 23);

        jLabel1.setText("Generated Folder:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(20, 20, 110, 14);

        jLabel2.setText("PCRE list:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(20, 70, 120, 14);

        jBtnLoad.setText("Load File");
        jBtnLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnLoadActionPerformed(evt);
            }
        });
        getContentPane().add(jBtnLoad);
        jBtnLoad.setBounds(20, 320, 140, 23);

        jbtnGen.setText("Generate HDL");
        jbtnGen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnGenActionPerformed(evt);
            }
        });
        getContentPane().add(jbtnGen);
        jbtnGen.setBounds(340, 320, 150, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbtnChanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnChanActionPerformed
        JFileChooser filechooser = new JFileChooser( new File(System.getProperty("user.dir")));
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = filechooser.showOpenDialog(null);
        File file;
        if(returnVal==JFileChooser.APPROVE_OPTION){
            file = filechooser.getSelectedFile();
            if(file.exists() && file.isDirectory()){
               this.jtxtfDir.setText(file.getAbsolutePath());
               this.GenDir = file.getAbsolutePath();
            }
        }else{
            file=null;
        }

}//GEN-LAST:event_jbtnChanActionPerformed

    private void jBtnLoadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnLoadActionPerformed
        JFileChooser filechooser=new JFileChooser(System.getProperty("user.dir"));
        
        int returnVal = filechooser.showOpenDialog(null);
        File file;
        if(returnVal==JFileChooser.APPROVE_OPTION){
            file = filechooser.getSelectedFile();
            if(file.exists() && file.isFile()){
               this.doLoadFile(file);
            }
        }else{
            file=null;
        }
    }//GEN-LAST:event_jBtnLoadActionPerformed

    private void jbtnGenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnGenActionPerformed
        this.doGenerateFile();
        Object []choices = {"OK", "Open Dir"};
        int choice = JOptionPane.showOptionDialog(null, " Finish Generating Verilog HDL !!!", "Finish", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,null, choices, choices[0]);
        if(choice == 1){
            System.out.println("choose open dir");
            if(File.separator.compareToIgnoreCase("\\") == 0){ // Window
                try {
                    // Window
                    Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL \"" + this.GenDir + "\"");
                } catch (IOException ex) {
                    Logger.getLogger(NfaEngineFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }//GEN-LAST:event_jbtnGenActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NfaEngineFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtnLoad;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbtnChan;
    private javax.swing.JButton jbtnGen;
    private javax.swing.JTextArea jtxtAPcre;
    private javax.swing.JTextField jtxtfDir;
    // End of variables declaration//GEN-END:variables

}
