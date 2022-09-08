package com.no.hurdles.spray.utils;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class LarkTalkingUtil {

    private RestTemplate restTemplate;

    private String url;

    private String secret;

    public LarkTalkingUtil(RestTemplate restTemplate, String url, String secret) {
        this.restTemplate = restTemplate;
        this.url = url;
        this.secret = secret;
    }

    /**
     * 发送文本消息
     * @param text
     */
    public void sendText(String text) {

        try {
            Long timestamp = System.currentTimeMillis() / 1000;
            String sign = genSign(secret, timestamp.toString());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HashMap<String, Object> map = new HashMap<>();
            map.put("timestamp", timestamp.toString());
            map.put("sign", sign);
            map.put("msg_type", "text");

            HashMap<String, String> content = new HashMap<>();
            content.put("text", text);
            map.put("content", content);

            HttpEntity<String> requestEntity = new HttpEntity<>(new Gson().toJson(map), headers);
            ResponseEntity<Object> result = restTemplate
                    .exchange(url, HttpMethod.POST, requestEntity, Object.class);
            log.info("larkTalking result={}", new Gson().toJson(result));
        } catch (Throwable t) {
            log.error("sendText error ", t);
        }
    }

    /**
     * 发送富文本消息
     * @param title 标题
     * @param list 富文本段落内容
     *             参考链接：https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/im-v1/message/create_json#45e0953e
     */
    public void sendRichText(String title, List<List<Element>> list) {

        try {
            Long timestamp = System.currentTimeMillis() / 1000;
            String sign = genSign(secret, timestamp.toString());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            Map<String, Object> map = new HashMap<>();
            map.put("timestamp", timestamp.toString());
            map.put("sign", sign);
            map.put("msg_type", "post");

            Map<String, Object> content = new HashMap<>();
            Map<String, Object> post = new HashMap<>();
            Map<String, Object> zh_cn = new HashMap<>();
            zh_cn.put("title", title);
            zh_cn.put("content", list);
            post.put("zh_cn", zh_cn);
            content.put("post", post);
            map.put("content", content);

            HttpEntity<String> requestEntity = new HttpEntity<>(new Gson().toJson(map), headers);
            ResponseEntity<Object> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Object.class);
            log.info("sendRichText result={}", new Gson().toJson(result));
        } catch (Throwable t) {
            log.error("sendRichText error ", t);
        }
    }

    /**
     * 签名认证
     * @param secret
     * @param timestamp
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private static String genSign(String secret, String timestamp)
            throws NoSuchAlgorithmException, InvalidKeyException {

        //把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;

        //使用HmacSHA256算法计算签名
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(new byte[] {});
        return new String(Base64.encodeBase64(signData));
    }

    /**
     * 富文本模型
     * 参考链接：https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/im-v1/message/create_json#45e0953e
     */
    @Data
    public static class Element {
        private String tag;
        private String text;

        public Element(String tag, String text) {
            this.tag = tag;
            this.text = text;
        }
    }
}