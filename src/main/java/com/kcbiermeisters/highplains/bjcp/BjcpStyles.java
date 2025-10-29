/*
 * BjcpStyles.java
 */
package com.kcbiermeisters.highplains.bjcp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * BjcpStyles
 * @author Eric Martin
 */
@Slf4j
public class BjcpStyles
{
	// list of categories
	@Getter
	private final List<BjcpCategory> categories = new ArrayList<>();

	// map from subcategory to category
	private final Map<String, BjcpCategory> subToCat = new HashMap<>();

	/**
	 * Constructor
	 */
	@SneakyThrows
	public BjcpStyles(final File styleGuide)
	{
		// read the file into our xml objects

		JAXBContext jaxbContext = JAXBContext.newInstance(XmlStyleguide.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		XmlStyleguide styleguide = (XmlStyleguide) jaxbUnmarshaller.unmarshal(styleGuide);

		// build map from subcategory to category

		for (XmlClass xmlClass : styleguide.getClasses())
		{
			for (XmlCategory xmlCategory : xmlClass.getCategories())
			{
				BjcpCategory category = new BjcpCategory(xmlCategory.getId(), xmlCategory.getName());
				categories.add(category);

				for (XmlSubcategory xmlSubcategory : xmlCategory.getSubcategories())
				{
					//log.debug("{} => {}", xmlSubcategory.getName(), category);

					subToCat.put(xmlSubcategory.getName(), category);
				}
			}
		}

		// add provisional style category

		BjcpCategory provisionalCategory = new BjcpCategory("PRX", "Provisional Style");
		categories.add(provisionalCategory);
	}

	/**
	 * addStyleAliases
	 */
	public void addStyleAliases(final Map<String, String> styleAliases)
	{
		for (Map.Entry<String, String> alias : styleAliases.entrySet())
		{
			// get matching category

			BjcpCategory matchingCategory = null;

			for (BjcpCategory category : categories)
			{
				if (category.getName().equals(alias.getValue()))
				{
					matchingCategory = category;
					break;
				}
			}

			// add to the subcategory to category map

			if (matchingCategory != null)
			{
				subToCat.put(alias.getKey(), matchingCategory);
			}
			else
			{
				log.error("Can't find match for style alias {}", alias);
			}
		}
	}

	/**
	 * getCategory
	 */
	public Optional<BjcpCategory> getCategory(final String subcategory)
	{
		if (subToCat.containsKey(subcategory))
		{
			return Optional.of(subToCat.get(subcategory));
		}

		return Optional.empty();
	}

	/**
	 * adjustFor2021BeerGuidelines
	 */
	public void adjustFor2021BeerGuidelines()
	{
		// 26 Trappist Ale -> Monastic Ale

		for (BjcpCategory category : categories)
		{
			if (category.getId().equals("26") && category.getName().equals("Trappist Ale"))
			{
				category.setName("Monastic Ale");
				break;
			}
		}

		// 28D Straight Sour Beer

		for (BjcpCategory category : categories)
		{
			if (category.getId().equals("28") && category.getName().equals("American Wild Ale"))
			{
				subToCat.put("Straight Sour Beer", category);
				break;
			}
		}

		// PRX Provisional Style -> LSX Local Styles
		// Dorada Pampeana
		// IPA Argenta
		// Italian Grape Ale
		// Catharina Sour
		// New Zealand Pilsner

		for (BjcpCategory category : categories)
		{
			if (category.getId().equals("PRX") && category.getName().equals("Provisional Style"))
			{
				category.setId("LSX");
				category.setName("Local Styles");

				subToCat.put("Dorada Pampeana", category);
				subToCat.put("IPA Argenta", category);
				subToCat.put("Italian Grape Ale", category);
				subToCat.put("Catharina Sour", category);
				subToCat.put("New Zealand Pilsner", category);

				break;
			}
		}
	}

	/**
	 * adjustFor2025CiderGuidelines
	 */
	public void adjustFor2025CiderGuidelines()
	{
		// TODO
	}
}
