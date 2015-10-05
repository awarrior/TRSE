package engine;

import org.gecode.IntVar;
import org.gecode.Space;
import org.gecode.VarArray;

/**
 * Core module in Engine needed to be realized. It is a detachable interface
 * used by Spring IoC.
 * 
 * @author jacky
 * @version 1.0.0
 */
public interface CoreIface {
	/**
	 * Post propagators and branchings.
	 * 
	 * @param s
	 *            receive Engine space object
	 * @param a
	 *            receive meta-variables array in Engine
	 */
	public void post(Space s, VarArray<IntVar> a);
}
