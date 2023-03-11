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

    @Bean
    public SmsClient createClient() {
        Credential cred = new Credential(secretId, secretKey);
        return new SmsClient(cred, "ap-nanjing");
    }

}
