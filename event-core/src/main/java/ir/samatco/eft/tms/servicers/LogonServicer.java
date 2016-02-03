package ir.samatco.eft.tms.servicers;

import ir.samatco.eft.tms.entity.ConfigParameter;
import ir.samatco.eft.tms.service.Exchangeable;
import ir.samatco.eft.tms.xml.Command;
import ir.samatco.eft.tms.xml.Response;
import ir.samatco.eft.tms.xml.Set;

/**
 * Created by Bamdad Aghili on 2/9/14.
 */
public class LogonServicer extends AbstractServicer {
    long longSerialNumber = 0;


    @Override
    protected Exchangeable innerHandleMessage(Exchangeable input) throws TmsException {
        Exchangeable returnValue = null;

        System.out.print("Handled by [LogonServicer] in  State:" + currentState + " Received Message Was:\n");
        switch (currentState) {
            case START_STATE:
                Command command = (Command) input;
                System.out.println(command);
                if ("Logon".equalsIgnoreCase(command.getAction())) {

                    try {
                        operationType = "LOGON";
                        Command managementCommand = new Command();
                        managementCommand.setAction("management");
                        if (isBanned()) {
                            managementCommand.getSets().add(createBanSet());
                            currentState++;
                            System.out.println(managementCommand);
                            returnValue = managementCommand;
                            break;
                        }
                        Set switchIp = new Set();
                        switchIp.setKey("switchip");
                        switchIp.setDir("config");
                        switchIp.setValue(((ConfigParameter) dbSqlSession.customSelectOne("ConfigParameter.fetchByName", "switchip")).getConfigValue());
                        managementCommand.getSets().add(switchIp);


                        Set switchPort = new Set();
                        switchPort.setKey("switchport");
                        switchPort.setDir("config");
                        switchPort.setValue(((ConfigParameter) dbSqlSession.customSelectOne("ConfigParameter.fetchByName", "switchport")).getConfigValue());
                        managementCommand.getSets().add(switchPort);

                        Set terminalNumber = new Set();
                        terminalNumber.setKey("posid");
                        terminalNumber.setDir("config");
                        terminalNumber.setValue(associatedTerminal.getTerminalNumber().toString());
                        managementCommand.getSets().add(terminalNumber);

                        Set merchantId = new Set();
                        merchantId.setKey("merchantid");
                        merchantId.setDir("config");
                        merchantId.setValue(Long.toString((long) dbSqlSession.customSelectOne("POS.getMerchantId", associatedTerminal.getTerminalNumber())));

                        managementCommand.getSets().add(merchantId);
                        currentState++;
                        System.out.println(managementCommand);
                        returnValue = managementCommand;

                    } catch (Exception ex) {
                        //                    throw new TmsException("ERROR: Problem with fetching initial config parameters\n");
                        TmsException tmsEx = new TmsException("\"ERROR: Problem with fetching initial config parameters.\n");
                        tmsEx.setDetailedMessage(ex.getMessage());
                        throw tmsEx;
                    }
                } else
                    returnValue = null;


                break;
            case 1:
                Response response = (Response) input;
                System.out.println(response);

                if ("Management".equalsIgnoreCase(response.getCommandName()) && "ACK".equalsIgnoreCase(response.getAction())) {
                    currentState = GO_AWAY_STATE;
                } else
                    returnValue = null;
                break;

            default:
                return returnValue;
        }


        return returnValue;
    }
}
