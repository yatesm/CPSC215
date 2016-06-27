package edu.clemson.cs.cpsc215.jymonte.webcrawler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
/**
 * AbstractWebElement class.  AbstractBaseClass which reduces code by providing functionality 
 * for saving WebElements to disk via inheritance. 
 * @author Yates Monteith
 *
 */
public abstract class AbstractWebElement implements WebElement {
	private String url;
	
	/**
	 * saveToDisk - Opens a connection to the url encapsulated by the AbstractWebElement class.
	 * 				Creates all folders in the local path where the file will be downloaded.
	 * 				Downloads file to the specified location.
	 */
	public void saveToDisk(String output) {
		InputStream inStream = null;
		BufferedOutputStream outStream = null;
		byte [] byteArray = new byte[2048];

		try {
			URL urlObject = new URL(this.getUrl());
		
			URLConnection urlConn = urlObject.openConnection();
			urlConn.setConnectTimeout(7);
			inStream = urlConn.getInputStream();
			this.url = this.url.substring("http://".length());
			this.makePathToFile(output);
			
			outStream = new BufferedOutputStream(new FileOutputStream(new 
					File(output + "/" + this.url)));
			while(inStream.read(byteArray, 0, byteArray.length) > 0) {
				outStream.write(byteArray);
			}
			System.out.println("Finished downloading: " + url);
				
		} catch(MalformedURLException e) {
			System.err.println("Malformed url encountered!");
			System.err.println(url);
		} catch(SocketTimeoutException e) {
			System.err.println("Timeout occured in url: " + url);
		} catch(FileNotFoundException e) {
			System.err.println("Could not find file: " + url);
		} catch (IOException e) {
			System.err.println("IOException Thrown!");
			e.printStackTrace();
		} finally {
			if(outStream != null) try {outStream.close();} catch(Exception e) {}
			if(inStream != null) try {inStream.close();} catch(Exception e) {}
			
		}
	}
	
	/**
	 * Creates the path to the local file modeled by the AbstractWebElement child class
	 * @param output - The path preceeding the name of the file.
	 */
	private void makePathToFile(String output) {
		String filePath="";
		try {
			filePath = output + "/" + this.url.substring(0, this.url.lastIndexOf('/'));
			File f = new File(filePath);
			f.mkdirs();
		} catch(StringIndexOutOfBoundsException e ) {
			System.err.println(filePath);
			System.err.println(this.url);
		}
	}

	
	/**
	 * AbstractWebElement Constructor
	 * @param url
	 */
	public AbstractWebElement(String url) {
		this.url = url;
	}
	
	/**
	 * getUrl - Returns the URL data element belonging to the member object
	 * @return - this.url
	 */
	public String getUrl() {
		return this.url;
	}
}
