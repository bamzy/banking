package ir.samatco.eft.tms.xml;


import ir.samatco.eft.tms.service.Exchangeable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Bamdad Aghili on 5/7/2014.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "error")
public class ErrorMessage implements Exchangeable {

    @XmlAttribute
    private String errorCommand;
    @XmlAttribute
    private String errorCode;
    @XmlAttribute
    private String errorType;

    public String getErrorCommand() {
        return errorCommand;
    }

    public void setErrorCommand(String errorCommand) {
        this.errorCommand = errorCommand;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

}
