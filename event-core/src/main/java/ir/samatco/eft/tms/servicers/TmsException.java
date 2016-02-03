package ir.samatco.eft.tms.servicers;

/**
 * Created by Bamdad Aghili on 3/2/14.
 */
public class TmsException extends Exception {
    public String getDetailedMessage() {
        return detailedMessage;
    }

    public void setDetailedMessage(String detailedMessage) {
        this.detailedMessage = detailedMessage;
    }

    private String detailedMessage;

    public TmsException(String message) {
        super(message);
    }
}
