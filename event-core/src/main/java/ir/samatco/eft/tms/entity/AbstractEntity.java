package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;
import ir.samatco.eft.tms.service.exception.WrongOperationException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: door
 * Date: 12/28/13
 * Time: 3:25 PM
 */
public abstract class AbstractEntity {
    protected Long id;

    public Map<String, Boolean> getUpdate() {
        return update;
    }

    protected Map

            <String, Boolean> update = new HashMap<>();

    public void setUpdate(Map<String, Boolean> update) {
        this.update = update;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    abstract public void set(Map<String, String[]> parameterMap, DbSqlSession dbSqlSession) throws IOException, NoSuchAlgorithmException, FieldException, WrongOperationException;

}
