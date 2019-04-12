package com.quantchi.scheduler;

import org.apache.derby.jdbc.EmbeddedDataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@ConditionalOnMissingBean(DataSource.class)
public class DerbyConfiguration {

  @Bean
  public DataSource derbyEmbedDatasource(){
    EmbeddedDataSource dataSource = new EmbeddedDataSource();
    dataSource.setCreateDatabase("schedule.derby");
    return dataSource;
  }

}
