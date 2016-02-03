package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Created by Bamdad Aghili on 2/17/14.
 */
public class ConfigParameter extends AbstractEntity {
    private Long version;
    private String configName;
    private String configValue;
    private String availability;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public static String getEntityName() {
        return "ConfigParameter";
    }


    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        availability = ConstraintService.createString("availability", paramMap, dbSqlSession, update)
                .lengthBetween(100, null).notNull().getField();
        version = ConstraintService.createLong("version", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        configName = ConstraintService.createString("configName", paramMap, dbSqlSession, update)
                .lengthBetween(50, null).notNull().getField();
        configValue = ConstraintService.createString("configValue", paramMap, dbSqlSession, update)
                .lengthBetween(255, null).notNull().getField();


    }


    @Override
    public String toString() {
        return "id=" + id + " , version=" + version + " , configName=" + configName + " , configValue=" + configValue + " , availability=" + availability;
    }
}
