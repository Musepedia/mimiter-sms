package com.mimiter.utils;

import com.mimiter.model.entity.SMSTemplate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSTemplateUtil {

    private static final Pattern TEMPLATE_PARAM_PATTERN = Pattern.compile("\\{\\d+}");

    public static int getTemplateParamCount(SMSTemplate template) {
        int count = 0;
        Matcher matcher = TEMPLATE_PARAM_PATTERN.matcher(template.getTemplateContent());
        while (matcher.find()) {
            count++;
        }

        return count;
    }
}
