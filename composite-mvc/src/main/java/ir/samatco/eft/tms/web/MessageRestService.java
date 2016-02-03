package ir.samatco.eft.tms.web;


import ir.samatco.eft.common.repository.ConstraintService;
import ir.samatco.eft.tms.entity.AbstractEntity;
import ir.samatco.eft.tms.entity.AssignHistory;
import ir.samatco.eft.tms.service.JsonGenericMessage;
import ir.samatco.eft.tms.service.TmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Bamdad Aghili
 * Date: 11/6/13
 * Time: 10:16 AM
 */

@Controller
public class MessageRestService {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    TmsService tmsService;


    @RequestMapping(value = "/create/{entity}/list", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String createEntityList(HttpServletRequest request, @PathVariable String entity) {
        return createEntity(request, entity, null);
    }

    @RequestMapping(value = "/delete/{entity}/list", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Map<String, Object> deleteEntityList(HttpServletRequest request, @PathVariable String entity) {
        return deleteEntity(request, entity);
    }


    @RequestMapping(value = "/create/engine", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String createEntityWF(HttpServletRequest request, @RequestParam(value = "defaultEngine") MultipartFile file) {
        try {
            return createEntity(request, "engine", file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return JsonGenericMessage.getDescriptionalMessage("Problem with Uploaded File").getJsonString(true);
        }
    }

    @RequestMapping(value = "/create/{entity}", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String createEntity(HttpServletRequest request, @PathVariable String entity, byte[] data) {
        try {
            Map<String, String[]> paramMap = new HashMap<String, String[]>(request.getParameterMap());
            String user = ConstraintService.createString("security_activeUserPath",
                    request.getHeader("security_activeUserPath")).notNull()
                    .removeExtraUserString().getField();
            String entityName = ConstraintService.createString("entity", entity.toLowerCase())
                    .notNull().getField();
            log.debug("create/{entity}: user={}, entityName={}, stringMap={}", user, entityName,
                    getReadyToPrintMap(paramMap));
            paramMap.put("entity", new String[]{entityName});
            AbstractEntity abstractEntity = tmsService.map2Entity(paramMap, entityName, data);
            String result = tmsService.insert(abstractEntity, user);
            if (result.equals("ok"))
                return JsonGenericMessage.getDescriptionalMessage(result).getJsonString(false);
            else
                return JsonGenericMessage.getDescriptionalMessage(result).getJsonString(true);

        } catch (Exception ex) {
            log.error("create/{entity}:", ex);
            return JsonGenericMessage.getDescriptionalMessage(ex.getMessage()).getJsonString(true);
        }
    }

    @RequestMapping(value = "/updateWF/{entity}", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Map<String, Object> updateEntityWF(HttpServletRequest request, @PathVariable String entity, @RequestParam(defaultValue = "aaa", value = "fileName") MultipartFile file) {
        System.out.println("updateWF");
        try {
            return updateEntity(request, entity, file.getBytes());
        } catch (IOException e) {
            log.error("updateWF/{entity}:", e);
            return getResultMap(e);
        }

    }


    @RequestMapping(value = "/update/{entity}", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Map<String, Object> updateEntity(HttpServletRequest request, @PathVariable String entity, byte[] data) {
        try {
            Map<String, String[]> paramMap = new HashMap<String, String[]>(request.getParameterMap());
            String user = ConstraintService.createString("security_activeUserPath",
                    request.getHeader("security_activeUserPath")).notNull()
                    .removeExtraUserString().getField();
            String entityName = ConstraintService.createString("entity", entity.toLowerCase())
                    .notNull().getField();
            log.debug("update/{entity}: user={}, entityName={}, stringMap={}", user, entityName,
                    getReadyToPrintMap(paramMap));
            paramMap.put("entity", new String[]{entityName});
            AbstractEntity abstractEntity = tmsService.map2Entity(paramMap, entityName, data);
            boolean toUpdate = false;
            for (String key : abstractEntity.getUpdate().keySet())
                if (!key.equals("id") && abstractEntity.getUpdate().get(key)) {
                    toUpdate = true;
                    break;
                }
            if (!toUpdate)
                return getResultMap("ok", false);
            String result = tmsService.update(abstractEntity, user);
            return getResultMap(result, !result.equals("ok"));

        } catch (Exception e) {
            log.error("update/{entity}:", e);
            return getResultMap(e);
        }
    }

    @RequestMapping(value = "/delete/{entity}", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Map<String, Object> deleteEntity(HttpServletRequest request, @PathVariable String entity) {
        try {
            Map<String, String[]> paramMap = new HashMap<String, String[]>(request.getParameterMap());
            String user = ConstraintService.createString("security_activeUserPath",
                    request.getHeader("security_activeUserPath")).notNull()
                    .removeExtraUserString().getField();
            String entityName = ConstraintService.createString("entity", entity.toLowerCase())
                    .notNull().getField();

            Long id = ConstraintService.createLong("id", paramMap)
                    .lengthBetween(20, null).between(null, 0L).notNull().getField();
            if (id == null)
                id = ConstraintService.createLong("terminalNumber", paramMap)
                        .lengthBetween(20, null).between(null, 0L).notNull().getField();
            log.debug("delete/{entity}: user={}, entityName={}, stringMap={}", user, entityName,
                    getReadyToPrintMap(paramMap));
            paramMap.put("entity", new String[]{entityName});
            AbstractEntity abstractEntity = tmsService.getEntity(entityName, null);
            abstractEntity.setId(id);
            return getResultMap(tmsService.delete(abstractEntity, user), false);


        } catch (Exception e) {
            log.error("update/{entity}:", e);
            return getResultMap(e);
        }

    }

    @RequestMapping(value = "/list/{entity}", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Map<String, Object> listEntity(HttpServletRequest request, @PathVariable String entity) {
        try {
            Map<String, String[]> paramMap = new HashMap<String, String[]>(request.getParameterMap());
            String user = ConstraintService.createString("security_activeUserPath",
                    request.getHeader("security_activeUserPath")).notNull()
                    .removeExtraUserString().getField();
            Boolean isDescNotAsc = ConstraintService.createBoolean("isDescNotAsc", paramMap)
                    .defaultValue(true).getField();

            String orderByField;
            if (entity.equalsIgnoreCase("Terminal"))
                orderByField = ConstraintService.createString("orderByField", paramMap)
                        .defaultValue("terminalNumber").getField();
            else
                orderByField = ConstraintService.createString("orderByField", paramMap)
                        .defaultValue("id").getField();
            Integer pageSize = ConstraintService.createInteger("pageSize", paramMap)
                    .defaultValue(10).getField();
            Integer pageNumber = ConstraintService.createInteger("pageNumber", paramMap)
                    .defaultValue(1).getField();
            String entityName = ConstraintService.createString("entity", entity.toLowerCase())
                    .notNull().getField();
            log.debug("list/{entity}: user={}, isDescNotAsc={}, orderByField={}, pageSize={}, " +
                    "pageNumber={}, entityName={}, paramMap={}", user, isDescNotAsc, orderByField, pageSize,
                    pageNumber, entityName, paramMap);
            JsonGenericMessage jsonGenericMessage = new JsonGenericMessage();
            return getResultMap(tmsService.list(pageSize, pageNumber, entityName,
                    paramMap, user, orderByField, isDescNotAsc), false);

        } catch (Exception e) {
            log.error("list/{entity}:", e);
            return getResultMap(e);
        }
    }


    @RequestMapping(value = "/listValues/{entity}", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Map<String, Object> listValues(HttpServletRequest request, @PathVariable String entity) {
        try {
            Map<String, String[]> paramMap = new HashMap<String, String[]>(request.getParameterMap());
            String user = ConstraintService.createString("security_activeUserPath",
                    request.getHeader("security_activeUserPath")).notNull()
                    .removeExtraUserString().getField();
            Boolean isDescNotAsc = ConstraintService.createBoolean("isDescNotAsc", paramMap)
                    .defaultValue(true).getField();
            String orderByField = ConstraintService.createString("orderByField", paramMap)
                    .defaultValue("id").getField();
            Integer pageSize = ConstraintService.createInteger("pageSize", paramMap)
                    .defaultValue(10).getField();
            Integer pageNumber = ConstraintService.createInteger("pageNumber", paramMap)
                    .defaultValue(1).getField();
            String entityName = ConstraintService.createString("entity", entity.toLowerCase())
                    .notNull().getField();
            log.debug("listValues/{entity}: user={}, isDescNotAsc={}, orderByField={}, pageSize={}, " +
                    "pageNumber={}, entityName={}, paramMap={}", user, isDescNotAsc, orderByField, pageSize,
                    pageNumber, entityName, paramMap);
            String capitalEntityName = entity.substring(0, 1).toUpperCase() + entity.substring(1);

            AbstractEntity abstractEntity = tmsService.map2Entity(paramMap, capitalEntityName, null);
//        if (id == null)
//            return JsonGenericMessage.getDescriptionalMessage("Not Parameter was set in your request").getJsonString(true);
//        abstractEntity.setId(id);
            Map<String, Object> result = tmsService.listValues(abstractEntity);

            /*ArrayList<> x = result.get("hits");*/
            Map<String, Object> resultMap = getResultMap(result, false);
//            Set<String> keyset = resultMap.keySet();
//            for (String s : keyset) {
//                System.out.println("key: " + s);
//                System.out.println(resultMap.get(s));
//            }
            //resultMap.remove("hits");
//            TerminalGroup_Plugin m = (TerminalGroup_Plugin)((ArrayList)resultMap.get("hits")).get(0);
//            m.setTerminalGroupId(2);
//            m.setPluginId(4);

            return resultMap;

        } catch (Exception e) {
            log.error("listValues/{entity}:", e);
            return getResultMap(e);
        }
    }


    @RequestMapping(value = "/assignto/operator", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String assignTo(HttpServletRequest request) {
        try {

            Map<String, String[]> paramMap = request.getParameterMap();
            Map<String, String> stringMap = new HashMap(paramMap.size());
            for (String key : paramMap.keySet())
                stringMap.put(key, paramMap.get(key)[0]);
            System.out.println("Assign for Entity: " + "Operator" + " with Parameters:");
            String activeUser = request.getHeader("security_activeUserPath");
            AbstractEntity abstractEntity = tmsService.map2Entity(paramMap, "AssignHistory", null);
            Long maxId = 0L;
            try {
                maxId = tmsService.getDbSqlSession().getMaxId(abstractEntity.getClass());
            } catch (NullPointerException ex) {
                maxId  = 0L;
            }
            abstractEntity.setId(maxId + 1);
            ((AssignHistory) abstractEntity).setSecurity_activeUserPath(activeUser);
            ((AssignHistory) abstractEntity).setTimeStamp(System.currentTimeMillis());
            String result = tmsService.update(abstractEntity, activeUser);
            if (result.equals("ok")) {
                return JsonGenericMessage.getDescriptionalMessage(result).getJsonString(false);
            } else {
                return JsonGenericMessage.getDescriptionalMessage(result).getJsonString(true);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return JsonGenericMessage.getDescriptionalMessage(e.getMessage()).getJsonString(true);
        }
    }

    private Map<String, String> getReadyToPrintMap(Map<String, String[]> stringMap) {
        Map<String, String> result = new HashMap<>(stringMap.size());
        for (String key : stringMap.keySet())
            result.put(key, Arrays.toString(stringMap.get(key)));
        return result;
    }

    private Map<String, Object> getResultMap(Map<String, Object> map) {
        return getResultMap(map, false);
    }

    private Map<String, Object> getResultMap(Map<String, Object> map, boolean error) {
        map.put("permission", true);
        map.put("error", error);
        return map;
    }

    private Map<String, Object> getResultMap(Exception e) {
        return getResultMap(e, true);
    }

    private Map<String, Object> getResultMap(Exception e, boolean error) {
        return getResultMap(e.getMessage(), error);
    }

    private Map<String, Object> getResultMap(String message, boolean error) {
        Map<String, Object> map = new HashMap<>();
        map.put("permission", true);
        map.put("error", error);
        map.put("message", message);
        return map;
    }


}



