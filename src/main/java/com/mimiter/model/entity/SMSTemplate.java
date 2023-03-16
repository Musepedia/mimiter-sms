package com.mimiter.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SMSTemplate {

    private Long templateId;

    private Long international = 0L;  // 是否国际/港澳台短信，其中0表示国内短信，1表示国际/港澳台短信，这里默认为国内短信

    private Long statusCode;  // 申请模板状态，其中0表示审核通过，1表示审核中，-1表示审核未通过或审核失败

    private String reviewReply;

    private String templateName;

    private Long createTime;

    private String templateContent;
}
