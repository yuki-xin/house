package xin.yuki.house;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseApplication.class, args);
    }


    @Bean
    public WxMpService wxMpService(){
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId("wx160547c4c39b47c2"); // 设置微信公众号的appid
        config.setSecret("b6d26b98705acb4897c3d9b863fba684"); // 设置微信公众号的app corpSecret


        WxMpService wxService = new WxMpServiceImpl();// 实际项目中请注意要保持单例，不要在每次请求时构造实例，具体可以参考demo项目
        wxService.setWxMpConfigStorage(config);

        return wxService;
    }

}
