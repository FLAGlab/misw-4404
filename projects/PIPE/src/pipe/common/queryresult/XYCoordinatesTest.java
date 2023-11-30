/**
 * 
 */
package pipe.common.queryresult;

import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author dazz
 * 
 */
public class XYCoordinatesTest extends TestCase
{

	private void printList(final List<XYCoordinate> list)
	{
		final StringBuilder b = new StringBuilder();
		for (final XYCoordinate c : list)
		{
			b.append(c.getX());
			b.append(" ");
			b.append(c.getY());
			b.append("\n");
		}
		QueryResultLoggingHandler.logger.info(b.toString());

	}

	public void testFromTo()
	{
		final XYCoordinates cs = new XYCoordinates();

		cs.add(new XYCoordinate(0, 10));
		cs.add(new XYCoordinate(1, 8));
		cs.add(new XYCoordinate(2, 0));
		cs.add(new XYCoordinate(3, 6));
		cs.add(new XYCoordinate(4, 100));
		cs.add(new XYCoordinate(5, 1));
		cs.add(new XYCoordinate(8, 0));
		cs.add(new XYCoordinate(6, 4));

		List<XYCoordinate> c = cs.getPoints();
		this.printList(c);
		Assert.assertEquals(8, c.size());

		c = cs.getFromYToY(3, 7);
		this.printList(c);
		Assert.assertEquals(2, c.size());

		c = cs.getFromXToX(3, 6);
		this.printList(c);
		Assert.assertEquals(3, c.size());
	}

	public void testMax()
	{
		final XYCoordinates cs = new XYCoordinates();

		cs.add(new XYCoordinate(0, 10));
		cs.add(new XYCoordinate(1, 8));
		cs.add(new XYCoordinate(2, 0));
		cs.add(new XYCoordinate(3, 6));
		cs.add(new XYCoordinate(4, 100));
		cs.add(new XYCoordinate(5, 1));
		cs.add(new XYCoordinate(8, 0));
		cs.add(new XYCoordinate(6, 4));

		List<XYCoordinate> c = cs.getFromXToX(-2, 77);
		this.printList(c);
		Assert.assertEquals(8, c.size());

		c = cs.getFromYToY(3, 101);
		this.printList(c);
		Assert.assertEquals(5, c.size());

		c = cs.getFromXToX(0.42312, 0.8);
		this.printList(c);
		Assert.assertEquals(0, c.size());

		c = cs.getFromYToY(1.1, 7.5);
		this.printList(c);
		Assert.assertEquals(2, c.size());

		c = cs.getFromYToY(1.1, 101.5);
		this.printList(c);
		Assert.assertEquals(5, c.size());
	}
}
