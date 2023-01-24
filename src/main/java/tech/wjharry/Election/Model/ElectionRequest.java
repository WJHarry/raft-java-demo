package tech.wjharry.Election.Model;

import java.io.Serializable;

/**
 * 选举请求
 */
public class ElectionRequest implements Serializable {
    // 任期
    private final int term;
    // 最新的日志索引
    private final int lastIndex;
    // 候选人
    private final int memberId;

    public ElectionRequest(int term, int lastIndex, int memberId) {
        this.term = term;
        this.lastIndex = lastIndex;
        this.memberId = memberId;
    }

    public int getTerm() {
        return term;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public int getMemberId() {
        return memberId;
    }
}
