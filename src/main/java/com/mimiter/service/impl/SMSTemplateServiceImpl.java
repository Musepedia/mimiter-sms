package com.mimiter.service.impl;

import com.mimiter.config.SMSConfig;
import com.mimiter.model.entity.SMSTemplate;
import com.mimiter.service.SMSTemplateService;
import com.mimiter.utils.SMSTemplateUtil;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("smsTemplateService")
@RequiredArgsConstructor
public class SMSTemplateServiceImpl implements SMSTemplateService {

    private final SMSConfig smsConfig;

    private final SmsClient smsClient;

    @Override
    public List<SMSTemplate> getSMSTemplateList() {
        List<SMSTemplate> templates = new ArrayList<>();

        try {
            DescribeSmsTemplateListRequest request = new DescribeSmsTemplateListRequest();
            request.setInternational(0L);  // 只获取国内短信模板
            DescribeSmsTemplateListResponse response = smsClient.DescribeSmsTemplateList(request);

            for (DescribeTemplateListStatus status : response.getDescribeTemplateStatusSet()) {
                SMSTemplate smsTemplate = new SMSTemplate();
                smsTemplate.setTemplateId(status.getTemplateId());
                smsTemplate.setTemplateName(status.getTemplateName());
                smsTemplate.setStatusCode(status.getStatusCode());
                smsTemplate.setTemplateContent(status.getTemplateContent());
                templates.add(smsTemplate);
            }
        } catch (TencentCloudSDKException e) {
            log.error(e.getMessage(), e);
        }
        return templates;
    }

    @Override
    public boolean sendWithSMSTemplate(SMSTemplate template, List<String> phoneNumbers, List<String> templateParams) {
        try {
            Assert.isTrue(SMSTemplateUtil.getTemplateParamCount(template) == templateParams.size(),
                    "模板参数数量错误");

            SendSmsRequest request = new SendSmsRequest();
            request.setSignName(smsConfig.getSignName());
            request.setSmsSdkAppId(smsConfig.getSdkAppId());
            request.setTemplateId(template.getTemplateId().toString());
            request.setPhoneNumberSet(phoneNumbers.toArray(new String[0]));
            request.setTemplateParamSet(templateParams.toArray(new String[0]));
            SendSmsResponse response = smsClient.SendSms(request);

            for (SendStatus status : response.getSendStatusSet()) {
                if (!status.getCode().equals("Ok")) {
                    return false;
                }
            }
        } catch (TencentCloudSDKException e) {
            log.error(e.getMessage(), e);
        }
        return true;
    }
}
