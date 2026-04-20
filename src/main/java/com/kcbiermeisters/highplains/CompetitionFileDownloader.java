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
		String compFileName =
				compUrl.getHost().replace('.', '-') +
				compUrl.getPath().replace('/', '-') +
				".html";

		// do we have a local historical file to use?

		File compFile = new File(outputDir, compFileName);

		try
		{
			File localFile = new File(inputDir, compFileName);

			if (localFile.exists())
			{
				log.info("{} => Using local historical file", compUrl);

				FileUtils.copyFile(localFile, compFile);

				return Optional.of(compFile);
			}
		}
		catch (Exception e)
		{
			// do nothing
		}

		// download the v3 HTML export from the live site

		try
		{
			URL exportUrl = new URL(compUrl + "/includes/output.inc.php?section=export-results&go=judging_scores&action=default&filter=default&view=html");

			FileUtils.copyURLToFile(exportUrl, compFile, 5000, 60000);

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

		// nothing else to try

		return Optional.empty();
	}
}
