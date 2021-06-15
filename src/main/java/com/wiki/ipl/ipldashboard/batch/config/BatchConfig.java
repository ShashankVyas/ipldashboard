package com.wiki.ipl.ipldashboard.batch.config;


import javax.sql.DataSource;

import com.wiki.ipl.ipldashboard.batch.processor.MatchProcessor;
import com.wiki.ipl.ipldashboard.data.MatchInput;
import com.wiki.ipl.ipldashboard.model.Match;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    private static final String[] FIELD_NAMES = new String[] { "id", "city", "date", "player_of_match", "venue",
            "neutral_venue", "team1", "team2", "toss_winner", "toss_decision", "winner", "result", "result_margin",
            "eliminator", "method", "umpire1", "umpire2" };

    @Bean
    public FlatFileItemReader<MatchInput> reader() {
        return new FlatFileItemReaderBuilder<MatchInput>().name("MatchItemReader")
                .resource(new ClassPathResource("IPL Matches 2008-2020.csv")).delimited().names(FIELD_NAMES)
                .fieldSetMapper(new BeanWrapperFieldSetMapper<MatchInput>() {
                     {
                        setTargetType(MatchInput.class);
                    }

                }).build();
    }

    @Bean
    public MatchProcessor matchProcessor() {
        return new MatchProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Match> writer(DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Match>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO match (id," + "city," + "date," + "player_of_match," + "venue,"
                        + "first_innings_team," + "second_innings_team," + "toss_winner," + "toss_decision,"
                        + "match_winner," + "result," + "result_margin," + "umpire1," + "umpire2)" + " VALUES ("
                        + ":id," + ":city," + ":date," + ":playerOfMatch," + ":venue," + ":firstInningsTeam,"
                        + ":secondInningsTeam," + ":tossWinner," + ":tossDecision," + ":matchWinner," + ":result,"
                        + ":resultMargin," + ":umpire1," + ":umpire2" + ")")
                .dataSource(dataSource).build();
    }

    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory
        .get("importUserJob")
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step1)
        .end()
        .build();
    }

    @Bean
    public Step step1(JdbcBatchItemWriter<Match> writer){
        return stepBuilderFactory
        .get("step1")
        .<MatchInput, Match> chunk(10)
        .reader(reader())
        .processor(matchProcessor())
        .writer(writer)
        .build();
    }

}