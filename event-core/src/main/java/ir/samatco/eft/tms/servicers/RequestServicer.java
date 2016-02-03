package ir.samatco.eft.tms.servicers;

import ir.samatco.eft.tms.entity.Changes;
import ir.samatco.eft.tms.entity.CharValue;
import ir.samatco.eft.tms.entity.IntegerValue;
import ir.samatco.eft.tms.entity.Request;
import ir.samatco.eft.tms.service.Exchangeable;
import ir.samatco.eft.tms.xml.Command;
import ir.samatco.eft.tms.xml.Entry;
import ir.samatco.eft.tms.xml.Response;

import java.util.Date;
import java.util.List;

/**
 * Created by Bamdad Aghili on 2/22/14.
 */
public class RequestServicer extends AbstractServicer {
    @Override
    protected Exchangeable innerHandleMessage(Exchangeable input) throws TmsException {
        System.out.print("Handled by [RequestServicer] in State:" + currentState + " Received Message Was:\n");
        Exchangeable returnValue = null;
        switch (currentState) {
            case START_STATE:
                Command command = (Command) input;
                System.out.println(command);
                if (!"Request".equalsIgnoreCase(command.getAction())) {
                    return null;
                }
                try {
                    operationType = "REQUEST";
                    for (Entry entry : command.getEntrys()) {
                        if ("RequestType".equalsIgnoreCase(entry.getKey())) {
                            Request request = new Request();
                            request.setStatus("Pending");
                            request.setDescription("no comment");
                            request.setType(entry.getValue());
                            request.setTerminalNumber(associatedTerminal.getTerminalNumber());
                            request.setDate(System.currentTimeMillis());
                            try {
                                request.setId(dbSqlSession.getMaxId(Request.class) + 1);
                            }catch(NullPointerException e){
                                request.setId(1L);
                            }


                            dbSqlSession.insert(request);
                            Changes changes = new Changes();
                            changes.setRecordId(request.getId());
                            changes.setSecurity_activeUserPath(request.getTerminalNumber().toString());
                            changes.setRecordId(request.getId());
                            changes.setTableName(Request.class.getSimpleName());
                            changes.setActionType("INSERT");
                            changes.setDescription(request.toString());
                            changes.setTimeStamp(new Date().getTime());
                            dbSqlSession.insert(changes);
                        }
                    }
                    Command managementCommand = new Command();
                    managementCommand.setAction("management");
                    if (isBanned()) {
                        managementCommand.getSets().add(createBanSet());
                        currentState++;
                        System.out.println(managementCommand);
                        returnValue = managementCommand;
                        break;
                    }
                    currentState++;
                    returnValue = managementCommand;


                } catch (Exception ex) {
                    TmsException tmsEx = new TmsException("ERROR: Problem with inserting  Pos request.\n");
                    tmsEx.setDetailedMessage(ex.getMessage());
                    throw tmsEx;
                }


                break;
            case 1:
                Response response = (Response) input;
                System.out.println(response);
                if ("Management".equalsIgnoreCase(response.getCommandName()) && "ACK".equalsIgnoreCase(response.getAction())) {
                    Command command2 = new Command();
                    command2.setAction("report");
                    command2.setEntrys(createReportEntries());
                    currentState++;
                    returnValue = command2;
                } else {
                    returnValue = null;
                }
                break;
            case 2:
                try {
                    Response response1 = (Response) input;
                    System.out.println(response1);

                    if ("Report".equalsIgnoreCase(response1.getCommandName()) && "ACK".equalsIgnoreCase(response1.getAction())) {
                        List<Entry> commandList1 = response1.getEntrys();
                        for (Entry entry : commandList1) {
                            if (!"Numerical".equalsIgnoreCase(entry.getType())) {
                                CharValue charValue = new CharValue();
                                charValue.setName(entry.getKey());
                                charValue.setValue(entry.getValue());
                                charValue.setAccessId(access.getId());
                                dbSqlSession.insert(charValue);
                            } else {
                                IntegerValue integerValue = new IntegerValue();
                                integerValue.setName(entry.getKey());
                                integerValue.setValue(Long.parseLong(entry.getValue()));
                                integerValue.setAccessId(access.getId());
                                dbSqlSession.insert(integerValue);
                            }
                            // insert report into access table
                        }
                        currentState = GO_AWAY_STATE;

                    } else {
                        returnValue = null;
                    }
                } catch (Exception ex) {
                    TmsException tmsEx = new TmsException("ERROR: Problem with inserting  charValue/CharValue.\n");
                    tmsEx.setDetailedMessage(ex.getMessage());
                    throw tmsEx;
                }

                break;

            default:
                return returnValue;
        }

        return returnValue;
    }
}
