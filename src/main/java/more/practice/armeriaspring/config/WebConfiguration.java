package more.practice.armeriaspring.config;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.client.logging.LoggingClient;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.MediaType;
import com.linecorp.armeria.server.healthcheck.HealthChecker;
import com.linecorp.armeria.server.tomcat.TomcatService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import more.practice.armeriaspring.controller.JokeAnnotatedService;
import more.practice.armeriaspring.controller.TodoAnnotatedService;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfiguration {
    @Bean
    public WebClient dadJokeAPIClient() {
        return WebClient.builder("https://icanhazdadjoke.com")
                .addHeader(HttpHeaderNames.ACCEPT, MediaType.JSON)
                .decorator(LoggingClient.newDecorator())
                .build();
    }

    public static Connector getConnector(ServletWebServerApplicationContext applicationContext) {
        final TomcatWebServer container = (TomcatWebServer) applicationContext.getWebServer();

        // Start the container to make sure all connectors are available.
        container.start();
        return container.getTomcat().getConnector();
    }

    @Bean
    public HealthChecker tomcatConnectorHealthChecker(ServletWebServerApplicationContext applicationContext) {
        final Connector connector = getConnector(applicationContext);
        return () -> connector.getState().isAvailable();
    }

    @Bean
    public TomcatService tomcatService(ServletWebServerApplicationContext applicationContext) {
        return TomcatService.of(getConnector(applicationContext));
    }

    @Bean
    public ArmeriaServerConfigurator armeriaServerConfigurator(TomcatService tomcatService, TodoAnnotatedService todoAnnotatedService, JokeAnnotatedService jokeAnnotatedService) {
        return serverBuilder -> {
            serverBuilder
                    .serviceUnder("/", tomcatService)
                    .annotatedService("/armeria", todoAnnotatedService)
                    .annotatedService("/armeria", jokeAnnotatedService);

        };
    }
}
