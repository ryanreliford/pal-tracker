package io.pivotal.pal.tracker;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import javax.xml.crypto.Data;

@SpringBootApplication
public class PalTrackerApplication {

    @Bean
    public DataSource getDataSource() {
        MysqlDataSource result = new MysqlDataSource();
        result.setUrl(System.getenv("SPRING_DATASOURCE_URL"));
        return result;
    }

    @Bean
    public TimeEntryRepository getTimeEntryRepository() {
        return new InMemoryTimeEntryRepository();
    }

    @Bean
    @Primary
    public JdbcTimeEntryRepository getJdbcTimeEntryRepository(DataSource ds) {
        return new JdbcTimeEntryRepository(ds);
    }

    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }
}