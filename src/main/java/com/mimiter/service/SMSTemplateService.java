package com.mimiter.service;

import com.mimiter.model.entity.SMSTemplate;

import java.util.List;

public interface SMSTemplateService {

    /**
     * 获取国内短信模板
     * @return 返回所有的国内短信模板
     */
    List<SMSTemplate> getSMSTemplateList();

    /**
     * 按短信模板向批量用户发送短信，填入模板的参数长度与模板所需参数数量需要一致，且每位用户的短信参数相同
     * @param template 短信模板
     * @param phoneNumbers 批量用户
     * @param templateParams 填入模板的短信参数
     * @return 是否全部提交成功
     */
    boolean sendWithSMSTemplate(SMSTemplate template, List<String> phoneNumbers, List<String> templateParams);
}
