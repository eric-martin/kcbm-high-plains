/*
 * BjcpStyleMap.java
 */
package com.kcbiermeisters.highplains.bjcp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * BjcpStyleMap
 * @author Eric Martin
 */
@Slf4j
@UtilityClass
public class BjcpStyleMap 
{
    /**
     * getInstance
     */
    @SneakyThrows
    public Map<String, String> getInstance(final File styleGuide)
    {
        // read the file into our xml objects

        JAXBContext jaxbContext = JAXBContext.newInstance(XmlStyleguide.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        XmlStyleguide styleguide = (XmlStyleguide) jaxbUnmarshaller.unmarshal(styleGuide);
        
        // build map from subcategory to category
        
        Map<String, String> styleMap = new HashMap<>();
        
        for (XmlClass xmlClass : styleguide.getClasses())
        {
            for (XmlCategory xmlCategory : xmlClass.getCategories())
            {
                for (XmlSubcategory xmlSubcategory : xmlCategory.getSubcategories())
                {
                    log.debug("{} => {}", xmlSubcategory.getName(), xmlCategory.getName());
                    
                    styleMap.put(xmlSubcategory.getName(), xmlCategory.getName());
                }               
            }
        }
                                
        // done
        
        return styleMap;
    }
}
