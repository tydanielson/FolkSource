package org.folksource.service.impl;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.folksource.service.ServiceHelper;
import org.folksource.service.WikimediaService;
import org.folksource.util.UserService;
import org.folksource.dao.jpa.UserDao;
import org.folksource.entities.User;
import org.glassfish.jersey.client.oauth1.AccessToken;
import org.glassfish.jersey.client.oauth1.ConsumerCredentials;
import org.glassfish.jersey.client.oauth1.OAuth1AuthorizationFlow;
import org.glassfish.jersey.client.oauth1.OAuth1ClientSupport;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.json.JSONArray;
import org.json.JSONObject;

public class WikimediaServiceImpl implements WikimediaService {
	final static Logger logger = LoggerFactory.getLogger(WikimediaServiceImpl.class);
	
	@Autowired
	private UserDao userDao;
	private UserService userService;
	private HashMap<String, OAuth1AuthorizationFlow> authFlowHashMap = new HashMap<String, OAuth1AuthorizationFlow>();
	
	private ServiceHelper serviceHelper;
	
	@Transactional
	public String getAuthUri(String username) {
		String consumerKey = serviceHelper.getAppProperties().getProperty("wiki.key");
		String consumerSecret = serviceHelper.getAppProperties().getProperty("wiki.secret");
		String wikiUrl = serviceHelper.getAppProperties().getProperty("wiki.url");
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);
		OAuth1AuthorizationFlow authFlow = OAuth1ClientSupport.builder(consumerCredentials)
			    .authorizationFlow(
			    	wikiUrl + "index.php?title=Special%3AOAuth%2Finitiate",
			    	wikiUrl + "index.php?title=Special%3AOAuth%2Ftoken",
			    	wikiUrl + "index.php?title=Special%3AOAuth%2Fauthorize")
			    .build();
		String authorizationUri = authFlow.start();

		String requestToken = authorizationUri.split("=")[2]; 
		
		addFlow(requestToken, authFlow);
		
		//store the requestToken as if it were the accessToken
		User u = userDao.getUserByUsername(username);
		u.setWikiToken(requestToken);
		userDao.merge(u);

		return authorizationUri + "&oauth_consumer_key=" + consumerKey;
	}
	
	@Transactional
	public String verify(String verifier, String requestToken){
		OAuth1AuthorizationFlow authFlow = getFlow(requestToken);   
//		String wikiUrl = serviceHelper.getAppProperties().getProperty("wiki.url");
//		String consumerKey = serviceHelper.getAppProperties().getProperty("wiki.key");
		
		AccessToken accessToken = authFlow.finish(verifier);
		
		User user = userDao.getUserByWikiToken(requestToken);
		
		user.setWikiToken(accessToken.getToken());
		user.setWikiTokenSecret(accessToken.getAccessTokenSecret());
		userDao.merge(user);
		
		//TODO get the user information for creating a profile
		//Feature feature = authFlow.getOAuth1Feature();
		//Client client = ClientBuilder.newBuilder().register(feature).build();
		
		//Response resp = client.target(wikiUrl + "index.php?title=Special%3AOAuth%2Fidentify").request().get();
		
		
		//StringWriter writer = new StringWriter();
		//try {
			//IOUtils.copy((InputStream) resp.getEntity(), writer, "UTF-8");
		//} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
		//String thestring = writer.toString();
		
//		byte[] decodedKey = Base64.getDecoder().decode(consumerKey);
//		SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
//		
//		JwtConsumer jwtConsumer = new JwtConsumerBuilder()
//	            .setRequireExpirationTime() // the JWT must have an expiration time
//	            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
//	            .setRequireSubject() // the JWT must have a subject claim
//	            .setExpectedIssuer("Issuer") // whom the JWT needs to have been issued by
//	            .setExpectedAudience("Audience") // to whom the JWT is intended for
//	            .setVerificationKey(originalKey) // verify the signature with the public key
//	            .build(); 
//		Key key;
//		
//		try
//	    {
//	        //  Validate the JWT and process it to the Claims
//	        JwtClaims jwtClaims = jwtConsumer.processToClaims(thestring);
//	        System.out.println("JWT validation succeeded! " + jwtClaims);
//	    }
//	    catch (InvalidJwtException e)
//	    {
//	        // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
//	        // Hopefully with meaningful explanations(s) about what went wrong.
//	        System.out.println("Invalid JWT! " + e);
//	    }

		return accessToken.toString();
	}
	
	private String getEditToken(String username) {
		String consumerKey = serviceHelper.getAppProperties().getProperty("wiki.key");
		String consumerSecret = serviceHelper.getAppProperties().getProperty("wiki.secret");
		String wikiUrl = serviceHelper.getAppProperties().getProperty("wiki.url");
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);
		
		AccessToken token = getUserWikiToken(username);
		
		Feature feature = OAuth1ClientSupport.builder(consumerCredentials)
			    .feature()
			    .accessToken(token)
			    .build();
		
		Client client = ClientBuilder.newBuilder().register(feature).build();
		Response resp = client.target(wikiUrl + "api.php?action=query&meta=tokens&type=csrf&format=json").request().get();
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy((InputStream) resp.getEntity(), writer, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Edit token: " + writer.toString());
		return writer.toString();
	}
	
//	private String getBotContent(String username) {
//		String consumerKey = serviceHelper.getAppProperties().getProperty("wiki.key");
//		String consumerSecret = serviceHelper.getAppProperties().getProperty("wiki.secret");
//		String wikiUrl = serviceHelper.getAppProperties().getProperty("wiki.url");
//		ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);
//		
//		AccessToken token = getUserWikiToken(username);
//		
//		Feature feature = OAuth1ClientSupport.builder(consumerCredentials)
//			    .feature()
//			    .accessToken(token)
//			    .build();
//		
//		Client client = ClientBuilder.newBuilder().register(feature).build();
//		Response resp = client.target(wikiUrl + "api.php?action=query&prop=revisions&rvprop=content&format=json&titles=User:FolksourceBot").request().get();
//		
//		StringWriter writer = new StringWriter();
//		try {
//			IOUtils.copy((InputStream) resp.getEntity(), writer, "UTF-8");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		JSONObject json = new JSONObject(writer.toString());
//		String content = json.getJSONObject("query").getJSONObject("pages").getJSONObject(json.getJSONObject("query").getJSONObject("pages").names().get(0).toString()).getJSONArray("revisions").getJSONObject(0).getString("*");
//		return content;
//	}

	@Override
	public void uploadPhoto(String username, File photo) {
		String consumerKey = serviceHelper.getAppProperties().getProperty("wiki.key");
		String consumerSecret = serviceHelper.getAppProperties().getProperty("wiki.secret");
		String wikiUrl = serviceHelper.getAppProperties().getProperty("wiki.url");
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);
		
		AccessToken token = getUserWikiToken(username);
		
		Feature feature = OAuth1ClientSupport.builder(consumerCredentials)
			    .feature()
			    .accessToken(token)
			    .build();
		
		Client client = ClientBuilder.newBuilder()
				.register(feature)
				.register(MultiPartFeature.class)
				.build();

		FileDataBodyPart filePart = new FileDataBodyPart("file", photo);
		
		String editToken = getEditToken(username);
		System.out.println("edit token: " + editToken);
		
		JSONObject json = new JSONObject(editToken);
		String csrfToken = json.getJSONObject("query").getJSONObject("tokens").getString("csrftoken");
		
		System.out.println("Received token: " + csrfToken);
		
		Format formatter = new SimpleDateFormat("YYYY-MM-dd_hh-mm-ss");
		Date date = new Date();
		String filetype = ".jpeg";
		//need to pull the name of the specific location and filetype from upload
		String filename = "photo_" + formatter.format(date) + filetype;
		
		MultiPart multipart = new FormDataMultiPart()
					.field("token", csrfToken)
					.field("filename", filename)
					.bodyPart(filePart);
		
		System.out.println("Created multipart form");
		
		Response resp = client.target(wikiUrl + "api.php?action=upload&format=json")
				.request()
				.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA_TYPE));
		
		System.out.println("Got back a response");
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy((InputStream) resp.getEntity(), writer, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception: " + e.toString());
		}
		System.out.println(writer.toString());
		
		
		
		String imageDirectory = serviceHelper.getAppProperties().getProperty("dir.images");
		File f = new File(imageDirectory + filename);
		try {
			FileUtils.copyFile(photo, f);
		} catch (IOException e) {
			System.out.println("Error uploading file: " + e.getMessage());
		}
		
		addRowToFolkSourceBot(username, "Need to get location", filename);
		
	}
	
	private AccessToken getUserWikiToken(String username) {
		String token = userDao.getUserByUsername(username).getWikiToken();
		String tokenSecret = userDao.getUserByUsername(username).getWikiTokenSecret();
		AccessToken accesstoken = new AccessToken(token, tokenSecret);
		return accesstoken;
	}
	
	public void addRowToFolkSourceBot(String username, String locationName, String fileName) {
		String article = "Minneapolis College of Art and Design";
		String consumerKey = serviceHelper.getAppProperties().getProperty("wiki.key");
		String consumerSecret = serviceHelper.getAppProperties().getProperty("wiki.secret");
		String wikiUrl = serviceHelper.getAppProperties().getProperty("wiki.url");
		ConsumerCredentials consumerCredentials = new ConsumerCredentials(consumerKey, consumerSecret);
		
		AccessToken token = getUserWikiToken(username);
		
		Feature feature = OAuth1ClientSupport.builder(consumerCredentials)
			    .feature()
			    .accessToken(token)
			    .build();
		
		Client client = ClientBuilder.newBuilder().register(feature).build();
		
		String editToken = getEditToken(username);
		
		JSONObject json = new JSONObject(editToken);
		String csrfToken = json.getJSONObject("query").getJSONObject("tokens").getString("csrftoken");
		
		//String test = getBotContent(username);
		//System.out.println(test);
		
		Form form = new Form();
		form.param("format", "json");
		form.param("action", "edit");
		form.param("title", "User:FolksourceBot");
		form.param("section", "new");
		//form.param("contentformat", "text/x-wiki");
		form.param("appendtext", "Article:" + article + ", File: " + fileName + ",  ~~~~");
		//form.param("text", "{| border=1 \n |- \n ! Heading 1 \n ! Heading 2 \n |-  \n | A  \n | B \n |}");
		form.param("token", csrfToken);
		
		Response resp = client.target(wikiUrl + "api.php")
				.request()
				.post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
		
		System.out.println("Got back a response");
		
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy((InputStream) resp.getEntity(), writer, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Exception: " + e.toString());
		}
		System.out.println(writer.toString());
	}
	
	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public ServiceHelper getServiceHelper() {
		return serviceHelper;
	}

	public void setServiceHelper(ServiceHelper serviceHelper) {
		this.serviceHelper = serviceHelper;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

    public OAuth1AuthorizationFlow getFlow(String authToken) {
        return authFlowHashMap.get(authToken);
    }

    public void addFlow(String authToken, OAuth1AuthorizationFlow flow) {
    	authFlowHashMap.put(authToken, flow);
    }





}
