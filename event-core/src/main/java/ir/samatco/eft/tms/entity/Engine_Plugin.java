package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Created by Bamdad Aghili on 4/23/2014.
 */
public class Engine_Plugin extends AbstractEntity {
    private Long engineId;
    private Long pluginId;

    public static String getEntityName() {
        return "Engine_Plugin";
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public Long getPluginId() {
        return pluginId;
    }

    public void setPluginId(Long pluginId) {
        this.pluginId = pluginId;
    }


    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        engineId = ConstraintService.createLong("engineId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(Engine.class).getField();
        pluginId = ConstraintService.createLong("pluginId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(Plugin.class).getField();
    }


}
