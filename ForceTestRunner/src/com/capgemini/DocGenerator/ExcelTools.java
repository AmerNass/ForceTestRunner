package com.capgemini.DocGenerator;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.biff.DisplayFormat;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import com.capgemini.Manager.Main;
import com.capgemini.Manager.TestInformationContainer;
import com.sforce.soap.apex.CodeCoverageResult;
import com.sforce.soap.apex.RunTestFailure;
import com.sforce.soap.apex.RunTestSuccess;

/**
 * Excel file generator
 * @author Amer Nassereldine
 *
 */
public class ExcelTools {

	private String inputFile;

	public enum EFormat {
		ARIALNORMAL, ARIALBOLD, ARIALITALIC, ARIALBOLDITALIC, ARIALNORMALGREEN, ARIALNORMALRED, PERCENTNUMBER_GREEN, PERCENTNUMBER_RED

	}

	private final int DEFAULT_POINT_SIZE = 11;

	/**
	 * Methode initial: elle prend les informations collectés sur les tests et
	 * les organise dans un fichier excel
	 * 
	 * @param infos
	 *            les informations collectées sur les tests
	 * @throws WriteException
	 * @throws IOException
	 */
	public static void runExcelMaker(TestInformationContainer infos)
			throws WriteException, IOException {

		String name = System.getProperty ( "os.name" ).toLowerCase();
		if(name.contains("windows")){
			ExcelTools test = new ExcelTools();
			test.setOutputFile("C:\\temp\\"+ "allTestRunResult.xls");
			test.write(infos);
			System.out.println("Please check the result file "
					+ "C:\\temp\\" + "allTestRunResult.xls");
		}
		else{

			ExcelTools test = new ExcelTools();
			test.setOutputFile("/var/tmp/"+ "allTestRunResult.xls");
			test.write(infos);
			System.out.println("Please check the result file "
					+ "/var/tmp/" + "allTestRunResult.xls");

		}

	}

	/**
	 * Choix du format de la cellule selon l'enum definit dans {@link EFormat}
	 * 
	 * @param format
	 * @return WritableCellFormat
	 * @throws WriteException
	 */
	public WritableCellFormat createFormatCellStatus(EFormat format)
			throws WriteException {

		WritableFont wfontStatus;
		switch (format) {
		case ARIALNORMAL:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
					DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE);
			break;
		case ARIALBOLD:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
					DEFAULT_POINT_SIZE, WritableFont.BOLD, false,
					UnderlineStyle.NO_UNDERLINE);
			break;
		case ARIALBOLDITALIC:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
					DEFAULT_POINT_SIZE, WritableFont.BOLD, true,
					UnderlineStyle.NO_UNDERLINE);
			break;
		case ARIALITALIC:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
					DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, true,
					UnderlineStyle.NO_UNDERLINE);
			break;
		case ARIALNORMALGREEN:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
					DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
			break;
		case ARIALNORMALRED:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
					DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE, Colour.RED);
			break;
		case PERCENTNUMBER_RED:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
				DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE, Colour.RED);
			break;
		case PERCENTNUMBER_GREEN:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
				DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
				UnderlineStyle.NO_UNDERLINE, Colour.GREEN);
			break;
		default:
			wfontStatus = new WritableFont(WritableFont.createFont("Arial"),
					DEFAULT_POINT_SIZE, WritableFont.NO_BOLD, false,
					UnderlineStyle.NO_UNDERLINE);

		}
		
		WritableCellFormat fCellstatus;
		if(format == EFormat.PERCENTNUMBER_GREEN || format == EFormat.PERCENTNUMBER_RED){
			fCellstatus = new WritableCellFormat(wfontStatus, jxl.write.NumberFormats.PERCENT_FLOAT);
		}else{
			fCellstatus = new WritableCellFormat(wfontStatus);
		}
		fCellstatus.setWrap(true);
		
		// fCellstatus.setAlignment(jxl.format.Alignment.CENTRE);
		fCellstatus.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
		fCellstatus.setBorder(jxl.format.Border.ALL,
				jxl.format.BorderLineStyle.THIN, jxl.format.Colour.BLACK);
		return fCellstatus;
	}

	public void setOutputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void write(TestInformationContainer infos) throws IOException,
	WriteException {
		File file = new File(inputFile);
		WorkbookSettings wbSettings = new WorkbookSettings();

		wbSettings.setLocale(new Locale("en", "EN"));

		WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);

		// creating and populating sheet of general informations :
		workbook.createSheet("General", 0);
		WritableSheet excelSheet = workbook.getSheet(0);
		populateSheetGeneral(excelSheet, infos);

		// creating and populating sheet of class description
		workbook.createSheet("Class Coverage ", 1);
		excelSheet = workbook.getSheet(1);
		populateSheetClassAndCoverage(excelSheet, infos);

		// creating and populating sheet of Test Method
		workbook.createSheet("Test Method ", 2);
		excelSheet = workbook.getSheet(2);
		populateSheetTestMethod(excelSheet, infos);
		
		// creating and populating sheet of Test class information
		workbook.createSheet("Test Class information ", 3);
		excelSheet = workbook.getSheet(3);
		populateSheetTestclassInformation(excelSheet, infos);

		workbook.write();
		workbook.close();
	}

	
	
	
	
	
	
	private void populateSheetTestclassInformation(WritableSheet sheet,
			TestInformationContainer infos) throws RowsExceededException, WriteException {
		//Map<String, Double> mapClassToExecutionTime = getTimeMap(infos);
		// Date of the test
		addLabel(sheet, 0, 0, "Test Class Name ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 0, "Time of execution ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		DecimalFormat formatter = new DecimalFormat("#0.000");     

		
		int i = 1;
		List<RunTestSuccess> successes = infos.getTestSuccess();
		List<RunTestFailure> failures = infos.getTestFailure();
		int sucessNumber = successes.size();
		int failureNumber = failures.size();
		
		Map<String, Double> mapClassToExecutionTime = getTimeMap(infos);
		
		for(String s : mapClassToExecutionTime.keySet()){
			addLabel(sheet, 0, i, s,
					createFormatCellStatus(EFormat.ARIALNORMAL));
			addLabel(sheet, 1, i,formatter.format( mapClassToExecutionTime.get(s)) + "  seconds",
				createFormatCellStatus(EFormat.ARIALNORMAL));
			i++;
			
		}

		CellView cv = new CellView();
		for (int x = 0; x < 10; x++) {
			cv = sheet.getColumnView(x);
			cv.setAutosize(true);
			sheet.setColumnView(x, cv);
		}

		for (int x = 0; x < mapClassToExecutionTime.keySet().size(); x++) {
			cv = sheet.getRowView(x);
			cv.setAutosize(true);
			sheet.setRowView(x, cv);
		}
		
	}
	
	
	
	
	
	
	
	

	private void populateSheetGeneral(WritableSheet sheet,
			TestInformationContainer infos) throws WriteException {

		CellView cv = new CellView();
		// cv.setFormat(arial);
		cv.setAutosize(true);

		// Date of the test
		addLabel(sheet, 0, 0, "Test Date : ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 0,
				(new SimpleDateFormat("yyyy/MM/dd  hh:mm:ss")).format(infos
						.getTestDate()),
						createFormatCellStatus(EFormat.ARIALNORMAL));

		// Number of tests
		addLabel(sheet, 0, 2, "Number of tests : ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 2, infos.getNumberOfTests() + "   test(s)",
				createFormatCellStatus(EFormat.ARIALNORMAL));

		// Number of failures
		addLabel(sheet, 0, 4, "Number of failures : ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 4,
				infos.getNumberOfFailure() + "   test(s) failed.",
				createFormatCellStatus(EFormat.ARIALNORMAL));

		// Number of failures
		addLabel(sheet, 0, 6, "TimeStamp : ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 6, infos.getTimeStamp() + " ms",
				createFormatCellStatus(EFormat.ARIALNORMAL));

		// percent of coverage
		addLabel(sheet, 0, 8, "% of Covered Classes : ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 8, infos.getCodeCoveragePercent() + "  %",
				createFormatCellStatus(EFormat.ARIALNORMAL));

		for (int x = 0; x < 10; x++) {
			cv = sheet.getColumnView(x);
			cv.setAutosize(true);
			sheet.setColumnView(x, cv);
		}

	}

	private void populateSheetClassAndCoverage(WritableSheet sheet,
			TestInformationContainer infos) throws WriteException {

		//Map<String, Double> mapClassToExecutionTime = getTimeMap(infos);
		// Date of the test
		addLabel(sheet, 0, 0, "Class Name ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 0, "Coverage % ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 2, 0, "More informations ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 3, 0, "Execution time ",
				createFormatCellStatus(EFormat.ARIALBOLD));

		List<CodeCoverageResult> toDisplay = infos.getCodeCoverages();
		for (int i = 1; i < toDisplay.size(); i++) {
			CodeCoverageResult tmp = toDisplay.get(i - 1);

			addLabel(sheet, 0, i, tmp.getName(),
					createFormatCellStatus(EFormat.ARIALNORMAL));
			
			double div = (1.0 - ((double) tmp.getNumLocationsNotCovered() / (double) tmp
					.getNumLocations()));// * 100.0;
			
			if (div > .75) {

				//addLabel(sheet, 1, i, new Float(div) + " %",
					//	createFormatCellStatus(EFormat.ARIALNORMALGREEN));
				addNumber(sheet, 1, i, new Float(div), createFormatCellStatus(EFormat.PERCENTNUMBER_GREEN));

				// cv.setFormat(arial);

				addLabel(
						sheet,
						2,
						i,
						"Code coverage for "
								+ tmp.getType()
								+ (tmp.getNamespace() == null ? "" : tmp
										.getNamespace() + ".") + tmp.getName()
										+ ": " + tmp.getNumLocationsNotCovered()
										+ " locations not covered out of "
										+ tmp.getNumLocations(),
										createFormatCellStatus(EFormat.ARIALNORMALGREEN));
			} else {
				//addLabel(sheet, 1, i, new Float(div) + " %",
					//	createFormatCellStatus(EFormat.ARIALNORMALRED));
				addNumber(sheet, 1, i, new Float(div), createFormatCellStatus(EFormat.PERCENTNUMBER_RED));

				// cv.setFormat(arial);

				addLabel(
						sheet,
						2,
						i,
						"Code coverage for "
								+ tmp.getType()
								+ (tmp.getNamespace() == null ? "" : tmp
										.getNamespace() + ".") + tmp.getName()
										+ ": " + tmp.getNumLocationsNotCovered()
										+ " locations not covered out of "
										+ tmp.getNumLocations(),
										createFormatCellStatus(EFormat.ARIALNORMALRED));
			}
			
			//addLabel(sheet, 3, i, mapClassToExecutionTime.get(tmp.getName()).toString() + "  seconds",
				//	createFormatCellStatus(EFormat.ARIALNORMAL));
		}

		CellView cv = new CellView();
		for (int x = 0; x < 10; x++) {
			cv = sheet.getColumnView(x);
			cv.setAutosize(true);
			sheet.setColumnView(x, cv);
		}

		for (int x = 0; x < toDisplay.size(); x++) {
			cv = sheet.getRowView(x);
			cv.setAutosize(true);
			sheet.setRowView(x, cv);
		}

	}

	private Map<String, Double> getTimeMap(TestInformationContainer infos) {
		Map<String, Double> result = new HashMap<String, Double>();
		
		List<RunTestSuccess> sucess = infos.getTestSuccess();
		List<RunTestFailure> failures = infos.getTestFailure();
		
		for(RunTestSuccess ts : sucess){
			if(! result.containsKey(ts.getName())){
				result.put(ts.getName() , 0.);
			} else{
				double dd = (result.get(ts.getName()) + ts.getTime())/1000;
				result.put(ts.getName(), dd);
			}
		}
		
		for(RunTestFailure ts : failures){
			if(! result.containsKey(ts.getName())){
				result.put(ts.getName() , 0.);
			} else{
				result.put(ts.getName(), (result.get(ts.getName()) + ts.getTime())/1000);
			}
		}
		
		for(String s : result.keySet()){
			System.out.println(s  + " *** " + result.get(s));
		}
		
		return result;
	}

	private void populateSheetTestMethod(WritableSheet sheet,
			TestInformationContainer infos) throws WriteException {

		// //List<CoverageAndFailureContainer> toDisplay =
		// ForceTestRunnerTools.computeCoverageAndFailure(infos);
		// WritableFont arial11ptblack = new WritableFont(WritableFont.ARIAL,
		// 11, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE);
		// arial = new WritableCellFormat(arial11ptblack);
		// arial.setWrap(true);
		//
		//
		// // Arial font + size = 11
		// WritableFont arial11pt = new WritableFont(WritableFont.ARIAL, 11,
		// WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
		// Colour.GREEN);
		// arialGreen = new WritableCellFormat(arial11pt);
		// arialGreen.setWrap(true);
		//
		// // Arial font + size = 11
		// WritableFont arial11ptRed = new WritableFont(WritableFont.ARIAL, 11,
		// WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
		// Colour.RED);
		// arialRed = new WritableCellFormat(arial11ptRed);
		// arialRed.setWrap(true);
		//
		// // create create a bold font with unterlines
		// WritableFont arial11ptBoldUnderline = new
		// WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false,
		// UnderlineStyle.NO_UNDERLINE);
		// arialBoldUnderline = new WritableCellFormat(arial11ptBoldUnderline);
		// // Lets automatically wrap the cells
		// arialBoldUnderline.setWrap(true);
		//
		// CellView cv = new CellView();
		// //cv.setFormat(arial);
		// cv.setAutosize(true);

		// Date of the test
		addLabel(sheet, 0, 0, "Class Name ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 1, 0, "Method Name ",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 2, 0, "TimeStamp",
				createFormatCellStatus(EFormat.ARIALBOLD));
		addLabel(sheet, 3, 0, "More informations ",
				createFormatCellStatus(EFormat.ARIALBOLD));

		List<RunTestFailure> toDisplay = infos.getTestFailure();
		for (int i = 1; (i - 1) < toDisplay.size(); i++) {
			RunTestFailure tmp = toDisplay.get(i - 1);

			addLabel(sheet, 0, i, tmp.getName(),
					createFormatCellStatus(EFormat.ARIALNORMAL));
			addLabel(sheet, 1, i, tmp.getMethodName(),
					createFormatCellStatus(EFormat.ARIALNORMAL));
			addLabel(sheet, 2, i, tmp.getTime() + " ms",
					createFormatCellStatus(EFormat.ARIALNORMAL));
			addLabel(sheet, 3, i,
					tmp.getMessage() + " : " + tmp.getStackTrace(),
					createFormatCellStatus(EFormat.ARIALNORMALRED));
		}

		CellView cv = new CellView();
		for (int x = 0; x < 10; x++) {
			cv = sheet.getColumnView(x);
			cv.setAutosize(true);
			sheet.setColumnView(x, cv);
		}

		for (int x = 0; x < toDisplay.size(); x++) {
			cv = sheet.getRowView(x);
			cv.setAutosize(true);
			sheet.setRowView(x, cv);
		}

	}

	private void addNumber(WritableSheet sheet, int column, int row,
			Float integer, WritableCellFormat format) throws WriteException,
			RowsExceededException {
		Number number;
		
		number = new Number(column, row, integer, format);
		sheet.addCell(number);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s,
			WritableCellFormat format) throws WriteException,
			RowsExceededException {
		Label label;
		label = new Label(column, row, s, format);
		sheet.addCell(label);
	}

}
