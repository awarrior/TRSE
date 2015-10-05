package model;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * A kind of edge with label weight information.
 * 
 * @author jacky
 * @version 1.0.0
 */
public class LabelWeightedEdge extends DefaultWeightedEdge {
	private static final long serialVersionUID = -4628324412772687414L;
	
	int label = LabelGraph.DEF_INT_LABEL;

	/**
	 * Retrieves the label of this edge. This is protected, for use by
	 * subclasses only (e.g. for implementing toString).
	 * 
	 * @return label of this edge
	 */
	protected int getLabel() {
		return label;
	}
	
}
