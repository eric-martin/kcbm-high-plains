/*
 * KcbmHighPlainsResults.java
 */
package com.kcbiermeisters.highplains;

import java.io.File;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.kcbiermeisters.highplains.bjcp.BjcpStyles;
import com.kcbiermeisters.highplains.circuit.AliasProperties;
import com.kcbiermeisters.highplains.circuit.CircuitResults;
import com.kcbiermeisters.highplains.circuit.Eligibility;
import com.kcbiermeisters.highplains.comp.CompetitionResults;
import com.kcbiermeisters.highplains.comp.WinningEntry;

/**
 * KcbmHighPlainsResults
 * @author Eric Martin
 */
public class KcbmHighPlainsResults
{
    /**
     * main
     */
    public static void main(String[] args) throws Exception
    {
    	// setup input directory
    	
    	File inputDir = new File("src/main/resources");
    	
        // create output directory
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(ZoneId.from(ZoneOffset.UTC));
        
        String directory = formatter.format(Instant.now());

        File outputDir = new File(directory);
        outputDir.mkdir();

        // read the style guide
        
        BjcpStyles styleMap = new BjcpStyles(new File(inputDir, "bjcp/2015_styleguide.xml"));

        styleMap.adjustFor2021Guidelines();
        
        Map<String, String> styleAliases = AliasProperties.readFile(new File(inputDir, "circuit/style-alias.properties"));
        styleMap.addStyleAliases(styleAliases);
        
        // tally up results

        Map<String, String> brewerAliases = AliasProperties.readFile(new File(inputDir, "circuit/brewer-alias.properties"));
        Map<String, String> clubAliases = AliasProperties.readFile(new File(inputDir, "circuit/club-alias.properties"));
        
        CircuitResults circuitResults = new CircuitResults(styleMap, brewerAliases, clubAliases);

        String[] competitions = {
            "https://kcbm.brewingcompetitions.com/",
			//"ibuopen.brewcompetition.com.html",
            //"https://www.lincolnlagers.com/cup",
            //"https://hoppyhalloween.com/comp/",
            //"https://foamcup.us/",
			//"https://stlbrews.brewingcompetitions.com"
        };
                        
        for (String competition : competitions)
        {
        	File competitionFile;

        	if (competition.contains("://"))
        	{
        		URL compUrl = new URL(competition + "/output/export.output.php?section=results&go=judging_scores&action=default&filter=default&view=html");

        		//competitionFile = new File(outputDir, compUrl.getHost() + ".html");

        		//FileUtils.copyURLToFile(compUrl, competitionFile, 5000, 5000);

        		//if (competitionFile.length() < 1000)
        		//{
        			compUrl = new URL(competition);

            		competitionFile = new File(outputDir, compUrl.getHost() + ".html");

            		FileUtils.copyURLToFile(compUrl, competitionFile, 5000, 5000);
        		//}
        	}
        	else
        	{
        		competitionFile = new File(inputDir, competition);

        		FileUtils.copyFile(competitionFile, new File(outputDir, competition));
        	}

        	List<WinningEntry> winningEntries = CompetitionResults.getResults(competitionFile);

            if (!winningEntries.isEmpty())
            {
                circuitResults.mergeWinningEntries(winningEntries);
            }
        }
                
        // write the results
        
        Map<String, String> clubState = AliasProperties.readFile(new File(inputDir, "circuit/club-state.properties"));

        Eligibility eligibility = new Eligibility(clubState);

        ResultsSpreadsheet resultsSpreadsheet = new ResultsSpreadsheet(directory, "high-plains-results-" + directory + ".xlsx");
        resultsSpreadsheet.createClubSheet(circuitResults.getClubResults(), eligibility);
        resultsSpreadsheet.createBrewerSheet(circuitResults.getBrewerResults(), eligibility);
        resultsSpreadsheet.createBrewerDetailsSheet(circuitResults.getBrewerResults(), styleMap.getCategories());
        resultsSpreadsheet.write();
        resultsSpreadsheet.close();
        
        // copy the log file
        
		File logFile = new File("kcbm-high-plains.log");
		
		FileUtils.copyFile(logFile, new File(outputDir, "kcbm-high-plains.log"));
        
        // done
        
        return;
    }
}
