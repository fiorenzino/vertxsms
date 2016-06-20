package nz.fiore.vertxsms.common;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.UpdateResult;

/**
 * Created by fiorenzo on 03/06/16.
 */
public abstract class AbstractRepository<T> implements Repository<T> {

    protected JDBCClient jdbcClient;

    protected Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void create(T t, Handler<AsyncResult<UpdateResult>> handler) {
//        String sql = "INSERT INTO Whisky (name, origin) VALUES ?, ?";
        jdbcClient.getConnection(connection -> {
            if (connection.failed()) {
                logger.error("create : connection operation has failed...: " + connection.cause().getMessage());
                handler.handle(Future.failedFuture(connection.cause()));
            } else {
                System.out.println("getInsertQueryWithParams: " + getInsertQueryWithParams(t));
//                connection.result().updateWithParams(getInsertQuery(t), getInsertJsonArray(t), handler);
                connection.result().update(getInsertQueryWithParams(t), handler);
            }
            connection.result().close();
        });
    }

    @Override
    public void update(T t, Handler<AsyncResult<UpdateResult>> handler) {
        jdbcClient.getConnection(connection -> {
            if (connection.failed()) {
                logger.error("update : connection operation has failed...: " + connection.cause().getMessage());
                handler.handle(Future.failedFuture(connection.cause()));
            } else {
                System.out.println("getUpdateQuery: " + getUpdateQuery(t));
                connection.result().updateWithParams(getUpdateQuery(t), getUpdateJsonArray(t), handler);
            }
            connection.result().close();
        });
    }

    @Override
    public void list(JsonArray options, Handler<AsyncResult<ResultSet>> handler) {
        jdbcClient.getConnection(connection -> {
            if (connection.failed()) {
                logger.error("list : connection operation has failed...: " + connection.cause().getMessage());
                handler.handle(Future.failedFuture(connection.cause()));
            } else {
                connection.result().queryWithParams(getListQuery(), new JsonArray(), handler);
            }
            connection.result().close();
        });
    }

    @Override
    public void fetch(Object id, Handler<AsyncResult<ResultSet>> handler) {
        jdbcClient.getConnection(connection -> {
            if (connection.failed()) {
                logger.error("fetch : connection operation has failed...: " + connection.cause().getMessage());
                handler.handle(Future.failedFuture(connection.cause()));
            } else {
                connection.result().queryWithParams(getFetchQuery(), new JsonArray().add(id), handler);
            }
            connection.result().close();
        });
    }

    @Override
    public void delete(Object id, Handler<AsyncResult<UpdateResult>> handler) {
        jdbcClient.getConnection(connection -> {
            if (connection.failed()) {
                logger.error("delete : connection operation has failed...: " + connection.cause().getMessage());
                handler.handle(Future.failedFuture(connection.cause()));
            } else {
                connection.result().updateWithParams(getDeleteQuery(), new JsonArray().add(id), handler);
            }
            connection.result().close();
        });

    }


    public abstract String getInsertQuery(T object);

    public abstract String getInsertQueryWithParams(T object);



    public abstract JsonArray getInsertJsonArray(T object);

    public abstract String getUpdateQuery(T object);

    public abstract JsonArray getUpdateJsonArray(T object);


    public abstract String getDeleteQuery();

    public abstract String getFetchQuery();

    public abstract String getListQuery();

}
