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
        generateAndSendEmail(new StringBuffer("test test og atter test"));
    }

    public static StringBuffer generateAndSendEmail(StringBuffer mailTxt) {

        try {

            String subject;
            if (mailTxt.toString().contains("TAKE THE BIKE")) {
                subject = "YrBike: TAKE THE BIKE dude :)";
            } else {
                subject = "YrBike: no need to bicycle to work today dude";
                return mailTxt;
            }

            mailTxt.append("Welcome to MailClient.generateAndSendEmail()<br>\n");

            mailServerProperties = System.getProperties();
            mailServerProperties.put("mail.smtp.port", "587");
            mailServerProperties.put("mail.smtp.auth", "true");
            mailServerProperties.put("mail.smtp.starttls.enable", "true");
            //System.out.println("Mail Server Properties have been setup successfully..");

            getMailSession = Session.getDefaultInstance(mailServerProperties, null);
            generateMailMessage = new MimeMessage(getMailSession);

            generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("allan.noergaard.skov@gmail.com"));
            generateMailMessage.setSubject(subject);
            generateMailMessage.setContent(mailTxt + "<br>\n<br>\nhttps://www.yr.no/place/Denmark/North_Jutland/Aalborg/hour_by_hour.html", "text/html");

            Transport transport = getMailSession.getTransport("smtp");

            transport.connect("smtp.gmail.com", "allan.noergaard.skov@gmail.com", "suxligxvuvjlrpdr\n");
            transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
            transport.close();

            mailTxt.append("mail send. Thanks<br>\n");

        } catch (Exception e) {
            mailTxt.append("Exception sending mail: " + e.toString() + "<br>\n");
        } finally {
            return mailTxt;
        }
    }
}
