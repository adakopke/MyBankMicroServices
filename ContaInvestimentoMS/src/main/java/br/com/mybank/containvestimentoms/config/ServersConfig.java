package br.com.mybank.containvestimentoms.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "my.app")
@Configuration("serversProperties")
@Getter
@Setter
public class ServersConfig {

    private String contacorrenteMS;

}
