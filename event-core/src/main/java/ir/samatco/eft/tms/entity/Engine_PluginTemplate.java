package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;
import ir.samatco.eft.tms.service.exception.WrongOperationException;

import java.util.Map;

/**
 * Created by Bamdad Aghili on 4/20/2014.
 */
public class Engine_PluginTemplate extends AbstractEntity {
    private Long engineId;
    private Long pluginTemplateId;
    private String pluginTemplateName;

    public String getPluginTemplateName() {
        return pluginTemplateName;
    }

    public void setPluginTemplateName(String templateName) {
        this.pluginTemplateName = templateName;
    }

    public static String getEntityName() {
        return "Engine_PluginTemplate";
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public Long getPluginTemplateId() {
        return pluginTemplateId;
    }

    public void setPluginTemplateId(Long pluginTemplateId) {
        this.pluginTemplateId = pluginTemplateId;
    }


    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException, WrongOperationException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 0L).getField();
        engineId = ConstraintService.createLong("engineId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(Engine.class).getField();
        pluginTemplateId = ConstraintService.createLong("pluginTemplateId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(PluginTemplate.class).getField();
        Engine_PluginTemplate engine_pluginTemplate = new Engine_PluginTemplate();
        engine_pluginTemplate.setPluginTemplateId(pluginTemplateId);
        engine_pluginTemplate.setEngineId(engineId);
        long exitsFlag = (long) dbSqlSession.customSelectOne("Engine_PluginTemplate.ifAlreadyExist", engine_pluginTemplate);
        if (exitsFlag > 0) {
            throw new WrongOperationException("Already Exists!");
        }
        if (previouslyExistCompatibleRecords4EnginePlugin(engine_pluginTemplate.getEngineId(), engine_pluginTemplate.getPluginTemplateId(), dbSqlSession)) {
            throw new WrongOperationException("There Are Plugins Added to Groups with this Constraint. Please Remove Them First And Then Try Again!");

        }

    }


    private boolean previouslyExistCompatibleRecords4EnginePlugin(Long engineId, Long pluginTemplateId, DbSqlSession dbSqlSession) {
        Engine_PluginTemplate engine_pluginTemplate = new Engine_PluginTemplate();
        engine_pluginTemplate.setEngineId(engineId);
        engine_pluginTemplate.setPluginTemplateId(pluginTemplateId);
        //String statement = engine_pluginTemplate.getClass().getCanonicalName() + ".previouslyExistCompatibleRecords4EnginePlugin";
        long count = (long) dbSqlSession.customSelectOne("Engine_PluginTemplate.previouslyExistCompatibleRecords4EnginePlugin", engine_pluginTemplate);
        return count >= 1;
    }

//    @Override
//    public void checkDelete(TmsService tmsService) throws FieldException, WrongOperationException {
//        new ConstraintService<>("id", id, tmsService.getDbSqlSession()).lengthBetween(20, null).between(null, 0).notNull();
//        Engine_PluginTemplate engine_pluginTemplate = (Engine_PluginTemplate) tmsService.getDbSqlSession().select(Engine_PluginTemplate.class.getName(), id);
//        if (previouslyExistCompatibleRecords4EnginePlugin(engine_pluginTemplate.getEngineId(), engine_pluginTemplate.getPluginTemplateId(), tmsService)) {
//            throw new WrongOperationException("There Are Plugins Added to Groups with this Constraint. Please Remove Them First And Then Try Again!");
//
//        }
//    }

    @Override
    public String toString() {
        return "Engine_PluginTemplate id:" + id + " , pluginTemplateId:" + pluginTemplateId + " , engineId:" + engineId;
    }
}
