package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;
import ir.samatco.eft.tms.service.exception.WrongOperationException;

import java.util.Map;

/**
 * Created by Bamdad Aghili on 3/17/14.
 */
public class TerminalGroup_Plugin extends AbstractEntity {
    private long terminalGroupId;
    private long pluginId;
    private String pluginName;


    public String getPluginName() {
        return pluginName;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public static String getEntityName() {
        return "TerminalGroup_Plugin";
    }

    public long getTerminalGroupId() {
        return terminalGroupId;
    }

    public void setTerminalGroupId(long terminalGroupId) {
        this.terminalGroupId = terminalGroupId;
    }

    public long getPluginId() {
        return pluginId;
    }

    public void setPluginId(long pluginId) {
        this.pluginId = pluginId;
    }


    private boolean checkEnginePluginIsCompatible(long terminalGroupId, long pluginId, DbSqlSession dbSqlSession) {
        Engine_Plugin engine_plugin = new Engine_Plugin();
        TerminalGroup terminalGroup = dbSqlSession.select("TerminalGroup", terminalGroupId);
        engine_plugin.setEngineId(terminalGroup.getEngineId());
        engine_plugin.setPluginId(pluginId);
        String statement = ".countCompatibilityRecords4PluginTemplateEngine";
        long count = (long) dbSqlSession.customSelectOne("TerminalGroup" + statement, engine_plugin);
        return count >= 1;
    }


    /*@Override
    public String toString() {
        return "TerminalGroup_pluginId:" + id + " , terminalGroupId:" + terminalGroupId + " , pluginId:" + pluginId;
    }*/

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException, WrongOperationException {
        Long terminalGroupIdLong, pluginIdLong, idLong;
        idLong = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        if (idLong != null)
            id = idLong;
        terminalGroupIdLong = ConstraintService.createLong("terminalGroupId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).getField();
        if (terminalGroupIdLong != null)
            terminalGroupId = terminalGroupIdLong;
        pluginIdLong = ConstraintService.createLong("pluginId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).getField();
        if (pluginIdLong != null)
            pluginId = pluginIdLong;
        TerminalGroup_Plugin terminalGroup_plugin = new TerminalGroup_Plugin();
        if (terminalGroupIdLong != null)
            terminalGroup_plugin.setTerminalGroupId(terminalGroupId);
        if (pluginIdLong != null)
            terminalGroup_plugin.setPluginId(pluginId);
        if (pluginIdLong != null && idLong != null) {
            if ((long) dbSqlSession.customSelectOne("TerminalGroup_Plugin.ifAlreadyExist", terminalGroup_plugin) > 1)
                throw new WrongOperationException("Already Exists!");
            if (!checkEnginePluginIsCompatible(terminalGroupId, pluginId, dbSqlSession))
                throw new WrongOperationException("According To Your Constraint This Plugin is Not Compatible with Your Engine!");
        }
    }


}
