package tests;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptorAdapter;

public class CopyHelper {
	@Autowired
	@Qualifier("inboundReadDirectory")
	public File inboundReadDirectory;
	
	
	public CopyHelper() {
        super();
    }

 	protected void copy(String quellName) throws IOException {
		Path quellPath = Paths.get(quellName);
		String fileName = quellPath.getFileName().toString();
    	Path zielPath = Paths.get(inboundReadDirectory.getAbsolutePath(),fileName);
       	Path tmpPath = Paths.get(inboundReadDirectory.getAbsolutePath(),fileName+".tmp");
        
    	Files.copy(quellPath,tmpPath);
    	Files.move(tmpPath,zielPath);
	}


}