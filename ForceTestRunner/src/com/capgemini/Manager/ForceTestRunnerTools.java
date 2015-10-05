package com.capgemini.Manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.sforce.soap.apex.CodeCoverageResult;
import com.sforce.soap.apex.RunTestFailure;
import com.sforce.soap.apex.RunTestSuccess;
import com.sforce.soap.apex.RunTestsRequest;
import com.sforce.soap.apex.RunTestsResult;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.GetUserInfoResult;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.ApexClass;
import com.sforce.soap.enterprise.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

/**
 * ForceTestRunnerTools repr�sente le coeur de l'application.
 * Dans cette classe la plupart des traitement sont faits:
 * -Connexion et deconnection au compte salesForce
 * -R�cuperation des classes de tests
 * -Execution des classes de tests
 * -Parser et exploit� les donn�es recu pour collecter les informations sur la couverture des tests ou 
 * leurs �checs
 * @author Amer Nasser el dine 
 * @version 1.0
 * 
 */
public class ForceTestRunnerTools {

	private static BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in));

	private EnterpriseConnection connection;

	private SoapConnection connector;

	private String authEndPoint = "";

	private ConnectorConfig config;

	private long timeStamp;

	private String userName;
	private String password;

	public ConnectorConfig getConfig() {
		return config;
	}

	public void setConfig(ConnectorConfig config) {
		this.config = config;
	}

	public static BufferedReader getReader() {
		return reader;
	}

	public static void setReader(BufferedReader reader) {
		ForceTestRunnerTools.reader = reader;
	}

	public EnterpriseConnection getConnection() {
		return connection;
	}

	public void setConnection(EnterpriseConnection connection) {
		this.connection = connection;
	}

	public SoapConnection getConnector() {
		return connector;
	}

	public void setConnector(SoapConnection connector) {
		this.connector = connector;
	}

	public String getAuthEndPoint() {
		return authEndPoint;
	}

	public void setAuthEndPoint(String authEndPoint) {
		this.authEndPoint = authEndPoint;
	}

	// Constructor
	public ForceTestRunnerTools(String userName, String password, String token, String authEndPoint) {
		this.authEndPoint = authEndPoint;
		this.userName = userName;
		this.password = password+token;

		System.out.println(">>>>>>>> user = \""+ userName +"\"");
	}

	//Utilitaire pour la saisie au clavier : pas besoin pour la version autonome (avec le bat)
	public String getUserInput(String prompt) {
		String result = "";
		try {
			System.out.print(prompt);
			result = reader.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return result;
	}


	/**
	 * Fonction d'authentification
	 * @return boolean indiquant si l'authentification a reussi ou pas.
	 */
	public boolean login() {
		boolean success = false;
		//		String username = getUserInput("Enter username: ");
		//		String password = getUserInput("Enter password: ");

		try {
			config = new ConnectorConfig();
			config.setUsername(userName);
			config.setPassword(password);

			System.out.println("AuthEndPoint: " + authEndPoint);
			config.setAuthEndpoint(authEndPoint);

			connection = new EnterpriseConnection(config);

			connector = new SoapConnection(config);

			printUserInfo(config);

			success = true;
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		} 

		return success;
	}

	public void printUserInfo(ConnectorConfig config) {
		try {
			GetUserInfoResult userInfo = connection.getUserInfo();

			System.out.println("\nLogging in ...\n");
			System.out.println("UserID: " + userInfo.getUserId());
			System.out.println("User Full Name: " + userInfo.getUserFullName());
			System.out.println("User Email: " + userInfo.getUserEmail());
			System.out.println();
			System.out.println("SessionID: " + config.getSessionId());
			System.out.println("Auth End Point: " + config.getAuthEndpoint());
			System.out
			.println("Service End Point: " + config.getServiceEndpoint());
			System.out.println();
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}

	/**
	 * mÃ©thode de dÃ©connection
	 */
	public void logout() {
		try {
			connection.logout();
			System.out.println("Logged out.");
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}



	/**
	 * Methode utile pour retrouver toutes les classes de test
	 * @return une Liste d'ApexClass representant la totalitÃ© des classes de tests avec les informations
	 * suivantes : nom de la classe, corps de la classe et le nom de l'utilisateur qui l'a crÃ©e
	 * @throws ConnectionException 
	 */
	public List<ApexClass> findTestMethod() throws ConnectionException {
		//config.setServiceEndpoint("https://cs7.salesforce.com/services/Soap/c/30.0");
		config.setServiceEndpoint(authEndPoint.replace("/s/", "/c/"));
		List<ApexClass> result = new ArrayList<ApexClass>();
		String soqlQuery = "SELECT Name, Body, IsValid,CreatedBy.Name FROM ApexClass";
		try {
			QueryResult qr = connection.query(soqlQuery);
			boolean done = false;
			//<<<<<<<<< a supprimer
			int counter = 5;
			//<<<<<<<<<<

			if (qr.getSize() > 0) {
				System.out.println("\nLogged-in user can see "
						+ qr.getRecords().length + " contact records.");

				int first = 0;

				while (!done) {
					if(first == 0){
						System.out.println("First Query ...");
						first++;
					} else if (first == 1){
						System.out.println("... and querying more");
					}else{
						System.out.println("and more ...");
					}
					SObject[] records = qr.getRecords();
					for (int i = 0; i < records.length; ++i) {
						ApexClass con = (ApexClass) records[i];

						//System.out.println(con.getName()+" : "+ con.getIsValid());
						if( /*(con.getName().contains("WS004_CallinSFDC_CreateAccount_TEST")) &&*/ (!con.getName().contains("MNE_TEST")) && con.getBody() != null && con.getBody().length() > 10 && !con.getBody().contains("(hidden)") && con.getBody().toLowerCase().contains("@istest"))
						{
							result.add(con);
						}
					}

					if (qr.isDone()) {
						done = true;
					} else {
						qr = connection.queryMore(qr.getQueryLocator());
					}
				}
			} else {
				System.out.println("No records found.");
			}
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
		config.setServiceEndpoint(authEndPoint.replace("/c/", "/s/"));
		return result;
	}


	public List<ApexClass> findAllClass() throws ConnectionException {
		config.setServiceEndpoint(authEndPoint.replace("/s/", "/c/"));
		List<ApexClass> result = new ArrayList<ApexClass>();
		String soqlQuery = "SELECT Name, Body, IsValid,CreatedBy.Name FROM ApexClass";
		try {
			QueryResult qr = connection.query(soqlQuery); 
			boolean done = false;

			if (qr.getSize() > 0) {
				System.out.println("\nLogged-in user can see "
						+ qr.getRecords().length + " contact records.");

				while (!done) {
					System.out.println("");
					SObject[] records = qr.getRecords();
					for (int i = 0; i < records.length; ++i) {
						ApexClass con = (ApexClass) records[i];


						if(con.getName().equals("EM014_DocPli_TEST")
								|| con.getName().contains("EM015_DocDonnee_TEST")
								|| con.getName().contains("EM016_DictionDonne_TEST")
								|| con.getName().contains("EM017_Colonnes_TEST")
								|| con.getName().contains("EM031_Document_TEST")
								|| con.getName().contains("EM040_Pli_TEST")) 
							result.add(con);

					}

					if (qr.isDone()) {
						done = true;
					} else {
						qr = connection.queryMore(qr.getQueryLocator());
					}
				}
			} else {
				System.out.println("No records found.");
			}
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
		config.setServiceEndpoint(authEndPoint.replace("/c/", "/s/"));
		return result;
	}


	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<

	/**
	 * Methide qui nous permet de prendre les nb premiere classe de test tri� par
	 * ordre croissant
	 * @param testClass
	 * @param nb le nombre de classe de teste qu'on souhaite extraire
	 * @return un taleau contenant le nom des classes
	 */
	@SuppressWarnings("unused")
	private String[] getTestClassName(List<ApexClass> testClass, int nb)
	{
		if(testClass.size() <= nb)
		{
			return getTestClassName2(testClass);
		}
		List<String> result = new ArrayList<String>();

		for(ApexClass ap : testClass)
		{
			result.add(ap.getName());
		}

		java.util.Collections.sort(result);

		List<String> FirstSorted = new ArrayList<String>();

		for(int i = 0; i< nb;  i++)
		{
			FirstSorted.add(result.get(i));
		}

		String[] array = FirstSorted.toArray(new String[FirstSorted.size()]);

		return array;
	}


	public RunTestsResult runAllTests(int nb) throws ConnectionException
	{
		//switch the service endpoint to the Apex service
		config.setServiceEndpoint(authEndPoint.replace("/c/", "/s/"));

		//Build another connection with the same config
		//which importantly contains a session ID.
		connector = new SoapConnection(config);


		RunTestsRequest request = new RunTestsRequest();
		//request.setAllTests(true);

		/* alternative a setAllTest(true); */
		request.setClasses(getTestClassName(findTestMethod()));



		/* à utiliser quand on a besoin de tester les 5 (par exemple) premier classe de tests */
		//request.setClasses(new String[] {"AP020_Case_TEST"});

		//compute time
		long start = System.currentTimeMillis();

		//CompileAndTestResult cResult = connector.compileAndTest(compileAndTest);
		System.out.println(">> Arrived to the BreakUp Point ! Sending Request !\nwaiting for response ...");

		//execution de toutes les classes
		RunTestsResult r = connector.runTests(request);
		System.out.println(">> Quit to the BreakUp Point !\nResult OK ..");

		System.out.println("*************** TEST Finished");
		//finish time computing
		timeStamp = (System.currentTimeMillis() - start);

		//return cResult.getRunTestsResult();
		return r;

	}

	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<


	@SuppressWarnings("unused")
	private String[] getTestClassName(List<ApexClass> testClass)
	{
		List<String> result = new ArrayList<String>();

		for(ApexClass ap : testClass)
		{
			System.out.println(ap.getName());
			result.add(ap.getName());
		}

		String[] array = result.toArray(new String[result.size()]);

		return array;
	}


	/**
	 * Cette classe se chargera d'executer la totalitÃ© des classes de tests
	 * @return Le rÃ©sultat de l'execution
	 * @throws ConnectionException
	 */

	public RunTestsResult runAllTests() throws ConnectionException
	{
		//switch the service endpoint to the Apex service
		config.setServiceEndpoint(authEndPoint.replace("/c/", "/s/"));

		//Build another connection with the same config
		//which importantly contains a session ID.
		connector = new SoapConnection(config);


		RunTestsRequest request = new RunTestsRequest();
		//request.setAllTests(true);

		/* alternative a setAllTest(true); */
		request.setClasses(getTestClassName(findTestMethod()));



		/* à utiliser quand on a besoin de tester les 5 (par exemple) premier classe de tests */
		//request.setClasses(new String[] {"DBRessourcesCauseV2_TEST"});

		//compute time
		long start = System.currentTimeMillis();

		//CompileAndTestResult cResult = connector.compileAndTest(compileAndTest);
		System.out.println(">> Arrived to the BreakUp Point ! Sending Request !\nwaiting for response ...");

		//execution de toutes les classes
		RunTestsResult r = connector.runTests(request);
		System.out.println(">> Quit to the BreakUp Point !\nResult OK ..");

		System.out.println("*************** TEST Finished");
		//finish time computing
		timeStamp = (System.currentTimeMillis() - start);

		//return cResult.getRunTestsResult();
		return r;

	}

	/**
	 * Cette classe est utile Ã  l'organisation des informations recu apres l'execution des tests
	 * @param result le rÃ©sultat de l'execution des test
	 * @return un conteneur dans lequel toutes les donnÃ©es recu apres l'execution des tests sont organisÃ©
	 */
	public TestInformationContainer parseResult(RunTestsResult result)
	{
		TestInformationContainer infos = new TestInformationContainer();
		infos.setTimeStamp(timeStamp);
		infos.setNumberOfFailure(result.getNumFailures());
		infos.setNumberOfTests(result.getNumTestsRun());


		int notCovered = 0;
		int covered = 0;

		List<RunTestFailure> listOfFail = new ArrayList<RunTestFailure>();
		for (RunTestFailure rtf : result.getFailures()) {
			listOfFail.add(rtf);
		}
		infos.setTestFailure(listOfFail);

		List<RunTestSuccess> listOfSuccess = new ArrayList<RunTestSuccess>();
		for (RunTestSuccess rtf : result.getSuccesses()) {
			listOfSuccess.add(rtf);
		}
		infos.setTestSuccess(listOfSuccess);

		List<CodeCoverageResult> listOfCoverage = new ArrayList<CodeCoverageResult>();
		for (CodeCoverageResult ccr : result.getCodeCoverage()) {
			if(ccr.getNamespace() == null || ccr.getNamespace().isEmpty()){
				if(ccr.getNumLocationsNotCovered() == 0)
				{
					covered ++;
				}
				else 
				{
					notCovered ++;
				}
				listOfCoverage.add(ccr);
			}
		}
		infos.setCodeCoverages(listOfCoverage);


		infos.setCoveredClassNumber(covered);
		infos.setNotCoveredClassNumber(notCovered);

		infos.setTestDate(Calendar.getInstance().getTime());

		System.out.println(covered +" / " + notCovered);
		double sum = covered + notCovered;
		double divide = covered / sum ;
		infos.setCodeCoveragePercent(divide * 100.0);

		return infos;
	}


	/**
	 * Une fonction qui interprete le rÃ©sultat des tests et collecte les information sur la couverture et les
	 * Ã©checs des tests
	 * @param infos les informations sur l'execution des classes de tests
	 * @return une liste d'information contenant la couverture et les Ã©checs des classes Apex
	 */
	public static List<CoverageAndFailureContainer> computeCoverageAndFailure(TestInformationContainer infos)
	{
		List<CoverageAndFailureContainer> res = new ArrayList<CoverageAndFailureContainer>();


		for(RunTestSuccess rtf : infos.getTestSuccess())
		{
			CoverageAndFailureContainer tmp = new CoverageAndFailureContainer();
			tmp.setClassName(rtf.getName());
			tmp.setFailed(false);
			for(CodeCoverageResult cvr : infos.getCodeCoverages())
			{
				if(cvr.getName().equals(rtf.getName()))
				{
					double divide = cvr.getNumLocationsNotCovered() / cvr.getNumLocations();
					double result = (1 - divide) * 100.00;
					tmp.setPercentOfCoverage(result);
				}
			}

			res.add(tmp);
		}


		for(RunTestFailure rtf : infos.getTestFailure())
		{
			CoverageAndFailureContainer tmp = new CoverageAndFailureContainer();
			tmp.setClassName(rtf.getName());
			tmp.setFailed(true);
			for(CodeCoverageResult cvr : infos.getCodeCoverages())
			{
				if(cvr.getName().equals(rtf.getName()))
				{
					double divide = cvr.getNumLocationsNotCovered() / cvr.getNumLocations();
					double result = (1 - divide) * 100.00;
					tmp.setPercentOfCoverage(result);
				}
			}

			res.add(tmp);
		}

		return res;
	}





	//////////////////////////////////// A effacer ////////////////////////////////////////////:

	@SuppressWarnings("unused")
	private String[] getTestClassName2(List<ApexClass> testClass)
	{
		List<String> result = new ArrayList<String>();

		for(ApexClass ap : testClass)
		{
			result.add(ap.getName());
		}

		java.util.Collections.sort(result);

		List<String> FirstSorted = new ArrayList<String>();

		boolean filtered = false;

		for(int i = 0; i< result.size();  i++)
		{
			if(result.get(i).contains("VFC070"))
				filtered=true;

			if(filtered)
				FirstSorted.add(result.get(i));
		}

		String[] array = FirstSorted.toArray(new String[FirstSorted.size()]);

		return array;
	}

	//////////////////////////////////// A effacer ////////////////////////////////////////////:

}







