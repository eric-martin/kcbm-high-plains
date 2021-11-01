/*
 * CircuitResults.java
 */
package com.kcbiermeisters.highplains.circuit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.kcbiermeisters.highplains.bjcp.BjcpCategory;
import com.kcbiermeisters.highplains.bjcp.BjcpStyles;
import com.kcbiermeisters.highplains.comp.Place;
import com.kcbiermeisters.highplains.comp.WinningEntry;

import lombok.extern.slf4j.Slf4j;

/**
 * CircuitResults
 * @author Eric Martin
 */
@Slf4j
public class CircuitResults 
{
	private final BjcpStyles bjcpStyles;
	
	private final Map<String, String> brewerAliases;
	
	private final Map<String, String> clubAliases;

	private final Map<Brewer, BrewerResults> brewerResults;
	
	/**
	 * Constructor
	 */
	public CircuitResults(final BjcpStyles bjcpStyles, final Map<String, String> brewerAliases, final Map<String, String> clubAliases)
	{
		this.bjcpStyles = bjcpStyles;
		this.brewerAliases = brewerAliases;
		this.clubAliases = clubAliases;
		this.brewerResults = new HashMap<>();
	}
	
	/**
	 * mergeWinningEntries
	 */
	public void mergeWinningEntries(final List<WinningEntry> winningEntries)
	{
        for (WinningEntry winningEntry : winningEntries)
        {
        	Optional<BjcpCategory> optCategory = bjcpStyles.getCategory(winningEntry.getStyle());
        	
            if (optCategory.isPresent())
            {
            	String name = winningEntry.getName();
            	name = brewerAliases.getOrDefault(name, name);
            	
            	String club = winningEntry.getClub();
            	club = clubAliases.getOrDefault(club, club);
            	
                Brewer brewer = new Brewer(name, club);
                
                if (!brewerResults.containsKey(brewer))
                {
                	// no match 
                	
                	boolean found = false;

                	// check for a match by name only
                	
                	Brewer possibleMatch = null;
                	
                	for (Brewer b : brewerResults.keySet())
                	{
                		if (b.getName().equals(brewer.getName()))
                		{
                			possibleMatch = b;
                			break;
                		}
                	}
                	
                	// possibly merge brewers
                	
                	if (possibleMatch != null)
                	{
                		if (brewer.hasNoClub())
                		{
                			// existing brewer has a club defined
                			// current brewer matches on name but no club defined
                			// match to the existing brewer with a club
                			
                			brewer = possibleMatch;
                			
                			found = true;
                		}
                		else if (possibleMatch.hasNoClub())
                		{
                			// existing brewer doesn't have a club defined
                			// current brewer matches on name and has a club defined
                			// replace the existing brewer record with the current one
                			
	                		BrewerResults results = brewerResults.remove(possibleMatch);
	                		results.getBrewer().setClub(brewer.getClub());
	                		brewerResults.put(brewer, results);
	                		
	                		found = true;
                		}
                	}
                	
                	// add the brewer is we couldn't find an existing matching record
                	
                	if (!found)
                	{
                		brewerResults.put(brewer, new BrewerResults(brewer));
                	}
                }
                
                Place place = winningEntry.getPlace();
                
                brewerResults.get(brewer).merge(optCategory.get(), place);
            }
            else
            {
                log.error("Unknown style: {}", winningEntry.getStyle());   
            }            
        }
        
        for (Brewer brewer : brewerResults.keySet())
        {
            log.debug("{} -> {}", brewer, brewerResults.get(brewer));
        }
	}
	
	/**
	 * getBrewerResults
	 */
	public List<BrewerResults> getBrewerResults()
	{
		List<BrewerResults> results = new ArrayList<>();
		results.addAll(brewerResults.values());
		
		Collections.sort(results, new ResultsComparator());
		Collections.reverse(results);
		
		return results;
	}
	
	/**
	 * getClubResults
	 */
	public List<ClubResults> getClubResults()
	{
		Map<String, ClubResults> clubResults = new HashMap<>();
		
		for (BrewerResults brewerResults : brewerResults.values())
		{
			String club = brewerResults.getBrewer().getClub();

			if (club != null && !club.isEmpty() && !club.trim().isEmpty())
			{
				if (!clubResults.containsKey(club))
				{
					clubResults.put(club, new ClubResults(club));
				}
				
				clubResults.get(club).add(brewerResults);
			}
		}
		
		List<ClubResults> results = new ArrayList<>();
		results.addAll(clubResults.values());
		
		Collections.sort(results, new ResultsComparator());
		Collections.reverse(results);
		
		return results;
	}
}
