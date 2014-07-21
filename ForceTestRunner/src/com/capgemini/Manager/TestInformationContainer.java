package com.capgemini.Manager;

import java.util.Date;
import java.util.List;

import com.sforce.soap.apex.CodeCoverageResult;
import com.sforce.soap.apex.RunTestFailure;
import com.sforce.soap.apex.RunTestSuccess;

/**
 * Utility class to gather information about a given test :
 * we can find this informations :
 * - test date
 * - Number of failure
 * - time of execution
 * - code coverage percent
 * - list of not covered class
 * 
 * @author Amer Nasser el dine
 *
 */
public class TestInformationContainer {
	
	private Date testDate;
	private int numberOfTests;
	private int numberOfFailure;
	private long timeStamp;
	private double codeCoveragePercent;
	private int CoveredClassNumber;
	private int notCoveredClassNumber;
	
	private List<RunTestFailure> testFailure ;
	private List<CodeCoverageResult> codeCoverages;
	private List<RunTestSuccess> testSuccess;
	
	
	public List<RunTestSuccess> getTestSuccess() {
		return testSuccess;
	}
	public void setTestSuccess(List<RunTestSuccess> testSuccess) {
		this.testSuccess = testSuccess;
	}
	public Date getTestDate() {
		return testDate;
	}
	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}
	public int getNumberOfTests() {
		return numberOfTests;
	}
	public void setNumberOfTests(int numberOfTests) {
		this.numberOfTests = numberOfTests;
	}
	public int getNumberOfFailure() {
		return numberOfFailure;
	}
	public void setNumberOfFailure(int numberOfFailure) {
		this.numberOfFailure = numberOfFailure;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public double getCodeCoveragePercent() {
		return codeCoveragePercent;
	}
	public void setCodeCoveragePercent(double d) {
		this.codeCoveragePercent = d;
	}
	public int getCoveredClassNumber() {
		return CoveredClassNumber;
	}
	public void setCoveredClassNumber(int coveredClassNumber) {
		CoveredClassNumber = coveredClassNumber;
	}
	public int getNotCoveredClassNumber() {
		return notCoveredClassNumber;
	}
	public void setNotCoveredClassNumber(int notCoveredClassNumber) {
		this.notCoveredClassNumber = notCoveredClassNumber;
	}
	public List<RunTestFailure> getTestFailure() {
		return testFailure;
	}
	public void setTestFailure(List<RunTestFailure> testFailure) {
		this.testFailure = testFailure;
	}
	public List<CodeCoverageResult> getCodeCoverages() {
		return codeCoverages;
	}
	public void setCodeCoverages(List<CodeCoverageResult> codeCoverages) {
		this.codeCoverages = codeCoverages;
	}
	
}
