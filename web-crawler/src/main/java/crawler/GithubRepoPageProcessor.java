package crawler;
/**
 * @author jiaqi
 * @version 创建时间：2017年12月22日 下午2:56:09
 * 类说明
 */
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.RedisScheduler;

public class GithubRepoPageProcessor implements PageProcessor {
	
	private Site site = Site.me().setRetryTimes(3).setSleepTime(100);
	
	@Override
	public void process(Page page) {
		 page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
	        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
	        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
	        if (page.getResultItems().get("name")==null){
	            //skip this page
	            page.setSkip(true);
	        }
	     page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static void main(String[] args) {
		 Spider.create(new GithubRepoPageProcessor())
         //从https://github.com/code4craft开始抓    
         .addUrl("https://github.com/code4craft/webmagic")
         //设置Scheduler，使用Redis来管理URL队列
         //.setScheduler(new RedisScheduler("localhost"))
         //设置Pipeline，将结果以json方式保存到文件
         .addPipeline(new JsonFilePipeline("D:\\data\\webmagic"))
         //开启5个线程同时执行
         .thread(100)
         //启动爬虫
         .run();
	}
}