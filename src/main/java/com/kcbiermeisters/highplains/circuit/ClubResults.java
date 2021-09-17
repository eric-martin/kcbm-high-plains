/*
 * ClubResults.java
 */
package com.kcbiermeisters.highplains.circuit;

import lombok.Getter;
import lombok.ToString;

/**
 * ClubResults
 * @author Eric Martin
 */
@Getter
@ToString(exclude = "club")
public class ClubResults implements Results
{
	private final String club;
	
    private int totalPoints;
	
	private int firstCount;
	private int secondCount;
	private int thirdCount;
    
    /**
     * Constructor
     */
    public ClubResults(final String club)
    {
    	this.club = club;
    }
    
    /**
     * add
     */
    public void add(final BrewerResults brewerResults)
    {
    	totalPoints += brewerResults.getTotalPoints();
    	
    	firstCount += brewerResults.getFirstCount();
    	secondCount += brewerResults.getSecondCount();
    	thirdCount += brewerResults.getThirdCount();
    }
}
