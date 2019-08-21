package xin.yuki.house.pipline;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;
import xin.yuki.house.dao.DbPostDao;
import xin.yuki.house.model.DbPost;

@Component
public class DbPostPipeline implements PageModelPipeline {

    private static Logger LOG = LoggerFactory.getLogger(DbPostPipeline.class);

    @Autowired
    private DbPostDao dbPostDao;

    @Autowired
    private WxMpService wxMpService;

    @Override
    public void process(Object o, Task task) {
        LOG.info(JSON.toJSONString(o));
        if (o instanceof DbPost) {
            DbPost post = (DbPost) o;
            JSONObject jsonObject = JSON.parseObject(post.getJson(), Feature.SupportAutoType);
            post.setText(jsonObject.getString("text"));
            try {
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                post.setDateCreated(parser.parse(jsonObject.getString("dateCreated")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            post.setCommentCount(jsonObject.getString("commentCount"));
            Optional<DbPost> byId = dbPostDao.findById(post.getId());
            if (!byId.isPresent()) {
                // 发微信消息
                WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                        .toUser("oYAECwdpwjd21xobMrrS4XdbsbYw")
                        .templateId("YQOi2XANJn5jG_E4WrNDUmBUD2JbGxr-5CMDUsvLYEs")
                        .url(post.getUrl())
                        .build();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                templateMessage.addData(new WxMpTemplateData("title", post.getTitle(), "#173177"));
                templateMessage.addData(new WxMpTemplateData("date", sdf.format(post.getDateCreated()), "#173177"));
                templateMessage.addData(new WxMpTemplateData("author", post.getAuthor(), "#173177"));
                templateMessage.addData(new WxMpTemplateData("count", post.getCommentCount(), null));
                templateMessage.addData(new WxMpTemplateData("text", post.getText(), null));

                try {
                    wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
                } catch (WxErrorException e) {
                    e.printStackTrace();
                }
            }
            dbPostDao.save(post);

        }

    }
}
