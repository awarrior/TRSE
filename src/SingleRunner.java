import java.io.File;
import java.util.Iterator;
import java.util.Set;

import view.DTNView;

import model.DTNGraph;
import model.LabelWeightedEdge;
import model.Translator;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import engine.Engine;
import engine.Options;

/**
 * TRSE Runner - Singleton
 * 
 * @author jacky
 * @version 1.0.0
 */
public class SingleRunner {
	private static String filename = "demo.dtp";

	public static void main(String[] args) {
		singleton(args, filename);
	}

	/**
	 * Run one sample.
	 * 
	 * @param args
	 *            main function arguments
	 * @param path
	 *            DTP file path based on Project Root Path
	 */
	public static void singleton(String[] args, String path) {
		Translator trans = new Translator();

		// read DTP
		path = Translator.getProjectRootPath() + SEP + path;
		String dtp = trans.readDTP(path);

		// build graph
		DTNGraph graph = trans.buildGraph(dtp);
		buildView(graph);

		// install options
		Options opt = new Options();
		opt.gui = true;
		opt.parse(args);

		// install engine
		Engine eng = new Engine(graph);

		// run
		opt.doSearch(eng);
	}

	private static void buildView(DTNGraph graph) {
		DirectedOrderedSparseMultigraph<String, String> g = new DirectedOrderedSparseMultigraph<String, String>();

		// vertex
		Set<String> vex = graph.vertexSet();
		Iterator<String> vit = vex.iterator();
		while (vit.hasNext()) {
			String v = vit.next();
			g.addVertex(v);
		}

		// edge
		Set<LabelWeightedEdge> edge = graph.edgeSet();
		Iterator<LabelWeightedEdge> eit = edge.iterator();
		while (eit.hasNext()) {
			LabelWeightedEdge e = eit.next();
			int l = graph.getEdgeLabel(e);
			int w = (int) graph.getEdgeWeight(e);
			String s = graph.getEdgeSource(e);
			String t = graph.getEdgeTarget(e);
			g.addEdge("<" + l + ", " + w + ">", s, t);
		}

		new DTNView(g);
	}

	public static String SEP = File.separator;

}
