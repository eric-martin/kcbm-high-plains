/*
 * XmlClass.java
 */
package com.kcbiermeisters.highplains.bjcp;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.ToString;

/**
 * XmlClass
 * @author Eric Martin
 */
@Getter
@ToString
public class XmlClass 
{
    @XmlAttribute(name = "type")
    private String type;

    @XmlElement(name = "category")
    private List<XmlCategory> categories;
}
