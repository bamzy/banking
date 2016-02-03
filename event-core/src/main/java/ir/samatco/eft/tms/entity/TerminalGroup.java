package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/26/14 3:06 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class TerminalGroup extends AbstractEntity {
    //    private Long id;
    private String name;
    private Long engineId;
    private Long groupId;
    private boolean active;
    private String dailyMessage;

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }


    public static String getEntityName() {
        return "TerminalGroup";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDailyMessage() {
        return dailyMessage;
    }

    public void setDailyMessage(String dailyMessage) {
        this.dailyMessage = dailyMessage;
    }

    @Override
    public String toString() {
        return "id=" + id + " , name=" + name + " , engineId=" + engineId + " ,groupId=" + groupId + " ,Active=" + active + " , DailyMessage=" + dailyMessage;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        name = ConstraintService.createString("name", paramMap, dbSqlSession, update)
                .lengthBetween(100, null).notNull().getField();
        active = ConstraintService.createBoolean("active", paramMap, dbSqlSession, update)
                .getField();
        dailyMessage = ConstraintService.createString("dailyMessage", paramMap, dbSqlSession, update)
                .lengthBetween(1000, null).getField();
        groupId = ConstraintService.createLong("groupId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(TerminalGroup.class).getField();
        engineId = ConstraintService.createLong("engineId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(Engine.class).getField();
    }

}
