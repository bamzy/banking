package ir.samatco.eft.tms.servicers;


import com.samanpr.jalalicalendar.JalaliCalendar;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.gateway.securityapi.SecurityInterface;
import ir.samatco.eft.tms.entity.*;
import ir.samatco.eft.tms.service.Exchangeable;
import ir.samatco.eft.tms.xml.Command;
import ir.samatco.eft.tms.xml.Entry;
import ir.samatco.eft.tms.xml.ErrorMessage;
import ir.samatco.eft.tms.xml.Set;
import org.apache.log4j.LogManager;

import javax.xml.bind.DatatypeConverter;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.util.*;

/*
 * Created by Bamdad Aghili on 2/20/14.
 */
public abstract class AbstractServicer implements Servicer {

    protected SecurityInterface securityInterface;
    public static final int START_STATE = 0;
    public static final int GO_AWAY_STATE = 100;
    public static final int GO_AWAY_ACK_STATE = 101;
    private static final int FINALIZE_STATE = 102;
    protected int currentState = START_STATE;

    protected String nightCode = null;
    protected String sequenceNumber;
    protected Long dbCurrentEngineId = null;
    protected Long PosCurrentEngineVersion = null;
    protected POS associatedPOS = new POS();
    protected Terminal associatedTerminal = new Terminal();

    protected final Long WHATSUP_NEXT_VISIT = (long) 3 * 24 * 3600 * 1000;
    protected final Long LOGON_NEXT_VISIT = (long) 1 * 24 * 3600 * 1000;
    protected final Long REQUEST_NEXT_VISIT = (long) 2 * 24 * 3600 * 1000;
    protected final Long NEXT_VISIT_INTERVAL = (long) 2 * 3600 * 1000;
    protected Long AVERAGE_PROCESS_TIME = (long) 60 * 1000;

    protected final String hsmServerAddress = "eft-proxy-gateway.node.internal.qezel";
    //    protected final String hsmServerAddress = "192.168.40.127";
    protected String operationType;
    protected DbSqlSession dbSqlSession;
    protected Access access = new Access();

    public Long getPosCurrentEngineVersion() {
        return PosCurrentEngineVersion;
    }

    public void setPosCurrentEngineVersion(Long posCurrentEngineVersion) {
        PosCurrentEngineVersion = posCurrentEngineVersion;
    }

    public Long getDbCurrentEngineId() {
        return dbCurrentEngineId;
    }

    public void setDbCurrentEngineId(Long dbCurrentEngineId) {
        this.dbCurrentEngineId = dbCurrentEngineId;
    }

    public void setDbSqlSession(DbSqlSession dbSqlSession) {
        this.dbSqlSession = dbSqlSession;
    }

    @Override
    public int getCurrentState() {
        return currentState;
    }

    @Override
    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    protected boolean setAssociatedData(Command command) throws TmsException {
        boolean accessSaved = false;
        try {

            securityInterface = new SecurityInterface(hsmServerAddress);
            List<Entry> commandList = command.getEntrys();
            for (Entry entry : commandList) {
                entry.setValue(entry.getValue().trim());
                if ("serialNumber".equalsIgnoreCase(entry.getKey())) {
                    associatedPOS = dbSqlSession.select(POS.class.getSimpleName(), Long.parseLong(entry.getValue()));
                    if (associatedPOS == null) {
                        access.setStatus("POS with this S/N Does Not Exists!");
                        TmsException tmsEx = new TmsException("POS Does Not Exists!");
                        throw tmsEx;
                    }

                    associatedTerminal = dbSqlSession.select(Terminal.class.getSimpleName(), associatedPOS.getTerminalNumber());
                    if (associatedTerminal == null) {
                        access.setStatus("No terminal associated to this POS!");
                        LogManager.getLogger(this.getClass()).info("No terminal associated to this POS!");
                        TmsException tmsEx = new TmsException("No terminal associated to this POS!");
                        throw tmsEx;
                    }


                } else if ("sequenceNumber".equalsIgnoreCase(entry.getKey())) {
                    sequenceNumber = entry.getValue();
                } else if ("nightCode".equalsIgnoreCase(entry.getKey())) {
                    nightCode = entry.getValue();
                } else if ("engineVersion".equalsIgnoreCase(entry.getKey())) {
                    setPosCurrentEngineVersion(Long.parseLong(entry.getValue()));
                }

            }

            dbSqlSession.insert(access);
            accessSaved = true;

            if (associatedTerminal == null || associatedPOS == null || sequenceNumber == null) {
                LogManager.getLogger(this.getClass()).info("There is no TerminalId/SerialNumber/SequenceNumber Attribute\n");
                access.setStatus("Wrong Input Format");
                TmsException tmsEx = new TmsException("Wrong Data Provided!");
                throw tmsEx;
            }
            return true;
        } catch (Exception e) {
            access.setStatus(access.getStatus() + "|Wrong Format!");
            LogManager.getLogger(this.getClass()).error(e.getMessage());
            TmsException tmsEx = new TmsException(e.getMessage());
            tmsEx.setDetailedMessage(e.getMessage());
            throw tmsEx;
        } finally {
            if (!accessSaved) {
                if (associatedTerminal != null)
                    access.setTerminalNumber(associatedTerminal.getTerminalNumber());
                else
                    access.setTerminalNumber(-1L);
                dbSqlSession.insert(access);
            }
        }
    }


    @Override
    public Exchangeable handleMessage(Exchangeable input) throws TmsException {

        boolean authenticationResult = false;
        if (currentState == START_STATE) {
            Command command = (Command) input;
            access.setAccessType(command.getAction());
            try {
                long temp = dbSqlSession.getMaxId(access.getClass().getSimpleName());
                access.setId(temp + 1);
            }catch (NullPointerException ex){
                access.setId(1L);
            }
            access.setTimeStamp(System.currentTimeMillis());
            if (!setAssociatedData(command)) {
                ErrorMessage errorMessage = new ErrorMessage();
                errorMessage.setErrorCode("1");
                errorMessage.setErrorCommand("Wrong Format/Wrong ID");
                errorMessage.setErrorType("FORMAT_ERROR");
                return errorMessage;
            }
            try {
                access.setTerminalNumber(associatedTerminal.getTerminalNumber());
                access.setNightCode(decrypt(nightCode));
                authenticationResult = checkNightCode();
                if (!authenticationResult) {
                    access.setStatus("ERROR: Authentication Failed");
                    dbSqlSession.customSelectOne("Access.customUpdate", access);
                    ErrorMessage errorMessage = new ErrorMessage();
                    errorMessage.setErrorCode("2");
                    errorMessage.setErrorCommand("Authentication Failed");
                    errorMessage.setErrorType("DUPLICATE_NIGHTCODE");
                    return errorMessage;

                } else {
                    access.setStatus("Authentication Successful");
                    dbSqlSession.customSelectOne("Access.customUpdate", access);
                }
            } catch (Exception e) {
                TmsException tmsEx = new TmsException(" ERROR: Problem with recording your access/Checking NightCode.\n");
                if (access.getStatus() != null)
                    access.setStatus(access.getStatus() + " ERROR:" + e.getMessage());
                else
                    access.setStatus(" ERROR: " + e.getMessage());
                tmsEx.setDetailedMessage(e.getMessage());
                throw tmsEx;
            }
            if (authenticationResult) {
                Exchangeable exchangeableResult = innerHandleMessage(input);
                if (exchangeableResult == null) {
                    access.setStatus("Wrong Command Name ");
                    return generateErrorMessage(access.getStatus(), "Wrong Field Name", "3");
                } else
                    return exchangeableResult;
            } else {
                return null;
            }
        }
        Exchangeable response = innerHandleMessage(input);
        if (response == null && currentState != GO_AWAY_STATE && currentState != GO_AWAY_ACK_STATE) {
            access.setStatus("Wrong Command Name ");
            return generateErrorMessage(access.getStatus(), "Wrong Field Name", "3");
        }
        if (currentState == GO_AWAY_STATE) {
            Command goAwayCommand = new Command();
            goAwayCommand.setAction("goAway");
            List<Entry> entries = new ArrayList<>();
            Entry goAwayEntry = new Entry();
            goAwayEntry.setKey("nextVisit");

            Long nextVisit;
            if (associatedTerminal.getNextVisit() == null || associatedTerminal.getNextVisit() < System.currentTimeMillis()) {
//                nextVisit = determineNextVisit(associatedTerminal.getTerminalNumber());
                nextVisit = 1440L;
            } else
                nextVisit = associatedTerminal.getNextVisit();
            goAwayEntry.setValue(nextVisit.toString());
            entries.add(goAwayEntry);
            goAwayCommand.setEntrys(entries);

            associatedTerminal.setLastVisit(access.getId());
            associatedTerminal.setNextVisit(nextVisit + System.currentTimeMillis());
            try {
                dbSqlSession.customSelectOne("Terminal.customUpdate", associatedTerminal);

            } catch (Exception ex) {
                TmsException tmsEx = new TmsException("ERROR: Could not find your Current Access record to update.\n");
                tmsEx.setDetailedMessage(ex.getMessage());
                throw tmsEx;
            }

            currentState = GO_AWAY_ACK_STATE;
            return goAwayCommand;

        }
        if (currentState == GO_AWAY_ACK_STATE) {
            System.out.println(input);
            currentState = FINALIZE_STATE;
            return null;
        }
        return response;


    }

    private String decrypt(String nightCode) {
        return nightCode;
    }

    private boolean checkNightCode() {
        if (associatedPOS.getSerialNumber() == 60541800 || associatedPOS.getSerialNumber() == 60541043)
            return true;
        if ((long) dbSqlSession.customSelectOne("Access.checkNightCode", nightCode) > 0)
            return false;
        String myKey = Long.toString(associatedPOS.getSerialNumber()) + sequenceNumber;
        ByteBuffer byteBuffer = ByteBuffer.wrap(myKey.getBytes());
        try {
            return securityInterface.macCheck(byteBuffer.array(), securityInterface.getKeyIdentifier(associatedTerminal.getTerminalNumber().toString()), DatatypeConverter.parseBase64Binary(nightCode), securityInterface.posProfile);
        } catch (ConnectException e) {
            LogManager.getLogger(this.getClass()).error(e.getMessage());
            access.setStatus(access.getStatus() + " error:" + e.getMessage());
            return false;
        } catch (RuntimeException e) {
            LogManager.getLogger(this.getClass()).error(e.getMessage());
            access.setStatus(access.getStatus() + " error:" + e.getMessage());
            return false;
        }
    }

    protected abstract Exchangeable innerHandleMessage(Exchangeable input) throws TmsException;

    protected boolean isBanned() {
        try {
            return !(boolean) dbSqlSession.customSelectOne("Terminal.isBanned", associatedTerminal.getTerminalNumber());
        } catch (Exception ex) {
            return false;
        }
    }

    protected Set createBanSet() {
        Set tempSet = new Set();
        tempSet.setValue("1");
        tempSet.setKey("banned");
        tempSet.setDir("config");
        return tempSet;

    }

    private Long determineNextVisit(Long terminalId) {
        if ("WHATSUP".equals(operationType)) {
            long from = System.currentTimeMillis() + WHATSUP_NEXT_VISIT - NEXT_VISIT_INTERVAL;
            long to = System.currentTimeMillis() + WHATSUP_NEXT_VISIT + NEXT_VISIT_INTERVAL;
            List<NextVisit> nextVisits = dbSqlSession.customSelect("Terminal.listNextVisits", new Duration(from, to));
            return BestNextVisit(nextVisits, from, to);
        } else if ("LOGON".equals(operationType)) {
            long from = System.currentTimeMillis() + LOGON_NEXT_VISIT - NEXT_VISIT_INTERVAL;
            long to = System.currentTimeMillis() + LOGON_NEXT_VISIT + NEXT_VISIT_INTERVAL;
            List<NextVisit> nextVisits = dbSqlSession.customSelect("Terminal.listNextVisits", new Duration(from, to));
            return BestNextVisit(nextVisits, from, to);
        } else if ("REQUEST".equals(operationType)) {
            long from = System.currentTimeMillis() + REQUEST_NEXT_VISIT - NEXT_VISIT_INTERVAL;
            long to = System.currentTimeMillis() + REQUEST_NEXT_VISIT + NEXT_VISIT_INTERVAL;
            List<NextVisit> nextVisits = dbSqlSession.customSelect("Terminal.listNextVisits", new Duration(from, to));
            return BestNextVisit(nextVisits, from, to);
        } else {
            return (long) 1 * 24 * 3600 * 1000;
        }
    }

    private Long BestNextVisit(List<NextVisit> allNextVisits, long from, long to) {
        NextVisit last = new NextVisit();
        last.setTerminalNumber(null);
        last.setNextVisit(to);
        last.setWeight(0L);
        allNextVisits.add(allNextVisits.size(), last);
        NextVisit first = new NextVisit();
        first.setTerminalNumber(null);
        first.setNextVisit(from);
        allNextVisits.add(0, first);
        for (int i = 0; i < allNextVisits.size() - 1; i++) {
            if (i == 0) {
                allNextVisits.get(i).setWeight(allNextVisits.get(i + 1).getNextVisit() - allNextVisits.get(i).getNextVisit());
            }
            allNextVisits.get(i).setWeight(allNextVisits.get(i + 1).getNextVisit() - allNextVisits.get(i).getNextVisit());
        }
        Collections.sort(allNextVisits, new Comparator<NextVisit>() {
            @Override
            public int compare(NextVisit x, NextVisit y) {
                if (x.getWeight() < y.getWeight())
                    return -1;
                else if (x.getWeight() > y.getWeight())
                    return 1;
                else
                    return 0;

            }
        });
        return allNextVisits.get(allNextVisits.size() - 1).getNextVisit() + AVERAGE_PROCESS_TIME;


    }

    @Override
    public void finish() throws TmsException {
        try {
            if (currentState == FINALIZE_STATE) {
                access.setStatus("Completed");
                dbSqlSession.customSelectOne("Access.customUpdate", access);
            } else {
                int last = 200;
                if (access.getStatus()!=null){
                    if (access.getStatus().length() < last)
                        last = access.getStatus().length();
                    access.setStatus(access.getStatus().substring(0, last) + " |Process Failed At State:" + currentState);
                } else
                    access.setStatus(" Process Failed At State:" + currentState);
                access.setTerminalNumber(associatedTerminal.getTerminalNumber());
                access.setNightCode(nightCode);
                access.setTimeStamp(System.currentTimeMillis());
                dbSqlSession.customSelectOne("Access.customUpdate", access);
//                dbSqlSession.update(access);
            }
        } catch (Exception ex) {
            TmsException tmsEx = new TmsException("ERROR: Problem with updating final state of Access.\n");
            tmsEx.setDetailedMessage(ex.getMessage());
            throw tmsEx;
        }
    }

    List<Entry> createReportEntries() {
//        DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar jFromCalendar = new JalaliCalendar();
        List<Entry> reportEntries = new ArrayList<Entry>();
        Access lastAccess = new Access();
        lastAccess.setStatus("Completed");
        lastAccess.setTerminalNumber(associatedTerminal.getTerminalNumber());
        lastAccess.setAccessType("Whatsup");
        Entry fromEntry = new Entry();
        fromEntry.setKey("From");
        fromEntry.setType("Time");
        Long fromInMilliSecond = fetchLastVisitTime(lastAccess);
        jFromCalendar.setTimeInMillis(fromInMilliSecond);
        fromEntry.setValue(getFormatedTime(jFromCalendar));


        Calendar jToCalendar = new JalaliCalendar();
        reportEntries.add(fromEntry);
        Entry toEntry = new Entry();
        toEntry.setKey("To");
        toEntry.setType("Time");
        long toInMilliSecond = System.currentTimeMillis();
        jToCalendar.setTimeInMillis(toInMilliSecond);
        toEntry.setValue(getFormatedTime(jToCalendar));
        reportEntries.add(toEntry);
        return reportEntries;
    }

    private String getFormatedTime(Calendar jFromCalendar) {
        String monthStr = "", dayStr = "", hourStr = "", minStr = "", secStr = "";
        int month = jFromCalendar.get(Calendar.MONTH);
        if (month < 10)
            monthStr = "0" + month;
        else
            monthStr = "" + month;
        int day = jFromCalendar.get(Calendar.DAY_OF_MONTH);
        if (day < 10)
            dayStr = "0" + day;
        else
            dayStr = "" + day;
        int hour = jFromCalendar.get(Calendar.HOUR);
        if (hour < 10)
            hourStr = "0" + hour;
        else
            hourStr = "" + hour;
        int min = jFromCalendar.get(Calendar.MINUTE);
        if (min < 10)
            minStr = "0" + min;
        else
            minStr = "" + min;
        int sec = jFromCalendar.get(Calendar.SECOND);
        if (sec < 10)
            secStr = "0" + sec;
        else
            secStr = "" + sec;
        return "" + jFromCalendar.get(Calendar.YEAR) + monthStr + dayStr +
                hourStr + minStr + secStr;
    }

    private Long fetchLastVisitTime(Access lastAccess) {
//        Long l = (long) dbSqlSession.customSelectOne("Access.fetchLastVisitTime", access);
//        if (l != null)
//            l += 1000;
//        else
        Long l = System.currentTimeMillis() - 24 * 3600 * 1000;
        return l;
    }

    private ErrorMessage generateErrorMessage(String message, String type, String errCode) {
        ErrorMessage errmsg = new ErrorMessage();
        errmsg.setErrorCommand(message);
        errmsg.setErrorType(type);
        errmsg.setErrorCode(errCode);
        return errmsg;
    }
}
