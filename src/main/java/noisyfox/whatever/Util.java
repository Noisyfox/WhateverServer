package noisyfox.whatever;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Noisyfox on 6/9/2014.
 */
public class Util {
    static Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader(" x-forwarded-for ");
        if (ip != null) {
            Matcher m = ipPattern.matcher(ip);
            if (m.find()) {
                return m.group();
            }
        }

        ip = request.getHeader(" Proxy-Client-IP ");
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getHeader(" WL-Proxy-Client-IP ");
        }
        if (ip == null || ip.length() == 0 || " unknown ".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static Connection connectDB() {
        final String driver = "com.mysql.jdbc.Driver";
        final String url = "jdbc:mysql://" + System.getenv("OPENSHIFT_MYSQL_DB_HOST") + ":" + System.getenv("OPENSHIFT_MYSQL_DB_PORT") + "/whatever";
        final String user = System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
        final String passwd = System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");

        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url, user, passwd);
            if (conn.isClosed()) {
                return null;
            }
            return conn;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
