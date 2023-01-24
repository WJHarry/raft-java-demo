package tech.wjharry.Common.Model;

import tech.wjharry.Common.MemberRole;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 集群成员上下文
 * 为保证服务并发安全，所有写操作都需经过并发安全的业务方法，而不是简单set
 */
public class MemberContext {

    private int id;

    private List<MemberInfo> memberList;

    // 角色
    private MemberRole role;

    // 当前任期
    private int currentTerm;

    // 最近一次投票投给了谁
    private int lastVoteId;

    // 最近一次投票的任期
    private int lastVoteTerm;

    // 最后一次收到心跳的时间
    private long lastHeartBreak;

    // 角色转换相关的锁
    private ReadWriteLock roleLock;

    // 投票相关的锁
    private ReadWriteLock voteLock;

    public MemberContext(int id, List<MemberInfo> memberList) {
        this.id = id;
        this.memberList = memberList;
        this.currentTerm = 0;
        this.role = MemberRole.FOLLOWER;
        this.lastVoteId = 0;
        this.lastVoteTerm = 0;
        this.lastHeartBreak = System.currentTimeMillis();
        this.roleLock = new ReentrantReadWriteLock();
        this.voteLock = new ReentrantReadWriteLock();
    }

    /**
     * 接受更新的Leader的心跳，自身要退化为候选人，并将当前任期更新
     * @return 是否接受Leader的心跳
     */
    public boolean acceptHeartBreak(int heartBreakTerm) {
        roleLock.writeLock().lock();

        if (heartBreakTerm < this.currentTerm) {
            return false;
        }

        this.role = MemberRole.FOLLOWER;
        this.lastHeartBreak = System.currentTimeMillis();
        this.currentTerm = heartBreakTerm;

        roleLock.writeLock().unlock();
        return true;
    }

    /**
     * 成为候选人
     * @return 是否允许成为候选人
     */
    public boolean beCandidate() {
        roleLock.writeLock().lock();

        if (this.role == MemberRole.LEADER) {
            return false;
        }

        this.role = MemberRole.CANDIDATE;
        this.currentTerm++;

        roleLock.writeLock().unlock();
        return true;
    }

    /**
     * 竞选成功
     * @return 是否承认这次竞选成功
     */
    public boolean voteSuccess() {
        roleLock.writeLock().lock();

        if (this.role == MemberRole.FOLLOWER) {
            return false;
        }

        this.role = MemberRole.LEADER;

        roleLock.writeLock().unlock();
        return true;
    }

    public void vote(int voteId, int voteTerm) {
        voteLock.writeLock().lock();

        this.lastVoteId = voteId;
        this.lastVoteTerm = voteTerm;

        voteLock.writeLock().unlock();
    }

    public int getId() {
        return id;
    }

    public List<MemberInfo> getMemberList() {
        return memberList;
    }

    public MemberRole getRole() {
        return role;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public int getLastVoteId() {
        return lastVoteId;
    }

    public int getLastVoteTerm() {
        return lastVoteTerm;
    }

    public long getLastHeartBreak() {
        return lastHeartBreak;
    }
}
