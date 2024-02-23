package nl.tudelft.simulation.medlabs.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

/**
 * ExcelUtil contains base utilities to read and write Excel files. The methods use the Apache POI library.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. The
 * MEDLABS project (Modeling Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at providing policy analysis
 * tools to predict and help contain the spread of epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research of Mingxin Zhang at TU Delft and is described in
 * the PhD thesis "Large-Scale Agent-Based Social Simulation" (2016). This software is licensed under the BSD license. See
 * license.txt in the main project.
 * </p>
 * @author Mingxin Zhang
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public final class ExcelUtil
{
    /** Utility class. */
    private ExcelUtil()
    {
        // Utility class
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param column as String (A, B, .., AA, AB, ..)
     * @return string value
     */
    public static String cellValue(final Row row, final String column)
    {
        int colnr = column.charAt(column.length() - 1) - 65;
        if (column.length() > 1)
        {
            colnr += 26 * (column.charAt(column.length() - 2) - 64);
        }
        return ExcelUtil.cellValue(row, colnr);
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param colnr column number(0-based)
     * @return string value
     */
    public static String cellValue(final Row row, final int colnr)
    {
        if (row == null)
        {
            return "";
        }
        Cell cell = row.getCell(colnr);
        if (cell == null)
        {
            return "";
        }
        switch (cell.getCellType())
        {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                {
                    return cell.getDateCellValue().toString();
                }
                else
                {
                    return "" + cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue() ? "1" : "0";
            case FORMULA:
                switch (cell.getCachedFormulaResultType())
                {
                    case STRING:
                        return cell.getRichStringCellValue().getString();
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell))
                        {
                            return cell.getDateCellValue().toString();
                        }
                        else
                        {
                            return "" + cell.getNumericCellValue();
                        }
                    case BOOLEAN:
                        return cell.getBooleanCellValue() ? "1" : "0";
                    default:
                        return "";
                }
            default:
                return "";
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param column as String (A, B, .., AA, AB, ..)
     * @return string value
     * @throws Exception
     */
    public static double cellValueDouble(final Row row, final String column) throws Exception
    {
        String s = ExcelUtil.cellValue(row, column);
        try
        {
            return Double.parseDouble(s);
        }
        catch (Exception cause)
        {
            throw new Exception("Error parsing Excel double value " + s + " in cell " + column + row.getRowNum(), cause);
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param column as String (A, B, .., AA, AB, ..)
     * @return string value
     */
    public static double cellValueDoubleNull(final Row row, final String column)
    {
        String s = ExcelUtil.cellValue(row, column);
        try
        {
            return Double.parseDouble(s);
        }
        catch (Exception cause)
        {
            return 0.0d;
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param column as String (A, B, .., AA, AB, ..)
     * @return string value
     */
    public static double cellValueDoubleMinOne(final Row row, final String column)
    {
        String s = ExcelUtil.cellValue(row, column);
        try
        {
            return Double.parseDouble(s);
        }
        catch (Exception cause)
        {
            return -1.0d;
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param colnr column number(0-based)
     * @return string value
     */
    public static double cellValueDoubleNull(final Row row, final int colnr)
    {
        String s = ExcelUtil.cellValue(row, colnr);
        try
        {
            return Double.parseDouble(s);
        }
        catch (Exception cause)
        {
            return 0.0d;
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param colnr column number(0-based)
     * @return string value
     */
    public static double cellValueDoubleMinOne(final Row row, final int colnr)
    {
        String s = ExcelUtil.cellValue(row, colnr);
        try
        {
            return Double.parseDouble(s);
        }
        catch (Exception cause)
        {
            return -1.0d;
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param column as String (A, B, .., AA, AB, ..)
     * @return string value
     * @throws Exception;
     */
    public static int cellValueInt(final Row row, final String column) throws Exception
    {
        String s = ExcelUtil.cellValue(row, column);
        try
        {
            double d = Double.parseDouble(s);
            return (int) Math.rint(d);
        }
        catch (Exception cause)
        {
            throw new Exception("Error parsing Excel integer value " + s + " in cell " + column + row.getRowNum(), cause);
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param column as String (A, B, .., AA, AB, ..)
     * @return int value
     */
    public static int cellValueIntNull(final Row row, final String column)
    {
        String s = ExcelUtil.cellValue(row, column);
        try
        {
            double d = Double.parseDouble(s);
            return (int) Math.rint(d);
        }
        catch (Exception cause)
        {
            return 0;
        }
    }

    /**
     * Value from Excel cell.
     * @param row as HSSF row
     * @param colnr column number(0-based)
     * @return int value
     */
    public static int cellValueIntNull(final Row row, final int colnr)
    {
        String s = ExcelUtil.cellValue(row, colnr);
        try
        {
            double d = Double.parseDouble(s);
            return (int) Math.rint(d);
        }
        catch (Exception cause)
        {
            return 0;
        }
    }
}
