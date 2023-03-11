import com.mimiter.App;
import com.mimiter.model.entity.SMSTemplate;
import com.mimiter.service.SMSTemplateService;
import com.mimiter.utils.SMSTemplateUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class SMSTemplateServiceTest {

    @Resource
    private SMSTemplateService smsTemplateService;

    @Test
    public void getSMSTemplate() {
        System.out.println(smsTemplateService.getSMSTemplateList());
    }

    @Test
    public void testSMSTemplateUtil() {
        SMSTemplate template = new SMSTemplate();
        template.setTemplateContent("用户{1}您好，您已于{2}成功报名“{3}”！活动详情见报名链接。");

        assert SMSTemplateUtil.getTemplateParamCount(template) == 3;
    }
}
