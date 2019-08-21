package xin.yuki.house.runner;

import static xin.yuki.house.util.ProxyIpUtil.getProxy;

import com.alibaba.fastjson.JSON;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.model.OOSpider;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import xin.yuki.house.model.DbPost;
import xin.yuki.house.pipline.DbPostPipeline;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(ApplicationRunner.class);
    @Autowired
    private DbPostPipeline dbPostPipeline;
    private OOSpider ooSpider;

    private static Set<String> urls =new HashSet<>();

    @Override
    public void run(String... args) throws Exception {
        // 获取代理
        String proxy = getProxy();
        // 设置代理
        HttpClientDownloader downloader = new HttpClientDownloader(){
            @Override
            protected void onSuccess(Request request) {
                super.onSuccess(request);
                urls.add(request.getUrl());
            }
        };
        downloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(proxy.split(":")[0],
                Integer.valueOf(proxy.split(":")[1]))));
        // 创建请求对象
        ooSpider = OOSpider
                .create(Site.me().setSleepTime(3000)
                                // URL请求失败后的retry
                                .setCycleRetryTimes(3)
                                // httpClient的retry
                                .setRetryTimes(3).setUserAgent(
                                "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586")
                                .setTimeOut(5000)
                        , dbPostPipeline, DbPost.class);
        // 设置爬虫配置
        ooSpider.setDownloader(downloader);
        ooSpider.addUrl("https://www.douban.com/group/haizhuzufang/discussion?start=0").thread(5)
                .run();
        LOG.info(JSON.toJSONString(urls));
        while (urls.size()<=0){
            proxy = getProxy();
            downloader.setProxyProvider(SimpleProxyProvider.from(new Proxy(proxy.split(":")[0],
                    Integer.valueOf(proxy.split(":")[1]))));
            ooSpider.addUrl("https://www.douban.com/group/haizhuzufang/discussion?start=0&t="+System.currentTimeMillis()).run();
        }
        Thread.sleep(10 * 60 * 1000);
    }

}
