package xin.yuki.house.model;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("https://www.douban.com/group/topic/\\d+")
@HelpUrl("https://www.douban.com/group/haizhuzufang/discussion?start=0")
@Data
@Document
public class DbPost {

    @ExtractByUrl
    private String url;

    @ExtractByUrl("https://www.douban.com/group/topic/(\\d+)")
    @Id
    private String id;

    @ExtractBy("//script[@type='application/ld+json']/html()")
    private String json;

    @ExtractBy(value = "//h1/text()")
    private String title;

    @ExtractBy("//div[@class='user-face']/a/img/@alt")
    private String author;

    private String text;
    private Date dateCreated;
    private String commentCount;



}
