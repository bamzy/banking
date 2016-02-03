package ir.samatco.eft.tms.service;

import ir.samatco.eft.common.repository.DbSqlSession;
import ir.samatco.eft.common.repository.exception.FieldException;
import ir.samatco.eft.common.sms.SmsPanel;
import ir.samatco.eft.config.ConfigProvider;
import ir.samatco.eft.tms.entity.*;
import ir.samatco.eft.tms.service.exception.WrongOperationException;

import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author bamdad aghili
 */
public class TmsService {

    private ConfigProvider configProvider;
    private DbSqlSession dbSqlSession;
    private static Hashtable environment = new Hashtable(5);
    private static DirContext ctx = null;
    private String roleDir;
    private String userDir;
    private String mainPath;
    private SmsPanel smsPanel;
    private ConfigProvider.ConfigContext tmsZookeeperContext;


    public void setConfigProvider(ConfigProvider configProvider) {
        this.configProvider = configProvider;
    }

    public SmsPanel getSmsPanel() {
        return smsPanel;
    }

    public void setSmsPanel(SmsPanel smsPanel) {
        this.smsPanel = smsPanel;
    }


    public DbSqlSession getDbSqlSession() {
        return dbSqlSession;
    }

    public void setDbSqlSession(DbSqlSession dbSqlSession) {
        this.dbSqlSession = dbSqlSession;
    }

    public String delete(AbstractEntity entity, String activeUser) throws FieldException, WrongOperationException {

        System.out.println(entity);
        if (!dbSqlSession.delete(entity.getClass().getSimpleName(), entity.getId())) {
            throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("Delete Failed: database problem").getJsonString());
        } else {
            Changes changes = new Changes();
            changes.setSecurity_activeUserPath(activeUser);
            changes.setRecordId(entity.getId());
            changes.setTableName(entity.getClass().getSimpleName());
            changes.setActionType("DELETE");
            changes.setTimeStamp(new Date().getTime());
            if (!dbSqlSession.insert(changes))
                throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("DB Change Log Failed But operation was successful: database problem").getJsonString());
            return "ok";
        }
    }

    public String update(AbstractEntity entity, String activeUser) throws FieldException, WrongOperationException, NamingException, IOException {

        Changes changes = null;
        if (entity instanceof AssignHistory) {
            if (!dbSqlSession.update(entity))
                throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("update failed: database problem").getJsonString());
            if (!dbSqlSession.insert(entity))
                throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("Assignment Log Failed: database problem").getJsonString());
            String mobile = (String) ctx.getAttributes(((AssignHistory) entity).getOperatorPath()).get("mobile").get();
            String message = "Dear. " + (ctx.getAttributes(((AssignHistory) entity).getOperatorPath()).get("givenName").get(0)) + " " + ((AssignHistory) entity).getDescription() + " for Request with ID:" + entity.getId();
            System.out.println(mobile);
            System.out.println(message);
            smsPanel.sendOne(tmsZookeeperContext.get("smsPanelUsername").getValue(), tmsZookeeperContext.get("smsPanelPassword").getValue(), mobile, "", message, false);
            return "ok";
        } else {
            changes = new Changes();
            changes.setSecurity_activeUserPath(activeUser);
            changes.setRecordId(entity.getId());
            changes.setTableName(entity.getClass().getSimpleName());
            changes.setActionType("UPDATE");
            changes.setDescription(entity.toString());
            changes.setTimeStamp(new Date().getTime());
        }
        if (!dbSqlSession.update(entity))
            throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("update failed: database problem").getJsonString());
        else {
            if (changes != null) {
                if (!dbSqlSession.insert(changes))
                    throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("DB Change Log Failed: database problem").getJsonString());
            }
            return "ok";
        }
    }


    public String insert(AbstractEntity entity, String activeUser) throws FieldException, WrongOperationException {
        try {
            entity.setId(dbSqlSession.getMaxId(entity.getClass()) + 1);    // generating new unique id
        } catch (NullPointerException e) { // first entity
            entity.setId(1L);
        }


        if (!dbSqlSession.insert(entity))
            throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("insert failed: database problem").getJsonString());
        else {
            Changes changes = new Changes();
            changes.setSecurity_activeUserPath(activeUser);
            changes.setRecordId(entity.getId());
            changes.setTableName(entity.getClass().getSimpleName());
            changes.setActionType("INSERT");
            changes.setDescription(entity.toString());
            changes.setTimeStamp(new Date().getTime());
            if (!dbSqlSession.insert(changes))
                throw new RuntimeException(JsonGenericMessage.getDescriptionalMessage("DB Change Log Failed But operation was successful: database problem").getJsonString());
            return "ok";
        }
    }


    public AbstractEntity map2Entity(Map<String, String[]> map, String entityName, byte[] uploadedBytes) throws IOException, NoSuchAlgorithmException, FieldException, WrongOperationException {
        AbstractEntity entity = (AbstractEntity) getEntity(entityName, uploadedBytes);
        entity.set(map, dbSqlSession);
        return entity;
    }

    public Map<String, Object> listValues(AbstractEntity abstractEntity) {
        Map<String, Object> result = new HashMap<>();

        List<AbstractEntity> values = new ArrayList<AbstractEntity>();
        if (abstractEntity instanceof AccessValue) {

            List<AbstractEntity> charListedValue = dbSqlSession.customSelect("CharValue.customList", abstractEntity);
            values.addAll(charListedValue);
            List<AbstractEntity> intListedValue = dbSqlSession.customSelect("IntegerValue.customList", abstractEntity);
            values.addAll(intListedValue);
        } else if (abstractEntity instanceof Engine) {
            List<AbstractEntity> listedValue = dbSqlSession.customSelect("Engine.listValues", abstractEntity);
            values.addAll(listedValue);
        } else if (abstractEntity instanceof Terminal) {
            System.out.println(((Terminal) abstractEntity).toString());
            Long groupId = ((Terminal) dbSqlSession.select(Terminal.class.getSimpleName(), ((Terminal) abstractEntity).getId())).getGroupId();
            while (groupId != null) {
                List<Plugin> listedValues = dbSqlSession.customSelect("Plugin.listPlugin4TerminalNumber", groupId);
                values.addAll(listedValues);
                groupId = fetchParentGroupId(groupId);
            }
        } else if (abstractEntity instanceof TerminalGroup_Plugin) {
            List<TerminalGroup_Plugin> listedValues = dbSqlSession.customSelect("TerminalGroup_Plugin.listValues", abstractEntity.getId());
            values.addAll(listedValues);
        } else if (abstractEntity instanceof Engine_PluginTemplate) {
            List<Engine_PluginTemplate> listedValues = dbSqlSession.customSelect("Engine_PluginTemplate.fetchEngine_PluginTemplateByEngineId", abstractEntity.getId());
            values.addAll(listedValues);
        }

        result.put("hits", values);


        return result;

    }

    private Long fetchParentGroupId(Long groupId) {
        long parentGroupValue = (long) dbSqlSession.customSelectOne("TerminalGroup.fetchParentGroupId", groupId);
        if (groupId == parentGroupValue) {
            return null;
        } else {
            return parentGroupValue;
        }
    }

    public Map<String, Object> list(Integer pageSize, Integer pageNumber, String entityName, Map<String, String[]> paramMap, String user,
                                    String orderByField, Boolean isDescNotAsc) throws FieldException, NamingException {
        AbstractEntity entity = (AbstractEntity) getEntity(entityName, null);
        String orderByDir = isDescNotAsc ? "desc" : "asc";
        long entityNumber = 0;

        List<?> entityList;
        if (entity instanceof Operator) {
            List<Object> tempListt = new ArrayList<>();
            entityList = fetchLdapOperators();
            entityNumber = entityList.size();
            if (paramMap.containsKey("operatorPath")) {
                for (Object nuser : entityList) {
                    if (((Operator) nuser).getUserPath().equalsIgnoreCase(paramMap.get("operatorPath")[0])) {
                        tempListt.add(nuser);
                        entityList = tempListt;
                        entityNumber = 1;
                    }
                }
            }
        } else {
            entityList = dbSqlSession.list(entity.getClass(), ((pageNumber - 1) * pageSize), pageSize, convertParamMap(paramMap, orderByField, orderByDir));
            entityNumber = dbSqlSession.count(entity.getClass(), convertParamMap(paramMap, orderByField, orderByDir));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalLength", entityNumber);
        result.put("hits", entityList);

        return result;
    }


    public static AbstractEntity getEntity(String entityName, byte[] uploadedBytes) {
        if (entityName.equalsIgnoreCase(Access.getEntityName()))
            return new Access();
        else if (entityName.equalsIgnoreCase(ConnectionType.getEntityName()))
            return new ConnectionType();
        else if (entityName.equalsIgnoreCase(Changes.getEntityName()))
            return new Changes();
        else if (entityName.equalsIgnoreCase(CharValue.getEntityName()))
            return new CharValue();
        else if (entityName.equalsIgnoreCase(ConfigParameter.getEntityName()))
            return new ConfigParameter();
        else if (entityName.equalsIgnoreCase(Engine.getEntityName())) {

            Engine engine = new Engine();
            if (uploadedBytes != null) {
                engine.setData(uploadedBytes);
                engine.setDataLength(uploadedBytes.length);
                try (InputStream is = new ByteArrayInputStream(uploadedBytes)) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                    DigestInputStream dis = new DigestInputStream(is, md);
                    engine.setChecksum(md.digest());
  /* Read stream to EOF as normal... */
                } catch (IOException e) {

                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();

                }

            }
            return engine;

        } else if (entityName.equalsIgnoreCase(POS.getEntityName()))
            return new POS();
        else if (entityName.equalsIgnoreCase(IntegerValue.getEntityName()))
            return new IntegerValue();
        else if (entityName.equalsIgnoreCase(Plugin.getEntityName()))
            return new Plugin();
        else if (entityName.equalsIgnoreCase(PluginTemplate.getEntityName()))
            return new PluginTemplate();
        else if (entityName.equalsIgnoreCase(Terminal.getEntityName()))
            return new Terminal();
        else if (entityName.equalsIgnoreCase(Request.getEntityName()))
            return new Request();
        else if (entityName.equalsIgnoreCase(TerminalGroup.getEntityName()))
            return new TerminalGroup();
        else if (entityName.equalsIgnoreCase(TerminalType.getEntityName()))
            return new TerminalType();
        else if (entityName.equalsIgnoreCase(TerminalGroup_Plugin.getEntityName()))
            return new TerminalGroup_Plugin();
        else if (entityName.equalsIgnoreCase(AccessValue.getEntityName()))
            return new AccessValue();
        else if (entityName.equalsIgnoreCase(Operator.getEntityName()))
            return new Operator();
        else if (entityName.equalsIgnoreCase(Engine_PluginTemplate.getEntityName()))
            return new Engine_PluginTemplate();
        else if (entityName.equalsIgnoreCase(AssignHistory.getEntityName()))
            return new AssignHistory();
        else
            throw new IllegalArgumentException("such entity does not exist");
    }

    public <T> T fetchById(Class<T> tClass, Long id) {
        return (T) dbSqlSession.select(tClass.getSimpleName(), id);
    }

    private List<Operator> fetchLdapOperators() {
        List<Operator> result = new ArrayList<>();
        if (initializeLdap()) {
            NamingEnumeration<NameClassPair> usersList = null;
            try {
                usersList = ctx.list(userDir + "," + mainPath);
                while (usersList.hasMore()) {
                    String userPath = usersList.next().getName();
                    Attribute managerRoles = ctx.getAttributes(userPath + "," + userDir + "," + mainPath).get("manager");
                    for (int i = 0; i < managerRoles.size(); i++) {
                        if (managerRoles.get(i).toString().contains("operator")) {
                            Operator operator = new Operator();
                            operator.setName((String) ctx.getAttributes(userPath + "," + userDir + "," + mainPath).get("givenName").get(0));
                            operator.setUserPath(userPath + "," + userDir + "," + mainPath);
                            operator.setMobileNumber((String) ctx.getAttributes(userPath + "," + userDir + "," + mainPath).get("mobile").get());
                            result.add(operator);
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                return result;
            }

        }
        return result;
    }

    private Map<String, String> convertParamMap(Map<String, String[]> paramMap, String orderByField,
                                                String orderByDir) {
        Map<String, String> result = new HashMap<>();
        for (String key : paramMap.keySet()) {
            String convertedKey;
            if (key.charAt(key.length() - 1) == ':') {
                convertedKey = key.substring(0, key.length() - 1) + "Equal";
                result.put(convertedKey, paramMap.get(key)[0]);
            } else if (key.charAt(key.length() - 1) == '<') {
                convertedKey = key.substring(0, key.length() - 1) + "LessThan";
                result.put(convertedKey, paramMap.get(key)[0]);
            } else if (key.charAt(key.length() - 1) == '>') {
                convertedKey = key.substring(0, key.length() - 1) + "GreaterThan";
                result.put(convertedKey, paramMap.get(key)[0]);
            } else if (key.charAt(key.length() - 1) == '$') {
                convertedKey = key.substring(0, key.length() - 1) + "Like";
                result.put(convertedKey, "%" + paramMap.get(key) + "%");
            }
        }
        result.put("orderByField", orderByField);
        result.put("orderByDir", orderByDir);
        return result;
    }

    private Operator fetchOperatorData(String userPath) throws NamingException {
        Operator operator = new Operator();
        operator.setMobileNumber((String) ctx.getAttributes(userPath + "," + userDir + "," + mainPath).get("mobile").get());
        operator.setName((String) ctx.getAttributes(userPath + "," + userDir + "," + mainPath).get("givenName").get(0));
        return operator;
    }

    public boolean verifyLdapUser(String activeUserPath, String operatorPath) throws NamingException {
        return true;
//        if (initializeLdap()) {
//            LinkedHashSet<String> allPermissionsThatUserHave = getAllPermissionsThatUserHave(activeUserPath);
//            Attribute manager = ctx.getAttributes(operatorPath).get("manager");
//
//            if (allPermissionsThatUserHave.contains("tms_create_assignment") && isOperator(manager))
//                return true;
//            else
//                return false;
//
//        } else
//            throw new RuntimeException("LDAP Server is Unreachable");
    }

    public boolean verifyLdapAdmin(String activeUserPath) throws NamingException {
        if (initializeLdap()) {
            Attribute managerRoles = ctx.getAttributes(activeUserPath).get("manager");
            if (managerRoles == null)
                return false;
            for (int i = 0; i < managerRoles.size(); i++) {
                //System.out.println("- " + managerRoles.get(i).toString());
                if (managerRoles.get(i).toString().contains("admin")) {
                    return true;
                }
            }
            return false;

        } else
            throw new RuntimeException("LDAP Server is Unreachable");
    }

    private boolean isOperator(Attribute manager) throws NamingException {
        for (int i = 0; i < manager.size(); i++)
            if (manager.get(i).toString().contains("operator"))
                return true;

        return false;
    }

    public boolean initializeLdap() {
        try {
            if (ctx != null)
                return true;
            tmsZookeeperContext = configProvider.getContext("tms");
            roleDir = tmsZookeeperContext.get("ldapRoleDir").getValue();
            userDir = tmsZookeeperContext.get("ldapUserDir").getValue();
            mainPath = tmsZookeeperContext.get("ldapMainPath").getValue();
            environment.put(Context.INITIAL_CONTEXT_FACTORY, tmsZookeeperContext.get("ldapInitialContextFactory").getValue());
//            environment.put(Context.PROVIDER_URL, tmsZookeeperContext.get("ldapServerAddress").getValue());
            environment.put(Context.PROVIDER_URL, "ldap://qezel-server:10389");
            environment.put(Context.SECURITY_AUTHENTICATION, tmsZookeeperContext.get("ldapSecurityAuthentication").getValue());
            environment.put(Context.SECURITY_PRINCIPAL, tmsZookeeperContext.get("ldapSecurityPrinciple").getValue());
            environment.put(Context.SECURITY_CREDENTIALS, tmsZookeeperContext.get("ldapSecurityCredentials").getValue());
            ctx = new InitialDirContext(environment);
            if (ctx != null)
                return true;
            return false;
        } catch (NamingException e) {
            e.printStackTrace();
            return false;

        }
    }

    private LinkedHashSet<String> getAllPermissionsThatUserHave(String userPath) throws NamingException {
        //getting roles assigned to this entity
        Attributes userAttributes = ctx.getAttributes(userPath);
        Attribute roles = userAttributes.get("manager");


        LinkedHashSet<String> usersPermissionsForSeeingMenus = new LinkedHashSet<>();
        for (int i = 0; i < roles.size(); i++) {
            String roleName = (String) roles.get(i);

            Attribute permissionAttribute = ctx.getAttributes(roleName).get("roleOccupant");

            if (permissionAttribute == null)
                continue;

            for (int j = 0; j < permissionAttribute.size(); j++) {
                String permissionName = (String) permissionAttribute.get(j);
                String cleanPermissionName = cleanLdapPermissionName(permissionName);
//                    System.out.println("perName:" +cleanPermissionName);
                if (!usersPermissionsForSeeingMenus.contains(cleanPermissionName))
                    usersPermissionsForSeeingMenus.add(cleanPermissionName);
            }
        }
        return usersPermissionsForSeeingMenus;
    }

    private String cleanLdapPermissionName(String permissionName) {
        //we want the first part. for example 'cn=list_bank'
        String firstPart = permissionName.split(",")[0];
        return firstPart.substring("cn=".length());
    }

}
