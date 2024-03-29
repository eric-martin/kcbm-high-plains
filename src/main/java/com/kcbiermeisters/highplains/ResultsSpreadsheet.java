/*
 * ResultsSpreadsheet.java
 */
package com.kcbiermeisters.highplains;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Optional;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kcbiermeisters.highplains.bjcp.BjcpCategory;
import com.kcbiermeisters.highplains.circuit.BrewerResults;
import com.kcbiermeisters.highplains.circuit.ClubResults;
import com.kcbiermeisters.highplains.circuit.Eligibility;
import com.kcbiermeisters.highplains.comp.Place;

import lombok.SneakyThrows;

/**
 * ResultsSpreadsheet
 * @author Eric Martin
 */
public class ResultsSpreadsheet implements Closeable
{
	private final String filePath;
	
	private final Workbook workbook;
		
	/**
	 * Constructor
	 */
	public ResultsSpreadsheet(final String directory, final String fileName)
	{
		this.filePath = directory + File.separator + fileName;
		this.workbook = new XSSFWorkbook();
	}
	
	/**
	 * createBrewerSheet
	 */
	public void createBrewerSheet(final List<BrewerResults> results, final Eligibility eligibility)
	{
		Sheet sheet = workbook.createSheet("Brewer");
        
        writeHeader(sheet, "Name", "Club", "State", "Total Points", "Gold", "Silver", "Bronze");
        
        CellStyle defaultStyle = workbook.createCellStyle();
        CellStyle ineligibleStyle = getIneligibleStyle();
        
        int rowNum = 0;
        int colNum = 0;

        for (BrewerResults result : results)
        {
        	String state = eligibility.getState(result);
        	boolean isEligible = eligibility.isEligible(state);
        	
        	Row row = sheet.createRow(++rowNum);
        	        	
        	CellStyle style = isEligible ? defaultStyle : ineligibleStyle;
        	
        	colNum = 0;
        	
        	createCell(row, colNum++, style, result.getBrewer().getName());
        	createCell(row, colNum++, style, result.getBrewer().getClub());
        	createCell(row, colNum++, style, state);
        	createCell(row, colNum++, style, result.getTotalPoints());
        	createCell(row, colNum++, style, result.getFirstCount());
        	createCell(row, colNum++, style, result.getSecondCount());
        	createCell(row, colNum++, style, result.getThirdCount());
        }
                
        autoSizeColumns(sheet, colNum);
	}
		
	/**
	 * createClubSheet
	 */
	public void createClubSheet(final List<ClubResults> results, final Eligibility eligibility)
	{
		Sheet sheet = workbook.createSheet("Club");
		
        writeHeader(sheet, "Club", "State", "Total Points", "Gold", "Silver", "Bronze");
        
        CellStyle defaultStyle = workbook.createCellStyle();
        CellStyle ineligibleStyle = getIneligibleStyle();

        int rowNum = 0;
        int colNum = 0;
        
        for (ClubResults result : results)
        {
        	String state = eligibility.getState(result);
        	boolean isEligible = eligibility.isEligible(state);

        	Row row = sheet.createRow(++rowNum);
        	
        	CellStyle style = isEligible ? defaultStyle : ineligibleStyle;
        	
        	colNum = 0;
        	
        	createCell(row, colNum++, style, result.getClub());
        	createCell(row, colNum++, style, state);
        	createCell(row, colNum++, style, result.getTotalPoints());
        	createCell(row, colNum++, style, result.getFirstCount());
        	createCell(row, colNum++, style, result.getSecondCount());
        	createCell(row, colNum++, style, result.getThirdCount());
        }
                
        autoSizeColumns(sheet, colNum);
	}
	
	/**
	 * createBrewerDetailsSheet
	 */
	public void createBrewerDetailsSheet(final List<BrewerResults> results, final List<BjcpCategory> categories)
	{
		Sheet sheet = workbook.createSheet("Brewer Details");
        
		// header
		
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(boldFont);
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        CellStyle headerRotatedStyle = workbook.createCellStyle();
        headerRotatedStyle.setFont(boldFont);
        headerRotatedStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        headerRotatedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerRotatedStyle.setRotation((short) 90);
        
    	Row header = sheet.createRow(0);
    	
    	int colNum = 0;

		Cell nameCell = header.createCell(colNum++);
		nameCell.setCellValue("Name");
		nameCell.setCellStyle(headerStyle);
		
		for (BjcpCategory category : categories)
		{
    		Cell catCell = header.createCell(colNum++);
    		catCell.setCellValue(category.getId() + ": " + category.getName());
    		catCell.setCellStyle(headerRotatedStyle);
    	}
        
        // data
        
        CellStyle style = workbook.createCellStyle();
        
        int rowNum = 0;

        for (BrewerResults result : results)
        {
        	Row row = sheet.createRow(++rowNum);
        	        	        	
        	colNum = 0;
        	
        	createCell(row, colNum++, style, result.getBrewer().getName());
        	
        	for (BjcpCategory category : categories)
    		{
        		Optional<Place> optPlace = result.getPlace(category);
        		
        		createCell(row, colNum++, style, optPlace.isPresent() ? optPlace.get().getMedal() : "");
    		}
        }
                
        autoSizeColumns(sheet, colNum);
	}
	
	/**
	 * write
	 */
	@SneakyThrows
	public void write()
	{
        FileOutputStream resultsXlsx = new FileOutputStream(filePath);
        workbook.write(resultsXlsx);        
        resultsXlsx.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	@SneakyThrows
	public void close()
	{
        workbook.close();
	}
	
	/**
	 * getIneligibleStyle
	 */
	private CellStyle getIneligibleStyle()
	{
        Font font = workbook.createFont();
		font.setStrikeout(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        return style;
	}
	
	/**
	 * writeHeader
	 */
	private void writeHeader(final Sheet sheet, final String ... columnNames)
	{
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        
        CellStyle style = workbook.createCellStyle();
        style.setFont(boldFont);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
    	Row header = sheet.createRow(0);
    	
    	int colNum = 0;
    	
    	for (String columnName : columnNames)
    	{
    		Cell cell = header.createCell(colNum++);
    		cell.setCellValue(columnName);
    		cell.setCellStyle(style);
    	}
	}
	
	/**
	 * createCell
	 */
	private void createCell(final Row row, final int column, final CellStyle style, final String value)
	{
		Cell cell = row.createCell(column);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}
	
	/**
	 * createCell
	 */
	private void createCell(final Row row, final int column, final CellStyle style, final double value)
	{
		Cell cell = row.createCell(column);
		cell.setCellStyle(style);
		cell.setCellValue(value);
	}

	/**
	 * autoSizeColumns
	 */
	private void autoSizeColumns(final Sheet sheet, final int numColumns)
	{
		for (int i = 0; i < numColumns; ++i)
		{
			sheet.autoSizeColumn(i);
		}
	}
}
