package com.mimiter.service.impl;

import com.google.protobuf.Empty;
import com.mimiter.*;
import com.mimiter.config.SMSConfig;
import com.mimiter.model.entity.SMSTemplate;
import com.mimiter.service.SMSTemplateService;
import com.mimiter.utils.SMSTemplateUtil;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class SMSTemplateServiceImpl
        extends SMSTemplateServiceGrpc.SMSTemplateServiceImplBase
        implements SMSTemplateService {

    private final SMSConfig smsConfig;

    private final SmsClient smsClient;

    private final Long NATIONAL_SMS_CODE = 0L;
    /**
     * 获取国内短信模板
     * @return 返回所有的国内短信模板
     */
    private List<SMSTemplate> listSMSTemplate() {
        List<SMSTemplate> templates = new ArrayList<>();

        try {
            DescribeSmsTemplateListRequest request = new DescribeSmsTemplateListRequest();
            request.setInternational(NATIONAL_SMS_CODE);  // 只获取国内短信模板
            DescribeSmsTemplateListResponse response = smsClient.DescribeSmsTemplateList(request);

            for (DescribeTemplateListStatus status : response.getDescribeTemplateStatusSet()) {
                SMSTemplate smsTemplate = new SMSTemplate();
                smsTemplate.setTemplateId(status.getTemplateId());
                smsTemplate.setInternational(NATIONAL_SMS_CODE);
                smsTemplate.setStatusCode(status.getStatusCode());
                smsTemplate.setReviewReply(status.getReviewReply());
                smsTemplate.setTemplateName(status.getTemplateName());
                smsTemplate.setCreateTime(status.getCreateTime());
                smsTemplate.setTemplateContent(status.getTemplateContent());
                templates.add(smsTemplate);
            }
        } catch (TencentCloudSDKException e) {
            log.error(e.getMessage(), e);
        }
        return templates;
    }

    /**
     * 按短信模板向批量用户发送短信，填入模板的参数长度与模板所需参数数量需要一致，且每位用户的短信参数相同
     * 该方法无法只需提供模板id，无法检查参数数量是否匹配
     * @param id 短信模板id
     * @param phoneNumbers 批量用户
     * @param templateParams 填入模板的短信参数
     * @return 是否全部提交成功
     */
    private boolean sendWithSMSTemplateById(Long id, List<String> phoneNumbers, List<String> templateParams) {
        try {
            SendSmsRequest request = new SendSmsRequest();
            request.setSignName(smsConfig.getSignName());
            request.setSmsSdkAppId(smsConfig.getSdkAppId());
            request.setTemplateId(id.toString());
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

    /**
     * 按短信模板向批量用户发送短信，填入模板的参数长度与模板所需参数数量需要一致，且每位用户的短信参数相同
     * 该方法会先检查参数数量是否匹配
     * @param template 短信模板
     * @param phoneNumbers 批量用户
     * @param templateParams 填入模板的短信参数
     * @return 是否全部提交成功
     */
    private boolean sendWithSMSTemplate(SMSTemplate template, List<String> phoneNumbers, List<String> templateParams) {
        Assert.isTrue(SMSTemplateUtil.getTemplateParamCount(template) == templateParams.size(),
                "模板参数数量错误");

        return sendWithSMSTemplateById(template.getTemplateId(), phoneNumbers, templateParams);
    }

    /**
     * rpc方法，调用listSMSTemplate()实现获取所有的国内短信模板
     */
    @Override
    public void getSMSTemplateList(Empty empty, StreamObserver<SMSTemplateReply> responseObserver) {
        List<SMSTemplate> smsTemplates = listSMSTemplate();
        List<RpcSMSTemplate> rpcSMSTemplates = smsTemplates
                .stream()
                .map(template -> RpcSMSTemplate
                        .newBuilder()
                        .setId(template.getTemplateId())
                        .setInternational(template.getInternational())
                        .setStatusCode(template.getStatusCode())
                        .setReviewReply(template.getReviewReply())
                        .setTemplateName(template.getTemplateName())
                        .setCreateTime(template.getCreateTime())
                        .setTemplateContent(template.getTemplateContent())
                        .build())
                .collect(Collectors.toList());

        SMSTemplateReply reply = SMSTemplateReply.newBuilder()
                .addAllTemplates(rpcSMSTemplates)
                .build();

        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    /**
     * rpc方法，调用sendWithSMSTemplateById()实现按短信模板向批量用户发送短信
     */
    @Override
    public void sendSMS(SMSRequest request, StreamObserver<SMSReply> responseObserver) {
        boolean status = sendWithSMSTemplateById(request.getTemplateId(),
                request.getPhoneNumbersList(),
                request.getParamsList());

        responseObserver.onNext(SMSReply
                .newBuilder()
                .setStatus(status)
                .build());

        responseObserver.onCompleted();
    }
}
