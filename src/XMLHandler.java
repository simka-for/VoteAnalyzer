import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class XMLHandler extends DefaultHandler {

    private static final SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    private HashMap<Voter, Integer> voterCounts;
    private Voter voter;

    public XMLHandler() {
        voterCounts = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        try {
            if (qName.equals("voter")) {
                Date birthDay = birthDayFormat.parse(attributes.getValue("birthDay"));
                voter = new Voter(attributes.getValue("name"), birthDay);

            } else if (qName.equals("visit") && voter != null) {

                int count = voterCounts.getOrDefault(voter, 0);
                voterCounts.put(voter, count + 1);
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equals("voter")) {
            voter = null;
        }
    }

    public void printDuplicatedVoters() {

        StringBuilder builder = new StringBuilder();

        for (Voter voter : voterCounts.keySet()) {
            int count = voterCounts.get(voter);
            if (count > 1) {
                builder.append(voter.toString()).append(" - ").append(count).append("\n");
            }
        }
        System.out.println(builder.toString());
    }
}
