package ir.samatco.eft.tms.service;

import ir.samatco.eft.tms.entity.Engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bamdad Aghili on 4/6/14.
 */
public class EngineFactory {
    static Map<Long, Engine> engines = new HashMap<>();

    public static Engine getEngine(Engine engine) throws IOException {
        Engine newEngine = engines.get(engine.getId());
        if (newEngine != null)
            return newEngine;

        newEngine = new Engine();
        newEngine.setId(engine.getId());
        newEngine.setDataLength(engine.getDataLength());
        newEngine.setChecksum(engine.getChecksum());
        newEngine.setName(engine.getName());
        newEngine.setDescription(engine.getDescription());

        byte[] allBytes = engine.getData();

        if (allBytes != null) {
            newEngine.setDataLength(allBytes.length);
        }
        newEngine.setData(allBytes);
        engines.put(newEngine.getId(), newEngine);
        return newEngine;
    }
}
