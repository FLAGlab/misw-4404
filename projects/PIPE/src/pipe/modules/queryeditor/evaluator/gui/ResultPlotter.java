/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;

import pipe.common.queryresult.ResultWrapper;
import pipe.common.queryresult.XYCoordinate;
import pipe.common.queryresult.XYCoordinates;
import pipe.modules.queryeditor.evaluator.QueryAnalysisException;

/**
 * @author dazz
 * 
 */
public abstract class ResultPlotter implements EvaluatorGuiLoggingHandler
{
	protected final Box		resultsPanel	= Box.createVerticalBox();
	protected JFreeChart	chart;
	protected ChartPanel	chartPanel;

	protected JPanel		graphPanel;

	protected JButton		switchViewBtn;

	protected Dimension		prefferedSize;

	public ResultPlotter(final Dimension prefferedSize) {
		this.prefferedSize = prefferedSize;
	}

	// currently
	// showing a
	// PDF view

	public abstract JComponent getChart(ResultWrapper w) throws QueryAnalysisException;

	public XYSeries getXYSeries(final XYCoordinates coords, final String plotName)
	{
		final XYSeries series = new XYSeries(plotName);
		for (final XYCoordinate c : coords.getPoints())
		{
			series.add(c.getX(), c.getY());
		}
		return series;
	}
}
