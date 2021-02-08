package flow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.transaction.IntegrationResourceHolder;
import org.springframework.messaging.Message;

public class MoveFileExecutor {
	private static int zähler; // braucht nicht synchronisiert werden, da in einer synchronisierte Prozedur;
	Logger LOG = LogManager.getLogger(MoveFileExecutor.class);
	
	public MoveFileExecutor() {
		// TODO Auto-generated constructor stub
	}

	public File verschiebeDatei(Message<?> message, File directory) {
		if (message != null) {
			synchronized (MoveFileExecutor.class) {
				return verschiebeDatei(directory, message);
			}
		}
		return null;
	}

	private File verschiebeDatei(File directory, Message<?> message) {
		LOG.debug("Message " + message);
		File quelle = (File) message.getPayload();
		if (quelle.exists()) {
			File ziel = new File(erzeugeZielName(directory, quelle));
			try {
				Files.move(quelle.toPath(), ziel.toPath());
				LOG.debug("Die Datei " + quelle.getAbsolutePath() + " wurde verschoben nach " + ziel.getAbsolutePath());
				return ziel;
			} catch (IOException e) {
				LOG.error("kann die Datei " + quelle.getAbsolutePath() + " nicht nach " + ziel.getAbsolutePath()
						+ " verschieben Grund: " + e.getLocalizedMessage());
			}
		} else {
			LOG.error("die Datei " + quelle.getAbsolutePath() + " existiert nicht ");

		}
		return null;
	}

	private String erzeugeZielName(File directory, File quelle) {
		long millis = System.currentTimeMillis();
		zähler++;
		return directory.getPath() + File.separator + zähler + "_" + millis + "_" + quelle.getName();
	}

}
