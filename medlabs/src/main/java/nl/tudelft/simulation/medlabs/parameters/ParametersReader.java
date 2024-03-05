package nl.tudelft.simulation.medlabs.parameters;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.djutils.logger.CategoryLogger;

import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterDouble;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterException;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterMap;
import nl.tudelft.simulation.dsol.model.inputparameters.InputParameterString;
import nl.tudelft.simulation.medlabs.excel.ExcelUtil;

/**
 * Read a number of default parameters from an Excel file and put them in an
 * InputParameterMap.
 * <p>
 * Copyright (c) 2014-2024 Delft University of Technology, Jaffalaan 5, 2628 BX
 * Delft, the Netherlands. All rights reserved. The MEDLABS project (Modeling
 * Epidemic Disease with Large-scale Agent-Based Simulation) is aimed at
 * providing policy analysis tools to predict and help contain the spread of
 * epidemics. It makes use of the DSOL simulation engine and the agent-based
 * modeling formalism. See for project information
 * <a href="http://www.simulation.tudelft.nl/"> www.simulation.tudelft.nl</a>.
 * The original MEDLABS Java library was developed as part of the PhD research
 * of Mingxin Zhang at TU Delft and is described in the PhD thesis "Large-Scale
 * Agent-Based Social Simulation" (2016). This software is licensed under the
 * BSD license. See license.txt in the main project.
 * </p>
 * 
 * @author Mingxin Zhang
 * @author <a href="https://www.tudelft.nl/averbraeck">Alexander Verbraeck</a>
 */
public class ParametersReader {
	/** Utility class. */
	private ParametersReader() {
		// utility class
	}

	/**
	 * Read a number of default parameters from an Excel file and put them in a map.
	 * 
	 * @param fis InputStream; the input stream for the Excel file
	 * @param map InputParameterMap; the map to store the parameters
	 * @throws InputParameterException on read error
	 */
	public static void readParametersXLS(final InputStream fis, final InputParameterMap map)
			throws InputParameterException {
		try {
			XSSFWorkbook wbST = new XSSFWorkbook(fis);
			XSSFSheet sheet = wbST.getSheet("Parameters");
			for (Row row : sheet) {
				if (row.getRowNum() > 0) {
					String code = ExcelUtil.cellValue(row, "A");
					String shortName = ExcelUtil.cellValue(row, "B");
					String description = ExcelUtil.cellValue(row, "C");
					Cell cell = row.getCell(3); // column "D"
					if (cell == null) {
						continue;
					}

					if (cell.getCellType() == CellType.STRING) {
						map.add(new InputParameterString(code, shortName, description,
								cell.getRichStringCellValue().getString(), row.getRowNum()));
					} else if (cell.getCellType() == CellType.NUMERIC) {
						map.add(new InputParameterDouble(code, shortName, description, cell.getNumericCellValue(),
								row.getRowNum()));
					} else if (cell.getCellType() == CellType.FORMULA) {
						if (cell.getCachedFormulaResultType() == CellType.STRING) {
							map.add(new InputParameterString(code, shortName, description,
									cell.getRichStringCellValue().getString(), row.getRowNum()));
						} else if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
							map.add(new InputParameterDouble(code, shortName, description, cell.getNumericCellValue(),
									row.getRowNum()));
						}
					}
				}
			}
			System.err.println("read parameters from " + fis.toString());

			fis.close();
		} catch (IOException exception) {
			CategoryLogger.always().warn(exception);
		}
	}

}
