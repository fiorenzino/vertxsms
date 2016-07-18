import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import nz.fiore.vertxsms.model.Message;
import nz.fiore.vertxsms.repository.MessageRepository;
import nz.fiore.vertxsms.verticle.MainVerticle;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@RunWith(VertxUnitRunner.class)
public class ScarichiRepositoryTest {

    Vertx vertx;
    JDBCClient jdbcClient;
    MessageRepository repo;
    Map<String, Vote> votes = new HashMap<>();
    Map<Integer, String> oevres = new HashMap<>();
    Map<Integer, Integer> numbers = new HashMap<>();

    @Before
    public void init() {
        vertx = Vertx.vertx();
        jdbcClient = JDBCClient.createNonShared(vertx, MainVerticle.mysqlConfig(true));
        repo = new MessageRepository(jdbcClient);
        oevres.put(1, "Scultura");
        oevres.put(2, "Pittura");
        oevres.put(3, "Pittura");
        oevres.put(4, "Fotografia");
        oevres.put(5, "Fotografia");
        oevres.put(6, "Pittura");
        oevres.put(27, "Pittura");
        oevres.put(7, "Pittura");
        oevres.put(8, "Fotografia");
        oevres.put(9, "Scultura");
        oevres.put(10, "Pittura");
        oevres.put(11, "Pittura");
        oevres.put(12, "Pittura");
        oevres.put(13, "Fotografia");
        oevres.put(14, "Fotografia");
        oevres.put(15, "Pittura");
        oevres.put(17, "Pittura");
        oevres.put(18, "Scultura");
        oevres.put(19, "Pittura");
        oevres.put(20, "Pittura");
        oevres.put(21, "Pittura");
        oevres.put(22, "Pittura");
        oevres.put(23, "Pittura");
        oevres.put(24, "Pittura");
        oevres.put(25, "Fotografia");
        oevres.put(28, "Scultura");
        oevres.put(29, "Fotografia");
        oevres.put(30, "Fotografia");


        numbers.put(1, 0);
        numbers.put(2, 0);
        numbers.put(3, 0);
        numbers.put(4, 0);
        numbers.put(5, 0);
        numbers.put(6, 0);
        numbers.put(27, 0);
        numbers.put(7, 0);
        numbers.put(8, 0);
        numbers.put(9, 0);
        numbers.put(10, 0);
        numbers.put(11, 0);
        numbers.put(12, 0);
        numbers.put(13, 0);
        numbers.put(14, 0);
        numbers.put(15, 0);
        numbers.put(17, 0);
        numbers.put(18, 0);
        numbers.put(19, 0);
        numbers.put(20, 0);
        numbers.put(21, 0);
        numbers.put(22, 0);
        numbers.put(23, 0);
        numbers.put(24, 0);
        numbers.put(25, 0);
        numbers.put(28, 0);
        numbers.put(29, 0);
        numbers.put(30, 0);
    }


    @Test
    public void list(TestContext context) {
        final Async async = context.async();


        repo.list(new JsonArray(), resultList -> {
            if (resultList.succeeded() && resultList.result().getNumRows() > 0) {
                List<Message> messages
                        = resultList.result().getRows().stream().map(Message::new).collect(Collectors.toList());
                AtomicInteger scarti = new AtomicInteger();
                messages.stream().forEach(msg -> {
                    Vote vote = new Vote();
                    if (votes.containsKey(msg.getFromMsg())) {
                        vote = votes.get(msg.getFromMsg());
                        if (vote.isComplete()) {
                            System.out.println("******************");
                            System.out.println("GIA VOTATO");
                            System.out.println(msg.getFromMsg() + ":" + vote);
                            System.out.println(msg);
                            System.out.println("******************");
                            scarti.incrementAndGet();
                            return;
                        } else {
                            System.out.println("VOTO NON COMPLETO");
                        }
                    }

                    if (msg.getText() != null && !msg.getText().trim().isEmpty()) {
                        int uno, due, tre;
                        if (msg.getText().contains(",")) {
                            String[] vals = msg.getText().trim().split(",");
                            if (vals.length > 0) {
                                uno = Integer.valueOf(vals[0].trim());
                                if (oevres.containsKey(uno) && oevres.get(uno).equals("Pittura"))
                                    vote.painture = uno;
                                if (oevres.containsKey(uno) && oevres.get(uno).equals("Scultura"))
                                    vote.sculpture = uno;
                                if (oevres.containsKey(uno) && oevres.get(uno).equals("Fotografia"))
                                    vote.photo = uno;
                            }
                            if (vals.length > 1) {
                                due = Integer.valueOf(vals[1].trim());
                                if (oevres.containsKey(due) && oevres.get(due).equals("Pittura"))
                                    vote.painture = due;
                                if (oevres.containsKey(due) && oevres.get(due).equals("Scultura"))
                                    vote.sculpture = due;
                                if (oevres.containsKey(due) && oevres.get(due).equals("Fotografia"))
                                    vote.photo = due;
                            }
                            if (vals.length > 2) {
                                tre = Integer.valueOf(vals[2].trim());
                                if (oevres.containsKey(tre) && oevres.get(tre).equals("Pittura"))
                                    vote.painture = tre;
                                if (oevres.containsKey(tre) && oevres.get(tre).equals("Scultura"))
                                    vote.sculpture = tre;
                                if (oevres.containsKey(tre) && oevres.get(tre).equals("Fotografia"))
                                    vote.photo = tre;
                            }
                        } else {
                            uno = Integer.valueOf(msg.getText().trim().replace(",", ""));
                            if (oevres.containsKey(uno) && oevres.get(uno).equals("Pittura"))
                                vote.painture = uno;
                            if (oevres.containsKey(uno) && oevres.get(uno).equals("Scultura"))
                                vote.sculpture = uno;
                            if (oevres.containsKey(uno) && oevres.get(uno).equals("Fotografia"))
                                vote.photo = uno;
                        }
                    }
//                    System.out.println(vote.toString());
                    votes.put(msg.getFromMsg(), vote);


                });

                System.out.println("VOTI: " + votes.size());
                System.out.println("SCARTI: " + scarti.get());
                votes.values().stream().forEach(vote -> {
                    System.out.println(vote);
                    if (vote.photo > 0) {
                        int val = numbers.get(vote.photo) + 1;
                        numbers.put(vote.photo, val);
                        System.out.println(vote.photo + ":" + val);
                    }
                    if (vote.painture > 0) {
                        int val = numbers.get(vote.painture) + 1;
                        numbers.put(vote.painture, val);
                        System.out.println(vote.painture + ":" + val);
                    }
                    if (vote.sculpture > 0) {
                        int val = numbers.get(vote.sculpture) + 1;
                        numbers.put(vote.sculpture, val);
                        System.out.println(vote.sculpture + ":" + val);

                    }
                });
                System.out.println("----------------------");
                System.out.println("----------------------");
                System.out.println("----------------------");
                numbers.keySet().stream().forEach(key -> {
                    System.out.println(key + "[" + oevres.get(key) + "]: " + numbers.get(key));
                });
                System.out.println("ok: " + votes.size());
                System.out.println("scarti: " + scarti.get());
            } else {
                resultList.cause().printStackTrace();
                Assert.fail();
            }
            async.complete();
        });

    }

}
