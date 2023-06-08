package more.practice.armeriaspring.controller;

import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.server.DecoratingHttpServiceFunction;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.auth.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AuthDecorator implements DecoratingHttpServiceFunction {

    private static final Logger logger = LoggerFactory.getLogger(AuthDecorator.class);
    private final Authorizer<HttpRequest> authorizer;

    public AuthDecorator(Authorizer<HttpRequest> authorizer) {
        this.authorizer = authorizer;
    }

    @Override
    public HttpResponse serve(HttpService delegate, ServiceRequestContext ctx, HttpRequest req) throws Exception {
        logger.info("인증 서버 동작");
        return HttpResponse.from(authorizer.authorize(ctx, req).handle((result, cause) -> {
            try {
                if (cause == null && result) {
                    return delegate.serve(ctx, req);
                }
                return HttpResponse.of(HttpStatus.UNAUTHORIZED);
            } catch (Exception e) {
                return Exceptions.throwUnsafely(e);
            }
        }));
    }
}
