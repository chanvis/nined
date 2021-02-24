package com.nined.userservice.mail;

import java.nio.charset.StandardCharsets;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.nined.userservice.constants.UserConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * Email message service using java mail sender API
 * 
 * @author vijay
 *
 */
@Slf4j
@Service
public class EmailService {

    @Value("${ep.email.noreply}")
    private String noReplyEmail;

    @Value("${ep.email.signature}")
    private String emailSignature;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    /**
     * Method responsible to send email
     * <li>if email template html is defined, same will be used</li>
     * 
     * @param mail
     */
    public void sendEmail(Mail mail) {
        if (mail != null) {
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message,
                        MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                        StandardCharsets.UTF_8.name());

                if (mail.getTemplate() != null) {
                    Context context = new Context();
                    if (mail.getModel() != null) {
                        mail.getModel().put(UserConstants.EMAIL_SIGNATURE, emailSignature);
                        context.setVariables(mail.getModel());
                    }
                    String html = templateEngine.process(mail.getTemplate(), context);
                    helper.setText(html, true);
                }

                helper.setTo(mail.getTo());
                helper.setSubject(mail.getSubject());
                helper.setFrom(noReplyEmail);

                emailSender.send(message);
            } catch (Exception e) {
                if (log.isErrorEnabled()) {
                    log.error("Exception occurred while sending email - {}", e);
                }
                throw new RuntimeException(e);
            }
        }
    }
}
