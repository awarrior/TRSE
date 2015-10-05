package engine;

import static org.gecode.GecodeEnumConstants.ES_FAILED;
import static org.gecode.GecodeEnumConstants.ES_NOFIX;
import static org.gecode.GecodeEnumConstants.PC_INT_VAL;
import static org.gecode.GecodeEnumConstants.PC_QUADRATIC_HI;
import model.DTNGraph;
import model.LabelWeightedEdge;

import org.gecode.ExecStatus;
import org.gecode.Gecode;
import org.gecode.IntVar;
import org.gecode.IntVarView;
import org.gecode.NaryPropagator;
import org.gecode.PropCost;
import org.gecode.Space;
import org.gecode.VarArray;
import org.gecode.ViewArray;


/**
 * Propagator use 'Floyd-Warshall Arithmetic' to ensure STP be consistent.
 * 
 * @author jacky
 * @version 1.0.0
 */
public class FWAPropagator extends NaryPropagator<IntVarView> {

	static void post(Space home, VarArray<IntVar> x) {
		ViewArray<IntVarView> xa = new ViewArray<IntVarView>(home,
				IntVarView.class, x);

		Gecode.addPropagator(home, new FWAPropagator(home, xa));
	}

	public FWAPropagator(Space home, ViewArray<IntVarView> iv0) {
		super(home, iv0, PC_INT_VAL);
	}

	public FWAPropagator(Space home, Boolean share, FWAPropagator p) {
		super(home, share, p);
	}

	/**
	 * Compute propagation cost.
	 */
	@Override
	public PropCost cost() {
		return PC_QUADRATIC_HI;
	}

	/**
	 * Propagation function. TODO
	 */
	@Override
	public ExecStatus propagate(Space home) {
		DTNGraph g = Engine.dtn;
		int n = Engine.dtn.map.size();

		// initialize distance graph
		int[][] DG = new int[n][n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (i == j)
					DG[i][j] = 0;
				else
					DG[i][j] = Integer.MAX_VALUE;

		// get assigned edge
		for (int i = 0; i < iv.size(); i++) {
			if (iv.get(i).assigned()) {
				LabelWeightedEdge e = g.arr.get(i).get(iv.get(i).val());
				int x = g.map.get(g.getEdgeSource(e));
				int y = g.map.get(g.getEdgeTarget(e));
				int w = (int) g.getEdgeWeight(e);
				if (DG[x][y] == Integer.MAX_VALUE || DG[x][y] > w)
					DG[x][y] = w;
			}
		}

		// check consistency
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					int t = intAdd(DG[i][k], DG[k][j]);
					if (DG[i][j] > t) {
						// whether exists a negative circle
						if (i == j && t < 0)
							return ES_FAILED;
						else
							DG[i][j] = t;
					}
				}
			}
		}

		// meet constraint
		return ES_NOFIX;
	}
	
	private int intAdd(int l, int r) {
		if (l == Integer.MAX_VALUE || r == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return (l + r);
	}
}