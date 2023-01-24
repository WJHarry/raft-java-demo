package tech.wjharry.Election.Model;

import java.io.Serializable;

/**
 * 选举投票意见
 */
public class ElectionAck implements Serializable {
    private final Boolean agree;

    private ElectionAck(Boolean agree) {
        this.agree = agree;
    }


    public static ElectionAck Agree() {
        return new ElectionAck(true);
    }

    public static ElectionAck DisAgree() {
        return new ElectionAck(false);
    }

    public Boolean getAgree() {
        return agree;
    }
}
