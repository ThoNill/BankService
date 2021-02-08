package flow;

import java.io.File;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class FlowConfiguration {


	@Bean(name = "fileInputChannel")
	public MessageChannel fileInputChannel() {
		return new DirectChannel();
	}

	@Bean(name = "fileMovedChannel")
	public MessageChannel fileMovedChannel() {
		return new DirectChannel();
	}

	@Bean(name = "fileFailedChannel")
	public MessageChannel fileFailedChannel() {
		return new DirectChannel();
	}

 
	@Bean(name = "inboundReadDirectory")
	public File inboundReadDirectory(@Value("${inbound.read.path}") String path) {
		return makeDirectory(path);
	}

	@Bean(name = "inboundProcessedDirectory")
	public File inboundProcessedDirectory(@Value("${inbound.processed.path}") String path) {
		return makeDirectory(path);
	}

	@Bean(name = "inboundFailedDirectory")
	public File inboundFailedDirectory(@Value("${inbound.failed.path}") String path) {
		return makeDirectory(path);
	}

	@Bean(name = "inboundOutDirectory")
	public File inboundOutDirectory(@Value("${inbound.out.path}") String path) {
		return makeDirectory(path);
	}

	private File makeDirectory(String path) {
		File file = new File(path);
		file.mkdirs();
		return file;
	}

}
