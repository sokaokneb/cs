package hu.benkoa.cs.log;

import hu.benkoa.cs.repo.Event;
import hu.benkoa.cs.repo.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class LogReaderTest {

    private LogReader logReader;

    @BeforeEach
    void setUp() {

        this.logReader = new LogReader();
        this.logReader.eventRepository = mock(EventRepository.class);

    }

    @Test
    void processLineInsert() {

        final String LINE = "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";

        when(this.logReader.eventRepository.findById("scsmbstgra")).thenReturn(Optional.empty());

        this.logReader.processLine(LINE);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(this.logReader.eventRepository).save(eventCaptor.capture());
        assertEquals("scsmbstgra", eventCaptor.getValue().getId());
        assertEquals("12345", eventCaptor.getValue().getHost());
        assertEquals("APPLICATION_LOG", eventCaptor.getValue().getType());
        assertEquals(Long.valueOf("1491377495212"), eventCaptor.getValue().getDuration());
        assertFalse(eventCaptor.getValue().getAlert());

    }

    @Test
    void processLineFinishAfterStart() {

        final String LINE = "{\"id\":\"scsmbstgra\", \"state\":\"FINISHED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495217}";

        when(this.logReader.eventRepository.findById("scsmbstgra")).thenReturn(Optional.of(Event.builder()
                .id("scsmbstgra")
                .host("12345")
                .type("APPLICATION_LOG")
                .duration(Long.valueOf("1491377495212"))
                .alert(false)
                .build())
        );

        this.logReader.processLine(LINE);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(this.logReader.eventRepository).save(eventCaptor.capture());
        assertEquals("scsmbstgra", eventCaptor.getValue().getId());
        assertEquals("12345", eventCaptor.getValue().getHost());
        assertEquals("APPLICATION_LOG", eventCaptor.getValue().getType());
        assertEquals(Long.valueOf("5"), eventCaptor.getValue().getDuration());
        assertTrue(eventCaptor.getValue().getAlert());

    }

    @Test
    void processLineStartAfterFinish() {

        final String LINE = "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";

        when(this.logReader.eventRepository.findById("scsmbstgra")).thenReturn(Optional.of(Event.builder()
                .id("scsmbstgra")
                .host("12345")
                .type("APPLICATION_LOG")
                .duration(Long.valueOf("1491377495217"))
                .alert(false)
                .build())
        );

        this.logReader.processLine(LINE);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(this.logReader.eventRepository).save(eventCaptor.capture());
        assertEquals("scsmbstgra", eventCaptor.getValue().getId());
        assertEquals("12345", eventCaptor.getValue().getHost());
        assertEquals("APPLICATION_LOG", eventCaptor.getValue().getType());
        assertEquals(Long.valueOf("5"), eventCaptor.getValue().getDuration());
        assertTrue(eventCaptor.getValue().getAlert());

    }

    @Test
    void processLineAlertFalse() {

        final String LINE = "{\"id\":\"scsmbstgra\", \"state\":\"FINISHED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495216}";

        when(this.logReader.eventRepository.findById("scsmbstgra")).thenReturn(Optional.of(Event.builder()
                .id("scsmbstgra")
                .host("12345")
                .type("APPLICATION_LOG")
                .duration(Long.valueOf("1491377495212"))
                .alert(false)
                .build())
        );

        this.logReader.processLine(LINE);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(this.logReader.eventRepository).save(eventCaptor.capture());
        assertEquals("scsmbstgra", eventCaptor.getValue().getId());
        assertEquals("12345", eventCaptor.getValue().getHost());
        assertEquals("APPLICATION_LOG", eventCaptor.getValue().getType());
        assertEquals(Long.valueOf("4"), eventCaptor.getValue().getDuration());
        assertFalse(eventCaptor.getValue().getAlert());

    }

}