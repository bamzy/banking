package ir.samatco.eft.tms.service;

import ir.samatco.eft.tms.codec.RawBytesCodec;
import ir.samatco.eft.tms.codec.XMLCodec;
import ir.samatco.eft.tms.servicers.Servicer;
import ir.samatco.eft.tms.servicers.TmsException;
import ir.samatco.eft.tms.xml.Command;
import ir.samatco.eft.tms.xml.ErrorMessage;
import ir.samatco.eft.tms.xml.RawData;
import org.springframework.beans.factory.ObjectFactory;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bamdad Aghili on 2/8/14.
 */
public class ProjectRepository {
    ObjectFactory<Servicer> logonServicerObjectFactory;
    ObjectFactory<Servicer> whatsupServicerObjectFactory;
    ObjectFactory<Servicer> requestServicerObjectFactory;
    private XMLCodec xmlCodec;
    private RawBytesCodec rawCodec;

    public void setLogonServicerObjectFactory(ObjectFactory<Servicer> logonServicerObjectFactory) {
        this.logonServicerObjectFactory = logonServicerObjectFactory;
    }

    public void setRequestServicerObjectFactory(ObjectFactory<Servicer> requestServicerObjectFactory) {
        this.requestServicerObjectFactory = requestServicerObjectFactory;
    }

    public void setWhatsupServicerObjectFactory(ObjectFactory<Servicer> whatsupServicerObjectFactory) {
        this.whatsupServicerObjectFactory = whatsupServicerObjectFactory;
    }


    private Map<Integer, Servicer> serviceMapper = new HashMap<>();


    public ReturnResponse getResponse(ByteBuffer input, int id) {
        ReturnResponse returnResponse = new ReturnResponse();
        try {
            Exchangeable decode = xmlCodec.decode(input);
            Exchangeable response = null;
            response = getServicer(id, decode).handleMessage(decode);
            if (response == null)
                return null;
            else if (response instanceof RawData) {
                returnResponse.setByteBuffer(rawCodec.encode(response));
                return returnResponse;
            } else {
                if (response instanceof ErrorMessage)
                    returnResponse.setError(true);
                returnResponse.setByteBuffer(xmlCodec.encode(response));
                return returnResponse;
            }
        } catch (TmsException e) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setErrorCommand(e.getMessage());
            returnResponse.setByteBuffer(xmlCodec.encode(errorMessage));
            returnResponse.setError(true);
            return returnResponse;
        } catch (Exception ex) {
            ErrorMessage errorMessage = new ErrorMessage();
            errorMessage.setErrorCommand(ex.getMessage());
            returnResponse.setByteBuffer(xmlCodec.encode(errorMessage));
            returnResponse.setError(true);
            return returnResponse;
        }
    }

    private Servicer getServicer(int id, Exchangeable exchangeable) {

        if (serviceMapper.containsKey(id))
            return serviceMapper.get(id);
        else {
            // Servicer servicer = new Servicer();
            Command tempCommand = (Command) exchangeable;
            if ("Logon".equalsIgnoreCase(tempCommand.getAction())) {
                serviceMapper.put(id, logonServicerObjectFactory.getObject());
            } else if ("Whatsup".equalsIgnoreCase(tempCommand.getAction())) {
                serviceMapper.put(id, whatsupServicerObjectFactory.getObject());
            } else if ("Request".equalsIgnoreCase(tempCommand.getAction())) {
                serviceMapper.put(id, requestServicerObjectFactory.getObject());
            } else throw new IllegalArgumentException("Wrong Action Command (" + tempCommand.getAction() + ")");
            return serviceMapper.get(id);
        }


    }

    public void deleteServicer(int id) throws TmsException {
        if (serviceMapper.containsKey(id)) {
            serviceMapper.get(id).finish();
            serviceMapper.remove(id);
        }
    }

    public void setXmlCodec(XMLCodec xmlCodec) {
        this.xmlCodec = xmlCodec;
    }

    public XMLCodec getXmlCodec() {
        return xmlCodec;
    }

    public void setRawCodec(RawBytesCodec rawCodec) {
        this.rawCodec = rawCodec;
    }

    public RawBytesCodec getRawCodec() {
        return rawCodec;
    }
}
