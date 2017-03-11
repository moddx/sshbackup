package org.tuxship.sshbackup;


import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by Matthias Ervens on 28.02.2017.
 */
@Configuration
@PropertySource("classpath:app.properties")
@ComponentScan(basePackages = {"org.tuxship.sshbackup"})
public class MainConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}

