package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/26/14 3:18 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class ConnectionType extends AbstractEntity {
    private String name;


    public static String getEntityName() {
        return "ConnectionType";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "id=" + id + " , name=" + name;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        name = ConstraintService.createString("name", paramMap, dbSqlSession, update)
                .lengthBetween(50, null).notNull().getField();
    }
}
