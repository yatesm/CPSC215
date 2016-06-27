package edu.clemson.cs.cpsc215.jymonte.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebPage class.  Models a webpage via URL and contains functionality to perform a 
 * breadth-first search on that webpage, noting the WebFiles, WebImages and WebPages contained
 * in the WebPage.
 * @author Yates Monteith
 *
 */
public class WebPage extends AbstractWebElement {
	public WebPage(String url, int depth) {
		super(url);
		this.depth = depth;
		System.out.println("New Webpage: Depth= " + depth + "url = " + url);
	}

	private String pageContents;
	private int depth;
	private Map<String, WebPage> pageMap = new HashMap<String, WebPage>();
	private Map<String, WebImage> imageMap = new HashMap<String, WebImage>();
	private Map<String, WebFile> fileMap = new HashMap<String, WebFile>();

	/**
	 * crawl - Performs Breadth-First Search on the WebPage retrieved from this.url.
	 * 
	 * Searches and stores any URLs associated with the following tags:
	 * 		a
	 * 		img
	 * 		script
	 * 		link
	 * Adds the URLs associated with each of these tags to the URL Repository and recurses 
	 * the search on the URLs retrieved from the URL modeled by the calling class.  
	 * 
	 */
	public void crawl() {
		if(this.depth > 0) {
			this.getWebpageText();
			this.findUrls("<a", "href=");
			this.findUrls("<img", "src=");
			this.findUrls("<script", "src=");
			this.findUrls("<link", "href=");
			System.out.println("adding stuff to repository");
			this.addToRepository();
			System.out.println("Adding this page to repository");
			DownloadRepository.getInstance().addPageToMap(this);
			System.out.println("Starting to crawl deeper");
			this.crawlDeeper();
		}
	}

	/**
	 * crawlDeeper() - The recursive part of the BFS; this crawls on each WebPage 
	 * retrieved from the initial parse of the HTML retrieved from the URL modeled by the 
	 * calling object.  Ensures that pages are not crawled twice by checking against the 
	 * DownloadRepository.
	 */
	private void crawlDeeper() {
		WebPage[] webArray = pageMap.values().toArray(new WebPage[0]);
		for (int i = 0; i < webArray.length; i++) {
			if(!DownloadRepository.getInstance().findWebPage(webArray[i].getUrl())) {
				System.out.println("Crawling " + webArray[i].getUrl());
				webArray[i].crawl();
			}
			else {
				System.out.println("Already crawled: " + webArray[i].getUrl());
			}
		}
	}

	/**
	 * addToRepository - Adds any images and files to the DownloadRepository for later downloading.
	 */
	private void addToRepository() {
		//DataRepository.getInstance().addPagesToRepository(pageMap);
		DownloadRepository.getInstance().addImagesToRepository(imageMap);
		DownloadRepository.getInstance().addFilesToRepository(fileMap);
	}
	

	/**
	 * getUrl - Returns the this.url of the calling object.
	 * @return - this.url
	 */
	public String getUrl() {
		return super.getUrl();
	}
	
	/**
	 * Builds a string containing the text of a webpage.
	 */
	private void getWebpageText() {
		BufferedReader buff = null;
		try {
			URL urlObject = new URL(this.getUrl());
			URLConnection urlConn = urlObject.openConnection();
			urlConn.setConnectTimeout(7);
			buff = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream()));
			String temp = "";
			this.pageContents = "";
			while ((temp = buff.readLine()) != null)
				pageContents += temp;
		} catch (MalformedURLException e) {
			System.err.println("Error: Malformed URL");
			e.printStackTrace();
		} catch(SocketTimeoutException e) {
			System.err.println("Timeout encountered for " + this.getUrl());
			this.pageContents = "null";
		} catch (IOException e) {
			System.err.println("Error: IOException");
			e.printStackTrace();
		} finally {
			if(buff != null) try {buff.close();} catch(Exception e) { }
		}
	}
	
	/**
	 * findUrls - Parses the text of a webpage retrieved by getWebpageText, looking for
	 * specific combinations of tag (i.e. <a, <img, <src) and a specific attribute (i.e., src=, href=, ...).
	 * 
	 * Upon finding a specific tag + attribute combination that describes a WebElement (page, image, file),
	 * it passes control to the createWebElement method to instantiate a new WebElement.
	 * @param tag
	 * @param attribute
	 */
	private void findUrls(String tag, String attribute) {
		String workingPageContents = pageContents;
		while(workingPageContents.contains(tag)) {
			int tagIndex = workingPageContents.indexOf(tag);
			workingPageContents = workingPageContents.substring(tagIndex+ 
					tag.length());
			int attIndex = workingPageContents.indexOf(attribute);
			workingPageContents = workingPageContents.substring(attIndex +
					attribute.length());
			char firstChar = workingPageContents.charAt(0);
			String attValue = "";
			if(firstChar == '\'' || firstChar == '"') {
				workingPageContents = workingPageContents.substring(1);
				attValue = workingPageContents.substring(0, 
						workingPageContents.indexOf(firstChar));
			} else {
				attValue = workingPageContents.substring(0, 
						workingPageContents.indexOf(" "));
			}
			if((attValue.contains("./") || attValue.contains("../")) ||
					!attValue.contains("http://"))
				attValue = resolveRelativeUrl(super.getUrl(), attValue);
			createWebElement(attValue);
		}
	}

	/**
	 * createWebElement - Examines a URL to determine whether or not the WebElement is a WebPage, 
	 * WebFile or WebImage.  Instantiates an object of the corresponding class and stores element within 
	 * the data structures of the calling object for temporary storage until the object can be stored 
	 * in the DownloadRepository. 
	 * @param foundUrl - URL of the WebElement to be created.
	 */
	private void createWebElement(String foundUrl) {
		if(foundUrl.endsWith(".htm") || foundUrl.endsWith(".php") ||
				foundUrl.endsWith(".html")) {
			System.out.println("Found webpage: " + foundUrl);
			this.pageMap.put(foundUrl, new WebPage(foundUrl, this.depth -1));
		}
		else if(foundUrl.endsWith(".bmp") || foundUrl.endsWith(".jpg") || 
				 foundUrl.endsWith(".png") || foundUrl.endsWith(".svg") || 
				 foundUrl.endsWith(".gif") || foundUrl.endsWith(".tiff")) {
			System.out.println("Found WebImage: " + foundUrl);
			this.imageMap.put(foundUrl, new WebImage(foundUrl));
		} else if(foundUrl.endsWith("/")) {
			System.out.println("Found webpage: " + foundUrl);
			foundUrl = foundUrl + "index.html";
			this.pageMap.put(foundUrl, new WebPage(foundUrl, this.depth -1));
		}
		else {
			System.out.println("Found WebFile: " + foundUrl);
			this.fileMap.put(foundUrl, new WebFile(foundUrl));
		}
		
	}

	/**
	 * resolveRelativeUrl - Helper function for resolving anomalous URLs 
	 * 	(i.e. http://cs.clemson.edu/~jymonte/docs/../docs/../docs/webcrawler.html, ../something/something.html, ...)
	 * @param url - Base URL
	 * @param relativeUrl - relative URL hosted at Base URL
	 * @return
	 */
	private String resolveRelativeUrl(String url, String relativeUrl) {
		if(relativeUrl.startsWith("./")) 
			relativeUrl = relativeUrl.substring(2, relativeUrl.length());
		url = url.substring(0, url.lastIndexOf("/"));
		while(relativeUrl.startsWith("../")) {
			relativeUrl = relativeUrl.substring(2, relativeUrl.length());
			url = url.substring(0, url.lastIndexOf("/"));
		}			
		return url + "/" + relativeUrl;
		
	}

}
