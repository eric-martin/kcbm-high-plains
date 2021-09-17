/*
 * AliasProperties.java
 */
package com.kcbiermeisters.highplains.circuit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * AliasProperties
 * @author Eric Martin
 */
@Slf4j
@UtilityClass
public class AliasProperties 
{
	/**
	 * readFile
	 */
	@SneakyThrows
	public Map<String, String> readFile(final File file)
	{
		Map<String, String> aliases = new HashMap<>();
		
        try (Scanner aliasScanner = new Scanner(file))
        {
            while (aliasScanner.hasNextLine())
            {
                String aliasLine = aliasScanner.nextLine();
                
                String[] aliasParts = aliasLine.split("=");
                
                if (aliasParts.length == 2)
                {
                    String alias = aliasParts[0];
                    String actual = aliasParts[1];
                    
                    log.debug("Alias => {} => {}", alias, actual);
                    
                    aliases.put(alias, actual);
                }
                else
                {
                    // TODO
                }
            }
        }

        return aliases;
	}
}
