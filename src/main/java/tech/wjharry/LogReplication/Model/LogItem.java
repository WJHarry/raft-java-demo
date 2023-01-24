package tech.wjharry.LogReplication.Model;

public class LogItem {

    private int term;

    private String operation;

    public LogItem(int term, String operation) {
        this.term = term;
        this.operation = operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public int getTerm() {
        return term;
    }

    public String getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return term + "-" + operation;
    }
}
