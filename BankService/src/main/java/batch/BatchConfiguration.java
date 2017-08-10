package batch;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.oxm.Marshaller; //Jaxb2Marshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;

import repositories.EinzahlungRepository;
import data.Datei;
import data.Einzahlung;
import data.KontoAuszug;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    private static final String OVERRIDDEN_BY_EXPRESSION = "";

    
    @Autowired
    public EinzahlungRepository einzahlungRepository;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    
    @Bean
    @Qualifier("myJob")
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer()).listener(listener)
                .flow(step1()).end().build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Einzahlung, Einzahlung> chunk(10)
                .reader(repoReader(OVERRIDDEN_BY_EXPRESSION))
                .processor(processor()).writer(writeToSystemOut()).build();
    }

    @Bean
    @JobScope
    public IteratorItemReader<Einzahlung> repoReader(
            @Value("#{jobParameters[datei]}") String dateiname) {
        return new IteratorItemReader<Einzahlung>(
                einzahlungRepository.findAll());
    }

    @Bean
    public ItemWriter<Einzahlung> writeToSystemOut() {
                ItemWriter<Einzahlung> writer = new ItemWriter<Einzahlung>() {
                    @Override
                    public void write(List<? extends Einzahlung> items)
                            throws Exception {
                        for(Einzahlung e : items) {
                            System.out.println(e);
                        }
                    }
                };        
         return writer;
    }

    @Bean
    public EinzahlungItemProcessor processor() {
        return new EinzahlungItemProcessor();
    }

    
    
    /*
    @Bean
    @JobScope
    public FlatFileItemReader<Einzahlung> reader(
            @Value("#{jobParameters[datei]}") String dateiname) {
        FlatFileItemReader<Einzahlung> reader = new FlatFileItemReader<Einzahlung>();

        reader.setResource(new ClassPathResource(dateiname));
        reader.setLineMapper(new DefaultLineMapper<Einzahlung>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames(new String[] { "firstName", "lastName" });
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<Einzahlung>() {
                    {
                        setTargetType(Einzahlung.class);
                    }
                });
            }
        });
        return reader;
    }


    @Bean
    public JdbcBatchItemWriter<Einzahlung> writer() {
        JdbcBatchItemWriter<Einzahlung> writer = new JdbcBatchItemWriter<Einzahlung>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Einzahlung>());
        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public ItemReader<Object> readerForXML() {
        StaxEventItemReader<Object> reader = new StaxEventItemReader<Object>();
        reader.setResource(new ClassPathResource("reports.xml"));
        reader.setFragmentRootElementName("parent");

        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(Datei.class, KontoAuszug.class,
                Einzahlung.class);

        reader.setUnmarshaller(unmarshaller);
        return reader;
    }
    */
}
