package batch;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import repositories.EinzahlungRepository;
import data.Einzahlung;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	
    public EinzahlungRepository einzahlungRepository;


	@Autowired
	public JobCompletionNotificationListener(EinzahlungRepository einzahlungRepository) {
		this.einzahlungRepository = einzahlungRepository;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");

			for (Einzahlung einzahlung : einzahlungRepository.findAll()) {
				log.info("Found <" + einzahlung + "> in the database.");
			}

		}
	}
}
