package com.heb.pm;

import com.heb.pm.core.repository.UserSearchRepository;
import com.heb.pm.util.jpa.DatasourceQueryLogger;
import com.heb.pm.util.security.wsag.ClientInfoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


/**
 * Configuration class for the test profile.
 *
 * @author d116773
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = {"com.heb.pm.core.repository", "com.heb.pm.dao.core.entity",
		"com.heb.pm.dictionary.repository", "com.heb.pm.dictionary.entity"})
public class TestConfiguration {

	/**
	 * Simulates the class to search for users since we don't want to connect to OVD for tests.
	 *
	 * @return A class that simulates the class to search for users.
	 */
	@Bean
	public UserSearchRepository userSearchRepository() {
		return new TestUserRepository();
	}

	/**
	 * Makes a JdbcTemplate for ARBAF to use. Here, we're putting all the tables in the
	 * main H2 DB. so we can use the primary connection.
	 *
	 * @param dataSource
	 * @return A JdbcTemplate for ARBAF to use.
	 */
	@Bean
	@Qualifier("arbafJdbcTemplate")
	public JdbcTemplate arbafJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	/**
	 * Constructs a ClientInfoService bean.
	 *
	 * @return A ClientInfoService bean.
	 */
	@Bean
	public ClientInfoService clientInfoService() {
		return new ClientInfoService();
	}

	@Bean
	public DatasourceQueryLogger datasourceQueryLogger() {
		return new DatasourceQueryLogger();
	}

}
