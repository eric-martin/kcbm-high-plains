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
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private static final Logger log = LoggerFactory.getLogger(KcbmHighPlainsResults.class);

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
        styleMap.adjustFor2021BeerGuidelines();
		//styleMap.adjustFor2025CiderGuidelines();
        
        Map<String, String> styleAliases = AliasProperties.readFile(new File(inputDir, "circuit/style-alias.properties"));
        styleMap.addStyleAliases(styleAliases);
        
        // tally up results

        Map<String, String> brewerAliases = AliasProperties.readFile(new File(inputDir, "circuit/brewer-alias.properties"));

        Map<String, String> clubAliases = AliasProperties.readFile(new File(inputDir, "circuit/club-alias.properties"));

        CircuitResults circuitResults = new CircuitResults(styleMap, brewerAliases, clubAliases);

        String[] competitions = {
			"https://kcbm.brewingcompetitions.com",
			"https://ibuopen.brewingcompetitions.com",
			"https://competitions.redearthbrewers.com/springbrewoff",
			"https://www.lincolnlagers.com/cup",
			"https://doggdayzz.brewingcompetitions.com",
            "https://hoppyhalloween.com/comp",
            //"https://foamcup.us"
        };

		CompetitionFileDownloader compFileDownloader = new CompetitionFileDownloader(inputDir, outputDir);
                        
        for (String competition : competitions)
        {
			URL compUrl = new URL(competition);

			Optional<File> compFile = compFileDownloader.download(compUrl);

			if (compFile.isPresent())
			{
				List<WinningEntry> winningEntries = CompetitionResults.getResults(compFile.get());

				if (!winningEntries.isEmpty())
				{
					circuitResults.mergeWinningEntries(winningEntries);
				}
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
