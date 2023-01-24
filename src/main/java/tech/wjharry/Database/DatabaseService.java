package tech.wjharry.Database;

import tech.wjharry.Common.FileWriter;
import tech.wjharry.Common.Singleton;
import tech.wjharry.LogReplication.Model.LogItem;

import java.io.IOException;

public class DatabaseService {
    public synchronized void put(String key, String value) {

        LogItem logItem = new LogItem(Singleton.memberContext.getCurrentTerm(), key + ":" + value);
        try {
            FileWriter.appendBinary(logItem.toString(), "log");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Singleton.logs.add(logItem);

        Singleton.dataMap.put(key, value);
    }

    public String get(String key) {
        return Singleton.dataMap.get(key);
    }
}
