package ir.samatco.eft.tms.entity;


import ir.samatco.eft.common.repository.DbSqlSession;

import java.util.Map;

/**
 * User: door
 * Date: 1/7/14
 * Time: 12:03 PM
 */
public class Changes extends AbstractEntity {

    private Long recordId;
    private Long timeStamp;
    private String tableName;
    private String actionType;
    private String description;
    private String security_activeUserPath;

    public String getSecurity_activeUserPath() {
        return security_activeUserPath;
    }

    public void setSecurity_activeUserPath(String security_activeUserPath) {
        this.security_activeUserPath = security_activeUserPath;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }


    public String getActionType() {
        return actionType;
    }

    public static String getEntityName() {
        return "Changes";
    }

    @Override
    public void set(Map<String, String[]> parameterMap, DbSqlSession dbSqlSession) {


    }

    public Long getId() {
        return id;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
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

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public String toString() {
        return "id=" + id + " , timeStamp=" + timeStamp + " , recordId=" + recordId + " , tableName=" + tableName;
    }

}
