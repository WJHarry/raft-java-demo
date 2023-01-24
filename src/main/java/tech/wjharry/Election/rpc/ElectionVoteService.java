package tech.wjharry.Election.rpc;

import tech.wjharry.Election.Model.ElectionAck;
import tech.wjharry.Election.Model.ElectionRequest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ElectionVoteService extends Remote {
    ElectionAck election(ElectionRequest request) throws RemoteException;
}
