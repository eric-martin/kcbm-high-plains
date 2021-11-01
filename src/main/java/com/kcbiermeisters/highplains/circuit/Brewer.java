/*
 * Brewer.java
 */
package com.kcbiermeisters.highplains.circuit;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Brewer
 * @author Eric Martin
 */
@Data
@AllArgsConstructor
public class Brewer
{
    private final String name;
    private String club;
    
    public boolean hasNoClub()
    {
    	return club == null || club.isEmpty() || club.isBlank();
    }
}
