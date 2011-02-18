package RuleDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

public class TestWriteObject {
	RuleDatabase db;

	public static void main(String[] args) {
		TestWriteObject ft = new TestWriteObject();
		ft.doTest();
	}

	private void doTest() {
		//this.createDatabase();
		//this.doWrite();
		this.doRead();
	}

	private void createDatabase() {
		String basefolder = System.getProperty("user.dir") + File.separator
				+ "rules.2.9" + File.separator;
		// RuleDatabase rd = new RuleDatabase();
		db = new RuleDatabase(basefolder);
		db.BuildDatabase();
		// rd.print4Test();
		//Statistic0 st = new Statistic0(db);
		//st.DoScript();
	}

	private void doWrite() {
		String filename = "database";

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(db);
			out.close();
			System.out.println("Write OK");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void doRead() {
		String filename = "database";
		RuleDatabase rdb;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try {
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			rdb = (RuleDatabase) in.readObject();
			in.close();

			// System.out.println("Database ruleset: ");
			System.out.println("Readed Database ruleset: ");
			for (RuleSet rs : rdb.lstSnortRuleSet) {
				System.out.println(rs.name);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

	}
}
