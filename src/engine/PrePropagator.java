package engine;

import static org.gecode.GecodeEnumConstants.ES_FAILED;
import static org.gecode.GecodeEnumConstants.ES_FIX;
import static org.gecode.GecodeEnumConstants.PC_INT_VAL;
import static org.gecode.GecodeEnumConstants.PC_QUADRATIC_HI;

import java.util.HashSet;
import java.util.Set;

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
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Preprocessing Propagator use 'Forward Check'&'Remove Sucked Variable' to
 * ensure STP be consistent.
 * 
 * @author jacky
 * @version 1.0.0
 */
public class PrePropagator extends NaryPropagator<IntVarView> {

	// core object in Spring IoC
	private DefCore config;

	static void post(Space home, VarArray<IntVar> x) {
		ViewArray<IntVarView> xa = new ViewArray<IntVarView>(home,
				IntVarView.class, x);

		Gecode.addPropagator(home, new PrePropagator(home, xa));
	}

	public PrePropagator(Space home, ViewArray<IntVarView> iv0) {
		super(home, iv0, PC_INT_VAL);
	}

	public PrePropagator(Space home, Boolean share, PrePropagator p) {
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
		Set<Integer> index = new HashSet<Integer>();
		for (int i = 0; i < iv.size(); i++) {
			if (iv.get(i).assigned()) {
				LabelWeightedEdge e = g.arr.get(i).get(iv.get(i).val());
				int x = g.map.get(g.getEdgeSource(e));
				index.add(x);
				int y = g.map.get(g.getEdgeTarget(e));
				index.add(y);
				int w = (int) g.getEdgeWeight(e);
				if (DG[x][y] == Integer.MAX_VALUE || DG[x][y] > w)
					DG[x][y] = w;
			}
		}

		// construct DG
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					int t = intAdd(DG[i][k], DG[k][j]);
					if (DG[i][j] > t) {
						DG[i][j] = t;
					}
				}
			}
		}

		// check consistency
		for (int a = 0; a < iv.size(); a++) {
			IntVarView ivv = iv.get(a);
			if (!ivv.assigned()) {
				for (int b = 0; b < ivv.size(); b++) {
					LabelWeightedEdge e = g.arr.get(a).get(b);
					int x = g.map.get(g.getEdgeSource(e));
					int y = g.map.get(g.getEdgeTarget(e));
					if (index.contains(x) && index.contains(y)) {
						int w = (int) g.getEdgeWeight(e);
						readConfig();

						// RSV
						if (config.isRSV_P() && DG[x][y] <= w) {
							if (ivv.eq(home, b).failed())
								return ES_FAILED;
							break;
						}

						// FC
						if (config.isFC_P() && intAdd(DG[y][x], w) < 0) {
							if (ivv.nq(home, b).failed())
								return ES_FAILED;
						}
					}
				}
			}
		}

		// meet constraint
		return ES_FIX;
	}

	private int intAdd(int l, int r) {
		if (l == Integer.MAX_VALUE || r == Integer.MAX_VALUE)
			return Integer.MAX_VALUE;
		else
			return (l + r);
	}

	private void readConfig() {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		config = (DefCore) ac.getBean("core");
	}

//	private int[][] deepCopy(int[][] g) {
//		int[][] cg = new int[g.length][];
//		for (int i = 0; i < g.length; i++) {
//			cg[i] = g[i].clone();
//		}
//		return cg;
//	}

}