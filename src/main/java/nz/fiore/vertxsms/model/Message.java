package nz.fiore.vertxsms.model;

import io.vertx.core.json.JsonObject;

import java.time.Instant;

/**
 * Created by fiorenzo on 14/06/16.
 */
public class Message {
    private Long id;
    private String fromMsg;
    private String toMsg;
    private String text;
    private String type;

    public Message() {
    }

    public Message(String fromMsg, String toMsg, String type, String text) {
        this.fromMsg = fromMsg;
        this.toMsg = toMsg;
        this.type = type;
        this.text = text;
    }

    public Message(JsonObject json) {
        this.id = json.getLong("id");
        this.fromMsg = json.getString("fromMsg");
        this.toMsg = json.getString("toMsg");
        this.type = json.getString("type");
        this.text = json.getString("text");

    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("id", this.id)
                .put("fromMsg", this.fromMsg)
                .put("toMsg", this.toMsg)
                .put("type", this.type)
                .put("text", this.text);
        return jsonObject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromMsg() {
        return fromMsg;
    }

    public void setFromMsg(String fromMsg) {
        this.fromMsg = fromMsg;
    }

    public String getToMsg() {
        return toMsg;
    }

    public void setToMsg(String toMsg) {
        this.toMsg = toMsg;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", fromMsg='" + fromMsg + '\'' +
                ", toMsg='" + toMsg + '\'' +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
