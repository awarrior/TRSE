package engine;

import static org.gecode.GecodeEnumConstants.ICL_BND;
import static org.gecode.GecodeEnumConstants.ICL_DEF;
import static org.gecode.GecodeEnumConstants.ICL_DOM;
import static org.gecode.GecodeEnumConstants.ICL_VAL;

import org.gecode.BABIterator;
import org.gecode.DFSIterator;
import org.gecode.FailStop;
import org.gecode.Gecode;
import org.gecode.IntConLevel;
import org.gecode.Space;
import org.gecode.Statistics;
import org.gecode.Stop;
import org.gecode.TimeStop;
import org.gecode.gist.Gist;
import org.gecode.gist.GistEventListener;

/**
 * Class implementing option parsing for the example scripts.
 * 
 * @author jacky
 * @version 1.0.0
 */
public class Options {
	public boolean gui = false, bab = false, naive = true, print = true;
	public int a_d = (int) Gecode.getDefault_a_d(), c_d = (int) Gecode
			.getDefault_c_d(), solutions = 1, iterations = 1, samples = 1,
			size = 1, timelimit = -1, faillimit = -1;
	public IntConLevel icl = ICL_DEF;
	public String name = "TRSE";
	public Mode mode = Mode.solution;
	public GistEventListener gel = null;

	public Options() {
	}

	public Options(String n) {
		name = n;
	}

	public void parse(String[] args) {
		parse(args, -1);
	}

	public void parse(String[] args, int sizemax) {
		int i = 0;
		try {
			while (i < args.length) {
				if (args[i].equalsIgnoreCase("-help")
						|| args[i].equalsIgnoreCase("--help")) {
					String modeline = "(";
					Mode[] ms = Mode.values();
					for (int j = 0; j < ms.length; ++j) {
						modeline += ms[j];
						if (j != ms.length - 1)
							modeline += " ";
					}
					modeline += ") default: " + mode;
					System.out.println("Options for " + name + "\n"
							+ "\t-gui default: " + gui + "\n"
							+ "\t\t use the Gist for search\n"
							+ "\t-nogui default: " + (!gui) + "\n"
							+ "\t\tdo not use the Gist for search\n"
							+ "\t-mode " + modeline + "\n"
							+ "\t\tprint solutions or measure time\n"
							+ "\t\t(applied when no gui is used)\n"
							+ "\t-solutions (int) default: " + solutions + "\n"
							+ "\t\tnumber of solutions\n"
							+ "\t-iterations (int) default: " + iterations
							+ "\n" + "\t\tnumber of iterations\n"
							+ "\t-samples (int) default: " + samples + "\n"
							+ "\t\tnumber of samples\n" + "\t(int)\n"
							+ "\t-size (int) default: " + size + "\n"
							+ "\t\tsize/instance of problem\n"
							+ "\t-print (boolean) default: " + print + "\n"
							+ "\t\tprint the solutions\n"
							+ "\t-a_d (int) default: " + a_d + "\n"
							+ "\t-c_d (int) default: " + c_d + "\n"
							+ "\t\trecomputation distances\n"
							+ "\t-bab (boolean) default: " + bab + "\n"
							+ "\t\tuse optimizing search\n"
							+ "\t-naive default: " + naive + "\n"
							+ "\t\tuse naive model\n" + "\t-smart default: "
							+ (!naive) + "\n" + "\t\tuse smart model\n"
							+ "\t-icl (dom bnd val def) default: " + getCL(icl)
							+ "\n" + "\t\twhich consistency level to us\ne"
							+ "\t-time (int) default: " + timelimit + "\n"
							+ "\t\tMaximum time to spend searching\n"
							+ "\t-fails (int) default: " + faillimit + "\n"
							+ "\t\tMaximum number of fails during search");
					System.exit(0);
				} else if (args[i].equalsIgnoreCase("-gui")) {
					gui = true;
				} else if (args[i].equalsIgnoreCase("-nogui")) {
					gui = false;
				} else if (args[i].equalsIgnoreCase("-mode")) {
					mode = Mode.valueOf(args[++i].toLowerCase());
				} else if (args[i].equalsIgnoreCase("-solutions")) {
					solutions = Integer.parseInt(args[++i]);
				} else if (args[i].equalsIgnoreCase("-iterations")) {
					iterations = Integer.parseInt(args[++i]);
				} else if (args[i].equalsIgnoreCase("-samples")) {
					samples = Integer.parseInt(args[++i]);
				} else if (args[i].equalsIgnoreCase("-size")) {
					size = Integer.parseInt(args[++i]);
				} else if (args[i].equalsIgnoreCase("-print")) {
					print = Boolean.parseBoolean(args[++i]);
				} else if (args[i].equalsIgnoreCase("-a_d")) {
					a_d = Integer.parseInt(args[++i]);
				} else if (args[i].equalsIgnoreCase("-c_d")) {
					c_d = Integer.parseInt(args[++i]);
				} else if (args[i].equalsIgnoreCase("-bab")) {
					bab = Boolean.parseBoolean(args[++i]);
				} else if (args[i].equalsIgnoreCase("-naive")) {
					naive = true;
				} else if (args[i].equalsIgnoreCase("-smart")) {
					naive = false;
				} else if (args[i].equalsIgnoreCase("-icl")) {
					icl = getCL(args[++i]);
				} else if (args[i].equalsIgnoreCase("-time")) {
					timelimit = Integer.parseInt(args[++i]);
				} else if (args[i].equalsIgnoreCase("-fails")) {
					faillimit = Integer.parseInt(args[++i]);
				} else {
					try {
						size = Integer.parseInt(args[i]);
					} catch (NumberFormatException nfe) {
						System.err.println("Unrecognized option: " + args[i]
								+ "\nUse -help for a list of options.");
						System.exit(1);
					}
				}
				++i;
			}
		} catch (Exception ex) {
			System.err.println("Erroneous argument for " + args[i - 1]);
			System.exit(1);
		}

		if (sizemax > 0)
			if (size < 0 || size >= sizemax) {
				System.err.println("Size must be between 0 and "
						+ (sizemax - 1));
				System.exit(1);
			}
	}

	/**
	 * Perform search according to options.
	 */
	public void doSearch(Space g) {
		doSearch(g, FTStop.create(faillimit, timelimit));
	}

	/**
	 * Perfom search according to options.
	 * <p>
	 * This method will override any stop-conditions specified on the
	 * command-line.
	 */
	@SuppressWarnings({ "static-access", "unused" })
	public void doSearch(Space g, Stop stop) {
		if (g.getName() == "") {
			g.setName(name);
		}

		if (!gui) {
			long startTime, time, nsols;
			org.gecode.Options o = new org.gecode.Options();
			o.setA_d(a_d);
			o.setC_d(c_d);
			o.setStop(stop);
			switch (mode) {
			case solution:
				Statistics stat = null;
				nsols = solutions;
				startTime = System.nanoTime();
				if (bab) {
					BABIterator<Space> search = new BABIterator<Space>(g, o);
					for (Space sol : search) {
						if (print)
							System.out.println(sol.toString());
						if (--nsols == 0)
							break;
					}
					stat = search.statistics();
				} else {
					DFSIterator<Space> search = new DFSIterator<Space>(g, o);
					for (Space sol : search) {
						if (print)
							System.out.println(sol.toString());
						if (--nsols == 0)
							break;
					}
					stat = search.statistics();
				}
				time = System.nanoTime() - startTime;
				System.out.println("Summary:" + "\n\truntime:      "
						+ (time / 1000000.0) + "ms\n\tsolutions:    "
						+ (solutions - nsols));
				if (stat != null) {
					System.out.println("  \tpropagations: "
							+ stat.getPropagate() + "\n\tfailures:     "
							+ stat.getFail() + "\n\tclones:       "
							+ stat.getClone() + "\n\tcommits:      "
							+ stat.getCommit() + "\n\tpeak memory:  "
							+ ((stat.getMemory() + 1023) / 1024) + "KB");
				}
				break;
			case time:
			case timenogc:
				double[] ts = new double[samples];
				for (int sit = samples; sit-- > 0;) {
					if (mode == Mode.timenogc)
						for (int k = 0; k < 3; ++k) {
							System.gc();
							System.runFinalization();
							System.gc();
							try {
								Thread.currentThread().sleep(50);
							} catch (Exception ex) {
							}
						}
					startTime = System.nanoTime();
					for (int kit = iterations; kit-- > 0;) {
						nsols = solutions;
						if (bab) {
							for (Space sol : new BABIterator<Space>(g, o)) {
								if (--nsols == 0)
									break;
							}
						} else {
							for (Space sol : new DFSIterator<Space>(g, o)) {
								if (--nsols == 0)
									break;
							}
						}
					}
					time = System.nanoTime() - startTime;
					ts[sit] = (((double) time) / iterations) / 1000000.0;
				}

				double m = arithmean(ts);
				double d = stddev(ts) * 100.0;
				System.out.printf(java.util.Locale.US,
						"\tRuntime: %.6fms (%.2f%% deviation)\n", m, d);
				((Engine) g).time = m;
				break;
			}
		} else {
			Gist gist = new Gist(g, bab);
			if (gel != null)
				gist.addEventListener(gel);
			if (solutions <= 0) {
				gist.exploreAll();
			} else if (solutions > 0) {
				while (solutions-- != 0)
					gist.exploreOne();
			}
		}
	}

	// Computing statistics
	double arithmean(double[] t) {
		if (t.length == 0)
			return 0.0;
		double res = 0.0;
		for (int i = t.length; i-- > 0;)
			res += t[i];
		return res / t.length;
	}

	double stddev(double[] t) {
		if (t.length < 2)
			return 0.0;
		double am = arithmean(t);
		double s = 0.0;
		for (int i = t.length; i-- > 0;) {
			double d = t[i] - am;
			s += d * d;
		}
		return Math.sqrt(s / (t.length - 1)) / am;
	}

	// Utility-handling of consistency-level arguments
	protected IntConLevel getCL(String icl) {
		if (icl.equalsIgnoreCase("dom"))
			return ICL_DOM;
		if (icl.equalsIgnoreCase("bnd"))
			return ICL_BND;
		if (icl.equalsIgnoreCase("val"))
			return ICL_VAL;
		if (icl.equalsIgnoreCase("def"))
			return ICL_DEF;
		throw new RuntimeException();
	}

	protected String getCL(IntConLevel icl) {
		switch (icl) {
		case ICL_DOM:
			return "dom";
		case ICL_BND:
			return "bnd";
		case ICL_VAL:
			return "val";
		case ICL_DEF:
			return "def";
		}
		throw new RuntimeException();
	}

	// Utility-handling of mode arguments
	public enum Mode {
		solution, time, timenogc
	};
}

class FTStop extends Stop {
	private TimeStop ts;
	private FailStop fs;

	private FTStop(int fails, int time) {
		ts = new TimeStop(time);
		fs = new FailStop(fails);
	}

	public boolean stop(Statistics s) {
		return fs.stop(s) || ts.stop(s);
	}

	public static Stop create(int fails, int time) {
		if (fails < 0 && time < 0)
			return null;
		if (fails < 0)
			return new TimeStop(time);
		if (time < 0)
			return new FailStop(fails);
		return new FTStop(fails, time);
	}
}
