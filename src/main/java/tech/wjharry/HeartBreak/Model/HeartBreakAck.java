package tech.wjharry.HeartBreak.Model;

import java.io.Serializable;

public class HeartBreakAck implements Serializable {

    private boolean accept;

    public HeartBreakAck(boolean accept) {
        this.accept = accept;
    }

    public void setAccept(boolean accept) {
        this.accept = accept;
    }

    public boolean isAccept() {
        return accept;
    }
}
