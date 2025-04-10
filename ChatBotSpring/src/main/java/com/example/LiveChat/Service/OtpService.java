package com.example.LiveChat.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, OtpData> otpCache = new HashMap<>();

    private static class OtpData {
        String otp;
        LocalDateTime expiryTime;

        OtpData(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    private String generateOtp() {
        Random random = new Random();
        return String.valueOf(100000 + random.nextInt(900000));
    }

    public void sendOtp(String email) {
        String otp = generateOtp();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(2);

        otpCache.put(email, new OtpData(otp, expiryTime));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("LiveChat - Your OTP Code");
            helper.setText("<html><body><h3>Welcome to LiveChat!</h3>"
                           + "<p>Your OTP is: <b>" + otp + "</b></p>"
                           + "<p>This OTP is valid for 2 minutes.</p>"
                           + "<p>Do not share this OTP with anyone.</p></body></html>", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    public boolean isOtpValid(String email, String otp) {
        OtpData otpData = otpCache.get(email);
        if (otpData == null || LocalDateTime.now().isAfter(otpData.expiryTime)) {
            otpCache.remove(email);
            return false;
        }
        return otp.equals(otpData.otp);
    }

    public void resendOtp(String email) {
        sendOtp(email);
    }
}
