package com.incapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class MailService {
	@Autowired
	JavaMailSender javaMailSender;
	
	public boolean simpleMail(String email,String sub,String message) {
		try {
			SimpleMailMessage mailMessage=new SimpleMailMessage();
			mailMessage.setTo(email);
			mailMessage.setSubject(sub);
			mailMessage.setText(message);
			javaMailSender.send(mailMessage);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public boolean htmlMail(String email,String sub, String message) {
		try {
			MimeMessage mailMessage=javaMailSender.createMimeMessage();
			boolean multiPart=true;
			MimeMessageHelper helper=new MimeMessageHelper(mailMessage,multiPart);
			helper.setTo(email);
			helper.setSubject(sub);
			message="<h2 style='background-color:blue;padding:20px;color:white;'>Message heading</h2>"
					+ "<p style='background-color:yellow; padding:20px;'>"+message+"</p>"
					+"<img src='https://images.pexels.com/photos/17366252/pexels-photo-17366252.jpeg' height='50px'>";
			helper.setText("text/html",message);
		
			javaMailSender.send(mailMessage);
			return true;
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
