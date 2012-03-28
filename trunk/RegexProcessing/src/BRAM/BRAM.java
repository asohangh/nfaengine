/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package BRAM;

import PCRE.Refer;
import RegexEngine.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

/**
 * 
 * @author Richard Le
 */
public class BRAM {

	// Bram height default is 256, width depend on szie of list block char
	public int width = -1;
	// Bit area
	public char[][] BRam; // Bram structure
	// Bram is apply on list of engine with coresponding list block char
	public LinkedList<ReEngine> engineList; // list of engine
	public LinkedList<BlockChar> listBlockChar; // need for routing from BRAM to
												// State block
	// indentify each BRAM
	public int ID; // ID of this BRAM
	// other attribute
	int row = -1;
	int col = -1;
	public String _outputFolder = "GeneratedFiles";

	public BRAM(int id) {
		this.engineList = new LinkedList<ReEngine>();
		this.listBlockChar = new LinkedList();
		this.ID = id;
		File file = new File(this._outputFolder);
		file.mkdir();
	}

	public void addEngine(ReEngine engine) {
		engine.ram_id = this.ID;
		this.engineList.add(engine);
		engine.order = this.engineList.indexOf(engine);
	}

	public void addEngine(LinkedList<ReEngine> lengine) {
		for (int i = 0; i < lengine.size(); i++) {
			this.addEngine(lengine.get(i));
		}
	}

	/**************************************
	 * This function to share charBlock
	 * 
	 ***************************************/
	/**
	 * note: +, all engine must use modifier i or not.
	 */
	public void unionCharBlocks() {
		// prepare block char list before unioning
		for (int i = 0; i < this.engineList.size(); i++) {
			// need to reduce block char on each engine
			engineList.get(i).reduceBlockChar();
			// add engine's block char list to this.listBlockChar
			this.listBlockChar.addAll(engineList.get(i).listBlockChar);
		}
		for (int i = 0; i < this.engineList.size(); i++) {
			this.engineList.get(i).updateBlockStateOrder();
		}

		// begin to reduce.
		for (int i = 0; i < this.listBlockChar.size(); i++) {
			BlockChar temp = this.listBlockChar.get(i);
			for (int j = i + 1; j < this.listBlockChar.size(); j++) {
				// System.out.println(" " + i + "." + j);
				BlockChar walk = this.listBlockChar.get(j);
				if (temp.compareTo(walk)) {
					// replace it in block state level.
					for (int k = 0; k < walk.lState.size(); k++) {
						BlockState bs = walk.lState.get(k);
						// there is two type of block state
						// if it is conrep, it will have a list of block char to
						// deal
						if (bs.isConRep) {
							BlockConRep bcr = ((BlockConRep) bs);
							bcr.lChar.remove(walk);
							bcr.lChar.add(temp);
						} else if (!bs.isEnd) {
							bs.acceptChar = temp;
						}
						temp.lState.add(bs);
					}
					// replace it in engine level
					ReEngine en = walk.engine;
					en.listBlockChar.remove(walk);
					en.listBlockChar.add(temp);
					this.listBlockChar.remove(j);
					j--; // just remove one char so ...
				}
			}
		}
		this.width = this.listBlockChar.size();
		// update order;
		for (int i = 0; i < this.listBlockChar.size(); i++) {
			// System.out.print(this.blockCharList.get(i).value + " ");
			this.listBlockChar.get(i).order = i;
		}
		/*
		 * for (int i = 0; i < this.engineList.size(); i++) {
		 * System.out.println("print STATE " + i);
		 * this.engineList.get(i).printBlockChar(); }
		 */
	}

	/*
	 * //update ID; for (int i = 0; i < this.listBlockChar.size(); i++) {
	 * //System.out.print(this.blockCharList.get(i).value + " ");
	 * this.listBlockChar.get(i).id = i;
	 * System.out.print(this.listBlockChar.get(i).value + "[" +
	 * this.listBlockChar.get(i).id + "] "); }
	 * 
	 * for (int i = 0; i < this.listBlockChar.size(); i++) { BlockChar bChar =
	 * this.listBlockChar.get(i); for (int j = 0; j < this.engineList.size();
	 * j++) { ReEngine engine = this.engineList.get(j); for (int k = 0; k <
	 * engine.listBlockChar.size(); k++) { BlockChar bc =
	 * engine.listBlockChar.get(k); if
	 * (bc.engine.rule.getModifier().contains("i") &&
	 * bChar.engine.rule.getModifier().contains("i") &&
	 * bc.value.compareToIgnoreCase(bChar.value) == 0) { bc.id = bChar.id; }
	 * else if ((!bc.engine.rule.getModifier().contains("i") ||
	 * !bChar.engine.rule.getModifier().contains("i")) &&
	 * bc.value.compareTo(bChar.value) == 0) { bc.id = bChar.id; } } } }
	 * 
	 * //need sort list for (int i = 0; i < this.engineList.size(); i++) {
	 * ReEngine engine = this.engineList.get(i); for (int j = 0; j <
	 * engine.listBlockChar.size(); j++) { BlockChar bc =
	 * engine.listBlockChar.get(j); for (int k = j + 1; k <
	 * engine.listBlockChar.size(); k++) { BlockChar bc_1 =
	 * engine.listBlockChar.get(k); BlockChar temp; if (bc.id > bc_1.id) { temp
	 * = bc; engine.listBlockChar.add(k + 1, temp);
	 * engine.listBlockChar.remove(j); j--; break; } } } }
	 * 
	 * System.out.println(); for (int i = 0; i < this.engineList.size(); i++) {
	 * ReEngine eng = this.engineList.get(i); System.out.println(" Eng" + eng.id
	 * + ": "); for (int j = 0; j < eng.listBlockChar.size(); j++) { BlockChar
	 * bc = eng.listBlockChar.get(j); System.out.print(bc.value + "[" + bc.id +
	 * "] "); } System.out.println(); } System.out.println();
	 * 
	 * 
	 * 
	 * }
	 */
	public void fillEntryValue() {

		this.BRam = new char[256][this.listBlockChar.size()];

		int hexValue = -1;
		for (col = 0; col < this.listBlockChar.size(); col++) {
			BlockChar temp = this.listBlockChar.get(col);
			switch (temp.id) {
			case Refer._char:
				// just single char
				this.fillSingle(temp.value, temp.engine.rule.getModifier());
				break;
			case Refer._class:
				// this is class char
				this.fillCharClass(temp.value, temp.engine.rule.getModifier());
				break;
			case Refer._neg_class:
				this.fillNegCharClass(temp.value,
						temp.engine.rule.getModifier());
				break;
			case Refer._ascii_hex:
				// \xFF
				hexValue = Integer.parseInt(temp.value.substring(2), 16);
				this.fillHex(hexValue);
				break;
			case Refer._class_digit:
				// \d
				this.fillDigitRange();
				break;
			case Refer._class_dot:
				// .
				this.fillDotClass(temp.engine.rule.getModifier());
				break;

			/*
			 * case Refer._char_start: case Refer._char_end: //FIXME hexValue =
			 * 10; this.buildHex(hexValue); break;
			 */
			case Refer._class_notspace:
				this.fillCharRangeNoSpace();
			case Refer._class_word:
				// \w
				this.fillCharRange();
				break;
			case Refer._op_between:
			case Refer._op_atleast:
			case Refer._op_exactly:
				break;
			default:
				this.fillSingle(temp.value.substring(0),
						temp.engine.rule.getModifier());
				break;
			}
		}

	}

	private void fillSingle(String value, String modifier) {
		// throw new UnsupportedOperationException("Not yet implemented");

		int ascii = (int) value.charAt(0);
		// iniitialize
		for (int i = 0; i < 256; i++) {
			BRam[i][col] = '0';
		}

		if (modifier.contains("i")) {
			if (ascii >= 65 && ascii <= 90) {
				BRam[ascii][col] = '1';
				BRam[ascii + 32][col] = '1';
			} else if (ascii >= 97 && ascii <= 122) {
				BRam[ascii][col] = '1';
				BRam[ascii - 32][col] = '1';
			} else // not letter
			{
				BRam[ascii][col] = '1';
			}
		} else { // case sensitive
			BRam[ascii][col] = '1';
		}
	}

	private void fillCharClass(String value, String modifier) {
		// initialize
		for (int i = 0; i < 256; i++) {
			BRam[i][col] = '0';
		}
		// value = [abc]
		int from = 0, to = 0;
		value = value.substring(1, value.length() - 1);
		for (int i = 0; i < value.length(); i++) {
			int hex;
			if (value.charAt(i) == '\\') {
				switch (value.charAt(i + 1)) {
				case 'x':
				case 'X':
					hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
					BRam[hex][col] = '1';
					i = i + 3;
					break;
				case 'd':
					for (int j = 48; j <= 57; j++) {
						BRam[j][col] = '1';
					}
					i++;
					break;
				case 'w':
					// TODO
					i++;
					break;
				case 's': // white space \x20 = 32
					BRam[32][col] = '1';
					i++;
					break;
				case 'n': // LF \x0A
					BRam[10][col] = '1';
					i++;
					break;
				case 'r': // CR \x0D
					BRam[14][col] = '1';
					i++;
					break;
				case 't': // tab \x09
					BRam[9][col] = '1';
					i++;
					break;
				case 'z': // range
					from = (int) Integer.valueOf(value.substring(i + 2, i + 4),
							16);
					to = (int) Integer.valueOf(value.substring(i + 4, i + 6),
							16);
					if (modifier.contains("i")) {
						int from1 = 0, to1 = 0;
						if (from >= 65 && from <= 90) {
							from1 = from + 32;
						} else if (from >= 97 && from <= 122) {
							from1 = from - 32;
						}
						if (to >= 65 && to <= 90) {
							to1 = to + 32;
						} else if (from >= 97 && from <= 122) {
							to1 = to - 32;
						}
						if ((from >= 65 && from <= 90 && to >= 65 && to <= 90)
								|| (from >= 97 && from <= 122 && to >= 97 && to <= 122)) {
							for (int j = from; j <= to; j++) {
								BRam[j][col] = '1';
							}
							for (int j = from1; j <= to1; j++) {
								BRam[j][col] = '1';
							}
						} else {// not letter
							for (int j = from; j <= to; j++) {
								BRam[j][col] = '1';
							}
						}
					} else { // case sensitive
						for (int j = from; j <= to; j++) {
							BRam[j][col] = '1';
						}
					}
					i = i + 3;
					break;
				default: // \?
					hex = (int) value.charAt(i + 1);
					BRam[hex][col] = '1';
					i++;
					break;

				}
			} else {
				// value.charAt(i) != '\\' ex: a
				BRam[(int) value.charAt(i)][col] = '1';

			}
		}
	}

	/* this finction to fill \w */
	private void fillCharRange() {
		// throw new UnsupportedOperationException("Not yet implemented");
		// 48 - 57 [0 - 9]
		// 65 - 90 [A - Z]
		// 97 - 122 [a - z]
		for (int i = 0; i <= 47; i++) {
			BRam[i][col] = '0';
		}
		for (int i = 48; i <= 57; i++) {
			BRam[i][col] = '1';
		}
		for (int i = 58; i <= 64; i++) {
			BRam[i][col] = '0';
		}
		for (int i = 65; i <= 90; i++) {
			BRam[i][col] = '1';
		}
		for (int i = 91; i <= 96; i++) {
			BRam[i][col] = '0';
		}
		for (int i = 97; i <= 122; i++) {
			BRam[i][col] = '1';
		}
		for (int i = 123; i <= 255; i++) {
			BRam[i][col] = '0';
		}
	}

	private void fillCharRangeNoSpace() {
		for (int i = 0; i <= 255; i++) {
			BRam[i][col] = '1';
		}
		char ch = ' ';
		BRam[(int) ch][col] = '0';
	}

	private void fillDigitRange() {
		// throw new UnsupportedOperationException("Not yet implemented");
		// 48 - 57 [0 - 9]
		for (int i = 0; i <= 47; i++) {
			BRam[i][col] = '0';
		}
		for (int i = 48; i <= 57; i++) {
			BRam[i][col] = '1';
		}
		for (int i = 58; i <= 255; i++) {
			BRam[i][col] = '0';
		}

	}

	private void fillNegCharClass(String value, String modifier) {
		// initialize
		for (int i = 0; i < 256; i++) {
			BRam[i][col] = '1';
		}
		// value = [^abc]
		int from = 0, to = 0;
		System.out.println("value: " + value);
		value = value.substring(2, value.length() - 1);
		System.out.println("value after: " + value);
		for (int i = 0; i < value.length(); i++) {
			int hex;
			if (value.charAt(i) == '\\') {
				switch (value.charAt(i + 1)) {
				case 'x':
				case 'X':
					hex = Integer.parseInt(value.substring(i + 2, i + 4), 16);
					BRam[hex][col] = '0';
					i = i + 3;
					break;
				case 'd':
					for (int j = 48; j <= 57; j++) {
						BRam[j][col] = '0';
					}
					i++;
					break;
				case 'w':
					// TODO
					i++;
					break;
				case 's': // white space \x20 = 32
					BRam[32][col] = '0';
					i++;
					break;
				case 'n': // LF \x0A
					BRam[10][col] = '0';
					i++;
					break;
				case 'r': // CR \x0D
					BRam[14][col] = '0';
					i++;
					break;
				case 't': // tab \x09
					BRam[9][col] = '0';
					i++;
					break;
				case 'z': // range
					from = (int) Integer.valueOf(value.substring(i + 2, i + 4),
							16);
					to = (int) Integer.valueOf(value.substring(i + 4, i + 6),
							16);
					if (modifier.contains("i")) {
						int from1 = 0, to1 = 0;
						if (from >= 65 && from <= 90) {
							from1 = from + 32;
						} else if (from >= 97 && from <= 122) {
							from1 = from - 32;
						}
						if (to >= 65 && to <= 90) {
							to1 = to + 32;
						} else if (from >= 97 && from <= 122) {
							to1 = to - 32;
						}
						if ((from >= 65 && from <= 90 && to >= 65 && to <= 90)
								|| (from >= 97 && from <= 122 && to >= 97 && to <= 122)) {
							for (int j = from; j <= to; j++) {
								BRam[j][col] = '0';
							}
							for (int j = from1; j <= to1; j++) {
								BRam[j][col] = '0';
							}
						} else {// not letter
							for (int j = from; j <= to; j++) {
								BRam[j][col] = '0';
							}
						}
					} else { // case sensitive
						for (int j = from; j <= to; j++) {
							BRam[j][col] = '0';
						}
					}
					i = i + 3;
					break;
				default: // \?
					BRam[(int) value.charAt(i + 1)][col] = '0';
					i++;
					break;

				}
			} else {
				// value.charAt(i) != '\\' ex: a
				BRam[(int) value.charAt(i)][col] = '0';

			}
		}
	}

	private void fillHex(int hexValue) {
		// throw new UnsupportedOperationException("Not yet implemented");
		for (int i = 0; i < 256; i++) {
			if (i != hexValue) {
				BRam[i][col] = '0';
			} else {
				BRam[i][col] = '1';
			}
		}

	}

	private void fillDotClass(String modifier) {
		for (int i = 0; i < 256; i++) {
			if (!modifier.contains("s")) {
				// match all except '\n'
				if (i == 10) {
					BRam[i][col] = '0';
				} else {
					BRam[i][col] = '1';
				}
			} else {
				BRam[i][col] = '1';
			}

		}
	}

	public void buildCOE() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(
					this._outputFolder + "BRAM_" + ID + ".coe"));
			bw.write(";");
			for (int i = this.listBlockChar.size() - 1; i >= 0; i--) {
				bw.write(this.listBlockChar.get(i).value + " ");
			}
			bw.write("\n");
			bw.write(";Block memory of depth=256, and width=" + width + "\n"
					+ "MEMORY_INITIALIZATION_RADIX=2;\n"
					+ "MEMORY_INITIALIZATION_VECTOR=\n");
			for (int i = 0; i < 256; i++) {
				for (int j = width - 1; j >= 0; j--) {
					bw.write(BRam[i][j]);
				}
				if (i != 255) {
					bw.write(",\n");
				} else if (i == 255) {
					bw.write(";\n");
				}
			}

			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Example: module BRAM_0(out,clk,sod,en,char); input clk, sod, en; input
	 * [7:0] char; output [2:0] out; wire [12:0] q_out; ////BRAM declare
	 * bram_entity_0 ram ( .addr(char), .clk(clk), .dout(q_out), engine_0
	 * en_0(out[0], clk, sod, en, q_out[0], q_out[1], q_out[2], q_out[3],
	 * q_out[4], q_out[5]); engine_1 en_1(out[1], clk, sod, en, q_out[1],
	 * q_out[2], q_out[3], q_out[6], q_out[7], q_out[8], q_out[9], q_out[10]);
	 * engine_2 en_2(out[2], clk, sod, en, q_out[1], q_out[6], q_out[11],
	 * q_out[12]); endmodule
	 */
	public void buildHDL() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					this._outputFolder + "BRAM_" + this.ID + ".v")));
			bw.write("module BRAM_" + this.ID + "(out,clk,sod,en,char);\n");
			bw.write("\tinput clk, sod, en;\n");
			bw.write("\tinput [7:0] char;\n");

			// get real number of pcre on Bram
			int noPcre = 0;
			for (int i = 0; i < this.engineList.size(); i++) {
				noPcre += this.engineList.get(i).listEndState.size();
			}
			//

			bw.write("\toutput [" + (noPcre - 1) + ":0] out;\n");
			bw.write("\twire [" + (this.width - 1) + ":0] q_out;\n");
			// BRAM declare
			// assume that output of single port ram is q_out[width - 1 : 0]
			bw.write("\n//BRAM declare \n");
			bw.write("\tbram_entity_"
					+ this.ID
					+ " ram (.addr(char),\n\t\t.clk(clk),\n\t\t.dout(q_out),\n\t\t.en(en));\n");
			// end of bram
			// declare engine
			int index = 0;
			for (int i = 0; i < this.engineList.size(); i++) {
				// currently, bram support engine with multi end state, so need
				// to
				// sperate upper index and lower index of ouput of each engine.
				int lindex = index;
				int uindex = index + this.engineList.get(i).listEndState.size()
						- 1;
				index = uindex + 1;
				if (lindex == uindex) {
					bw.write("\tengine_" + this.ID + "_"
							+ this.engineList.get(i).order + " engine_"
							+ this.ID + "_" + this.engineList.get(i).order
							+ "(.out(out[" + uindex
							+ "]), \n\t\t.clk(clk), .sod(sod), \n\t\t.en(en)"); // thieu
																				// char
				} else {
					bw.write("\tengine_" + this.ID + "_"
							+ this.engineList.get(i).order + " engine_"
							+ this.ID + "_" + this.engineList.get(i).order
							+ "(.out(out[" + uindex + ":" + lindex
							+ "]), \n\t\t.clk(clk), .sod(sod), \n\t\t.en(en)"); // thieu
																				// char
				}
				// routing to each engine
				ReEngine te = this.engineList.get(i);
				// routing to each engine
				for (int j = 0; j < te.listBlockChar.size(); j++) {
					bw.write(",\n\t\t .in_" + te.listBlockChar.get(j).order
							+ "(q_out[" + te.listBlockChar.get(j).order + "])");
				}

				bw.write(");\n");
			}

			bw.write("\n");
			bw.write("endmodule\n");
			bw.flush();
			bw.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void buildXCO() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					this._outputFolder + "bram_entity_" + this.ID + ".xco")));
			bw.write("# BEGIN Project Options\n"
					+ "SET flowvendor = Foundation_iSE\n"
					+ "SET vhdlsim = True\n"
					+ "SET verilogsim = True\n"
					+ "SET workingdirectory = "
					+ "."
					+ "\n"
					+ // working dir
					"SET speedgrade = -7\n"
					+ "SET simulationfiles = Behavioral\n"
					+ "SET asysymbol = True\n"
					+ "SET addpads = False\n"
					+ "SET device = xc2vp50\n"
					+ "SET implementationfiletype = Edif\n"
					+ "SET busformat = BusFormatAngleBracketNotRipped\n"
					+ "SET foundationsym = False\n"
					+ "SET package = ff1148\n"
					+ "SET createndf = False\n"
					+ "SET designentry = VHDL\n"
					+ "SET devicefamily = virtex2p\n"
					+ "SET formalverification = False\n"
					+ "SET removerpms = False\n"
					+ "# END Project Options\n"
					+ "# BEGIN Select\n"
					+ "SELECT Single_Port_Block_Memory family Xilinx,_Inc. 6.2\n"
					+ "# END Select\n" + "# BEGIN Parameters\n"
					+ "CSET handshaking_pins=false\n" + "CSET init_value=0\n"
					+ "CSET coefficient_file=" + "BRAM_"
					+ this.ID
					+ ".coe\n"
					+ "CSET select_primitive=512x36\n"
					+ "CSET initialization_pin_polarity=Active_High\n"
					+ "CSET global_init_value=0\n"
					+ "CSET depth=256\n"
					+ "CSET write_enable_polarity=Active_High\n"
					+ "CSET port_configuration=Read_Only\n"
					+ "CSET enable_pin_polarity=Active_High\n"
					+ "CSET component_name="
					+ "bram_entity_"
					+ this.ID
					+ "\n"
					+ "CSET active_clock_edge=Rising_Edge_Triggered\n"
					+ "CSET additional_output_pipe_stages=0\n"
					+ "CSET disable_warning_messages=true\n"
					+ "CSET limit_data_pitch=18\n"
					+ "CSET primitive_selection=Select_Primitive\n"
					+ "CSET enable_pin=true\n"
					+ "CSET init_pin=false\n"
					+ "CSET write_mode=Read_After_Write\n"
					+ "CSET has_limit_data_pitch=false\n"
					+ "CSET load_init_file=true\n"
					+ "CSET width="
					+ this.width
					+ "\n"
					+ "CSET register_inputs=false\n"
					+ "# END Parameters\n" + "GENERATE");

			bw.flush();
			bw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void buildCORE_RAM_HDL() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					this._outputFolder + "bram_entity_" + this.ID + ".v")));
			bw.write("`timescale 1ns/1ps\n" + "module bram_entity_" + this.ID
					+ "(\n" + "addr,\n" + "clk,\n" + "dout,\n" + "en);\n"
					+ "input [7 : 0] addr;\n" + "input clk;\n" + "output ["
					+ (this.width - 1) + " : 0] dout;\n" + "input en;\n"
					+ "// synopsys translate_off\n" + "\tBLKMEMSP_V6_2 #(\n"
					+ "\t8,	// c_addr_width\n" + "\t\"0\",	// c_default_data\n"
					+ "\t256,	// c_depth\n" + "\t0,	// c_enable_rlocs\n"
					+ "\t0,	// c_has_default_data\n" + "\t0,	// c_has_din\n"
					+ "\t1,	// c_has_en\n" + "\t0,	// c_has_limit_data_pitch\n"
					+ "\t0,	// c_has_nd\n" + "\t0,	// c_has_rdy\n"
					+ "\t0,	// c_has_rfd\n" + "\t0,	// c_has_sinit\n"
					+ "\t0,	// c_has_we\n" + "\t18,	// c_limit_data_pitch\n"
					+ "\t\"bram_entity_0.mif\",	// c_mem_init_file\n"
					+ "\t0,	// c_pipe_stages\n" + "\t0,	// c_reg_inputs\n"
					+ "\t\"0\",	// c_sinit_value\n" + "\t" + this.width
					+ ",	// c_width\n" + "\t0,	// c_write_mode\n"
					+ "\t\"0\",	// c_ybottom_addr"
					+ "\t1,	// c_yclk_is_rising\n" + "\t1,	// c_yen_is_high\n"
					+ "\t\"hierarchy1\",	// c_yhierarchy\n"
					+ "\t0,	// c_ymake_bmm\n"
					+ "\t\"512x36\",	// c_yprimitive_type\n"
					+ "\t1,	// c_ysinit_is_high\n"
					+ "\t\"1024\",	// c_ytop_addr\n"
					+ "\t1,	// c_yuse_single_primitive\n"
					+ "\t1,	// c_ywe_is_high\n"
					+ "\t1)	// c_yydisable_warnings\n" + "\t\tinst (\n"
					+ "\t\t.ADDR(addr),\n" + "\t\t.CLK(clk),\n"
					+ "\t\t.DOUT(dout),\n" + "\t\t.EN(en),\n" + "\t\t.DIN(),\n"
					+ "\t\t.ND(),\n" + "\t\t.RFD(),\n" + "\t\t.RDY(),\n"
					+ "\t\t.SINIT(),\n" + "\t\t.WE());\n" + "endmodule\n");

			// synopsys translate_on

			// FPGA Express black box declaration
			// synopsys attribute fpga_dont_touch "true"
			// synthesis attribute fpga_dont_touch of bram_entity_0 is "true"

			// XST black box declaration
			// box_type "black_box"
			// synthesis attribute box_type of bram_entity_0 is "black_box"
			bw.flush();
			bw.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * this function in charge of print list block char and coressponding block
	 * state
	 */
	public void printBlockCharBram() {
		for (int i = 0; i < this.engineList.size(); i++) {
			this.engineList.get(i).order = i;
			this.engineList.get(i).updateBlockStateOrder();
		}
		System.out.println("Print BRAM " + this.ID + " \n\tbegin");
		for (int i = 0; i < this.listBlockChar.size(); i++) {
			BlockChar bc = this.listBlockChar.get(i);
			System.out.print("[" + bc.order + ":" + bc.value + "] ");
			for (int j = 0; j < bc.lState.size(); j++) {
				System.out.print(" _ " + bc.lState.get(j).engine.order + "."
						+ bc.lState.get(j).order);
			}
			System.out.print("\n");
		}
		System.out.println("\tend");
	}
}
