package xin.yuki.house.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyIpUtil {

    private static Logger logger = LoggerFactory.getLogger(ProxyIpUtil.class);

    public ProxyIpUtil() {
    }

    public static int checkProxy(Proxy proxy, String validSite) {
        try {
            URL url = new URL(validSite != null ? validSite : "http://www.baidu.com/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection(proxy);
            httpURLConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.connect();
            int statusCode = httpURLConnection.getResponseCode();
            return statusCode;
        } catch (IOException var5) {
            logger.error(var5.getMessage());
            return -2;
        }
    }

    public static String getProxy() {
        try {
            URL url = new URL(
                    "http://pool-admin.yuki.xin/api/v1/proxy/?https=1&region=中国");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.connect();
            InputStream responseMessage = httpURLConnection.getInputStream();
            JSONObject jsonObject = JSON.parseObject(IOUtils.toString(responseMessage));
            String proxy = jsonObject.getJSONObject("data").getString("proxy");
            if (checkProxy(new Proxy(
                    Type.HTTP, new InetSocketAddress(proxy.split(":")[0],
                    Integer.valueOf(proxy.split(":")[1]))), "https://www.douban.com/group/haizhuzufang") == 200) {
                logger.info("Success Get Proxy:"+proxy);
                return proxy;
            }
            return getProxy();
        } catch (IOException var5) {
            logger.error(var5.getMessage());
            return getProxy();
        }
    }

    public static int checkProxyRepeat(Proxy proxy, String validSite) {
        for (int i = 0; i < 3; ++i) {
            int statusCode = checkProxy(proxy, validSite);
            if (statusCode > 0) {
                return statusCode;
            }
        }

        return -2;
    }
}