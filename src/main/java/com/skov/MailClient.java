package com.skov;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailClient {

    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage generateMailMessage;

    public static void main(String[] args) {
        generateAndSendEmail("test test og atter test");
    }

    public static void generateAndSendEmail(String txt) {

        try {

            System.out.println("Ill send a mail with the following content: " + txt);

            mailServerProperties = System.getProperties();
            mailServerProperties.put("mail.smtp.port", "587");
            mailServerProperties.put("mail.smtp.auth", "true");
            mailServerProperties.put("mail.smtp.starttls.enable", "true");
            //System.out.println("Mail Server Properties have been setup successfully..");

            getMailSession = Session.getDefaultInstance(mailServerProperties, null);
            generateMailMessage = new MimeMessage(getMailSession);

            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("allan.noergaard.skov@gmail.com"));
            generateMailMessage.setSubject("YrBike note-to-self...");
            generateMailMessage.setContent(txt + "<br><br>https://www.yr.no/place/Denmark/North_Jutland/Aalborg/", "text/html");

            Transport transport = getMailSession.getTransport("smtp");

            transport.connect("smtp.gmail.com", "allan.noergaard.skov@gmail.com", "suxligxvuvjlrpdr\n");
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();

            System.out.println("mail send. Thanks");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
}
