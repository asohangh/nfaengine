/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.read.biff.BiffException;
import jxl.write.*;
import VRTSignature.*;

/**
 *
 * @author heckarim
 */
/**
 * 
 * Purpose of this Class: 1. Parse output of Extractor 16 05 2011. 2. Give
 * function for other class.
 * 
 */
public class Parser_16_05_2011 {

	public class Configuration_Parser_16_05_2011 {
		/*
		 * 
		 * This class is in charge configuring parameters and task to do.
		 */
		public final String outputDir = System.getProperty("user.dir")
				+ File.separator + "output.2.9" + File.separator + "extract"
				+ File.separator;
		public final String inputDir = System.getProperty("user.dir")
				+ File.separator + "output.2.9" + File.separator;
		public final String ruleDir = System.getProperty("user.dir")
				+ File.separator + "rules.2.9" + File.separator;
		public final String outDirByRules = System.getProperty("user.dir")
				+ File.separator + "output.2.9" + File.separator + "extract"
				+ File.separator + "byRules" + File.separator;

		// rules =
		// "web-activex;spyware-put; netbios; web-client; backdoor; web-misc; exploit; oracle; smtp; voip; imap; ftp; misc; policy;specific-threats; web-php; chat; sql; pop3; web-iis; dos; web-cgi;nntp; botnet-cnc"

		public final String rules = "web-activex;spyware-put; netbios; web-client; backdoor; web-misc; exploit; oracle; smtp; voip; imap; ftp; misc; policy;specific-threats; web-php; chat; sql; pop3; web-iis; dos; web-cgi;nntp; botnet-cnc";
		public final String outputFile = "test1.pcres";
		public final int supported = 1;// support or not support pcres.
		public final String excelfile = inputDir + "snort2.9.extraction.xls"; // extracted
																				// file
																				// from
																				// Extractor.
		/*
		 * Action's Configured values: 0: output pcre 'supported' from 'rules'
		 * to outputDir+outputfile
		 * 
		 * 1: outputpcre 'supported' from 'rules' to outputDir+"byRules"
		 * 
		 * 2: 3:
		 */
		public final int actions = 1;

	}

	private RuleDatabase db;
	private Configuration_Parser_16_05_2011 config = new Configuration_Parser_16_05_2011();
	LinkedList<PCRE> lpcre;
	LinkedList<PCRE> lconstraint;
	Workbook workbook = null;

	public static void main(String[] args) throws IOException, WriteException,
			BiffException {
		Parser_16_05_2011 ex = new Parser_16_05_2011();
		ex.Action();
	}

	Parser_16_05_2011(String file) {
		try {

			this.workbook = Workbook.getWorkbook(new File(config.excelfile));
		} catch (IOException ex) {
			Logger.getLogger(Parser_16_05_2011.class.getName()).log(
					Level.SEVERE, null, ex);
		} catch (BiffException ex) {
			Logger.getLogger(Parser_16_05_2011.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	private Parser_16_05_2011() {
	}

	public LinkedList<String> getPCREbyRuleset(String rulename, int support) {
		if (workbook == null) {
			return null;
		} else {
			return this.getPCREbyRuleset(this.workbook, rulename, support);
		}
	}

	private void Action() throws IOException, WriteException, BiffException {
		workbook = Workbook.getWorkbook(new File(config.excelfile));
		// LinkedList<String> lpcre = this.getPCREbyRuleset(workbook, rule, 1);

		switch (config.actions) {
		case 0:
			this.Extract();
			break;
		case 1:
			this.ExtractByRules();
			break;
		default:
		}

	}

	private void ExtractByRules() throws IOException, WriteException,
			BiffException {
		// check Directory structure

		// Parse and output required rules
		String[] array = config.rules.split(";");
		LinkedList<String> lret = new LinkedList<String>();
		for (int i = 0; i < array.length; i++) {
			lret = this.getPCREbyRuleset(workbook, array[i].trim(), config.supported);
			System.out.println("ExtractByRules():   " + array[i]);
			System.out.println("Pcre size " + lret.size());
			LinkedList<String> lreduce = this.reduceSamePCRE(lret);
			LinkedList<String> lnoconstraint = this.reduceConstraint(lreduce);
			LinkedList<String> lnosimple = this.reduceNoSimple(lnoconstraint);
			System.out.println("Pcre size reduce " + lreduce.size());
			System.out.println("Pcre size no constraint "
					+ lnoconstraint.size());
			System.out.println("Pcre size no simple " + lnosimple.size());
			this.outputofile(config.outDirByRules + array[i].trim()+ ".rules", lnoconstraint);
		}
	}

	public void Extract() throws IOException, WriteException, BiffException {
		LinkedList<String> lpcre = this.getPCREbyListRuleset(workbook,
				config.rules, config.supported);
		System.out.println("Pcre size " + lpcre.size());

		LinkedList<String> lreduce = this.reduceSamePCRE(lpcre);
		LinkedList<String> lnoconstraint = this.reduceConstraint(lreduce);
		LinkedList<String> lnosimple = this.reduceNoSimple(lnoconstraint);
		System.out.println("Pcre size reduce " + lreduce.size());
		System.out.println("Pcre size no constraint " + lnoconstraint.size());
		System.out.println("Pcre size no simple " + lnosimple.size());

		// this.outputofile(this.outputfolder + rule + ".ns.ncr.pcre",
		// lnosimple);
		this.outputofile(config.outputDir + config.outputFile, lreduce);
		/*
		 * if (lpcre == null) { System.out.println("null"); } else { for (int i
		 * = 0; i < lpcre.size(); i++) { System.out.println(lpcre.get(i)); } }
		 */
	}

	/**
	 * 
	 * @param rulename
	 * @param support
	 *            0: no 1: yes 2: all
	 * @return
	 */
	public LinkedList<String> getPCREbyRuleset(Workbook book, String rulename,
			int support) {
		LinkedList<String> lpcre = new LinkedList<String>();
		Sheet sheet = book.getSheet(rulename);
		if (sheet == null) {
			return null;
		}
		// begin to read
		int index = 1;
		while (index < sheet.getRows()) {
			String pStatus = sheet.getCell(3, index).getContents();
			String pcre = sheet.getCell(4, index).getContents();
			if (support == 1) {
				if (pStatus.compareToIgnoreCase("1") == 0) {
					lpcre.add(pcre);
				}
			} else if (support == 0) {
				if (pStatus.compareToIgnoreCase("0") == 0) {
					lpcre.add(pcre);
				}
			} else {
				lpcre.add(pcre);
			}
			index++;
		}
		return lpcre;
	}

	private void outputofile(String string, LinkedList<String> lstring)
			throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(string));

		for (int i = 0; i < lstring.size(); i++) {
			bw.write(lstring.get(i) + "\n");
		}

		bw.flush();
		bw.close();
	}

	private LinkedList<String> reduceSamePCRE(LinkedList<String> lpcre) {
		LinkedList<String> ret = new LinkedList<String>();
		if (!lpcre.isEmpty()) {
			ret.add(lpcre.getFirst());
		}
		for (int i = 1; i < lpcre.size(); i++) {
			String pe = lpcre.get(i);
			boolean same = false;
			for (int j = 0; j < ret.size(); j++) {
				if (ret.get(j).compareTo(pe) == 0) {// the same
					same = true;
					break;
				}
			}
			if (!same) {
				ret.addLast(pe);
			}
		}
		return ret;
	}

	private LinkedList<String> reduceConstraint(LinkedList<String> lpcre) {
		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < lpcre.size(); i++) {
			String pe = lpcre.get(i);
			if (Refer.isHaveConstraint(pe)) {
				continue;
			}
			ret.add(pe);
		}
		return ret;
	}

	/**
	 * this reduction base on lenght of pcre and heuristic situation.
	 * 
	 * @param lpcre
	 * @return
	 * 
	 */
	private LinkedList<String> reduceNoSimple(LinkedList<String> lpcre) {
		LinkedList<String> ret = new LinkedList<String>();
		for (int i = 0; i < lpcre.size(); i++) {
			String pe = lpcre.get(i);
			if (pe.length() < 40 || pe.length() > 120) {
				continue;
			}
			if (pe.startsWith("/<OBJEC"))
				continue;
			ret.add(pe);
		}
		return ret;
	}

	private LinkedList<String> getPCREbyListRuleset(Workbook workbook,
			String rules, int type) {
		String[] array = rules.split(";");
		LinkedList<String> lret = new LinkedList<String>();
		for (int i = 0; i < array.length; i++) {
			lret.addAll(this.getPCREbyRuleset(workbook, array[i], type));
		}
		return lret;
	}
}
