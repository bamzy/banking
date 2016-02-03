package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/26/14 3:03 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class Request extends AbstractEntity {
    private Long terminalNumber;
    private Long timeStamp;
    private String type;
    private String description;
    private String status;
    private String operatorPath;
    private String security_activeUserPath;

    public String getSecurity_activeUserPath() {
        return security_activeUserPath;
    }

    public void setSecurity_activeUserPath(String security_activeUserPath) {
        this.security_activeUserPath = security_activeUserPath;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getOperatorPath() {
        return operatorPath;
    }

    public void setOperatorPath(String operatorPath) {
        this.operatorPath = operatorPath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static String getEntityName() {
        return "Request";
    }

    public Long getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(Long terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    public Long getDate() {
        return timeStamp;
    }

    public void setDate(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        terminalNumber = ConstraintService.createLong("terminalNumber", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).foreignKey(Terminal.class).notNull().getField();
        timeStamp = ConstraintService.createLong("timeStamp", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).foreignKey(Access.class).notNull().getField();
        type = ConstraintService.createString("type", paramMap, dbSqlSession, update)
                .lengthBetween(100, null).notNull().getField();
        description = ConstraintService.createString("description", paramMap, dbSqlSession, update)
                .lengthBetween(1000, null).getField();
        status = ConstraintService.createString("status", paramMap, dbSqlSession, update)
                .lengthBetween(1000, null).getField();
        operatorPath = ConstraintService.createString("operatorPath", paramMap, dbSqlSession, update)
                .lengthBetween(255, null).notNull().getField();
//        if (!tmsService.verifyLdapUser(security_activeUserPath, operatorPath))
//            throw new RuntimeException("Permission Denied For This User!");

    }


    @Override
    public String toString() {
        return "terminalNumber=" + terminalNumber + " , timeStamp=" + timeStamp + " , type=" + type + " , description=" + description + " , status=" + status;
    }
}
