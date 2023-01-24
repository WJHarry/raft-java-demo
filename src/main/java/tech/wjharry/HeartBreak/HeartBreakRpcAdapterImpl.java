package tech.wjharry.HeartBreak;

import tech.wjharry.Common.MemberRole;
import tech.wjharry.Common.Singleton;
import tech.wjharry.HeartBreak.Model.HeartBreak;
import tech.wjharry.HeartBreak.Model.HeartBreakAck;
import tech.wjharry.HeartBreak.rpc.HeartBreakRpcAdapter;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class HeartBreakRpcAdapterImpl extends UnicastRemoteObject implements HeartBreakRpcAdapter {
    public HeartBreakRpcAdapterImpl() throws RemoteException {
        super();
    }

    @Override
    public HeartBreakAck send(HeartBreak heartBreak) {
        System.out.println("收到Leader心跳" + heartBreak.getId() + " term:" + heartBreak.getTerm());

        boolean bAccept = Singleton.memberContext.acceptHeartBreak(heartBreak.getTerm());
        return new HeartBreakAck(bAccept);
    }
}
