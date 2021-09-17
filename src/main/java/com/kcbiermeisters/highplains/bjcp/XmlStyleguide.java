/*
 * XmlStyleguide.java
 */
package com.kcbiermeisters.highplains.bjcp;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.ToString;

/**
 * XmlStyleguide
 * @author Eric Martin
 */
@Getter
@ToString
@XmlRootElement(name = "styleguide")
public class XmlStyleguide 
{
    @XmlElement(name = "class")
    private List<XmlClass> classes;
}
