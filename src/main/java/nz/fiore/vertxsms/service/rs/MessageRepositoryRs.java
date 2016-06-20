package nz.fiore.vertxsms.service.rs;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import nz.fiore.vertxsms.common.AbstractRepositoryRs;
import nz.fiore.vertxsms.management.AppConstants;
import nz.fiore.vertxsms.model.Message;
import nz.fiore.vertxsms.repository.MessageRepository;

/**
 * Created by fiorenzo on 03/06/16.
 */
public class MessageRepositoryRs extends AbstractRepositoryRs<Message> {

    public MessageRepositoryRs(Router router, JDBCClient jdbcClient, Vertx vertx) {
        super(router, new MessageRepository(jdbcClient), vertx, AppConstants.MESSAGES_PATH);
    }


    public MessageRepositoryRs() {
    }

    @Override
    public Message fromBodyAsJson(JsonObject jsonObject) {
        return new Message(jsonObject);
    }


    @Override
    public Message decode(String jsonString) {
        return Json.decodeValue(jsonString, Message.class);
    }

    public void simple(RoutingContext routingContext) {
        String from = routingContext.request().getParam("From");
        logger.info("from:" + from);
        String to = routingContext.request().getParam("To");
        logger.info("to:" + to);
        String type = routingContext.request().getParam("Type");
        logger.info("type:" + type);
        String text = routingContext.request().getParam("Text");
        logger.info("text:" + text);
        String messageUUID = routingContext.request().getParam("MessageUUID");
        logger.info("messageUUID:" + messageUUID);
        Message message = new Message(from, to, type, text);
        this.repository.create(message, single -> {
            if (single.failed()) {
                end404(routingContext, single.cause().getMessage());
                return;
            }
            logger.info("_id: " + single.result().getKeys());
            HttpServerResponse response = routingContext.response()
                    .putHeader("content-type",
                            "application/xml; charset=utf-8");
            allowOrigin(routingContext, response).end("<Response></Response>");
        });


    }



}


