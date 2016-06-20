package nz.fiore.vertxsms.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import nz.fiore.vertxsms.management.AppConstants;
import nz.fiore.vertxsms.service.rs.MessageRepositoryRs;

import static nz.fiore.vertxsms.management.AppConstants.*;

/**
 * Created by fiorenzo on 14/06/16.
 */
public class MainVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger(MainVerticle.class);

    private JDBCClient jdbcClient;
    private boolean local = false;

    @Override
    public void start() throws Exception {
        Router router = Router.router(vertx);
        jdbcClient = JDBCClient.createShared(vertx, mysqlConfig(local));
        router.route().handler(BodyHandler.create());


        MessageRepositoryRs messageRepositoryRs = new MessageRepositoryRs(router, jdbcClient, vertx);
        vertx.deployVerticle(messageRepositoryRs);

        String address = System.getProperty("http.address");
        String port = System.getProperty("http.port");
        if (local) {
            address = "localhost";
            port = "8080";
        }
        router.routeWithRegex("^(?!/api).+").handler(StaticHandler.create("assets"));


        logger.info("address: " + address + ", port: " + port);
        HttpServerOptions options = new HttpServerOptions();
        options.setCompressionSupported(true);
        vertx.createHttpServer(options)
                .requestHandler(router::accept)
//                .listen(PORT);
                .listen(
                        Integer.valueOf(port), address);

    }

    @Override
    public void stop() throws Exception {
    }

    public static JsonObject mysqlConfig(boolean local) {
        String username = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        String password = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
        String host = System.getenv("OPENSHIFT_MYSQL_DB_HOST");
        String port = System.getenv("OPENSHIFT_MYSQL_DB_PORT");
        String appName = System.getenv("OPENSHIFT_APP_NAME");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + appName;
        logger.info("username:" + username + ",password:" + password + ",url:" + url);
        JsonObject pgConfig = new JsonObject();
        if (local) {
            pgConfig.put("url", MYSQL_URL)
                    .put("driver_class", MYSQL_DRIVERCLASS)
                    .put("user", MYSQL_USER)
                    .put("password", MYSQL_PWD)
                    .put("max_pool_size", MYSQL_MAXPOOLSIZE);
        } else {
            pgConfig.put("url", url)
                    .put("driver_class", MYSQL_DRIVERCLASS)
                    .put("user", username)
                    .put("password", password)
                    .put("max_pool_size", MYSQL_MAXPOOLSIZE);
        }
        return pgConfig;
    }
}
