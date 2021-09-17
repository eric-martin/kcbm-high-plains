/*
 * Eligibility.java
 */
package com.kcbiermeisters.highplains.circuit;

import java.util.Map;
import java.util.Set;

/**
 * Eligibility
 * @author Eric Martin
 */
public class Eligibility 
{
	private static final String UNKNOWN_STATE = "?";
	
    private static final Set<String> ELIGIBLE_STATES = Set.of("OK", "KS", "MO", "NE", "IA", "SD", "ND", "MN", UNKNOWN_STATE);

    private final Map<String, String> clubState;
    
    public Eligibility(final Map<String, String> clubState)
    {
    	this.clubState = clubState;
    }
    
    public String getState(final BrewerResults brewerResults)
    {
    	if (brewerResults.getBrewer().getClub().isBlank())
    	{
    		// TODO ... no club ... brewer to state lookup
    		
    		return UNKNOWN_STATE;
    	}
    	
    	return clubState.getOrDefault(brewerResults.getBrewer().getClub(), UNKNOWN_STATE);
    }
    
    public String getState(final ClubResults clubResults)
    {
    	return clubState.getOrDefault(clubResults.getClub(), UNKNOWN_STATE);
    }
    
    public boolean isEligible(final String state)
    {
    	return ELIGIBLE_STATES.contains(state);
    }
}
