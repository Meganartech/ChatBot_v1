package com.VsmartEngine.Chatbot.MailConfiguration;

import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender mailSender; // 🔧 Add this field

    public boolean sendEmail(String to, String subject, String body) {
        try {
            JavaMailSenderImpl mailSenderImpl = new JavaMailSenderImpl();
            mailSenderImpl.setHost("smtp.hostinger.com");
            mailSenderImpl.setPort(587); // ✅ Use 587 for STARTTLS
            mailSenderImpl.setUsername("raji@meganartech.com");
            mailSenderImpl.setPassword("$Raji@2000");

//            mailSenderImpl.setPort(587);

            Properties props = mailSenderImpl.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true"); // optional, but helpful
            props.put("mail.debug", "true");


            this.mailSender = mailSenderImpl;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("raji@meganartech.com");

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    }

