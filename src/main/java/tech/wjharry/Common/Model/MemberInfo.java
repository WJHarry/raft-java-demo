package tech.wjharry.Common.Model;

public class MemberInfo {

    private int id;

    private String host;

    private int port;

    private int lastIndex;

    public MemberInfo(int id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.lastIndex = 0;
    }

    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }
}
