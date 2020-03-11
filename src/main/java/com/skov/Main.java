package com.skov;

import java.net.URL;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {

    private static Document getYrDataXml(String url) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new URL(url).openStream());
    }

    public static void main(String[] args) {
        String mailTxt = null;

        try {
            System.out.println("Welcome!");

            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); //starter med søndag = 1
            System.out.println("dayOfWeek=" + dayOfWeek);

            Double rainSumHoursOfInterest = getRainSumHoursOfInterest(getYrDataXml("https://www.yr.no/place/Denmark/North_Jutland/Aalborg/forecast_hour_by_hour.xml"));

            if (dayOfWeek == 6 || dayOfWeek == 7) { //ikke fredag eller lørdag
                mailTxt = "dude, it is weekend, dont go to work tomorrow! RainSum=" + rainSumHoursOfInterest;
                System.out.println(mailTxt);
                return;
            }

            if (rainSumHoursOfInterest > 1.0) {
                mailTxt  = "Dude, it is raining more than 1.0mm, dont take the bike. rainSumHoursOfInterest=" + rainSumHoursOfInterest;
                System.out.println(mailTxt);
                return;
            }

            mailTxt = "Dude, weather is fine next 2 days, rain is below 1.0mm. TAKE THE BIKE!! rainSumHoursOfInterest=" + rainSumHoursOfInterest;
            System.out.println(mailTxt);

        } catch (Exception e) {
            System.out.println("Exception!! " + e);
        } finally {
            if (mailTxt != null) {
                MailClient.generateAndSendEmail(mailTxt);
            }
            System.out.println("Bye");
        }
    }

    public static Double getRainSumHoursOfInterest(Document document) throws Exception {
        int count = 0;
        Double rainSum = 0.0;
        Double rainSumHoursOfInterest = 0.0;

        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                if (node.getNodeName().equals("time")) {
                    String periodFrom = node.getAttributes().item(0).getNodeValue();
                    String periodTo = node.getAttributes().item(1).getNodeValue();
                    Double rainInPeriod = Double.valueOf(((DeferredElementNSImpl) node).getElementsByTagName("precipitation").item(0).getAttributes().item(0).getNodeValue());
                    rainSum += rainInPeriod;

                    if (periodFrom.matches(".*T05.*|.*T06.*|.*T07.*|.*T08.*|.*T09.*|.*T13.*|.*T14.*|.*T15.*|.*T16.*|.*T17.*|.*T18.*")) {
                        rainSumHoursOfInterest += rainInPeriod;
                    }
                }
            }
        }
        System.out.println("I have fetched data from YR from number of hours= " + count + ", rainSum=" + rainSum + ", rainSumHoursOfInterest=" + rainSumHoursOfInterest);
        return rainSumHoursOfInterest;
    }
}
