/*
 * BrewerResults.java
 */
package com.kcbiermeisters.highplains.circuit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.kcbiermeisters.highplains.bjcp.BjcpCategory;
import com.kcbiermeisters.highplains.comp.Place;

import lombok.Getter;
import lombok.ToString;

/**
 * BrewerResults
 * @author Eric Martin
 */
@ToString(exclude = "brewer")
public class BrewerResults implements Results
{
	@Getter
	private final Brewer brewer;
	
    private final Map<BjcpCategory, Place> results;
    
    /**
     * Constructor
     */
    public BrewerResults(final Brewer brewer)
    {
    	this.brewer = brewer;
    	this.results = new HashMap<>();
    }
    
    /**
     * merge
     */
    public void merge(final BjcpCategory category, final Place place)
    {
        if (results.containsKey(category))
        {
            Place existingPlace = results.get(category);
            
            if (existingPlace.getPoints() < place.getPoints())
            {
                results.put(category, place);
            }
        }
        else
        {
            results.put(category, place);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalPoints()
    {
    	int totalPoints = 0;
    	
    	for (Place place : results.values())
    	{
    		totalPoints += place.getPoints();
    	}
    	
    	return totalPoints;
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public int getFirstCount() 
	{
		return getCount(Place.FIRST);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getSecondCount() 
	{
		return getCount(Place.SECOND);
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public int getThirdCount() 
	{
		return getCount(Place.THIRD);
	}
	
	/**
	 * getPlace
	 */
	public Optional<Place> getPlace(final BjcpCategory category)
	{
		if (results.containsKey(category))
		{
			return Optional.of(results.get(category));
		}
				
		return Optional.empty();
	}
    
    /**
     * getCount
     */
    private int getCount(final Place place)
    {
    	int count = 0;

    	for (Place p : results.values())
    	{
    		if (place.equals(p))
    		{
    			++count;
    		}
    	}

    	return count;
    }
}
