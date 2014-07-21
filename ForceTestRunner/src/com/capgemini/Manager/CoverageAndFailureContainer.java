package com.capgemini.Manager;
/**
 * Utility class : used to wrap all informations about coverage and failures
 * @author Amer Nassereldine
 *
 */
public class CoverageAndFailureContainer {
	private String className ;
	private boolean failed ;
	private double percentOfCoverage;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public boolean isFailed() {
		return failed;
	}
	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	public double getPercentOfCoverage() {
		return percentOfCoverage;
	}
	public void setPercentOfCoverage(double result) {
		this.percentOfCoverage = result;
	}
	
	
}
