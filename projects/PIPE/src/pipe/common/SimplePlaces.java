package pipe.common;

import java.io.Serializable;

import pipe.common.dataLayer.DataLayer;
import pipe.common.dataLayer.Place;

public class SimplePlaces implements Serializable
{
	/**
	 * Simple Places is a simplified version of the pipe.dataLayer.Place class
	 * Its purpose is to provide a simple serializable object for socket
	 * transmission to processing clusters. The necessary attributes from Places
	 * for building a 'mod' file are IDs, current markings and length
	 */
	private static final long	serialVersionUID	= 1L;

	public final String[]		ids;
	public final int[]			marking;
	public final int			length;
	public final String[]		names;

	public SimplePlaces(final DataLayer pnmldata) {
		int i;
		final Place[] places = pnmldata.getPlaces();

		this.length = places.length;

		this.ids = new String[this.length];
		this.marking = new int[this.length];
		this.names = new String[this.length];

		for (i = 0; i < this.length; i++)
		{
			this.ids[i] = places[i].getId();
			this.names[i] = places[i].getName();
			this.marking[i] = places[i].getCurrentMarking();
		}
	}
}
