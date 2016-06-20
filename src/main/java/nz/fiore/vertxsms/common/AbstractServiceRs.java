package nz.fiore.vertxsms.common;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;

/**
 * Created by fiorenzo on 03/06/16.
 */
abstract public class AbstractServiceRs extends AbstractVerticle {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected Router router;
    protected String path;

    public AbstractServiceRs(Router router, Vertx vertx, String path) {
        this.router = router;
        this.vertx = vertx;
        this.path = path;
    }


    public AbstractServiceRs() {
    }

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        logger.info("start " + getClass().getSimpleName());
        startWebApp((start) -> {
            if (start.succeeded()) {
                completeStartup(start, startFuture);
            } else {
                logger.error("error - startWebApp: " + start.cause().getMessage());
            }
        });
    }

    protected void completeStartup(AsyncResult<HttpServer> http, Future<Void> fut) {
        if (http.succeeded()) {
            logger.info(getClass().getSimpleName() + " Application started");
            fut.complete();
        } else {
            fut.fail(http.cause());
        }
    }

    protected abstract void startWebApp(Handler<AsyncResult<HttpServer>> next);

}


