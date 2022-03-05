package hu.benkoa.cs.log;

import lombok.Data;

@Data
public class LogEntry {

    private String id;
    private String state;
    private long timestamp;
    private String type;
    private String host;

    public boolean isStart() {
        return "STARTED".equals(this.state);
    }

    public boolean isEnd() {
        return "FINISHED".equals(this.state);
    }

}
