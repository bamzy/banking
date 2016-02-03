package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;

import java.util.Map;

/**
 * Date: 1/25/14 5:26 PM of tms
 *
 * @author Bamdad Aghili (bamdad.ag@gmail.com)
 */
public class POS extends AbstractEntity {
    private Long serialNumber;
    private String assetNumber;
    private Long terminalNumber;


    public static String getEntityName() {
        return "Pos";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }


    public Long getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(Long terminalNumber) {
        this.terminalNumber = terminalNumber;
    }

    @Override
    public String toString() {
        return "id=" + id + " , serialNumber=" + serialNumber + " , assetNumber=" + assetNumber + " , terminalNumber=" + terminalNumber;
    }

    @Override
    public void set(Map<String, String[]> paramMap, DbSqlSession dbSqlSession) throws FieldException {
        id = ConstraintService.createLong("id", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        serialNumber = ConstraintService.createLong("serialNumber", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).notNull().getField();
        assetNumber = ConstraintService.createString("assetNumber", paramMap, dbSqlSession, update)
                .lengthBetween(30, null).notNull().getField();
        terminalNumber = ConstraintService.createLong("terminalNumber", paramMap, dbSqlSession, update)
                .lengthBetween(20, null).between(null, 1L).foreignKey(Terminal.class).notNull().getField();

    }


}
