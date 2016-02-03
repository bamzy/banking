package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/25/14 4:14 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class PluginTemplate extends AbstractEntity {
    private String title;
    private String name;
    private String description;
    private Long version;
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static String getEntityName() {
        return "PluginTemplate";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "PluginTemplate id=" + id + " , name=" + name + " , title=" + title + " , description=" + description + " , version=" + version + " , content=" + content;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        name = ConstraintService.createString("name", paramMap, dbSqlSession, update)
                .lengthBetween(50, null).notNull().getField();
        title = ConstraintService.createString("title", paramMap, dbSqlSession, update)
                .lengthBetween(50, null).notNull().getField();
        description = ConstraintService.createString("description", paramMap, dbSqlSession, update)
                .lengthBetween(2000, null).notNull().getField();
        version = ConstraintService.createLong("version", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        String tempContent = ConstraintService.createString("content", paramMap, dbSqlSession, update)
                .lengthBetween(null, 1).notNull().getField();
        content = tempContent.replaceAll("&#xA;","");


    }
}
