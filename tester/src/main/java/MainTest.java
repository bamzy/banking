import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Created by Bamdad Aghili on 2/22/14.
 */

public class MainTest {

    public static final int SIZE_LEN = 6;
    private static int Min = 100000, Max = 999999;

    public static void main(String[] args) throws Exception {
//        Sender sender = new Sender("192.168.10.43", 2700);
//        Sender sender = new Sender("192.168.10.70", 2700);
        Sender sender = new Sender("qezel-server", 2700);
        sender.connect();
//        LogonTester(sender);
//
//        sender.disconnect();
//        sender.connect();
//        failLogonTester(sender);
//        sender.disconnect();
//
//        sender.connect();
//        WhatsUpTester(sender);
//        WhatsUpwithEngineTester(sender);


//        sender.disconnect();


//        sender.connect();
        RequestTester(sender);
        sender.disconnect();
    }

    private static boolean WhatsUpTester(Sender sender) throws Exception {
        try {

            System.out.println("------------------------------------WhatsUpTester------------------------------------");
            sendString(sender, "<command action=\"Whatsup\">\n" +
                    "\n" +
                    "\t<entry key=\"nightCode\" value=\"" + Min + (int) (Math.random() * ((Max - Min) + 1)) + "\" />\n" +
                    "\t<entry key=\"serialNumber\" value=\"60541804\" />\n" +
                    "\t<entry key=\"sequenceNumber\" value=\"111111\" />\n" +
                    "\t<entry key=\"engineVersion\" value=\"0\" />\n" +
                    "</command>\n");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"management\" />");
            System.out.println("TMS Response: " + sender.read());

            sendString(sender, "<response action=\"ACK\" commandName=\"Report\">\n" +
                    "\t<entry key=\"paper\"    value=\"10\"  type=\"Numerical\"/>\n" +
                    "\t<entry key=\"glee\"     value=\"10\"  type=\"Numerical\"/>\n" +
                    "\t<entry key=\"alsdklkj\" value=\"10\"  type=\"Numerical\"/>\n" +
                    "\t<entry key=\"engineId\"     value=\"3\"   type=\"Numerical\"/>\n" + "</response>\n");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"SwStatus\">\n" +
//                    "\t<xmlplugin name=\"payment\" title=\"ov6koQ==\" version=\"1.0.0\"/>\n" +
//                    "\t<xmlplugin name=\"charge\" title=\"pqSRqg==\" version=\"1.0.0\"/>\n" +
                    "\t<xmlplugin name=\"charge\" title=\"ABCD\" version=\"1.0.0\"/>\n" +
                    "\t<xmlplugin name=\"charge\" title=\"EFGH\" version=\"1.0.0\"/>\n" +
                    "</response>\n");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"UpdatePlugin\" />");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"GoAway\" />");
            System.out.println("------------------------------------------------------------------------");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return false;
        }
    }


    private static boolean WhatsUpwithEngineTester(Sender sender) throws Exception {
        try {
            System.out.println("----------------------------------------------WhatsUpTester------------------------------------------------");
            sendString(sender, "<command action=\"Whatsup\">\n" +
                    "\n" +
                    "\t<entry key=\"nightCode\" value=\"" + Min + (int) (Math.random() * ((Max - Min) + 1)) + "\" />\n" +
                    "\t<entry key=\"sequenceNumber\" value=\"111111\" />\n" +
                    "\t<entry key=\"serialNumber\" value=\"60541804\" />\n" +
                    "\n" +
                    "</command>\n");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"management\" />");
            System.out.println("TMS Response: " + sender.read());

            sendString(sender, "<response action=\"ACK\" commandName=\"Report\">\n" +
                    "\t<entry key=\"paper\"    value=\"10\"  type=\"Numerical\"/>\n" +
                    "\t<entry key=\"glee\"     value=\"10\"  type=\"Numerical\"/>\n" +
                    "\t<entry key=\"alsdklkj\" value=\"10\"  type=\"Numerical\"/>\n" +
                    "\t<entry key=\"engineId\"     value=\"3\"   type=\"Numerical\"/>\n" + "</response>\n");

            System.out.println("TMS Response: " + sender.read());
            int i = 1, j = 0;
            FileChannel channel = new FileOutputStream(new File("out.AGN"), true).getChannel();
            while (i <= 120264) {
                j = i + 1000;
                sendString(sender, "<response action=\"ACK\" commandName=\"updateEngine\">\n" +
                        "\t<part from=\"" + i + "\"  to=\"" + j + "\"  />\n" +
                        "</response>\n");
                byte[] readBytes = sender.readBytes();
                System.out.println("TMS Responsed " + readBytes.length + "Bytes");
                ByteBuffer buffer = ByteBuffer.wrap(readBytes);
                byte[] size = new byte[6];
                buffer.get(size, 0, 6);
                System.out.println("Size:" + Arrays.toString(size));
                buffer.get(size, 0, 6);
                System.out.println("Size:" + Arrays.toString(size));
                channel.write(buffer);
                i = j + 1;
            }
            channel.close();

            sendString(sender, "<response action=\"ACK\" commandName=\"updateEngine\">\n" +
                    "\t<entry key=\"transmissionStatus\" type=\"String\" value=\"finished\"/>" +
                    "</response>\n");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"SwStatus\">\n" +
//                    "\t<xmlplugin name=\"payment\" title=\"ov6koQ==\" version=\"1.0.0\"/>\n" +
//                    "\t<xmlplugin name=\"charge\" title=\"pqSRqg==\" version=\"1.0.0\"/>\n" +
                    "\t<xmlplugin name=\"charge\" title=\"ABCD\" version=\"1.0.0\"/>\n" +
                    "\t<xmlplugin name=\"charge\" title=\"EFGH\" version=\"1.0.0\"/>\n" +
                    "</response>\n");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"UpdatePlugin\" />");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"GoAway\" />");
            System.out.println("------------------------------------------------------------------------");
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            return false;
        }
    }

    private static void sendString(Sender sender, String str) throws Exception {
        String lenStr = Integer.toString(str.length());
        while (lenStr.length() < SIZE_LEN) {
            lenStr = "0" + lenStr;
        }
        sender.write(lenStr);
        sender.write(str);
    }

    private static boolean LogonTester(Sender sender) throws Exception {
        try {
            System.out.println("------------------------------------LogonTester------------------------------------");
            sendString(sender, "<command action=\"Logon\">\n" +
                    "\n" +
                    "\t<entry key=\"nightCode\" value=\"" + Min + (int) (Math.random() * ((Max - Min) + 1)) + "\" />\n" +
                    "\t<entry key=\"serialNumber\" value=\"60541043\" />\n" +
                    "\t<entry key=\"sequenceNumber\" value=\"111111\" />\n" +
                    "\t<entry key=\"engineVersion\" value=\"0\" />\n" +
                    "\n" +
                    "</command>\n");
            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"Management\" />");
            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"GoAway\" />");
            System.out.println("------------------------------------------------------------------------");
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }

    }

    private static boolean failLogonTester(Sender sender) throws Exception {
        try {
            System.out.println("------------------------------------LogonTester------------------------------------");
            sendString(sender, "<command action=\"Logon\">\n" +
                    "\n" +
                    "\t<entry key=\"nightCode\"fasfsf  value=\"124\" />\n" +
                    "\t<entry key=\"serialNumber\" value=\"1312\" />\n" +
                    "\n" +
                    "</command>\n");
            sender.disconnect();
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }

    }

    private static boolean RequestTester(Sender sender) throws Exception {
        try {
            System.out.println("------------------------------------RequestTester------------------------------------");
            sendString(sender, "<command action=\"Request\">\n" +
                    "\n" +
                    "\t<entry key=\"nightCode\" type=\"Numerical\" value=\"" + Min + (int) (Math.random() * ((Max - Min) + 1)) + "\" />\n" +
                    "\t<entry key=\"serialNumber\" value=\"60541800\" />\n" +
                    "\t<entry key=\"sequenceNumber\" value=\"111111\" />\n" +
                    "\t<entry key=\"requestType\" type=\"Numerical\" value=\"paperRoll\" />\n" +
                    "</command>\n");

            System.out.println("TMS Response: " + sender.read());
            sendString(sender, "<response action=\"ACK\" commandName=\"management\" />");
            System.out.println("TMS Response: " + sender.read());

            sendString(sender, "<response action=\"ACK\" commandName=\"Report\">\n" +
                    "\t<Entry key=\"paper\" value=\"10\"/>\n" +
                    "\t<Entry key=\"disk\" value=\"3\"/>\n" + "</response>\n");

            System.out.println("TMS Response: " + sender.read());

            sendString(sender, "<response action=\"ACK\" commandName=\"GoAway\" />");
            System.out.println("------------------------------------------------------------------------");
            return true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
