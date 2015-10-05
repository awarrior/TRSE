import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.DTNGraph;
import model.Translator;
import engine.Engine;
import engine.Options;
import engine.Options.Mode;

/**
 * TRSE Runner - Datasets
 * 
 * @author jacky
 * @version 1.0.0
 */
public class DatasetRunner {

	public static void main(String[] args) {

		int n = 20;
		for (int i = 1; i <= SAMPLE_NUM; i++) {
			String subFolder = n + "_" + i + "_" + n * i;
			System.out.println("\nFolder: " + subFolder);
			savInfo(n, "\nFolder: " + subFolder + "\n");
			dataset(n, args, subFolder);
		}

	}

	/**
	 * Write the calculated results into file 'info'.
	 * 
	 * @param n
	 *            filename
	 * @param str
	 *            result string
	 */
	public static void savInfo(int n, String str) {
		try {
			String path = Translator.getProjectRootPath() + SEP + "info" + SEP
					+ "A_" + n + ".info";
			FileWriter fw = new FileWriter(path, true);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(str);

			bw.close();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run a dataset.
	 * 
	 * @param n
	 *            filename
	 * @param args
	 *            main function arguments
	 * @param path
	 *            dataset/[sub-folder]
	 * @return average runtime
	 */
	public static void dataset(int n, String[] args, String path) {
		Translator trans = new Translator();
		String subFolder = Translator.getProjectRootPath() + SEP + "dataset"
				+ SEP + path + SEP;
		int onehun = 0;
		for (int i = 1; i <= 10; i++) {

			// read DTP
			String filename = path + "_" + i + ".tsat";
			String dtp = trans.readDTP(subFolder + filename);

			// build graph
			DTNGraph graph = trans.buildGraph(dtp);

			// install options
			Options opt = new Options();
			opt.gui = false;
			opt.mode = Mode.time;
			opt.timelimit = 100000;
			opt.parse(args);

			// install engine
			Engine eng = new Engine(graph);

			// run
			opt.doSearch(eng);

			// save in file
			if (eng.time >= 100000)
				onehun ++;
			savInfo(n, String.format("%.6f\n", eng.time));
			
			if (onehun >= 26) {
				savInfo(n, "100000\n");
				break;
			}
		}
	}

	public static String SEP = File.separator;
	public static int SAMPLE_NUM = 50;
	public static int R_NUM = 14;
	public static int N_NUM = 5;

}
