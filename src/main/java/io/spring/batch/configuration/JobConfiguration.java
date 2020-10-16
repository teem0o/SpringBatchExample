/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.batch.configuration;

import io.spring.batch.model.Customer;
import io.spring.batch.model.CustomerExcelRowMapper;
//import io.spring.batch.model.CustomerFieldSetMapper;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.mapping.BeanWrapperRowMapper;
import org.springframework.batch.item.excel.poi.PoiItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;

/**
 * @author Michael Minella
 */
@Configuration
public class JobConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	ItemReader<Customer> excelItemReader() {
		PoiItemReader<Customer> reader = new PoiItemReader<>();
		reader.setLinesToSkip(1);
		reader.setResource(new ClassPathResource("data/customer.xlsx"));
		reader.setRowMapper(excelRowMapper());
		return reader;
	}
	private RowMapper<Customer> excelRowMapper() {
		BeanWrapperRowMapper<Customer> rowMapper = new BeanWrapperRowMapper<>();
		rowMapper.setTargetType(Customer.class);
//		return rowMapper;
		return new CustomerExcelRowMapper();
	}
//	@Bean
//	public FlatFileItemReader<Customer> customerItemReader() {
//		FlatFileItemReader<Customer> reader = new FlatFileItemReader<>();
//
//		reader.setLinesToSkip(1);
//		reader.setResource(new ClassPathResource("/data/customer.csv"));
//		reader.setLineMapper(customerLineMapper());
//
//		return reader;
//	}
//	private LineMapper<Customer> customerLineMapper(){
//		DefaultLineMapper<Customer> customerLineMapper = new DefaultLineMapper<>();
//		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
//		tokenizer.setNames(new String[] {"id", "firstName", "lastName", "birthdate"});
//
//		customerLineMapper.setLineTokenizer(tokenizer);
//		customerLineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
//		customerLineMapper.afterPropertiesSet();
//		return customerLineMapper;
//	}

//	@Bean
//	public ItemWriter<Customer> customerItemWriterConsole() {
//		return items -> {
//			for (Customer item : items) {
//				System.out.println(item.toString());
//			}
//		};
//	}

	@Bean
	public JdbcBatchItemWriter<Customer> customerItemWriter() {
		JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("INSERT INTO CUSTOMER VALUES (:id, :firstName, :lastName, :birthdate)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();

		return itemWriter;
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
				.allowStartIfComplete(true)
				.<Customer, Customer>chunk(10)
//				.reader(customerItemReader())
				.reader(excelItemReader())
				.writer(customerItemWriter())
				.build();
	}

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
				.incrementer(new RunIdIncrementer())
				.flow(step1())
				.end()
				.build();
	}
}
