package com.objetivos.batch.demo.config;

import com.objetivos.batch.demo.domain.Persona;
import com.objetivos.batch.demo.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final PersonaRepository repository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    @Bean
    public FlatFileItemReader<Persona> itemReader(){
        FlatFileItemReader<Persona> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/personas.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1); //Se salta la primera línea del csv
        itemReader.setLineMapper(lineMapper());
        return itemReader;
    }

    private LineMapper<Persona> lineMapper(){
        DefaultLineMapper<Persona> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(","); //Delimitador entre cada atributo del csv.
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("dni", "nombre", "apellido", "sexo");

        BeanWrapperFieldSetMapper<Persona> fieldSetMapper = new BeanWrapperFieldSetMapper<>(); //Ayuda a transformar cada linea del csv a una Persona
        fieldSetMapper.setTargetType(Persona.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public PersonaProcessor processor(){
        return new PersonaProcessor();
    }

    @Bean
    public RepositoryItemWriter<Persona> write(){
        RepositoryItemWriter<Persona> writer = new RepositoryItemWriter<>();
        writer.setRepository(repository);
        writer.setMethodName("save"); //El método del repository a guardar
        return writer;
    }

    @Bean
    public Step armarSteps(){
        return new StepBuilder("importar csv", jobRepository)
                .<Persona, Persona>chunk(10, platformTransactionManager)
                .reader(itemReader())
                .processor(processor())
                .writer(write())
                .build();
    }

    @Bean
    public Job runJob(){
        return new JobBuilder("importar personas", jobRepository)
                .start(armarSteps())
                .build();
    }
}
