package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

/**
 * Format a DTP into a graph which uses String as vertex's type and
 * DefaultWeightedEdge as edge's type
 * 
 * @author jacky
 * @version 1.0.0
 */
public class Translator {

	@Test
	public void test() {

		// read DTP
		String path = getProjectRootPath() + File.separator + "demo.tsat";
		String dtp = readDTP(path);

		// translate into graph
		DTNGraph g = buildGraph(dtp);
		System.out.println(g);

		// get all vertices
		Set<String> set = g.vertexSet();
		System.out.println(set);

		// get multi-edge
		Iterator<LabelWeightedEdge> it = g.getAllEdges("x1", "x6").iterator();
		while (it.hasNext()) {
			LabelWeightedEdge edge = it.next();
			System.out.println(g.getEdgeLabel(edge) + " "
					+ g.getEdgeWeight(edge));
		}

		// get specified edge
		ArrayList<LabelWeightedEdge> edges = g.arr.get(4);
		for (LabelWeightedEdge e : edges)
			System.out.println(g.getEdgeSource(e) + "-" + g.getEdgeTarget(e));

		// have to check the edge whether null or not
		LabelWeightedEdge ee = g.getEdge("x1", "x2");
		if (ee != null)
			System.out.println(g.getEdgeWeight(ee));

		// // get vertex formers
		// Set<String> s = g.getVexFormers("x2", new HashSet<String>());
		// System.out.println(s);
	}

	/**
	 * Read DTP file.
	 * 
	 * @param path
	 *            the DTP file path
	 * @return whether read successfully
	 */
	public String readDTP(String path) {
		StringBuilder dtp = new StringBuilder();
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);

			// read from file
			while (br.ready()) {
				String line = br.readLine();
				
				// If first char is #, then pass.
				if (line.charAt(0) == '#')
					continue;
				dtp.append(line);
			}

			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dtp.toString().replaceAll(" ", "");
	}

	/**
	 * Translate a DTP into a graph.
	 * 
	 * @param dtp
	 *            the description of DTP
	 * @return relative graph object
	 */
	public DTNGraph buildGraph(String dtp) {
		DTNGraph graph = new DTNGraph(LabelWeightedEdge.class);

		// split DTP
		int vnum = 0;
		String[] clauses = dtp.split("\\^");
		for (String clause : clauses) {
			ArrayList<LabelWeightedEdge> tmpArr = new ArrayList<LabelWeightedEdge>();
			int label = graph.arr.size() + 1;

			// split clause
			String[] constraints = clause.split("v");
			for (String constraint : constraints) {

				// split constraint
				String[] parts = constraint.split("<=");
				String[] vertexs = parts[0].split("\\-");
				String a = vertexs[0];
				String b = vertexs[1];
				int c = Integer.parseInt(parts[1]);

				// assume a is not equal with b when a-b<=c
				if (!a.equals(b)) {
					if (graph.addVertex(a))
						graph.map.put(a, vnum++);
					if (graph.addVertex(b))
						graph.map.put(b, vnum++);
					LabelWeightedEdge edge = graph.addEdge(a, b);
					graph.setEdgeWeight(edge, c);
					graph.setEdgeLabel(edge, label);
					tmpArr.add(edge);
				}
			}

			graph.arr.add(tmpArr);
		}

		return graph;
	}

	public static String getProjectRootPath() {
		File file = new File("");
		String path = "";
		try {
			path = file.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return path;
	}

}
