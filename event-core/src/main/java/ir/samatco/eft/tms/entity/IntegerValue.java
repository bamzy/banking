package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/26/14 3:19 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class IntegerValue extends AbstractEntity {
    //    private Long id;
    private String name;
    private Long value;
    private Long accessId;


    public static String getEntityName() {
        return "IntegerValue";
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

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public Long getAccessId() {
        return accessId;
    }

    public void setAccessId(Long accessId) {
        this.accessId = accessId;
    }

    @Override
    public String toString() {
        return "id=" + id + " , name=" + name + " , value=" + value + " , accessId=" + accessId;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        name = ConstraintService.createString("name", paramMap, dbSqlSession, update)
                .lengthBetween(100, null).notNull().getField();
        value = ConstraintService.createLong("value", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        accessId = ConstraintService.createLong("accessId", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).foreignKey(Access.class).notNull().getField();

    }
}
