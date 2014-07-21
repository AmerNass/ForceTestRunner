package com.capgemini.Manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.sforce.soap.apex.CodeCoverageResult;
import com.sforce.soap.apex.CodeLocation;
import com.sforce.soap.apex.RunTestFailure;
import com.sforce.soap.apex.RunTestSuccess;
import com.sforce.soap.apex.RunTestsResult;

/**
 * Utility class: used to extract informations from classes, such number of failures
 * or number of success, with the there respective methods
 * @author anassere
 *
 */

public class ClassInformationExtractor {

	public static void extractAllMethods(RunTestsResult res) throws UnsupportedEncodingException, IOException {
		
		StringBuffer b = new StringBuffer("");
		
		b.append("NumFailures : "+ res.getNumFailures())
		.append("\nNumTestsRun : "+ res.getNumTestsRun()+"\n\n****** Methods ******* \n\n");
		
		
		for(RunTestFailure f : res.getFailures())
		{
			b.append(f.getName()+"\t"+f.getMethodName()+"\n");
		}
		
		b.append("\n\nSuccess : \n\n");
		
		for(RunTestSuccess s : res.getSuccesses())
		{
			b.append(s.getName()+"\t"+s.getMethodName()+"\n");
		}
		

		
		File f = new File("c:/TestClassInfo.txt");
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(b.toString().getBytes("UTF-8"));
		fos.close();
	}

}
