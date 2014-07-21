package com.capgemini.Manager;

import java.io.File;
import java.io.IOException;

import jxl.write.WriteException;

import com.capgemini.DocGenerator.ExcelTools;
import com.capgemini.PropertiesTools.PropertiesManager;
import com.capgemini.mailByProtocols.SendMailTLS;
import com.sforce.soap.apex.RunTestsResult;
import com.sforce.ws.ConnectionException;

public class Main {

	public static void main(String[] args) throws WriteException, IOException, ConnectionException {

		try{
			String tester = args[0];
		} catch(Exception e){
			System.out.println("Error : ");
			System.out.println("-------");
			System.out.println("usage : java -jar ForceTestRunner.jar pathToConfigFile");
			return ;
		}

		File f = new File(args[0]);
		System.out.println(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		if(!f.exists() || f.isDirectory())
		{
			f.createNewFile();
			System.out.println("Le fichier < "+args[0]+" >n'était pas crée ..");
			System.out.println("Création de ce fichier pour vous ...  OK");
			System.out.println("Veuillez renseigner les informations dans le fichier :");
			System.out.println(args[0]);
			System.out.println("Finish !");

			return ;
		}


		PropertiesManager p = new PropertiesManager(args[0]);

		ForceTestRunnerTools runner = new ForceTestRunnerTools(p.getUsernameValue(), p.getPasswordValue(),p.getTokenValue(), p.getAuthEndPointValue());

		if(runner.login())
		{
			System.out.println("START :");
			RunTestsResult res = runner.runAllTests();
			ExcelTools.runExcelMaker(runner.parseResult(res));

			ClassInformationExtractor.extractAllMethods(res);

		}
		String name = System.getProperty ( "os.name" ).toLowerCase();
		if(name.contains("windows")){
			f = new File("C:\\temp\\"+"allTestRunResult.xls");
		}
		else{
			f = new File("/var/tmp/"+"allTestRunResult.xls");

		}
		if(!f.exists() || f.isDirectory())
		{
			return ;
		}

		for(String mail : p.getMailsListValue())
		{
			if(name.contains("windows")){
				System.out.println("Sending mail to : "+mail);
				SendMailTLS.sendExcelFile("C:\\temp\\"+"allTestRunResult.xls", mail);
			}
			else{
				System.out.println("Sending mail to : "+mail);
				SendMailTLS.sendExcelFile("/var/tmp/"+"allTestRunResult.xls", mail);
			}
		}
	}
}

