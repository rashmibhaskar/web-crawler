import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;


public class App {

    public static void main(String[] args) throws Exception {
    	
        CrawlConfig config = new CrawlConfig();
        
        config.setCrawlStorageFolder("../crawler_project/src/results");
        config.setMaxPagesToFetch(100);
        config.setMaxDepthOfCrawling(16);
        config.setPolitenessDelay(700);
        config.setIncludeBinaryContentInCrawling(true);
        
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
     
        controller.addSeed("https://www.wsj.com");
        controller.start(BasicCrawler.class, 18);
        
        createFiles(controller);
        System.out.println("========Calculating Statistics========");
        String command = "python ../crawler_project/get_stats.py";
        Runtime.getRuntime().exec(command);

    }

    private static String writetoCSVstring(String sb, String filename) throws IOException {

        PrintWriter writer;
        try {

            FileWriter fileWriter = new FileWriter(filename, true); // Set true for append mode
            writer = new PrintWriter(fileWriter);
            writer.println(sb);
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.printf("ERROR : %s", e.getMessage());
            new Exception("Error while creating output file", e);
        }
        return filename;
    }
    
    private static void createFiles(CrawlController controller) throws IOException {
        String urls = "URL,Size(Bytes),# of Outlinks,Content-Type\n";
        String csv1 = "URL,Status\n";
        String csv3 = "URL,URL Type\n";

        for (Object obj : controller.getCrawlersLocalData()) {
            String[] csvs = (String[]) obj;
            urls+=csvs[1];
            csv1+=csvs[0];
            csv3+=csvs[2];
        }

        writetoCSVstring(csv1, "../crawler_project/src/results/fetch_wsj.csv");
        writetoCSVstring(urls, "../crawler_project/src/results/visit_wsj.csv");
        writetoCSVstring(csv3, "../crawler_project/src/results/urls_wsj.csv");
        
    	
    }

}