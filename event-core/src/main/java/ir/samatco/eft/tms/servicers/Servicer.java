package ir.samatco.eft.tms.servicers;

import ir.samatco.eft.tms.service.Exchangeable;

/**
 * Created by Bamdad Aghili on 2/22/14.
 */
public interface Servicer {
    public int getCurrentState();

    public void setCurrentState(int currentState);

    public Exchangeable handleMessage(Exchangeable input) throws TmsException;

    public void finish() throws TmsException;
}
