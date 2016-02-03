package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 2/3/14 3:07 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class Plugin extends AbstractEntity {

    private Long pluginTemplateId;
    private String pluginValues;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getEntityName() {
        return "Plugin";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getPluginTemplateId() {
        return pluginTemplateId;
    }

    public void setPluginTemplateId(Long pluginTemplateId) {
        this.pluginTemplateId = pluginTemplateId;
    }

    public String getPluginValues() {
        return pluginValues;
    }

    public void setPluginValues(String pluginValues) {
        this.pluginValues = pluginValues;
    }

    @Override
    public String toString() {
        return "id=" + id + " , pluginTemplateId=" + pluginTemplateId + " , pluginValues=" + pluginValues + " , name=" + name;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        pluginTemplateId = ConstraintService.createLong("pluginTemplateId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().foreignKey(PluginTemplate.class).getField();
        name = ConstraintService.createString("name", paramMap, dbSqlSession, update)
                .lengthBetween(50, null).notNull().getField();
        pluginValues = ConstraintService.createString("pluginValues", paramMap, dbSqlSession, update)
                .getField();


    }

}
