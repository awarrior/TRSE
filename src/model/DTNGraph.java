package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.EdgeFactory;
import org.jgrapht.Graph;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DirectedWeightedMultigraph;

/**
 * DTN - label directed weighted multigraph.
 * 
 * @author jacky
 * @version 1.0.0
 */
public class DTNGraph extends
		DirectedWeightedMultigraph<String, LabelWeightedEdge> implements
		LabelGraph<String, LabelWeightedEdge> {

	private static final long serialVersionUID = 3221635545492222618L;

	// store edges related to label index
	public List<ArrayList<LabelWeightedEdge>> arr = new ArrayList<ArrayList<LabelWeightedEdge>>();
	
	// vertex index map
	public Map<String, Integer> map = new HashMap<String, Integer>();

	/**
	 * Creates a new label directed weighted multigraph.
	 * 
	 * @param edgeClass
	 *            class on which to base factory for edges
	 */
	public DTNGraph(Class<? extends LabelWeightedEdge> edgeClass) {
		this(new ClassBasedEdgeFactory<String, LabelWeightedEdge>(edgeClass));
	}

	/**
	 * Creates a new label directed weighted multigraph with the specified edge
	 * factory.
	 * 
	 * @param ef
	 *            the edge factory of the new graph.
	 */
	public DTNGraph(EdgeFactory<String, LabelWeightedEdge> ef) {
		super(ef);
	}

	@Override
	public String toString() {
		String str = "vertices: " + vertexSet() + "\n";
		str += "edges: \n";
		for (int i = 0; i < arr.size(); i++) {
			for (int j = 0; j < arr.get(i).size(); j++) {
				LabelWeightedEdge e = arr.get(i).get(j);
				str += "<" + (i + 1) + ", " + getEdgeSource(e) + ", "
						+ getEdgeTarget(e) + ", " + getEdgeWeight(e) + ">\n";
			}
		}
		return str;
	}

	@Override
	public void setEdgeLabel(LabelWeightedEdge e, int label) {
		e.label = label;
	}

	@Override
	public int getEdgeLabel(LabelWeightedEdge e) {
		return e.label;
	}

//	/**
//	 * Get a set contained edgeSource and its former vertices.
//	 */
//	public Set<String> getVexFormers(String edgeSource, Set<String> set) {
//		if (set.contains(edgeSource))
//			return new HashSet<String>();
//		set.add(edgeSource);
//		for (LabelWeightedEdge e : incomingEdgesOf(edgeSource)) {
//			String s = getEdgeSource(e);
//			if (!set.contains(s))
//				set.addAll(getVexFormers(s, set));
//		}
//		return set;
//	}
//
//	/**
//	 * Get a set contained edgeTarget and its latter vertices.
//	 */
//	public Set<String> getVexLatters(String edgeTarget, Set<String> set) {
//		if (set.contains(edgeTarget))
//			return new HashSet<String>();
//		set.add(edgeTarget);
//		for (LabelWeightedEdge e : outgoingEdgesOf(edgeTarget)) {
//			String s = getEdgeTarget(e);
//			if (!set.contains(s))
//				set.addAll(getVexLatters(s, set));
//		}
//		return set;
//	}

}

/**
 * An interface for a graph whose edges have labels and weights.
 * 
 * @param <V>
 *            vertex
 * @param <E>
 *            edge
 * 
 * @author jacky
 * @version 1.0.0
 */
interface LabelGraph<V, E> extends Graph<V, E> {

	/**
	 * The default label for an edge.
	 */
	public static int DEF_INT_LABEL = 0;

	/**
	 * Setter of label edge.
	 * 
	 * @param e
	 *            edge on which to set label
	 * @param label
	 *            new label for edge
	 */
	public void setEdgeLabel(E e, int label);

	/**
	 * Getter of label edge.
	 * 
	 * @param e
	 *            specified edge
	 * @return edge label value
	 */
	public int getEdgeLabel(E e);

}
