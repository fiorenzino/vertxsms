/**
 * Created by fiorenzo on 29/06/16.
 */
public class Vote {
    public int photo;
    public int painture;
    public int sculpture;

    public Vote() {
    }

    @Override
    public String toString() {
        return "Vote{" +
                (photo > 0 ? "photo=" + photo : "") +
                (painture > 0 ? ", painture=" + painture : "") +
                (sculpture > 0 ? ", sculpture=" + sculpture : "") +
                '}';
    }

    public boolean isComplete() {
        return (photo > 0) && (painture > 0) && (sculpture > 0);

    }
}
