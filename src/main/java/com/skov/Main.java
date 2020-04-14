package com.skov;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {

    static StringBuffer mailTxt = new StringBuffer("Welcome<br>\n");


    public static void main(String[] args) throws Exception {
        doExecute();
        //getRainSumHoursOfInterest(getYrDataXml("https://www.yr.no/place/Denmark/North_Jutland/Aalborg/forecast_hour_by_hour.xml"));
    }


    private static Document getYrDataXml(String url) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new URL(url).openStream());
    }

    public static String doExecute() {
        mailTxt = new StringBuffer("Welcome! LocalDateTime.now()=" + LocalDateTime.now() + "<br><br>\n");

        try {

            int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK); //starter med søndag = 1
            mailTxt.append("dayOfWeek=" + dayOfWeek + "<br>\n<br>\n<br>\n");

            Double rainSumHoursOfInterest =
                getRainSumHoursOfInterest(getYrDataXml("https://www.yr.no/place/Denmark/North_Jutland/Aalborg/forecast_hour_by_hour.xml"));

            mailTxt.append("<br>\n<br>\n<br>\n");


            if (dayOfWeek == 6 || dayOfWeek == 7) { //ikke fredag eller lørdag
                mailTxt.append("dude, it is weekend, dont go to work tomorrow! RainSum=" + rainSumHoursOfInterest + "<br>\n");
                return mailTxt.toString();
            }

            if (rainSumHoursOfInterest > 1.0) {
                mailTxt.append(
                    "Dude, it is raining more than 1.0mm, dont take the bike. rainSumHoursOfInterest=" + rainSumHoursOfInterest + "<br>\n");
                return mailTxt.toString();
            }

            mailTxt.append(
                "Dude, weather is fine next 2 days, rain is below 1.0mm. TAKE THE BIKE!! rainSumHoursOfInterest=" + rainSumHoursOfInterest +
                    "<br>\n");

        } catch (Exception e) {
            mailTxt.append("Exception!! " + e + "<br>\n");
        } finally {
            if (mailTxt != null) {
                MailClient.generateAndSendEmail(mailTxt);
            }
            mailTxt.append("Bye<br>\n<br>\n<br>\n<br>\n");
            System.out.println(mailTxt);
            return mailTxt.toString();

        }
    }

    public static Double getRainSumHoursOfInterest(Document document) throws Exception {
        int count = 0;
        Double rainSum = 0.0;
        Double rainSumHoursOfInterest = 0.0;
        int numberOfTimesToGet = 30;

        NodeList nodeList = document.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {

            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                if (node.getNodeName().equals("time")) {
                    String periodFrom = node.getAttributes().item(0).getNodeValue();
                    //String periodTo = node.getAttributes().item(1).getNodeValue();
                    Double rainInPeriod = Double.valueOf(
                        ((DeferredElementNSImpl) node).getElementsByTagName("precipitation").item(0).getAttributes().item(0).getNodeValue());
                    rainSum += rainInPeriod;

                    if (periodFrom.matches(".*T05.*|.*T06.*|.*T07.*|.*T08.*|.*T09.*|.*T13.*|.*T14.*|.*T15.*|.*T16.*|.*T17.*|.*T18.*")) {
                        rainSumHoursOfInterest += rainInPeriod;
                    }
                    mailTxt.append(
                        "count=" + count++ + ", periodFrom=" + periodFrom + ", rainInPeriod=" + rainInPeriod + ", rainSum=" + rainSum +
                            ", rainSumHoursOfInterest=" + rainSumHoursOfInterest + "<br>\n");

                    if (count > numberOfTimesToGet) {
                        break;
                    }

                }
            }
        }
        mailTxt.append("I have fetched data from YR from number of hours= " + count + ", rainSum=" + rainSum + ", rainSumHoursOfInterest=" +
            rainSumHoursOfInterest + "<br>\n");
        return rainSumHoursOfInterest;
    }
}
