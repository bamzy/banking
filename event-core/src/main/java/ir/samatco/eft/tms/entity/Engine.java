package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/18/14
 * <p/>
 * Created by Bamdad Aghili on 4/20/2014.
 */
public class Engine extends AbstractEntity {
    private String name;
    private String description;
    private Long version;

    private byte[] checksum;
    private byte[] data;
    private int dataLength;


    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public static String getEntityName() {
        return "Engine";
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }


    public byte[] getChecksum() {
        return checksum;
    }

    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "id=" + id + " , name=" + name + " , description=" + description + " , version=" + version + " , dataLength=" + dataLength + " , checksum=" + checksum;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            if (obj instanceof Engine) {
                if (getId() == ((Engine) (obj)).getId()) {
                    return true;
                }
            } else {
                return super.equals(obj);
            }
        } catch (Exception ex) {
            return false;
        }
        return false;

    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {

        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        name = ConstraintService.createString("name", paramMap, dbSqlSession, update)
                .lengthBetween(100, null).notNull().getField();
        description = ConstraintService.createString("description", paramMap, dbSqlSession, update)
                .lengthBetween(1000, null).notNull().getField();
        version = ConstraintService.createLong("version", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();


    }


}
