package ir.samatco.eft.tms.entity;


import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by bamdad on 6/8/14.
 */
public class AssignHistory extends AbstractEntity {
    private Long requestId;
    private Long timeStamp;
    private String security_activeUserPath;
    private String operatorPath;
    private String description;
    private String mobileNumber;

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }


    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public static String getEntityName() {
        return "AssignHistory";
    }


    public String getSecurity_activeUserPath() {
        return security_activeUserPath;
    }

    public void setSecurity_activeUserPath(String security_activeUserPath) {
        this.security_activeUserPath = security_activeUserPath;
    }

    public String getOperatorPath() {
        return operatorPath;
    }

    public void setOperatorPath(String operatorPath) {
        this.operatorPath = operatorPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRequestId() {

        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }


    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws IOException, NoSuchAlgorithmException, FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        timeStamp = ConstraintService.createLong("timeStamp", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        requestId = ConstraintService.createLong("requestId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        operatorPath = ConstraintService.createString("operatorPath", paramMap, dbSqlSession, update)
                .lengthBetween(255, null).notNull().getField();
        security_activeUserPath = ConstraintService.createString("security_activeUserPath", paramMap, dbSqlSession, update)
                .lengthBetween(255, null).notNull().getField();
        description = ConstraintService.createString("description", paramMap, dbSqlSession, update)
                .lengthBetween(255, null).notNull().getField();


    }


    @Override
    public String toString() {
        return "id=" + id + " , operatorPath=" + operatorPath + " , requestId=" + requestId + " , security_activeUserPath=" + security_activeUserPath;
    }
}
