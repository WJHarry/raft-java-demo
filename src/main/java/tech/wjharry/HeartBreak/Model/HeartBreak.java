package tech.wjharry.HeartBreak.Model;

import java.io.Serializable;

/**
 * 心跳包
 */
public class HeartBreak implements Serializable {

    private int id;

    private int term;

    public HeartBreak(int id, int term) {
        this.id = id;
        this.term = term;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getId() {
        return id;
    }

    public int getTerm() {
        return term;
    }
}
