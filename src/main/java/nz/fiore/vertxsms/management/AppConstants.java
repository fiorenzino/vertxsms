package nz.fiore.vertxsms.management;

/**
 * Created by fiorenzo on 28/05/16.
 */
public class AppConstants {

    //rest path
    public static final String APP_PATH = "/";
    public static final String API_PATH = APP_PATH + "api/";
    public static final String APP_CONTEST = API_PATH + "v1/";
    public static final String MESSAGES_PATH = APP_CONTEST + "messages";


    // mysql
    public static final String MYSQL_URL = "jdbc:mysql://localhost:3306/vota";
    public static final String MYSQL_DRIVERCLASS = "com.mysql.jdbc.Driver";
    public static final String MYSQL_USER = "root";
    public static final String MYSQL_PWD = "flower";
    public static final int MYSQL_MAXPOOLSIZE = 30;

    public static final int PORT = 8080;


}
