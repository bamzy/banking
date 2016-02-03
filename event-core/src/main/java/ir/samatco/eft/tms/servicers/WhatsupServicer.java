package ir.samatco.eft.tms.servicers;

import ir.samatco.eft.tms.entity.CharValue;
import ir.samatco.eft.tms.entity.Engine;
import ir.samatco.eft.tms.entity.IntegerValue;
import ir.samatco.eft.tms.entity.TerminalGroup;
import ir.samatco.eft.tms.service.Exchangeable;
import ir.samatco.eft.tms.xml.*;
import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bamdad Aghili on 2/15/14.
 */
public class WhatsupServicer extends AbstractServicer {

    private Engine associatedEngine;

    @Override
    protected Exchangeable innerHandleMessage(Exchangeable input) throws TmsException {
        System.out.print("Handled by [WhatsUpServicer] in State:" + currentState + " Received Message Was:\n");
        Exchangeable returnValue = null;
        switch (currentState) {
            case START_STATE:
                Command command = (Command) input;
                System.out.println(command);
                if ("WhatsUp".equalsIgnoreCase(command.getAction())) {
                    try {
                        operationType = "WHATSUP";
//                        if ((long) dbSqlSession.customSelectOne("Access.checkNightCode", nightCode) == 0) {
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
//                        }
                    } catch (Exception ex) {
                        TmsException tmsEx = new TmsException("ERROR: Problem with check of NightCode.\n");
                        tmsEx.setDetailedMessage(ex.getMessage());
                        throw tmsEx;
                    }
                } else {
                    access.setStatus(access.getStatus() + "Authentication Failed");
                    returnValue = null;
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
                    access.setTimeStamp(System.currentTimeMillis());
                } else {
                    returnValue = null;
                }

                break;
            case 2:
                Response response1 = (Response) input;
                System.out.println(response1);
                try {
                    if ("Report".equalsIgnoreCase(response1.getCommandName()) && "ACK".equalsIgnoreCase(response1.getAction())) {
                        List<Entry> commandList1 = response1.getEntrys();
                        for (Entry entry : commandList1) {
                            if ("Numerical".equalsIgnoreCase(entry.getType())) {
                                IntegerValue integerValue = new IntegerValue();
                                integerValue.setName(entry.getKey());
                                integerValue.setValue(Long.parseLong(entry.getValue()));
                                integerValue.setAccessId(access.getId());
                                dbSqlSession.insert(integerValue);

                            } else {
                                CharValue charValue = new CharValue();
                                charValue.setName(entry.getKey());
                                charValue.setValue(entry.getValue());
                                charValue.setAccessId(access.getId());
                                dbSqlSession.insert(charValue);
                            }
                        }

                        Command command3 = new Command();
                        List<Entry> entries = new ArrayList<>();
                        Long associatedEngineId = findAssociatedEngineId(associatedTerminal.getTerminalNumber());
                        Engine associatedDbEngine = dbSqlSession.select(Engine.class.getSimpleName(), associatedEngineId);
                        if (associatedDbEngine.getVersion() == getPosCurrentEngineVersion() || associatedEngineId == 0 || associatedEngineId == null) {
                            Command command5 = new Command();
                            command5.setAction("swStatus");
                            currentState = currentState + 2;
                            returnValue = command5;
                        } else {
                            setDbCurrentEngineId(associatedEngineId);
                            associatedEngine = dbSqlSession.select(Engine.class.getSimpleName(), getDbCurrentEngineId());
                            Entry engineIdEntry = new Entry();
                            engineIdEntry.setKey("engineVersion");
                            engineIdEntry.setValue(associatedEngine.getVersion() + "");
                            entries.add(engineIdEntry);
                            Entry engineNameEntry = new Entry();
                            engineNameEntry.setKey("engineName");
                            engineNameEntry.setValue(associatedEngine.getName());
                            entries.add(engineNameEntry);
                            Entry engineDataLengthEntry = new Entry();
                            engineDataLengthEntry.setKey("engineDataLength");
                            engineDataLengthEntry.setValue(associatedEngine.getDataLength() + "");
                            entries.add(engineDataLengthEntry);
                            Entry checksumEntry = new Entry();
                            checksumEntry.setKey("checksum");
                            checksumEntry.setValue(Base64.encodeBase64URLSafeString(associatedEngine.getChecksum()));
                            entries.add(checksumEntry);
                            command3.setEntrys(entries);
                            command3.setAction("updateEngine");
                            currentState++;
                            returnValue = command3;
                        }
                    } else {

                        returnValue = null;
                    }
                } catch (Exception ex) {
                    TmsException tmsEx = new TmsException("ERROR:" + ex.getMessage() + "\n");
                    tmsEx.setDetailedMessage(ex.getMessage());
                    throw tmsEx;
                }
                break;
            case 3:
                Response response4 = (Response) input;
                int startByte, endByte;
                try {
                    if ("UpdateEngine".equalsIgnoreCase(response4.getCommandName()) && "ACK".equalsIgnoreCase(response4.getAction())) {
                        if (response4.getParts().size() != 0) {
                            startByte = response4.getParts().get(0).getFrom();
                            endByte = response4.getParts().get(0).getTo();
                            RawData rawEngineData = new RawData((Engine) dbSqlSession.select(Engine.class.getSimpleName(), getDbCurrentEngineId()), startByte, endByte);
                            returnValue = rawEngineData;
                        } else if ("TransmissionStatus".equalsIgnoreCase(response4.getEntrys().get(0).getKey()) && "Finished".equalsIgnoreCase(response4.getEntrys().get(0).getValue())) {
                            Command command3 = new Command();
                            command3.setAction("swStatus");
                            currentState++;
                            returnValue = command3;

                        }
                    } else
                        returnValue = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    TmsException tmsEx = new TmsException("ERROR: Problem with inserting  charValue/CharValue.\n");
                    tmsEx.setDetailedMessage(ex.getMessage());
                    throw tmsEx;
                }
                break;
            case 4:
                try {
                    Response response2 = (Response) input;
                    System.out.println(response2);
                    if ("SwStatus".equalsIgnoreCase(response2.getCommandName()) && "ACK".equalsIgnoreCase(response2.getAction())) {
                        List<XmlPlugin> finalPluginList = getRequiredPlugins(response2.getXmlPlugins());
                        if (!finalPluginList.isEmpty()) {
                            Command command4 = new Command();
                            command4.setAction("updatePlugin");
                            command4.setXmlPlugins(finalPluginList);
                            currentState++;
                            returnValue = command4;
                        } else
                            currentState = GO_AWAY_STATE;

                    } else {
                        returnValue = null;
                    }
                } catch (Exception ex) {
                    TmsException tmsEx = new TmsException("ERROR: Problem with Add report to DB check TerminalNumber.\n");
                    ex.printStackTrace();
                    tmsEx.setDetailedMessage(ex.getMessage());
                    throw tmsEx;
                }
                break;
            case 5:
                Response response3 = (Response) input;
                System.out.println(response3);
                if ("UpdatePlugin".equalsIgnoreCase(response3.getCommandName()) && "ACK".equalsIgnoreCase(response3.getAction())) {
                    currentState = GO_AWAY_STATE;
                } else {
                    returnValue = null;
                }
                break;
            default:
                return returnValue;
        }

        return returnValue;
    }

    private Long findAssociatedEngineId(Long terminalId) {
        Long groupId, engineId = null, tempGroupId;
        groupId = (long) dbSqlSession.customSelectOne("Terminal.fetchTerminalGroupId", terminalId);
        while (engineId == null) {
            engineId = ((TerminalGroup) dbSqlSession.select(TerminalGroup.class.getSimpleName(), groupId)).getEngineId();
            groupId = ((TerminalGroup) dbSqlSession.select(TerminalGroup.class.getSimpleName(), groupId)).getGroupId();
            if (null == groupId)
                break;

        }
        return engineId;
    }

    private List<XmlPlugin> getRequiredPlugins(List<XmlPlugin> alreadyHasPlugins) {
        List<XmlPlugin> finalPluginList = new ArrayList<XmlPlugin>();
        List<XmlPlugin> finalShouldHave = new ArrayList<XmlPlugin>();

        Long posGroupId = (long) dbSqlSession.customSelectOne("Terminal.fetchTerminalGroupId", associatedTerminal.getTerminalNumber());

        while (posGroupId != null) {
            List<XmlPlugin> shouldHavePlugins = dbSqlSession.customSelect("PluginTemplate.fetchPosPlugins", posGroupId);
            //if there is no same plugin with lower version then add shouldhave to finalshouldhave
            boolean exist;
            for (XmlPlugin shouldHavePlugin : shouldHavePlugins) {
                exist = false;
                for (XmlPlugin finalxmlPlugin : finalShouldHave) {
                    if (shouldHavePlugin.getTitle().equalsIgnoreCase(finalxmlPlugin.getTitle())) {
                        exist = true;
                    }
                }
                if (!exist) {
                    finalShouldHave.add(shouldHavePlugin);
                }
            }
            posGroupId = fetchParentGroupId(posGroupId);

        }


        for (XmlPlugin shouldHavePlugin : finalShouldHave) {
            boolean existflag = false;
            for (XmlPlugin alreadyHasPlugin : alreadyHasPlugins) {
                if (shouldHavePlugin.getTitle().equalsIgnoreCase(alreadyHasPlugin.getTitle()) && shouldHavePlugin.getVersion().equalsIgnoreCase(alreadyHasPlugin.getVersion())) {
                    existflag = true;
                }
            }
            if (!existflag) {
                shouldHavePlugin.replacePluginValuesInConte();
                shouldHavePlugin.setAction("add");
                finalPluginList.add(shouldHavePlugin);
            }
        }
        for (XmlPlugin alreadyHasPlugin : alreadyHasPlugins) {
            boolean existflag = false;
            for (XmlPlugin finalPlugin : finalShouldHave) {
                if (alreadyHasPlugin.getTitle().equalsIgnoreCase(finalPlugin.getTitle()) && alreadyHasPlugin.getVersion().equalsIgnoreCase(finalPlugin.getVersion())) {
                    existflag = true;
                }
            }
            if (!existflag) {
                alreadyHasPlugin.setAction("delete");
                alreadyHasPlugin.setContent("");
                finalPluginList.add(alreadyHasPlugin);
            }
        }
        List<XmlPlugin> reversedFinalPluginList = new ArrayList<XmlPlugin>();
        for (int i = finalPluginList.size() - 1; i >= 0; i--) {
            reversedFinalPluginList.add(finalPluginList.get(i));
        }
        return reversedFinalPluginList;

    }

    private Long fetchParentGroupId(Long groupId) {

        long parentGroupValue = (long) dbSqlSession.customSelectOne("TerminalGroup.fetchParentGroupId", groupId);
        if (groupId == parentGroupValue) {
            return null;
        } else {
            return parentGroupValue;
        }
    }

}
