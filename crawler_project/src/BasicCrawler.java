import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.HashSet;


public class BasicCrawler extends WebCrawler {
    
    final static Pattern EXCLUSIONS = Pattern.compile(".*(\\.(" + "css|js|json|webmanifest|ttf|wav|avi|mov|mpeg|mpg|ram|m4v|wma|wmv|mid|mp2|mp3|mp4|zip|rar|gz|exe|ico|woff2))$");
    // private static final Pattern IMG_PATTERNS = Pattern.compile(".*(\\.(jpg|jpeg|png|svg))$");
    
    HashSet<String> visited_url_set = new HashSet<>();

    String fetch_csv = "";
    String visit_csv = "";
    String urls_csv = "";


    @Override
    public Object getMyLocalData() {
        return new String[]{fetch_csv, visit_csv, urls_csv};
    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        String link_ = webUrl.getURL().toLowerCase().replaceAll(",", "-");
        fetch_csv += link_ + "," + statusCode + "\n";
        visited_url_set.add(link_);
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String link_ = url.getURL().toLowerCase().replaceAll(",", "-");
        boolean allowed_domian = link_.startsWith("http://www.wsj.com/") || link_.startsWith("https://www.wsj.com/");
        urls_csv += ((allowed_domian) ? (link_ + ",OK\n"):(link_ + ",N_OK\n"));
        // if(IMG_PATTERNS.matcher(link_).matches()){
        //     return true;
        // }
        return !EXCLUSIONS.matcher(link_).matches() && !visited_url_set.contains(link_) && allowed_domian;
    }

    @Override
    public void visit(Page page) {
        String link_ = page.getWebURL().getURL().toLowerCase().replaceAll(",", "-");
        String content_type = page.getContentType().split(";")[0];
        // boolean allowed_content_type = content_type.contains("image") | content_type.contains("html") | content_type.contains("doc") | content_type.contains("pdf");

        // if (allowed_content_type || page.getParseData() instanceof HtmlParseData) {
        //     HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        //     Set<WebURL> outgoing_links = htmlParseData.getOutgoingUrls();
        //     visit_csv += link_ + "," + page.getContentData().length + "," + outgoing_links.size() + "," + content_type + "\n";
        // }

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            Set<WebURL> outgoing_links = htmlParseData.getOutgoingUrls();
            visit_csv += link_ + "," + page.getContentData().length + "," + outgoing_links.size() + "," + content_type + "\n";
        }
        else{
            visit_csv += link_ + "," + page.getContentData().length + "," + "0" + "," + content_type + "\n";
        }

    }
}