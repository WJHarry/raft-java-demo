package tech.wjharry.HeartBreak;

import tech.wjharry.Common.MemberRole;
import tech.wjharry.Common.Singleton;
import tech.wjharry.HeartBreak.Model.HeartBreak;
import tech.wjharry.HeartBreak.Model.HeartBreakAck;
import tech.wjharry.HeartBreak.rpc.HeartBreakRpcAdapter;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class HeartBreakThread extends Thread {
    @Override
    public void run() {
        while (Singleton.memberContext.getRole() == MemberRole.LEADER) {
            Singleton.memberContext.getMemberList().parallelStream()
                    .filter(member -> member.getId() != Singleton.memberContext.getId())
                    .forEach(member -> {
                        var heartBreakServer = (HeartBreakRpcAdapter) Singleton.membersRemoteServices.get(member.getId()).get("heartBreak");
                        System.out.println("发送心跳" + member.getId());
                        try {
                            HeartBreakAck ack = heartBreakServer.send(new HeartBreak(Singleton.memberContext.getId(), Singleton.memberContext.getCurrentTerm()));
                            if (!ack.isAccept()) {
                                System.out.println("心跳被" + member.getId() + "拒绝");
                            }
                        } catch (RemoteException e) {
                            System.out.println(member.getId() + "未收到心跳响应 ");
                            e.printStackTrace();
                            try {
                                Registry memberRegistry = LocateRegistry.getRegistry(member.getHost(), member.getPort());
                                heartBreakServer = (HeartBreakRpcAdapter) memberRegistry.lookup("heartBreak");
                                Singleton.membersRemoteServices.get(member.getId()).put("heartBreak", heartBreakServer);
                            } catch (Exception ex) {
                                System.out.println("重连" + member.getId() + "失败");
                                e.printStackTrace();
                            }
                        }
                    });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("error while sleeping" + e.getMessage());
                break;
            }
        }
    }
}
