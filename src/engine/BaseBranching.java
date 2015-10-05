package engine;

import static org.gecode.GecodeEnumConstants.ES_FAILED;
import static org.gecode.GecodeEnumConstants.ES_OK;

import org.gecode.Branching;
import org.gecode.ExecStatus;
import org.gecode.Gecode;
import org.gecode.IntModEvent;
import org.gecode.IntVar;
import org.gecode.IntVarView;
import org.gecode.JavaBranchingDesc;
import org.gecode.Space;
import org.gecode.VarArray;
import org.gecode.ViewArray;

/**
 * Branching use 'Minimal Remainder Variable' heuristic.
 * 
 * @author jacky
 * @version 1.0.0
 */
class BaseBranching extends Branching {
	private ViewArray<IntVarView> x;

	public static void post(Space home, VarArray<IntVar> x) {
		Gecode.addBranching(home, new BaseBranching(home, x));
	}

	public BaseBranching(Space home, VarArray<IntVar> x0) {
		x = new ViewArray<IntVarView>(home, IntVarView.class, x0);
	}

	public BaseBranching(Space home, Boolean share, BaseBranching m) {
		x = new ViewArray<IntVarView>(home, share, m.x);
	}

	/**
	 * Return if this branching has alternatives left.
	 */
	@Override
	public boolean status(Space home) {
		return (!x.assigned());
	}

	/**
	 * Return a branching description. TODO
	 */
	@Override
	public JavaBranchingDesc description(Space home) {
		int index = 0;
		for (IntVarView v : x) {
			if (!v.assigned()) {
				return new JavaBranchingDesc(index, v.min());
			}
			index++;
		}
		return null;
	}

	/**
	 * Commit to an alternative with branching description.
	 */
	@Override
	public ExecStatus commit(Space home, JavaBranchingDesc d, long a) {
		IntVarView ivv = x.get(d.pos());
		int v = d.val();
		IntModEvent ime = (a == 0) ? ivv.eq(home, v) : ivv.nq(home, v);
		return ime.failed() ? ES_FAILED : ES_OK;
	}

}