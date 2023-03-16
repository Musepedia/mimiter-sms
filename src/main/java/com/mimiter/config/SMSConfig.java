package com.mimiter.config;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "sms")
public class SMSConfig {

    private String secretId;

    private String secretKey;

    private String sdkAppId;

    private String signName;

    // 成功报名活动时用的模板
    // 您好，您已于{1}成功报名“{2}”！活动详情见报名链接。
    private Long activityEnrollmentTemplateId;

    // 取消报名活动时用的模板
    // 您已取消报名活动“{1}”。退款金额{2}元将于24小时内返还，退款细则见报名链接。
    private Long activityCancellationTemplateId;

    @Bean
    public SmsClient createClient() {
        Credential cred = new Credential(secretId, secretKey);
        return new SmsClient(cred, "ap-nanjing");
    }

}
