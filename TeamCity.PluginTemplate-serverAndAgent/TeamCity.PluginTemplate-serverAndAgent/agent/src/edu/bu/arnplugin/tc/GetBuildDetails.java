
package edu.bu.arnplugin.tc;

import edu.bu.arnplugin.bean.Change;
import edu.bu.arnplugin.bean.TCResponse;
import jetbrains.buildServer.agent.BuildProgressLogger;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GetBuildDetails {
	
	private static ArrayList<String> workItems =new ArrayList<String>();
	public static ArrayList<String> getWorkItems(String buildNo, BuildProgressLogger logger) throws ClientProtocolException, IOException, ParserConfigurationException, SAXException {
		

		
		//buildNum = getLatestBuild();
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		String url = "http://localhost/httpAuth/app/rest/changes?locator=build:(number:" +buildNo  + ",running:true)&fields=count,change:(comment)";
	    String userName = "karunesh";
	    String password = "teamcity";
	    String authString = userName + ":" + password;
	    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
	    String authStringEnc = new String(authEncBytes);

	    HttpGet httpGet = new HttpGet(url);
	    httpGet.setHeader("Accept", "application/json");
	    httpGet.setHeader("Authorization", "Basic " + authStringEnc);
	    
	    HttpResponse response = httpClient.execute(httpGet);
			logger.message("Status code :"+response.getStatusLine().getStatusCode());
	    InputStream responseStream = response.getEntity().getContent();

	    /*DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document doc = db.parse(responseStream);
	    Element e  = (Element)doc.getDocumentElement().getElementsByTagName("comment").item(0);
	    String comment = e.getTextContent();
	    String requiredString = comment.substring(comment.indexOf("#")+1);
	    requiredString = requiredString.substring(0, comment.indexOf(" "));
	    */
	    ObjectMapper mapper = new ObjectMapper();
	    TCResponse res = mapper.readValue(responseStream, TCResponse.class);
	    for(Change change : res.getChanges()){
	    	String comment = change.getComment();
	    	//String[] requiredString = comment.split("#");
	    	Matcher matcher = Pattern.compile("#[a-zA-Z0-9]+").matcher(comment);
	    	while (matcher.find()) {
	    		String temp = comment.substring(matcher.start(),matcher.end());
	    		String id =temp.substring(1, temp.length());
	    		if(!workItems.contains(id)){
	    		workItems.add(id);
	    		}
	    	}
		    //requiredString = requiredString.replace("\n","");
		   // workItems.add(requiredString);
	    	System.out.println();
	    }
	    
	    return workItems;
	}
	
	public static String getLatestBuild() throws ClientProtocolException, IOException, ParserConfigurationException, SAXException{
		String bNumber;
		HttpClient httpClient = HttpClientBuilder.create().build();
		String url = "http://localhost/httpAuth/api/buildTypes/id:CloudcomputingARN_BuildF/builds?count=1";
		String userName = "seshank4";
	    String password = "Password@1";
	    String authString = userName + ":" + password;
	    byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
	    String authStringEnc = new String(authEncBytes);
	    
	    HttpGet httpGet = new HttpGet(url);
	    //shttpGet.setHeader("Accept", "application/json");
	    httpGet.setHeader("Authorization", "Basic " + authStringEnc);
	    
	    HttpResponse response = httpClient.execute(httpGet);
	    InputStream responseStream = response.getEntity().getContent();
	   	    
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    Document doc = db.parse(responseStream);
	    Element e  = (Element)doc.getDocumentElement().getElementsByTagName("build").item(0);
	    bNumber = e.getAttribute("number");
	    
	    
	    return bNumber;
		
	}

}
