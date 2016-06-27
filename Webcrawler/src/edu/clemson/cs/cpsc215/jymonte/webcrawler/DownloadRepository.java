package edu.clemson.cs.cpsc215.jymonte.webcrawler;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * DownloadRepository class.  Singleton class that is used for storing WebElements that will be downloaded 
 * at the completion of all Breadth First Searches on WebPage objects.
 * @author Yates Monteith
 *
 */
public class DownloadRepository {
	private static DownloadRepository instance;
	private Map<String, WebImage> imageMap;
	private Map<String, WebFile> fileMap;
	private Map<String, WebPage> pageMap;
	private String saveLocation;
	static {
		instance = new DownloadRepository();
	}

	/**
	 * Private DownloadRepository constructor.  Should only be called by the static getInstance() function,
	 * ensuring only one copy of the DownloadRepository is instantiated at one time.
	 */
	private DownloadRepository() {
		imageMap = new HashMap<String, WebImage>();
		fileMap = new HashMap<String, WebFile>();
		pageMap = new HashMap<String, WebPage>();
	}
	
	/**
	 * saveFilesToDisk - Iterates through each of the data structures (WebPage, WebImage, WebFile), calling the 
	 * saveFiles() method.
	 */
	public void saveFilesToDisk() {
		WebElement [] elementArray;
		elementArray = imageMap.values().toArray(new WebElement[0]);
		saveFiles(elementArray);
		elementArray = pageMap.values().toArray(new WebElement[0]);
		saveFiles(elementArray);
		elementArray = fileMap.values().toArray(new WebElement[0]);
		saveFiles(elementArray);
	}
	
	/**
	 * saveFiles - Iterates through each of the WebElements in the WebElement 
	 * array parameter, calling AbstractWebElement.saveToDisk()
	 * @param elementArray
	 */
	private void saveFiles(WebElement [] elementArray) {
		for(int i = 0; i < elementArray.length; i++)
			elementArray[i].saveToDisk(saveLocation);
	}

	/**
	 * Static getInstance method - Returns a pointer to the only instantiated 
	 * object of type DownloadRepository
	 * @return
	 */
	public static DownloadRepository getInstance() {
		return instance;
	}

	/**
	 * setSaveLocation - Mutator for the saveLocation variable, which is used to determine where to save files.
	 * @param string
	 */
	public void setSaveLocation(String string) {
		this.saveLocation = string;
		
	}

	/**
	 * Adds all non-duplicate entries in the passed parameter to this.imageMap<String, WebImage> 
	 * @param imageMap
	 */
	public void addImagesToRepository(Map<String, WebImage> imageMap) {
		this.imageMap.putAll(imageMap);
	}
	
	/**
	 * Adds all non-duplicate entries in the passed parameter to this.fileMap<String, WebFile> 
	 * @param imageMap
	 */
	public void addFilesToRepository(Map<String, WebFile> fileMap) {
		this.fileMap.putAll(fileMap);
	}
	
	/**
	 * Adds all non-duplicate entries in the passed parameter to this.pageMap<String, WebPage> 
	 * @param imageMap
	 */
	public void addPagesToRepository(Map<String, WebPage> pageMap) {
		pageMap.putAll(pageMap);
	}

	/**
	 * Adds a single webpage to the DownloadRepository.pageMap<String, WebPage> object
	 * @param webPage
	 */
	public void addPageToMap(WebPage webPage) {
		pageMap.put(webPage.getUrl(), webPage);
	}
	
	/**
	 * Retrieves a webpage from the DownloadRepository.pageMap<String, WebPage> object 
	 * based on the passed URL
	 * @param url
	 * @return
	 */
	public boolean findWebPage(String url) {
		return pageMap.containsKey(url);
	}
	
	/**
	 * Ensures the creation of the output directory required to save the WebElements 
	 * stored in the DownloadRepository.
	 */
	public void makeOutputDirectory() {
		File f = new File(saveLocation);
		if(!f.exists())
			f.mkdirs();
	}

	/**
	 * Prints statistics on the size of each of the Maps containing WebElements.
	 */
	public void printStatistics() {
		System.out.println("Pages: " + pageMap.size());
		System.out.println("Images: " + imageMap.size());
		System.out.println("Files: " + fileMap.size());
		
	}

}
