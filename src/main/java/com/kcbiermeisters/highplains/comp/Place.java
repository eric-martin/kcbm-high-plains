/*
 * Place.java
 */
package com.kcbiermeisters.highplains.comp;

import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Place
 * @author Eric Martin
 */
@AllArgsConstructor
public enum Place
{
    FIRST(5),
    SECOND(3),
    THIRD(1),
    FOURTH(0),
    HM(0),
    NA(0);
    
    @Getter
    private final int points;
    
    /**
     * fromHtmlString
     */
    public static Optional<Place> fromHtmlString(final String place)
    {
        if ("1st".equals(place))
        {
            return Optional.of(FIRST);
        }
        
        if ("2nd".equals(place))
        {
            return Optional.of(SECOND);
        }
        
        if ("3rd".equals(place))
        {
            return Optional.of(THIRD);
        }
        
        if ("4th".equals(place))
        {
            return Optional.of(THIRD);
        }
        
        if ("HM".equals(place))
        {
            return Optional.of(HM);
        }
        
        if ("N/A".equals(place))
        {
            return Optional.of(NA);
        }
        
        return Optional.empty();
    }
}
