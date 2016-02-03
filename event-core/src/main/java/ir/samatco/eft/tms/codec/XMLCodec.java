package ir.samatco.eft.tms.codec;

import ir.samatco.eft.tms.service.Exchangeable;
import ir.samatco.eft.tms.xml.Command;
import ir.samatco.eft.tms.xml.ErrorMessage;
import ir.samatco.eft.tms.xml.Response;
import org.apache.log4j.LogManager;
import org.elasticsearch.common.io.stream.ByteBufferStreamInput;

import javax.xml.bind.*;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Bamdad Aghili on 2/8/14.
 */
public class XMLCodec implements Codec {
    private int sizeLength = 6;

    @Override
    public Exchangeable decode(ByteBuffer input) {
        ByteBuffer tmp = null;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Command.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            tmp = input.duplicate();
            Command command = (Command) jaxbUnmarshaller.unmarshal(new ByteBufferStreamInput(input));
            return command;
        } catch (JAXBException e) {
            if (e instanceof UnmarshalException) {
                try {
                    JAXBContext jaxbContext = JAXBContext.newInstance(Response.class);
                    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    Response response = (Response) jaxbUnmarshaller.unmarshal(new ByteBufferStreamInput(tmp));
                    return response;
                } catch (JAXBException e1) {
                    System.out.println("\nERROR: There is error with input XML format\n");
                    LogManager.getLogger(this.getClass()).error("ERROR: There is error with input XML format Message:" + e.getMessage());
                }
            }
        }
        return null;
    }


    @Override
    public ByteBuffer encode(Exchangeable input) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            JAXBContext jaxbContext = null;
            if (input instanceof Command)
                jaxbContext = JAXBContext.newInstance(Command.class);
            else if (input instanceof Response)
                jaxbContext = JAXBContext.newInstance(Response.class);
            else if (input instanceof ErrorMessage)
                jaxbContext = JAXBContext.newInstance(ErrorMessage.class);
            else return null;
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(input, result);
            String tempResult = result.toString();
            tempResult = tempResult.replace("&lt;", "<").replace("&gt;", ">").replace("&#x9;", "    ").replace("&#xD;&#xA;", "\n").replace("&quot;", "'");
            return ByteBuffer.wrap(tempResult.getBytes());
        } catch (JAXBException e) {
            System.out.println("\nERROR: There is error with input XML format\n");
            LogManager.getLogger(this.getClass()).error("ERROR: There is error with input XML format Message:" + e.getMessage());
            return null;
        }
    }
}
