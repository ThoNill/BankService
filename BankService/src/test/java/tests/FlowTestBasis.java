package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class FlowTestBasis {

    @Autowired
    @Qualifier("inboundReadDirectory")
    public File inboundReadDirectory;
    
    @Autowired
    @Qualifier("inboundProcessedDirectory")
    public File inboundProcessedDirectory;
    
    @Autowired
    @Qualifier("inboundFailedDirectory")
    public File inboundFailedDirectory;
    
    @Autowired
    @Qualifier("inboundOutDirectory")
    public File inboundOutDirectory;

    public FlowTestBasis() {
        super();
    }

    @After
    public void tearDown() throws Exception {
        cleanDirectories();
    }

    @Before
    public void createFiles() throws IOException, InterruptedException {
        cleanDirectories();
        
        Files.createDirectories(inboundReadDirectory.toPath());
        Files.createDirectories(inboundProcessedDirectory.toPath());
        Files.createDirectories(inboundFailedDirectory.toPath());
        Files.createDirectories(inboundOutDirectory.toPath());
        
        Files.copy(Paths.get("./src/test/resources/kontoauszug.xml"),
                new FileOutputStream(inboundReadDirectory.getAbsolutePath()
                        + "/kontoauszug.xml"));
    }

    protected void cleanDirectories() throws FileNotFoundException {
        TestUtils.deleteRecursive(inboundReadDirectory);
        TestUtils.deleteRecursive(inboundProcessedDirectory);
        TestUtils.deleteRecursive(inboundFailedDirectory);
        TestUtils.deleteRecursive(inboundOutDirectory);
        
    }

}