package engine;


import model.DTNGraph;
import model.LabelWeightedEdge;

import org.gecode.IntVar;
import org.gecode.Space;
import org.gecode.VarArray;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * An engine supports solving DTP puzzle using different kinds of heuristics.
 * 
 * @author jacky
 * @version 1.0.0
 */
public class Engine extends Space {
	
	// do search time
	public double time = 0;

	// meta-variables
	public VarArray<IntVar> metaVarArr;
	// DTN graph
	public static DTNGraph dtn;

	// used by Spring IoC
	private CoreIface ci;

	public CoreIface getCi() {
		return ci;
	}

	public void setCi(CoreIface ci) {
		this.ci = ci;
	}

	public Engine() {
		super("TRSE");
	}

	/**
	 * Constructor for setting up the script.
	 * 
	 * @param opt
	 *            options used for customization
	 */
	public Engine(DTNGraph graph) {
		super("TRSE");
		dtn = graph;

		// modeling

		// Create a meta-variable array and its size is decided by the graph
		// label array. The domain of the meta-variables in their array
		// is the indexes of the meta-values to each meta-variable.
		int n = dtn.arr.size();
		metaVarArr = new VarArray<IntVar>(n);
		for (int i = 0; i < n; i++) {
			metaVarArr.add(new IntVar(this, 0, dtn.arr.get(i).size() - 1));
		}

		// Post core module
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		Engine ci = (Engine) ac.getBean("engine");
		ci.getCi().post(this, metaVarArr);

	}

	/**
	 * Copy-constructor.
	 * 
	 * @param share
	 *            whether share or not
	 * @param engine
	 *            the copied object
	 */
	public Engine(Boolean share, Engine engine) {
		super(share, engine);
		metaVarArr = new VarArray<IntVar>(this, share, engine.metaVarArr);
	}

	/**
	 * Format the answer of DTP
	 * 
	 * @return the answer string
	 */
	// TODO
	public String toString() {
		String sov = "Solution(L:label, S:source, T:target, W:weight):\n";
		for (int i = 0; i < metaVarArr.size(); i++) {
			if (metaVarArr.get(i).assigned()) {
				sov += "\tL=" + (i + 1) + " ";
				int j = metaVarArr.get(i).val();
				LabelWeightedEdge e = dtn.arr.get(i).get(j);
				sov += "\tS=" + dtn.getEdgeSource(e);
				sov += "\tT=" + dtn.getEdgeTarget(e);
				sov += "\tW=" + (int) dtn.getEdgeWeight(e);
				sov += "\n";
			}
		}
		return sov;
	}
}

/**
 * Default realization of Core in Engine.
 * 
 * @author jacky
 * @version 1.0.0
 */
class DefCore implements CoreIface {

	// on or off flags used by Spring IoC
	private boolean FWA_P, MRV_B, FC_P, RSV_P;

	public boolean isFC_P() {
		return FC_P;
	}

	public void setFC_P(boolean fC_P) {
		FC_P = fC_P;
	}

	public boolean isFWA_P() {
		return FWA_P;
	}

	public void setFWA_P(boolean fWA_P) {
		FWA_P = fWA_P;
	}

	public boolean isRSV_P() {
		return RSV_P;
	}

	public void setRSV_P(boolean rSV_P) {
		RSV_P = rSV_P;
	}

	public boolean isMRV_B() {
		return MRV_B;
	}

	public void setMRV_B(boolean mRV_B) {
		MRV_B = mRV_B;
	}

	@Override
	// TODO
	public void post(Space s, VarArray<IntVar> a) {

		// Post propagators.
		FWAPropagator.post(s, a);
		if (FC_P || RSV_P)
			PrePropagator.post(s, a);

		// Post branchings.
		if (MRV_B)
			MRVBranching.post(s, a);
		else
			BaseBranching.post(s, a);
	}
}
