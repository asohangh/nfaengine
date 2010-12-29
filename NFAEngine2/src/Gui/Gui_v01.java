/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Gui_v01.java
 *
 * Created on Dec 15, 2010, 9:13:00 AM
 */
package Gui;

import HDL_Bram.Generator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import javax.swing.JFileChooser;

/**
 *
 * @author heckarim
 */
public class Gui_v01 extends javax.swing.JFrame {

    private String GenFolder;
    private Generator Generator;
    private LinkedList<LinkedList<String>> listRule;

    /** Creates new form Gui_v01 */
    public Gui_v01() {
        initComponents();
        this.Generator = new Generator();
        this.setBounds(0, 0, 500, 500);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textField1 = new java.awt.TextField();
        label1 = new java.awt.Label();
        button1 = new java.awt.Button();
        textField2 = new java.awt.TextField();
        label2 = new java.awt.Label();
        button2 = new java.awt.Button();
        textArea1 = new java.awt.TextArea();
        button3 = new java.awt.Button();
        button4 = new java.awt.Button();
        button5 = new java.awt.Button();
        button6 = new java.awt.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("helloworld");
        setAlwaysOnTop(true);
        getContentPane().setLayout(null);
        getContentPane().add(textField1);
        textField1.setBounds(70, 20, 290, 19);

        label1.setText("Input");
        getContentPane().add(label1);
        label1.setBounds(10, 20, 42, 19);
        label1.getAccessibleContext().setAccessibleName("lblInput");

        button1.setLabel("...");
        button1.setName("btnInput"); // NOI18N
        button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button1ActionPerformed(evt);
            }
        });
        getContentPane().add(button1);
        button1.setBounds(380, 20, 28, 23);
        getContentPane().add(textField2);
        textField2.setBounds(70, 60, 290, 19);

        label2.setText("Output");
        getContentPane().add(label2);
        label2.setBounds(10, 60, 47, 19);

        button2.setLabel("...");
        button2.setName("btnOutput"); // NOI18N
        button2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button2ActionPerformed(evt);
            }
        });
        getContentPane().add(button2);
        button2.setBounds(380, 60, 28, 23);
        getContentPane().add(textArea1);
        textArea1.setBounds(20, 100, 410, 280);

        button3.setLabel("GenHDL");
        button3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button3ActionPerformed(evt);
            }
        });
        getContentPane().add(button3);
        button3.setBounds(150, 400, 65, 23);

        button4.setLabel("Gen testb");
        button4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button4ActionPerformed(evt);
            }
        });
        getContentPane().add(button4);
        button4.setBounds(240, 400, 70, 23);

        button5.setLabel("Process PCRE");
        button5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button5ActionPerformed(evt);
            }
        });
        getContentPane().add(button5);
        button5.setBounds(20, 400, 110, 23);

        button6.setLabel("GenCOE");
        button6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button6ActionPerformed(evt);
            }
        });
        getContentPane().add(button6);
        button6.setBounds(340, 400, 66, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed

        JFileChooser filechooser = new JFileChooser(System.getProperty("user.dir"));

        int returnVal = filechooser.showOpenDialog(null);
        File file;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = filechooser.getSelectedFile();
            if (file.exists() && file.isFile()) {
                this.textField1.setText(file.getAbsolutePath());
                this.doLoadFile(file);
                this.Generator.pcrefile = file.getAbsolutePath();
            }
        } else {
            file = null;
        }
    }//GEN-LAST:event_button1ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        JFileChooser filechooser = new JFileChooser(new File(System.getProperty("user.dir")));
        filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = filechooser.showOpenDialog(null);
        File file;
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = filechooser.getSelectedFile();
            if (file.exists() && file.isDirectory()) {
                this.textField2.setText(file.getAbsolutePath());
                this.GenFolder = file.getAbsolutePath() + File.separator;
                this.Generator.genfolder = this.GenFolder;
            }
        } else {
            file = null;
        }
    }//GEN-LAST:event_button2ActionPerformed

    private void button3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button3ActionPerformed
        this.Generator.listRule = this.listRule;
        this.Generator.GenHDLv01();
    }//GEN-LAST:event_button3ActionPerformed

    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button5ActionPerformed
        this.listRule = new LinkedList<LinkedList<String>>();
        String s;
        LinkedList<String> lpcre = null;
        String pcrelist = this.textArea1.getText();
        String []split =pcrelist.split("\n");
        for(int i =0; i<split.length;i++) {
            s = split[i];
            s = s.trim();
            if (s.isEmpty()) {
                continue;
            }

            if (s.startsWith("#bram")) {
                if (lpcre != null) {
                    this.listRule.add(lpcre);
                }
                lpcre = new LinkedList<String>();
                continue;
            }
            s = s + 'i'; //todo all is case sensitive
            lpcre.add(s);
        }
        if (lpcre != null) {
            this.listRule.add(lpcre);
        }
    }//GEN-LAST:event_button5ActionPerformed

    private void button4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button4ActionPerformed
        this.genTestBench();
    }//GEN-LAST:event_button4ActionPerformed

    private void button6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button6ActionPerformed
        this.Generator.geCOEv01();
    }//GEN-LAST:event_button6ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Gui_v01().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private java.awt.Button button2;
    private java.awt.Button button3;
    private java.awt.Button button4;
    private java.awt.Button button5;
    private java.awt.Button button6;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.TextArea textArea1;
    private java.awt.TextField textField1;
    private java.awt.TextField textField2;
    // End of variables declaration//GEN-END:variables

    private void doLoadFile(File file) {
        try {
            this.textArea1.setText("");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = br.readLine()) != null) {
                //this.doc.in
                this.textArea1.insert(s + "\n", this.textArea1.getCaretPosition());
            }
            br.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void genTestBench() {
        this.Generator.GenTestBenchv01();
    }
}