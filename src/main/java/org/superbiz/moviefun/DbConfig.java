package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.movies.Movie;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    @Qualifier("albums")
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        HikariDataSource pool = new HikariDataSource();
        pool.setDataSource(dataSource);
        pool.setMaximumPoolSize(30);

        return pool;
    }

    @Bean
    @Qualifier("movies")
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);

        HikariDataSource pool = new HikariDataSource();
        pool.setDataSource(dataSource);
        pool.setMaximumPoolSize(30);

        return pool;
    }

    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabase(Database.MYSQL);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);
        adapter.setShowSql(true);
        return adapter;
    }

    @Bean
    @Qualifier("albums")
    public LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(@Qualifier("albums") DataSource albumsDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(albumsDataSource);
        factoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        factoryBean.setPackagesToScan(Album.class.getPackage().getName());
        factoryBean.setPersistenceUnitName("pu_albums");

        return factoryBean;
    }

    @Bean
    @Qualifier("movies")
    public LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(@Qualifier("movies")DataSource moviesDataSource, HibernateJpaVendorAdapter hibernateJpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(moviesDataSource);
        factoryBean.setJpaVendorAdapter(hibernateJpaVendorAdapter);
        factoryBean.setPackagesToScan(Movie.class.getPackage().getName());
        factoryBean.setPersistenceUnitName("pu_movies");

        return factoryBean;
    }

    @Bean
    @Qualifier("albums")
    public PlatformTransactionManager albumsPlatformTransactionManager(@Qualifier("albums")LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean){
        PlatformTransactionManager tx = new JpaTransactionManager(albumsLocalContainerEntityManagerFactoryBean.getObject());
        return tx;
    }

    @Bean
    @Qualifier("movies")
    public PlatformTransactionManager moviesPlatformTransactionManager(@Qualifier("movies")LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean){
        PlatformTransactionManager tx = new JpaTransactionManager(moviesLocalContainerEntityManagerFactoryBean.getObject());
        return tx;
    }
}
