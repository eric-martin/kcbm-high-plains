/*
 * XmlSubcategory.java
 */
package com.kcbiermeisters.highplains.bjcp;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.ToString;

/**
 * XmlSubcategory
 * @author Eric Martin
 */
@Getter
@ToString
public class XmlSubcategory 
{
    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "name")
    private String name;
}
