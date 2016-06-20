package nz.fiore.vertxsms.repository;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import nz.fiore.vertxsms.common.AbstractRepository;
import nz.fiore.vertxsms.model.Message;

/**
 * Created by fiorenzo on 03/06/16.
 */
public class MessageRepository extends AbstractRepository<Message> {

    static String INSERT = "INSERT INTO messages ( ";
    static String VALUES = " VALUES( ";


    public MessageRepository() {
    }

    public MessageRepository(JDBCClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }


    @Override
    public String getInsertQueryWithParams(Message message) {
        JsonArray jsonArray = new JsonArray();
        StringBuffer sb = new StringBuffer();
        StringBuffer values = new StringBuffer();


        if (message.getFromMsg() != null) {
            jsonArray.clear();
            jsonArray.add(message.getFromMsg());
            sb.append(", fromMsg");
            values.append(", '" + jsonArray.getString(0) + "'");
        }

        if (message.getToMsg() != null) {
            jsonArray.clear();
            jsonArray.add(message.getToMsg());
            sb.append(", toMsg");
            values.append(", '" + jsonArray.getString(0) + "'");
        }

        if (message.getText() != null) {
            jsonArray.clear();
            jsonArray.add(message.getText());
            sb.append(", text");
            values.append(", '" + jsonArray.getString(0) + "'");
        }

        if (message.getType() != null) {
            jsonArray.clear();
            jsonArray.add(message.getType());
            sb.append(", type");
            values.append(", '" + jsonArray.getString(0) + "'");
        }

        return new StringBuffer(INSERT).append(sb.substring(1))
                .append(" )").append(VALUES)
                .append(values.substring(1))
                .append(")").toString();
    }


    @Override
    public String getInsertQuery(Message message) {
        StringBuffer sb = new StringBuffer();
        StringBuffer values = new StringBuffer();

        if (message.getFromMsg() != null) {
            sb.append(", fromMsg");
            values.append(", ?");
        }
        if (message.getToMsg() != null) {
            sb.append(", toMsg");
            values.append(", ?");
        }
        if (message.getText() != null) {
            sb.append(", text");
            values.append(", ?");
        }
        if (message.getType() != null) {
            sb.append(", type");
            values.append(", ?");
        }

        return new StringBuffer(INSERT).append(sb.substring(1))
                .append(" )").append(VALUES)
                .append(values.substring(1))
                .append(")").toString();
    }

    @Override
    public JsonArray getInsertJsonArray(Message message) {
        JsonArray jsonArray = new JsonArray();
        if (message.getFromMsg() != null) {
            jsonArray.add(message.getFromMsg());
        }
        if (message.getToMsg() != null) {
            jsonArray.add(message.getToMsg());
        }
        if (message.getText() != null) {
            jsonArray.add(message.getText());
        }
        if (message.getType() != null) {
            jsonArray.add(message.getType());
        }

        return jsonArray;
    }

    @Override
    public String getUpdateQuery(Message configurazione) {
        return " UPDATE messages set fromMsg = ?, toMsg=?, type=?, text=? where id = ?";
    }

    @Override
    public JsonArray getUpdateJsonArray(Message configurazione) {
        return getInsertJsonArray(configurazione)
                .add(configurazione.getId());
    }

    @Override
    public String getDeleteQuery() {
        return "delete fromMsg messages where id = ?";

    }

    @Override
    public String getFetchQuery() {
        return "select * from messages where id = ?";
    }

    @Override
    public String getListQuery() {
        return "select * from messages ";
    }


}
