package nz.fiore.vertxsms.common;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import nz.fiore.vertxsms.model.Message;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fiorenzo on 03/06/16.
 */
abstract public class AbstractRepositoryRs<T> extends AbstractVerticle {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected Repository<T> repository;
    protected Router router;
    protected String path;

    public AbstractRepositoryRs(Router router, Repository<T> repository, Vertx vertx, String path) {
        this.router = router;
        this.repository = repository;
        this.vertx = vertx;
        this.path = path;
    }


    public AbstractRepositoryRs() {
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


    protected void startWebApp(Handler<AsyncResult<HttpServer>> next) {
        router.post(this.path).handler(this::simple);
        router.get(this.path).handler(this::getList);
        router.get(this.path + "/:id").handler(this::fetch);
//        router.put(this.path + "/:id").handler(this::update);
//        router.delete(this.path + ":id").handler(this::delete);
        next.handle(Future.succeededFuture());
    }


    protected abstract void simple(RoutingContext routingContext);


    protected void create(RoutingContext routingContext) {
        T t = fromBodyAsJson(routingContext.getBodyAsJson());
        logger.error("RICEVUTO:" + t);

        this.repository.create(t, single -> {
            if (single.failed()) {
                end404(routingContext, single.cause().getMessage());
                return;
            }
            logger.info("_id: " + single.result().getKeys());
            HttpServerResponse response = routingContext.response()
                    .setStatusCode(201)
                    .putHeader("content-type",
                            "application/json; charset=utf-8");
            allowOrigin(routingContext, response)
                    .end(Json.encodePrettily(single.result()));
        });

    }

    protected void fetch(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            end404(routingContext, "no id");
            return;
        }
        this.repository.fetch(Long.valueOf(id), result -> {
            if (result.failed()) {
                end404(routingContext, result.cause().getMessage());
                return;
            }
            if (result.result().getNumRows() > 0) {
                HttpServerResponse response = routingContext.response()
                        .setStatusCode(200)
                        .putHeader("content-type",
                                "application/json; charset=utf-8");
                allowOrigin(routingContext, response)
                        .end(Json.encodePrettily(result.result().getRows().get(0)));
            } else {
                HttpServerResponse response = routingContext.response()
                        .setStatusCode(401)
                        .putHeader("content-type",
                                "application/json; charset=utf-8");
                allowOrigin(routingContext, response)
                        .end();
            }

        });
    }


    protected void update(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            end404(routingContext, "no id");
            return;
        }
        T t = decode(routingContext.getBodyAsString());
        this.repository.update(t,
                updated -> {
                    if (updated.failed()) {
                        end404(routingContext, updated.cause().getMessage());
                        return;
                    }
                    HttpServerResponse response = routingContext.response()
                            .putHeader("content-type",
                                    "application/json; charset=utf-8");
                    allowOrigin(routingContext, response)
                            .end(Json.encodePrettily(updated.result()));

                });
    }

    protected void delete(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            end404(routingContext, "no id");
            return;
        }
        this.repository.delete(
                Long.valueOf(id),
                deleted -> {
                    if (deleted.failed()) {
                        end404(routingContext, deleted.cause().getMessage());
                        return;
                    }
                    HttpServerResponse response = routingContext.response()
                            .setStatusCode(204);
                    allowOrigin(routingContext, response).end();
                }
        );

    }

    protected void getList(RoutingContext routingContext) {
        JsonArray jsonArray = new JsonArray();

        this.repository.list(jsonArray,
                list -> {
                    if (list.failed()) {
                        end404(routingContext, list.cause().getMessage());
                        return;
                    }
                    List<T> ts
                            = list.result().getRows().stream().map(this::fromBodyAsJson).collect(Collectors.toList());
                    HttpServerResponse response = routingContext.response()
                            .putHeader("content-type",
                                    "application/json; charset=utf-8");
                    allowOrigin(routingContext, response).end(Json.encodePrettily(ts));
                }
        );
    }


    protected void end404(RoutingContext routingContext, String msg) {
        HttpServerResponse response = routingContext.response()
                .setStatusCode(404).setStatusMessage("ERROR CONTEXT: " + msg);
        allowOrigin(routingContext, response)
                .end();
    }


    public abstract T fromBodyAsJson(JsonObject jsonObject);

    public abstract T decode(String jsonString);

    protected HttpServerResponse allowOrigin(RoutingContext routingContext, HttpServerResponse response) {
        if (routingContext.request().getHeader("Origin") != null) {
            response.putHeader("Access-Control-Allow-Origin",
                    routingContext.request().getHeader("Origin"));
        }
        return response;
    }
}


