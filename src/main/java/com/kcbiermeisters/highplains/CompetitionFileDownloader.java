package com.kcbiermeisters.highplains;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * CompetitionFileDownloader
 */
@Slf4j
public class CompetitionFileDownloader
{
	private final File inputDir;
	private final File outputDir;

	public CompetitionFileDownloader(final File inputDir, final File outputDir)
	{
		this.inputDir = inputDir;
		this.outputDir = outputDir;
	}

	/**
	 * download
	 */
	public Optional<File> download(final URL compUrl)
	{
		// do we have a local override?

		File compFile = new File(outputDir, compUrl.getHost() + ".html");

		try
		{
			File localFile = new File(inputDir, compUrl.getHost() + ".html");

			if (localFile.exists())
			{
				log.info("{} => Using local override file", compUrl);

				FileUtils.copyFile(localFile, compFile);

				return Optional.of(compFile);
			}
		}
		catch (Exception e)
		{
			// do nothing
		}

		// can we get a 'new' export?

		try
		{
			URL exportUrl = new URL(compUrl, "/includes/output.inc.php?section=export-results&go=judging_scores&action=default&filter=default&view=html");

			FileUtils.copyURLToFile(exportUrl, compFile, 5000, 5000);

			if (1000 < compFile.length())
			{
				log.info("{} => Using live site export", compUrl);
			}

			return Optional.of(compFile);
		}
		catch (Exception e)
		{
			// do nothing
		}

		// can we get an 'old' export?

		try
		{
			URL exportUrl = new URL(compUrl, "/output/export.output.php?section=results&go=judging_scores&action=default&filter=default&view=html");
			FileUtils.copyURLToFile(exportUrl, compFile, 5000, 5000);

			if (1000 < compFile.length())
			{
				log.info("{} => Using live site export", compUrl);
			}

			return Optional.of(compFile);
		}
		catch (Exception e)
		{
			// do nothing
		}

		// can we get the main display?

		try
		{
			FileUtils.copyURLToFile(compUrl, compFile, 5000, 5000);

			log.info("{} => Using live site", compUrl);

			return Optional.of(compFile);
		}
		catch (Exception e)
		{
			// do nothing
		}

		// nothing else to try

		return Optional.empty();
	}
}
