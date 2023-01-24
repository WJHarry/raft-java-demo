package tech.wjharry.HeartBreak.rpc;

import tech.wjharry.HeartBreak.Model.HeartBreak;
import tech.wjharry.HeartBreak.Model.HeartBreakAck;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HeartBreakRpcAdapter extends Remote {
    HeartBreakAck send(HeartBreak heartBreak) throws RemoteException;
}
