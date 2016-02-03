package ir.samatco.eft.tms.entity;


import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/25/14 5:51 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class Access extends AbstractEntity {
    private Long timeStamp;
    private String accessType;
    private Long terminalNumber;
    private String nightCode;
    private String status;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getNightCode() {
        return nightCode;
    }

    public void setNightCode(String nightCode) {
        this.nightCode = nightCode;
    }


    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }


    public Long getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(Long terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    public static String getEntityName() {
        return "Access";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void checkDelete() {
        if (id == null)
            throw new IllegalArgumentException("parameter " + id + "(long) is required");
    }

    @Override
    public String toString() {
        return "id=" + id + " , timeStamp=" + timeStamp + " , accessType=" + accessType + " , status=" + status;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {

        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        timeStamp = ConstraintService.createLong("timeStamp", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();

        terminalNumber = ConstraintService.createLong("terminalNumber", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        accessType = ConstraintService.createString("terminalNumber", paramMap, dbSqlSession, update)
                .lengthBetween(50, null).notNull().getField();
        nightCode = ConstraintService.createString("nightCode", paramMap, dbSqlSession, update)
                .lengthBetween(255, null).notNull().getField();
        status = ConstraintService.createString("status", paramMap, dbSqlSession, update)
                .lengthBetween(255, null).notNull().getField();


    }

}
