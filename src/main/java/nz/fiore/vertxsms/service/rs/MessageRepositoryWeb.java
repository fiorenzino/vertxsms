package nz.fiore.vertxsms.service.rs;

import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.TemplateHandler;
import nz.fiore.vertxsms.common.AbstractRepositoryWeb;
import nz.fiore.vertxsms.common.Repository;
import nz.fiore.vertxsms.model.Message;

/**
 * Created by fiorenzo on 17/07/16.
 */
public class MessageRepositoryWeb extends AbstractRepositoryWeb<Message> {


    public MessageRepositoryWeb(Router router, Repository<Message> repository, Vertx vertx, TemplateHandler templateHandler, String path) {
        super(router, repository, vertx, templateHandler, path);
    }

    @Override
    public Message fromBodyAsJson(JsonObject jsonObject) {
        return new Message(jsonObject);
    }

    @Override
    public Message decode(String jsonString) {
        return Json.decodeValue(jsonString, Message.class);
    }
}