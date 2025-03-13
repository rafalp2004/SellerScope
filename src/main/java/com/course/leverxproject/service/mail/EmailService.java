package com.course.leverxproject.service.mail;

public interface EmailService {
    void sendMail(String subject,String text, String address, String from);
}
