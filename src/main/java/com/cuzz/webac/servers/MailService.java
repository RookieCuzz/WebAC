package com.cuzz.webac.servers;

import com.cuzz.webac.utils.constants.RedisConstants;
import com.cuzz.webac.utils.SpawnCheckNumber;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@PropertySource(value= "classpath:application.yml",encoding = "utf-8")
public class MailService {

    @Value("${fromEmail}")
    private String fromEmail;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private RedisService redisService;

    /**
     * 发送验证码给用户
     *
     * @param email 用户邮箱
     * @return 用户验证码
     */
    public SpawnCheckNumber sendCheckNumber(String email) throws Exception {
        // 生成验证码
        SpawnCheckNumber checkNumber = new SpawnCheckNumber();

        // 创建 MimeMessage
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);

        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("银月谷验证码");

        // 将验证码替换到 HTML 模板中
        String content = "<html>\n" +
                "<meta charset=\"utf-8\">\n" +
                "<body>\n" +
                "<div id=\"contentDiv\" style=\"position:relative;font-size:14px;height:auto;padding:15px 15px 10px 15px;z-index:1;zoom:1;line-height:1.7;\" class=\"body\">\n" +
                "    <div id=\"qm_con_body\">\n" +
                "        <div id=\"mailContentContainer\" class=\"qmbox qm_con_body_content qqmail_webmail_only\" style=\"opacity: 1;\">\n" +
                "            <style type=\"text/css\">\n" +
                "                .qmbox h1, .qmbox h2, .qmbox h3 {\n" +
                "                    color: #e11e1e;\n" +
                "                }\n" +
                "                .qmbox p {\n" +
                "                    padding: 0;\n" +
                "                    margin: 0;\n" +
                "                    color: #333;\n" +
                "                    font-size: 16px;\n" +
                "                }\n" +
                "                .qmbox hr {\n" +
                "                    background-color: #d9d9d9;\n" +
                "                    border: none;\n" +
                "                    height: 1px;\n" +
                "                }\n" +
                "                .qmbox .eo-link {\n" +
                "                    color: #0576b9;\n" +
                "                    text-decoration: none;\n" +
                "                    cursor: pointer;\n" +
                "                }\n" +
                "                .qmbox .eo-link:hover {\n" +
                "                    color: #3498db;\n" +
                "                }\n" +
                "                .qmbox .eo-link:hover {\n" +
                "                    text-decoration: underline;\n" +
                "                }\n" +
                "                .qmbox .eo-p-link {\n" +
                "                    display: block;\n" +
                "                    margin-top: 20px;\n" +
                "                    color: #009cff;\n" +
                "                    text-decoration: underline;\n" +
                "                }\n" +
                "                .qmbox .p-intro {\n" +
                "                    padding: 30px;\n" +
                "                }\n" +
                "                .qmbox .p-code {\n" +
                "                    padding: 0 30px 0 30px;\n" +
                "                }\n" +
                "                .qmbox .p-news {\n" +
                "                    padding: 0px 30px 30px 30px;\n" +
                "                }\n" +
                "            </style>\n" +
                "            <div style=\"max-width:800px;padding-bottom:10px;margin:20px auto 0 auto;\">\n" +
                "                <table cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#fff2e8;border-collapse: collapse; border:1px solid #e5e5e5;box-shadow: 0 10px 15px rgba(0, 0, 0, 0.05);text-align: left;width: 100%;font-size: 14px;border-spacing: 0;\">\n" +
                "                    <tbody>\n" +
                "                    <tr style=\"background-color: #f8f8f8;\">\n" +
                "                        <td>\n" +
                "                            <img style=\"padding: 15px 0 15px 30px;width:50px\" src=\"https://img.picui.cn/free/2024/10/07/6703b0924f8c6.png\">\n" +
                "                            <span style=\"font-size: 20px;position:relative;top:-5 mail/sendmail/sendApx\">银月谷验证码</span>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td class=\"p-intro\">\n" +
                "                            <h1 style=\"font-size: 26px; font-weight: bold;\">验证您的验证码</h1>\n" +
                "                            <p style=\"line-height:1.75em;\">欢迎您加入 银月谷。</p>\n" +
                "                            <p style=\"line-height:1.75em;\">若非您本人操作，请忽视此信息。</p>\n" +
                "                            <p style=\"line-height:1.75em;\">以下是您的邮箱验证码，请将它输入到 银月谷 的验证码输入框中:</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td class=\"p-code\">\n" +
                "                            <p style=\"color: #253858;text-align:center;line-height:1.75em;background-color: #f2f2f2;min-width: 200px;margin: 0 auto;font-size: 28px;border-radius: 5px;border: 1px solid #d9d9d9;font-weight: bold;\">\n" +
                "                                " + checkNumber.getNumber() + " <!-- 插入验证码 -->\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td class=\"p-intro\">\n" +
                "                            <p style=\"line-height:1.75em;\">此邮件包含您的一些 银月谷 账号信息，请不要回复或转发它，以免带来信息泄露风险。</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td class=\"p-intro\">\n" +
                "                            <hr>\n" +
                "                            <p style=\"text-align: center;line-height:1.75em;\">银月谷</p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    </tbody>\n" +
                "                </table>\n" +
                "            </div>\n" +
                "            <style type=\"text/css\">\n" +
                "                .qmbox style, .qmbox script, .qmbox head, .qmbox link, .qmbox meta { display: none !important; }\n" +
                "            </style>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <style>\n" +
                "        #mailContentContainer .txt {height:auto;}\n" +
                "    </style>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";

        // 设置邮件内容为 HTML
        message.setText(content, true);

        // 打印验证码日志
//        log.info("发送的邮件验证码为: {}", checkNumber.getNumber());

        // 将验证码存入 Redis，设置过期时间
        redisService.setCacheObject(RedisConstants.EMAIL_CODE + email, checkNumber.getNumber());
        redisService.expire(RedisConstants.EMAIL_CODE + email, RedisConstants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        // 发送邮件
        javaMailSender.send(mimeMessage);

        return checkNumber;
    }
}
