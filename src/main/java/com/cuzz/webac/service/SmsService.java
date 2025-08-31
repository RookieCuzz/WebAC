package com.cuzz.webac.service;

import com.cuzz.webac.caches.Caches;
import com.cuzz.webac.model.dto.SmsSendResultDTO;
import jakarta.annotation.Resource;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmsService {
    @Value("${sms.host}") private String host;
    @Value("${sms.path}") private String path;
    @Value("${sms.appcode}") private String appCode;
    @Value("${sms.smsSignId}") private String smsSignId;
    @Value("${sms.templateId}") private String templateId;
    @Resource
    private Caches caches;

    public SmsSendResultDTO sendCode(String mobile, int minute) {
        SecureRandom sr = new SecureRandom();
        int n = sr.nextInt(10_000);      // 0..9999
        String code = String.format("%04d", n);

        SmsSendResultDTO result= sendCode(mobile,code,minute);
        result.setCode(code);
        return result;
    }

    public SmsSendResultDTO sendCode(String mobile, String code, int minute) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URI uri = new URIBuilder(host + path)
                    .build();

            HttpPost post = new HttpPost(uri);
            post.setHeader("Authorization", "APPCODE " + appCode);

            // 表单参数
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("mobile", mobile));
            params.add(new BasicNameValuePair("param", String.format("**code**:%s,**minute**:%d", code, minute)));
            params.add(new BasicNameValuePair("smsSignId", smsSignId));
            params.add(new BasicNameValuePair("templateId", templateId));
            post.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

            try (CloseableHttpResponse resp = client.execute(post)) {
                int status = resp.getStatusLine().getStatusCode();
                String body = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                boolean ok = status >= 200 && status < 300;


                return SmsSendResultDTO.of(ok, status, body);
            }
        } catch (Exception e) {
            return SmsSendResultDTO.of(false, 500, "Exception: " + e.getMessage());
        }
    }
}