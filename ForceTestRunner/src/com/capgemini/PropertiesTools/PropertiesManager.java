package com.capgemini.PropertiesTools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * config file reader
 * @author anassere
 *
 */
public class PropertiesManager {
	private String fileName;
	
	private final String username = "USERNAME";
	private final String password = "PASSWORD";
	private final String token = "TOKEN";
	private final String authEndPoint = "ENDPOINT";
	private final String mailsList = "MAILSLIST";
	
	// pas besoin dans cette version, a activé si on souhaite crypté le mot de passe
	private final BasicTextEncryptor encryptor = new BasicTextEncryptor();
	
	
	private String usernameValue;
	private String passwordValue;
	private String tokenValue;
	private String authEndPointValue;
	private List<String> mailsListValue;
	
	public PropertiesManager(String fileName) throws IOException
	{
		this.fileName = fileName;
		
        Properties prop = new Properties();
 
        FileInputStream inputStream = new FileInputStream(fileName);
        prop.load(inputStream);
  
        // get the property value and print it out
        usernameValue = prop.getProperty(username);
        passwordValue = prop.getProperty(password);
        tokenValue = prop.getProperty(token);
        authEndPointValue = prop.getProperty(authEndPoint);
        
        mailsListValue = new ArrayList<String>();
        
        String tmp = prop.getProperty(mailsList);
        tmp = tmp.trim();
        
        for(String s : tmp.split(";"))
        {
        	mailsListValue.add(s);
        }

	}
	
	
	public String getTokenValue() {
		return tokenValue;
	}
	
	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}
	
	public String getToken() {
		return token;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getAuthEndPoint() {
		return authEndPoint;
	}
	
	public String getMailsList() {
		return mailsList;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUsernameValue() {
		return usernameValue;
	}

	public void setUsernameValue(String usernameValue) {
		this.usernameValue = usernameValue;
	}

	public String getPasswordValue() {
		return passwordValue;
	}

	public void setPasswordValue(String passwordValue) {
		this.passwordValue = passwordValue;
	}

	public String getAuthEndPointValue() {
		return authEndPointValue;
	}

	public void setAuthEndPointValue(String authEndPointValue) {
		this.authEndPointValue = authEndPointValue;
	}

	public List<String> getMailsListValue() {
		return mailsListValue;
	}

	public void setMailsListValue(List<String> mailsListValue) {
		this.mailsListValue = mailsListValue;
	}

	public BasicTextEncryptor getEncryptor() {
		return encryptor;
	}

	
}