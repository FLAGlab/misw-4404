package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import pipe.common.AnalysisSettings;
import pipe.modules.queryeditor.evaluator.SettingsManager;

public class EvaluationSettingsServerPanel extends ServerSettingsBasicPanel
{

	private JPanel			serverPanel	= null;
	private final JSpinner	processorNo;

	public EvaluationSettingsServerPanel(JDialog parent) {
		// Use parent to construct basic server panel
		super(parent);
		this.serverPanel = super.getPanel();

		// Extract processor number value from analysisSettings
		final AnalysisSettings analysisSettings = SettingsManager.getAnalysisSettings();
		final int noOfProcessorsExtracted = analysisSettings.numProcessors;

		// Add additional processor entry components
		this.serverPanel.add(new JLabel("No. Processors"));
		this.processorNo = new JSpinner(new SpinnerNumberModel(noOfProcessorsExtracted, 2, 10000, 1));
		this.serverPanel.add(this.processorNo);
		this.serverPanel.setMaximumSize(new Dimension(	Integer.MAX_VALUE,
														this.serverPanel.getPreferredSize().height));
	}

	public int getNumProcessors() throws NumberFormatException
	{
		// Test that it's a valid int, exception raise if not
		final Integer value = (Integer) this.processorNo.getValue();
		return value.intValue();
	}
}