package de.b4;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

@QuarkusTest
public class FileServiceTest {

    @Inject
    FileService fileService;

    @Test
    public void testFileService() {

    }
}
