/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import javax.swing.AbstractAction;

/**
 * @author dazz
 * 
 */
public abstract class TabOpenAction extends AbstractAction
{

	QueryOperationNode	node;

	public TabOpenAction(final QueryOperationNode node) {
		this.node = node;
	}

}
