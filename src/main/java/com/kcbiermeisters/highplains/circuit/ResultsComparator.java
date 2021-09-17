/*
 * ResultsComparator.java
 */
package com.kcbiermeisters.highplains.circuit;

import java.util.Comparator;

/**
 * ResultsComparator
 * @author Eric Martin
 */
public class ResultsComparator implements Comparator<Results>
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final Results first, final Results second) 
	{
		int tp = Integer.compare(first.getTotalPoints(), second.getTotalPoints());
		
		if (tp == 0)
		{
			int g = Integer.compare(first.getFirstCount(), second.getFirstCount());
			
			if (g == 0)
			{
				int s = Integer.compare(first.getSecondCount(), second.getSecondCount());
				
				if (s == 0)
				{
					return Integer.compare(first.getThirdCount(), second.getThirdCount());
				}
				
				return s;
			}
			
			return g;
		}
		
		return tp;
	}
}
