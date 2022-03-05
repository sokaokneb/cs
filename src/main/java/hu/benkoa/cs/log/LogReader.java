package hu.benkoa.cs.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.benkoa.cs.repo.Event;
import hu.benkoa.cs.repo.EventRepository;
import hu.benkoa.cs.repo.EventStartEndTime;
import hu.benkoa.cs.repo.EventStartEndTimeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
@Slf4j
public class LogReader {

    @Autowired
    private EventStartEndTimeRepository eventStartEndTimeRepository;

    @Autowired
    private EventRepository eventRepository;

    public void processFile(String path) {
        this.readFileAndPopulateEventStartEnds(path);
        this.printEventStartEnds();
        this.populateEvents();
        this.printEventStartEnds();
        this.printEvents();
    }

    private void readFileAndPopulateEventStartEnds(String path) {

        log.info("Reading log file {}...", path);

        ObjectMapper objectMapper = new ObjectMapper();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line;
            while ((line = br.readLine()) != null) {

                LogEntry logEntry = objectMapper.readValue(line, LogEntry.class);
                log.debug("Read log entry: {}", logEntry);

                this.eventStartEndTimeRepository.findById(logEntry.getId()).ifPresentOrElse(

                        eventStartEndTime -> {
                            log.debug("Event {} found, updating existing entry", logEntry.getId());
                            this.eventStartEndTimeRepository.save(EventStartEndTime.builder()
                                    .id(logEntry.getId())
                                    .host(logEntry.getHost())
                                    .type(logEntry.getType())
                                    .start(logEntry.isStart() ? logEntry.getTimestamp() : eventStartEndTime.getStart())
                                    .end(logEntry.isEnd() ? logEntry.getTimestamp() : eventStartEndTime.getEnd())
                                    .build());
                        },

                        () -> {
                            log.debug("Event {} not found, creating new entry", logEntry.getId());
                            this.eventStartEndTimeRepository.save(EventStartEndTime.builder()
                                    .id(logEntry.getId())
                                    .host(logEntry.getHost())
                                    .type(logEntry.getType())
                                    .start(logEntry.isStart() ? logEntry.getTimestamp() : null)
                                    .end(logEntry.isEnd() ? logEntry.getTimestamp() : null)
                                    .build());
                        }

                );

            }

        } catch (IOException e) {
            log.error("Error reading file", e);
        }

    }

    private void printEventStartEnds() {
        log.debug("Contents of EventStartEndTimeRepository:");
        this.eventStartEndTimeRepository.findAll().forEach(eventStartEndTime -> log.debug(eventStartEndTime.toString()));
    }

    private void populateEvents() {

        log.info("Populating events...");

        this.eventStartEndTimeRepository.findAll().forEach(eventStartEndTime -> {
            this.eventRepository.save(Event.builder()
                    .id(eventStartEndTime.getId())
                    .duration(eventStartEndTime.getEnd() - eventStartEndTime.getStart())
                    .type(eventStartEndTime.getType())
                    .host(eventStartEndTime.getHost())
                    .alert(eventStartEndTime.getEnd() - eventStartEndTime.getStart() > 4)
                    .build());
            this.eventStartEndTimeRepository.delete(eventStartEndTime);
        });

    }

    private void printEvents() {
        log.debug("Contents of EventRepository:");
        this.eventRepository.findAll().forEach(event -> log.debug(event.toString()));
    }

}
