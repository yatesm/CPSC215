package edu.clemson.cs.cpsc215.jymonte.webcrawler;

/**
 * WebCrawler class.  Contains the driver for executing the program and basic 
 * logic to execute the program.
 * @author Yates Monteith
 *
 */

public class WebCrawler {
	public static void main(String [] args) {
		if(args.length < 2) {
			System.err.println("Incorrect usage");
			System.err.println("Usage: sourceUrl depth outputLocation ");
			System.exit(-1);
		}
		WebPage initialPage = new WebPage(args[0], Integer.parseInt(args[1]));
		DownloadRepository.getInstance().setSaveLocation(args[2]);
		DownloadRepository.getInstance().makeOutputDirectory();
		initialPage.crawl();		
		DownloadRepository.getInstance().saveFilesToDisk();
		DownloadRepository.getInstance().printStatistics();
	}

}
