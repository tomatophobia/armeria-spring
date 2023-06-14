package more.practice.armeriaspring.config;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.client.logging.LoggingClient;
import com.linecorp.armeria.common.*;
import com.linecorp.armeria.common.metric.MeterIdPrefixFunction;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.annotation.ExceptionHandlerFunction;
import com.linecorp.armeria.server.healthcheck.HealthChecker;
import com.linecorp.armeria.server.metric.MetricCollectingService;
import com.linecorp.armeria.server.metric.MetricCollectingServiceBuilder;
import com.linecorp.armeria.server.metric.PrometheusExpositionService;
import com.linecorp.armeria.server.tomcat.TomcatService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import com.linecorp.armeria.spring.DocServiceConfigurator;
import com.linecorp.armeria.spring.HealthCheckServiceConfigurator;
import com.linecorp.armeria.spring.MetricCollectingServiceConfigurator;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import more.practice.armeriaspring.controller.AuthDecorator;
import more.practice.armeriaspring.controller.BadRequestExceptionHandler;
import more.practice.armeriaspring.controller.JokeAnnotatedService;
import more.practice.armeriaspring.controller.TodoAnnotatedService;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static com.linecorp.armeria.common.HttpHeaderNames.AUTHORIZATION;

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
    public ArmeriaServerConfigurator armeriaServerConfigurator(
            TomcatService tomcatService,
            TodoAnnotatedService todoAnnotatedService,
            JokeAnnotatedService jokeAnnotatedService
    ) {
//        return new ArmeriaServerConfigurator() {
//            @Override
//            public void configure(ServerBuilder serverBuilder) {
//            serverBuilder
//                    .defaultVirtualHost()
//                    .service("/test", (ctx, req) -> HttpResponse.of(HttpStatus.OK))
//                    .serviceUnder("/", tomcatService)
//                    .annotatedService("/armeria", todoAnnotatedService)
//                    .annotatedService("/armeria", jokeAnnotatedService);
//            }
//        };
        return serverBuilder -> {
            serverBuilder
                    .defaultVirtualHost()
                    .service("/test", (ctx, req) -> HttpResponse.of(HttpStatus.OK))
                    .serviceUnder("/", tomcatService)
                    .annotatedService("/armeria", todoAnnotatedService)
                    .annotatedService("/armeria", jokeAnnotatedService);

        };
    }

    @Bean
    @Order(3)
    public ArmeriaServerConfigurator armeriaServerConfigurator2() {
        return new ArmeriaServerConfigurator() {
            @Override
            public void configure(ServerBuilder serverBuilder) {

            }
        };
    }

    @Bean
    @Order(2)
    public Consumer<ServerBuilder> serverBuilderConsumer() {
        return new Consumer<ServerBuilder>() {
            private int num = 1;

            @Override
            public void accept(ServerBuilder serverBuilder) {

            }
        };
    }

    @Bean
    @Order(1)
    public Consumer<ServerBuilder> serverBuilderConsumer2() {
        return new Consumer<ServerBuilder>() {
            private int num = 2;

            @Override
            public void accept(ServerBuilder serverBuilder) {
            }
        };
    }

    @Bean
    public MetricCollectingServiceConfigurator metricCollectingServiceConfigurator() {
        return metricCollectingServiceBuilder -> metricCollectingServiceBuilder
                .successFunction((context, log) -> {
                    final int statusCode = log.responseHeaders().status().code();
                    return statusCode >= 200 && statusCode < 400;
                });
    }

    @Bean
    public MeterIdPrefixFunction meterIdPrefixFunction() {
        return MeterIdPrefixFunction.ofDefault("my.armeria.service");
    }

//    @Bean
//    public ExceptionHandlerFunction badRequestExceptionHandler() {
//        return new BadRequestExceptionHandler();
//    }

    @Bean
    public DependencyInjector dependencyInjector() {
        return DependencyInjector.ofSingletons(
                new BadRequestExceptionHandler(),
                new AuthDecorator((ctx, req) ->
                        CompletableFuture.supplyAsync(
                                () -> req.headers().get(AUTHORIZATION).equals("auth-token")
                        )
                )
        );
    }

    @Bean
    public DocServiceConfigurator docServiceConfigurator() {
        return docServiceBuilder -> docServiceBuilder
                .exampleRequests(TodoAnnotatedService.class, "create", "{\"id\":\"42\", \"value\":\"foo bar\"}");
    }

    @Bean
    public HealthCheckServiceConfigurator healthCheckServiceConfigurator() {
        return healthCheckServiceBuilder -> healthCheckServiceBuilder
                .updatable(true)
                .startUnhealthy();
    }
}
