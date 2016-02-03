package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * some fields of terminal such as detailed status of terminal, application version are removed.
 * they should be stored in TMS. we just persists here fields that are required in financial reports
 *
 * @author meysam
 */
public class Terminal extends AbstractEntity {
    private Long terminalNumber;
    private String status;
    private boolean active;
    private Long startupDate;
    private String description;
    private Integer maxPasswordLength;
    private Long lastVisit;
    private Long nextVisit;
    private Long terminalTypeId;
    private Long merchantId;
    private Long groupId;


    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public static String getEntityName() {
        return "Terminal";
    }

    public Long getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(Long terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getStartupDate() {
        return startupDate;
    }

    public void setStartupDate(Long startupDate) {
        this.startupDate = startupDate;
    }


    public Integer getMaxPasswordLength() {
        return maxPasswordLength;
    }

    public void setMaxPasswordLength(Integer maxPasswordLength) {
        this.maxPasswordLength = maxPasswordLength;
    }

    public Long getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Long lastVisit) {
        this.lastVisit = lastVisit;
    }

    public Long getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(Long nextVisit) {
        this.nextVisit = nextVisit;
    }

    public Long getTerminalTypeId() {
        return terminalTypeId;
    }

    public void setTerminalTypeId(Long terminalTypeId) {
        this.terminalTypeId = terminalTypeId;
    }

    @Override
    public String toString() {
        return "Terminal id=" + id + " , terminalNumber=" + terminalNumber + " , status=" + status + " , startupDate=" + startupDate + " , description=" + description + " , lastVisit=" + lastVisit + " , nextVisit=" + nextVisit + " , terminalTypeId=" + terminalTypeId + " , merchantId=" + merchantId;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("terminalNumber", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        terminalNumber = ConstraintService.createLong("terminalNumber", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        description = ConstraintService.createString("description", paramMap, dbSqlSession, update)
                .lengthBetween(1000, null).getField();
        status = ConstraintService.createString("status", paramMap, dbSqlSession, update)
                .lengthBetween(100, null).getField();
        active = ConstraintService.createBoolean("active", paramMap, dbSqlSession, update)
                .lengthBetween(100, null).getField();
        startupDate = ConstraintService.createLong("startupDate", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        lastVisit = ConstraintService.createLong("lastVisit", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).getField();
        nextVisit = ConstraintService.createLong("nextVisit", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).getField();
        maxPasswordLength = ConstraintService.createInteger("maxPasswordLength", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1).notNull().getField();
        merchantId = ConstraintService.createLong("merchantId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        groupId = ConstraintService.createLong("groupId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(TerminalGroup.class).getField();
        terminalTypeId = ConstraintService.createLong("terminalTypeId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(TerminalType.class).getField();
    }
}
