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

        Elements body = htmlResults.select("body");
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