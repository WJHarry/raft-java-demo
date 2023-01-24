package tech.wjharry.Common;

import tech.wjharry.Common.Model.MemberContext;
import tech.wjharry.Common.Model.MemberInfo;
import tech.wjharry.Common.Netty.NettyServer;
import tech.wjharry.Election.Service.ElectionVoteServiceImpl;
import tech.wjharry.Election.rpc.ElectionVoteService;
import tech.wjharry.HeartBreak.HeartBreakListenerDaemonThread;
import tech.wjharry.HeartBreak.HeartBreakRpcAdapterImpl;
import tech.wjharry.HeartBreak.rpc.HeartBreakRpcAdapter;
import tech.wjharry.LogReplication.Model.LogItem;

import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;
import java.util.stream.Collectors;

public class Server {

    @SuppressWarnings("unchecked")
    public Server() throws Exception {
        Config config = new Config();
        var memberConfigMap = (Map<String, Object>) config.readConf("member");
        MemberContext memberContext = new MemberContext(
                (int) memberConfigMap.get("id"),
                ((List<Map<String, Object>>) memberConfigMap.get("membersList")).stream()
                        .map(i -> new MemberInfo((int) i.get("id"), (String) i.get("host"), (int) i.get("port")))
                        .collect(Collectors.toList()));
        Singleton.memberContext = memberContext;

        List<MemberInfo> otherMembers = memberContext.getMemberList().stream()
                .filter(m -> m.getId() != memberContext.getId()).collect(Collectors.toList());

        Registry serverRegistry = null;
        try {

            MemberInfo memberInfo = memberContext.getMemberList().stream()
                    .filter(m -> m.getId() == memberContext.getId())
                    .findFirst().orElseThrow();

            // 注册服务，每个成员自己都是服务提供方，也都是调用方
            serverRegistry = LocateRegistry.createRegistry(memberInfo.getPort());

            ElectionVoteService voteService = new ElectionVoteServiceImpl();
            serverRegistry.rebind("vote", voteService);

            HeartBreakRpcAdapter heartBreakService = new HeartBreakRpcAdapterImpl();
            serverRegistry.rebind("heartBreak", heartBreakService);

            System.out.printf("rpc server %d start at %s:%d\n", memberInfo.getId(), memberInfo.getHost(), memberInfo.getPort());
        } catch (RemoteException e) {
            System.out.println("Error while registering rpc server");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Map<Integer, Map<String, Remote>> membersRemoteServices = new HashMap<>();
        // 获取其他成员的服务
        Set<Integer> connecting = otherMembers.stream().map(MemberInfo::getId).collect(Collectors.toSet());
        while (connecting.size() > 0) {
            for (MemberInfo memberInfo : otherMembers) {
                try {
                    Registry memberRegistry = LocateRegistry.getRegistry(memberInfo.getHost(), memberInfo.getPort());
                    Map<String, Remote> remoteServices = Map.of(
                            "vote", memberRegistry.lookup("vote"),
                            "heartBreak", memberRegistry.lookup("heartBreak"));
                    membersRemoteServices.put(memberInfo.getId(), remoteServices);
                    connecting.remove(memberInfo.getId());
                } catch (RemoteException e) {
                    System.out.println("cannot find server" + memberInfo.getId() + " " + memberInfo.getHost() + ":" + memberInfo.getPort() + " " + e.getMessage());
                } catch (NotBoundException e) {
                    System.out.println("cannot find remote services " +
                            memberInfo.getId() + " " + memberInfo.getHost() + ":" + memberInfo.getPort());
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("error while sleeping");
                e.printStackTrace();
            }
        }
        Singleton.membersRemoteServices = membersRemoteServices;

        System.out.println("集群启动");

        Singleton.logs = new ArrayList<>();
        int port = (int) ((Map<String, Object>) ((Map<String, Object>) config.readConf("app")).get("server")).get("port");
        NettyServer nettyServer = new NettyServer(port);
        nettyServer.start();

        InetAddress addr = InetAddress.getLocalHost();
        System.out.println("server start at " + addr + ":" + port);
    }

    public void start() {
        // 开启监听Leader健康的守护线程
        Thread thread = new HeartBreakListenerDaemonThread();
        thread.setDaemon(true);
        thread.start();
    }
}
