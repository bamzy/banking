package ir.samatco.eft.tms.entity;

import ir.samatco.eft.common.repository.DbSqlSession;

import java.util.Map;

/**
 * Created by Bamdad Aghili on 4/17/2014.
 */
public class Operator extends AbstractEntity {
    //    private Long id;
    private String name;
    private String mobileNumber;
    private String userPath;


    public static String getEntityName() {
        return "Operator";
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

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


    public String getUserPath() {
        return userPath;
    }

    public void setUserPath(String userPath) {
        this.userPath = userPath;
    }


    @Override
    public void set(Map<String, String[]> parameterMap, DbSqlSession dbSqlSession) {

    }


}
