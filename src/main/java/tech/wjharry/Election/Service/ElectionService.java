package tech.wjharry.Election.Service;

import tech.wjharry.Common.MemberRole;
import tech.wjharry.Common.Singleton;
import tech.wjharry.Election.Model.ElectionAck;
import tech.wjharry.Election.Model.ElectionRequest;
import tech.wjharry.Election.rpc.ElectionVoteService;
import tech.wjharry.HeartBreak.HeartBreakThread;

import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

public class ElectionService {

    public void start() {
        // 身份转变为候选人，任期自增
        boolean bAccept = Singleton.memberContext.beCandidate();
        if (!bAccept) {
            return;
        }

        AtomicInteger agreeCount = new AtomicInteger(0);
        Singleton.memberContext.getMemberList().parallelStream()
                .filter(member -> member.getId() != Singleton.memberContext.getId())
                .forEach(member -> {
                    var voteService = (ElectionVoteService) Singleton.membersRemoteServices.get(member.getId()).get("vote");
                    ElectionAck election = null;
                    try {
                        System.out.println("向" + member.getId() + "争取投票");
                        election = voteService.election(
                                new ElectionRequest(Singleton.memberContext.getCurrentTerm(),
                                        Singleton.logs.size() - 1,
                                        Singleton.memberContext.getId()));
                    } catch (RemoteException e) {
                        System.out.println("争取投票时异常" + e.getMessage());
                        election = ElectionAck.DisAgree();
                    }
                    if (election.getAgree()) {
                        System.out.println(member.getId() + "投票给我");
                        agreeCount.getAndIncrement();
                    }
                });

        // 得到大多数同意则选举成功
        if (agreeCount.get() >= Singleton.memberContext.getMemberList().size() / 2.0) {
            System.out.println("竞选成功");
            bAccept = Singleton.memberContext.voteSuccess();
            if (!bAccept) {
                return;
            }

            // 选举成功后开始心跳
            Thread thread = new HeartBreakThread();
            thread.start();
        } else {
            System.out.println("竞选失败");
        }
    }
}
