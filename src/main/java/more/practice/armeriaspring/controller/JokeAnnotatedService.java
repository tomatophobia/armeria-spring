package more.practice.armeriaspring.controller;

import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.annotation.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class JokeAnnotatedService {
    private static final Logger logger = LoggerFactory.getLogger(TodoAnnotatedService.class);

    private final WebClient dadJokeWebclient;

    @Autowired
    public JokeAnnotatedService(WebClient dadJokeWebclient) {
        this.dadJokeWebclient = dadJokeWebclient;
    }

    @Get("/joke")
    public HttpResponse get() {
        return dadJokeWebclient.get("/");
    }
}
