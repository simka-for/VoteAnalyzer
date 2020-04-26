import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class XMLHandler extends DefaultHandler {

    StringBuilder insertQuery;
    private final HashMap<Voter,Byte> voters;
    private Voter voter;

    int maxSize = 3_000_000;

    public XMLHandler() {
        voters = new HashMap<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

       try{
           if (voters.size() >= maxSize) {
               writeToDb();
               voters.clear();
           }
           if (qName.equals("voter") && voter == null) {

               voter = new Voter(attributes.getValue("name"), attributes.getValue("birthDay"));
           }
           if (qName.equals("visit") && voter != null) {

               int count = voters.getOrDefault(voter,(byte) 0);
               count++;
               voters.put(voter,(byte) count);

           }
       }catch (Exception ex){
           ex.printStackTrace();
       }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {

        if (qName.equals("voter")) {
            voter = null;
        }
    }


    public void writeToDb () {

        try {
            insertQuery = new StringBuilder();
            for (Voter voter : voters.keySet()) {

                String name = voter.getName();
                String birthDate = voter.getBirthDay();
                int count = voters.get(voter);
                insertQuery.append(insertQuery.length() == 0 ? "" : ",")
                        .append("('").append(name).append("','").append(birthDate).append("',").append(count).append(")");
            }
            DBConnection.executeMultiInsert(insertQuery);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
