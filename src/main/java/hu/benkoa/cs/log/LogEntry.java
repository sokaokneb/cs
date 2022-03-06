package hu.benkoa.cs.log;

import lombok.Data;

@Data
public class LogEntry {

    private String id;
    private String state;
    private long timestamp;
    private String type;
    private String host;

}
