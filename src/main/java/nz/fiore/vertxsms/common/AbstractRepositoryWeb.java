package nz.fiore.vertxsms.common;

import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.TemplateHandler;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by fiorenzo on 03/06/16.
 */
abstract public class AbstractRepositoryWeb<T> extends AbstractVerticle {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected Repository<T> repository;
    protected Router router;
    protected TemplateHandler templateHandler;

    protected String path;

    public AbstractRepositoryWeb(Router router, Repository<T> repository, Vertx vertx, TemplateHandler templateHandler, String path) {
        this.router = router;
        this.repository = repository;
        this.vertx = vertx;
        this.path = path;
        this.templateHandler = templateHandler;
    }


    public AbstractRepositoryWeb() {
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
        logger.info(this.path);
        router.route(this.path + "*").handler(templateHandler);
        router.get(this.path + "list.html").handler(this::getList);
        router.post(this.path).handler(this::create);
        router.get(this.path + "view.html/:id").handler(this::fetch);
        router.get(this.path + "edit.html/:id").handler(this::fetch);
        router.put(this.path + "view.html/:id").handler(this::update);
        router.delete(this.path + ":id").handler(this::delete);
        next.handle(Future.succeededFuture());
    }


    protected void create(RoutingContext routingContext) {
        T t = fromBodyAsJson(routingContext.getBodyAsJson());
        logger.error("RICEVUTO:" + t);

        this.repository.create(t, single -> {
            if (single.failed()) {
                Session session = routingContext.session();
                session.put("error", single.cause().getMessage());
                return;
            }
            Session session = routingContext.session();
            session.put("element", single.result());
        });

    }

    protected void fetch(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            Session session = routingContext.session();
            session.put("error", "no id");
            return;
        }
        this.repository.fetch(Long.valueOf(id), result -> {
            if (result.failed()) {
                Session session = routingContext.session();
                session.put("error", result.cause().getMessage());
                return;
            }
            Session session = routingContext.session();
            session.put("element", result.result().getRows().get(0));
        });
    }


    protected void update(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            Session session = routingContext.session();
            session.put("error", "no id");
            return;
        }
        T t = decode(routingContext.getBodyAsString());
        this.repository.update(t,
                updated -> {
                    if (updated.failed()) {
                        Session session = routingContext.session();
                        session.put("error", updated.cause().getMessage());
                        return;
                    }
                    Session session = routingContext.session();
                    session.put("element", updated.result());

                });
    }

    protected void delete(RoutingContext routingContext) {
        String id = routingContext.request().getParam("id");
        if (id == null) {
            Session session = routingContext.session();
            session.put("error", "no id");
            return;
        }
        this.repository.delete(
                Long.valueOf(id),
                deleted -> {
                    if (deleted.failed()) {
                        Session session = routingContext.session();
                        session.put("error", deleted.cause().getMessage());
                        return;
                    }
                }
        );

    }

    protected void getList(RoutingContext routingContext) {
        JsonArray jsonArray = new JsonArray();

        this.repository.list(jsonArray,
                list -> {
                    if (list.failed()) {
                        Session session = routingContext.session();
                        session.put("error", list.cause().getMessage());
                        return;
                    }
                    List<T> ts
                            = list.result().getRows().stream().map(this::fromBodyAsJson).collect(Collectors.toList());
                    Session session = routingContext.session();
                    session.put("list", ts);
                }
        );
    }


    public abstract T fromBodyAsJson(JsonObject jsonObject);

    public abstract T decode(String jsonString);

}


