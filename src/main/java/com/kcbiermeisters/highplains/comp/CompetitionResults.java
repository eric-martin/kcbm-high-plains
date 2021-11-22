/*
 * CompetitionResults.java
 */
package com.kcbiermeisters.highplains.comp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * CompetitionResults
 * @author Eric Martin
 */
@Slf4j
@UtilityClass
public class CompetitionResults
{
    /**
     * getResults
     */
    @SneakyThrows
    public List<WinningEntry> getResults(final File file)
    {
        log.info("{}", file);
        
        Document htmlResults = Jsoup.parse(file, null);
        
        List<WinningEntry> winningEntries = new ArrayList<>();

        Element body = htmlResults.select("body").first();
        
        Elements script = body.select("script");
        
        if (!script.isEmpty())
        {
        	// remove best of show tables
        	        	
            for (Element child : body.select("div"))
        	{
            	if ("bcoem-winner-table".equals(child.className()))
        		{
        			Element heading = child.select("h3").first();
        			
        			if (heading.text().contains("Best of Show") || 
        				heading.text().contains("Okie Cup") ||
        				heading.text().contains("The FOAM Cup"))
        			{
        				child.remove();
        			}
        		}
        	}
        }
        
        Elements tables = body.select("table");
        
        for (Element table : tables) 
        {
            Elements rows = table.select("tr");
            
            for (int i = 1; i < rows.size(); i++)
            {
                Element row = rows.get(i);
                Elements cols = row.select("td");
                
                String placeText = cols.get(0).ownText();
                String brewerText = cols.get(1).ownText();
                String styleText = cols.get(3).ownText();
                String clubText = cols.get(4).ownText();
                
                if (brewerText.contains("Co-Brewer:"))
                {
                	brewerText = brewerText.substring(0, brewerText.indexOf("Co-Brewer:")).trim();
                }
                
                if (styleText.contains(":"))
                {
                	styleText = styleText.substring(styleText.indexOf(':') + 1).trim();
                }
                
                log.debug("{} / {} / {} / {}", placeText, brewerText, styleText, clubText);
                
                Optional<Place> optPlace = Place.fromHtmlString(placeText);
                
                if (optPlace.isPresent())
                {
                    Place place = optPlace.get();

                    if (0 < place.getPoints())
                    {
                        WinningEntry entry = new WinningEntry(place, brewerText, styleText, clubText);                        
                        winningEntries.add(entry);
                    }
                }
                else
                {
                    log.error("Unknown place string: {}", placeText);
                }
            }
        }
        
        return winningEntries; 
    }
    
    
}
