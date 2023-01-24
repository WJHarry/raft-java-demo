package tech.wjharry.Common;

import com.fasterxml.jackson.databind.ObjectMapper;
import tech.wjharry.Common.Model.MemberContext;
import tech.wjharry.Database.DatabaseService;
import tech.wjharry.Election.Service.ElectionService;
import tech.wjharry.LogReplication.Model.LogItem;

import java.rmi.Remote;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Singleton {

    public static MemberContext memberContext;

    public final static ElectionService electionService = new ElectionService();

    public final static ObjectMapper jsonObjectMapper = new ObjectMapper();

    public static Map<Integer, Map<String, Remote>> membersRemoteServices;

    public static DatabaseService databaseService;

    public static List<LogItem> logs;

    public static final Map<String, String> dataMap = new ConcurrentHashMap<>();
}
