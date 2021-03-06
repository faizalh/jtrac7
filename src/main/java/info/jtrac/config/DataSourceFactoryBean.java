/*
 * Copyright 2002-2005 the original author or authors.
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

package info.jtrac.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

/**
 * spring factory bean to conditionally create the right data source
 * either embedded or apache dbcp or JNDI datasource
 */
public class DataSourceFactoryBean implements FactoryBean, DisposableBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private String validationQuery;
    private String dataSourceJndiName;

    private DataSource dataSource;

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public void setDataSourceJndiName(String dataSourceJndiName) {
        this.dataSourceJndiName = dataSourceJndiName;
    }

    public Object getObject() throws Exception {
        if (StringUtils.hasText(dataSourceJndiName)) {
            logger.info("JNDI datasource requested, looking up datasource from JNDI name: '" + dataSourceJndiName + "'");
            JndiObjectFactoryBean factoryBean = new JndiObjectFactoryBean();
            factoryBean.setJndiName(dataSourceJndiName);
            // "java:comp/env/" will be prefixed if the JNDI name doesn't already have it
            factoryBean.setResourceRef(true);
            // this step actually does the JNDI lookup
            try {
                factoryBean.afterPropertiesSet();
            } catch (Exception e) {
                logger.error("datasource init from JNDI failed : " + e);
                logger.error("aborting application startup");
                throw new RuntimeException(e);
            }
            dataSource = (DataSource) factoryBean.getObject();
        } else if (url.startsWith("jdbc:hsqldb:file")) {
            logger.info("embedded HSQLDB mode detected, switching on spring single connection data source");
            SingleConnectionDataSource ds = new SingleConnectionDataSource();
            ds.setUrl(url);
            ds.setDriverClassName(driverClassName);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setSuppressClose(true);
            dataSource = ds;
        } else {
            logger.info("Not using embedded HSQLDB or JNDI datasource, switching on Apache DBCP data source connection pooling");
            BasicDataSource ds = new BasicDataSource();
            ds.setUrl(url);
            ds.setDriverClassName(driverClassName);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setValidationQuery(validationQuery);
            ds.setTestOnBorrow(false);
            ds.setTestWhileIdle(true);
            ds.setTimeBetweenEvictionRunsMillis(600000);
            dataSource = ds;
        }
        return dataSource;
    }

    public Class getObjectType() {
        return DataSource.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() throws Exception {
        if (dataSource instanceof SingleConnectionDataSource) {
            logger.info("attempting to shut down embedded HSQLDB database");
            Connection con = dataSource.getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate("SHUTDOWN");
            stmt.close();
            con.close();
            logger.info("embedded HSQLDB database shut down successfully");
        } else if (dataSource instanceof BasicDataSource) {
            logger.info("attempting to close Apache DBCP data source");
            ((BasicDataSource) dataSource).close();
            logger.info("Apache DBCP data source closed successfully");
        } else {
            logger.info("context shutting down for JNDI datasource");
        }
    }

}
