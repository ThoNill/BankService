package flow;

import java.io.File;

import org.hibernate.pretty.MessageHelper;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class MoveFileTransformer extends MoveFileExecutor implements Transformer{

	private File outputDirectory;

	public MoveFileTransformer(File outputDirectory) {
		super();
		this.outputDirectory = outputDirectory;
	}



	@Override
	public Message<?> transform(Message<?> message) {
		File movedFile = verschiebeDatei(message, outputDirectory);
		return MessageBuilder.withPayload(movedFile).build();
	}

}
