package eu.arrowhead.common.log4j2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.arrowhead.common.Constants;

public class JDBCConnectionFactoryForLog4J2 {
	
	//=================================================================================================
	// members
	
	private static Properties props;
	private static DataSource dataSource;

	static {
		try {
			init();
		} catch (final IOException ex) {
			// this class' purpose to configure logging so in case of exceptions we can't use logging
			System.out.println(ex.getMessage()); 
			ex.printStackTrace(); 
		}
	}

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static Connection getConnection() throws SQLException {
		if (dataSource == null) {
			final HikariConfig config = new HikariConfig();
			config.setJdbcUrl(props.getProperty(Constants.DATABASE_URL));
			config.setUsername(props.getProperty(Constants.DATABASE_USER));
			config.setPassword(props.getProperty(Constants.DATABASE_PASSWORD));
			config.setDriverClassName(props.getProperty(Constants.DATABASE_DRIVER_CLASS));
			
			dataSource = new HikariDataSource(config);
		}
		
		return dataSource.getConnection();
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private static void init() throws IOException {
		InputStream propStream = null;
		
		try {
			final File propertiesFile = new File(Constants.APPLICATION_PROPERTIES);
			if (!propertiesFile.exists()) {
				propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.APPLICATION_PROPERTIES);
			} else {
				propStream = new FileInputStream(propertiesFile);
			}
			
			final Properties temp = new Properties();
			temp.load(propStream);
	
			props = temp;
		} finally {
			if (propStream != null) {
				propStream.close();
			}
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private JDBCConnectionFactoryForLog4J2() {
		throw new UnsupportedOperationException();
	}
}