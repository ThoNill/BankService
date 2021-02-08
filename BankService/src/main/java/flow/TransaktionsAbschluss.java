package flow;

import java.io.File;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transaction.IntegrationResourceHolder;
import org.springframework.integration.transaction.TransactionSynchronizationProcessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import repositories.EingangsDateiRepository;

public class TransaktionsAbschluss implements TransactionSynchronizationProcessor {
	Logger LOG = LogManager.getLogger(TransaktionsAbschluss.class);

	private File processedDirectory;

	private File failedDirectory;

	private EingangsDateiRepository eingangsDateiRepository;

	private MessageChannel processedChannel;

	private MessageChannel failedChannel;

	private MoveFileExecutor moveFile = new MoveFileExecutor();

	public TransaktionsAbschluss(File processedDirectory, MessageChannel processedChannel, File failedDirectory,
			MessageChannel failedChannel, EingangsDateiRepository eingangsDateiRepository) {
		super();
		this.processedDirectory = processedDirectory;
		this.failedDirectory = failedDirectory;
		this.eingangsDateiRepository = eingangsDateiRepository;
		this.processedChannel = processedChannel;
		this.failedChannel = failedChannel;
	}

	@Override
	public void processBeforeCommit(IntegrationResourceHolder holder) {
	}

	@Override
	public void processAfterCommit(IntegrationResourceHolder holder) {
		LOG.debug("ProcessedDirectory: " + processedDirectory);
		LOG.debug("Holder: " + holder);
		LOG.debug("Holder Message: " + holder.getMessage());
		LOG.debug("Holder Attributes: " + holder.getAttributes());
		sendHolderToChannel(holder, processedChannel, processedDirectory);
	}

	private void sendHolderToChannel(IntegrationResourceHolder holder, MessageChannel channel, File directory) {
		Message msg = holder.getMessage();
		if (msg != null) {
			File file = moveFile.verschiebeDatei(msg, directory);
			sendMessageToChannel(file, channel);
		}
	}

	@Override
	public void processAfterRollback(IntegrationResourceHolder holder) {
		sendHolderToChannel(holder, failedChannel, failedDirectory);
	}

	private void sendMessageToChannel(File file, MessageChannel channel) {
		if (file != null) {
			sendMessage(file, channel);
		} else {
			sendMessage(file, failedChannel);
		}
	}

	private void sendMessage(File file, MessageChannel channel) {
		if (channel != null) {
			LOG.debug("File: " + file + " to " + channel);
			Message msg =MessageBuilder.withPayload((file==null) ? "NN" : file).build();
			channel.send(msg);
		}
	}

}
