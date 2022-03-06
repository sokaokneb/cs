package hu.benkoa.cs.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.benkoa.cs.repo.Event;
import hu.benkoa.cs.repo.EventRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Slf4j
public class LogReader {

    @Autowired
    EventRepository eventRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void processFile(String path) throws IOException {

        log.info("Reading log file {}...", path);
        Files.lines(Path.of(path)).forEach(this::processLine);
        log.info("Log file processed successfully");

        log.debug("Contents of EventRepository:");
        this.eventRepository.findAll().forEach(event -> log.debug(event.toString()));

    }

    @SneakyThrows
    void processLine(String line) {

        LogEntry logEntry = this.objectMapper.readValue(line, LogEntry.class);
        log.debug("Read log entry: {}", logEntry);

        this.eventRepository.findById(logEntry.getId()).ifPresentOrElse(

                event -> {
                    log.debug("Event {} found, updating existing entry", logEntry.getId());
                    long duration = Math.abs(logEntry.getTimestamp() - event.getDuration());
                    this.eventRepository.save(Event.builder()
                            .id(logEntry.getId())
                            .host(logEntry.getHost())
                            .type(logEntry.getType())
                            .duration(duration)
                            .alert(duration>4)
                            .build());
                },

                () -> {
                    log.debug("Event {} not found, inserting new entry", logEntry.getId());
                    this.eventRepository.save(Event.builder()
                            .id(logEntry.getId())
                            .host(logEntry.getHost())
                            .type(logEntry.getType())
                            .duration(logEntry.getTimestamp())
                            .alert(false)
                            .build());
                }

        );

    }

}
