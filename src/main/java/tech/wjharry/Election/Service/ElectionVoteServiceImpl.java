package tech.wjharry.Election.Service;

import tech.wjharry.Common.MemberRole;
import tech.wjharry.Common.Singleton;
import tech.wjharry.Election.Model.ElectionAck;
import tech.wjharry.Election.Model.ElectionRequest;
import tech.wjharry.Election.rpc.ElectionVoteService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ElectionVoteServiceImpl extends UnicastRemoteObject implements ElectionVoteService {

    @Override
    public synchronized ElectionAck election(ElectionRequest request) {
        System.out.println("收到投票请求" + request.getMemberId());

        // 1. 候选人只能给自己投票
        // 2. 一个任期只能投票一次
        if (Singleton.memberContext.getRole() != MemberRole.FOLLOWER ||
                request.getTerm() <= Singleton.memberContext.getLastVoteTerm()) {
            return ElectionAck.DisAgree();
        }

        // 3. 候选人的日志如果没有自己的新，拒绝竞选
        if (request.getLastIndex() < Singleton.logs.size()) {
            return ElectionAck.DisAgree();
        }

        Singleton.memberContext.vote(request.getMemberId(), request.getTerm());
        return ElectionAck.Agree();
    }

    public ElectionVoteServiceImpl() throws RemoteException {
        super();
    }
}
