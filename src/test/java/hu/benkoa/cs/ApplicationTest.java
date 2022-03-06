package hu.benkoa.cs;

import hu.benkoa.cs.log.LogReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class ApplicationTest {

    private Application application;

    @BeforeEach
    void setUp() {

        this.application = new Application();
        this.application.logReader = mock(LogReader.class);

    }

    @Test
    void commandLineRunner() throws Exception {

        final String path = "testfile.txt";

        this.application.commandLineRunner(null).run(path);
        verify(this.application.logReader).processFile(path);

    }

    @Test
    void commandLineRunnerNoArgs() {

        Exception ex = assertThrows(RuntimeException.class, () -> this.application.commandLineRunner(null).run());
        assertEquals("Please provide path to log file as a command line argument", ex.getMessage());

    }

}