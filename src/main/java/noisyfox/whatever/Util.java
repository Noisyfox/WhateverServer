package noisyfox.whatever;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static boolean isMultipartContent(HttpServletRequest request){
        String type = null;
        String type1 = request.getHeader("Content-Type");
        String type2 = request.getContentType();
        // If one value is null, choose the other value
        if (type1 == null && type2 != null) {
            type = type2;
        }
        else if (type2 == null && type1 != null) {
            type = type1;
        }
        // If neither value is null, choose the longer value
        else if (type1 != null) {
            type = (type1.length() > type2.length() ? type1 : type2);
        }

        if (type == null ||
                !type.toLowerCase().startsWith("multipart/form-data")) {
            return false;
        }

        return true;
    }

    public static String genJSON(boolean success, String message) {
        String json = null;
        try {
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("success", success);
            jsonObj.put("message", message);
            json = jsonObj.toString();
        } catch (JSONException e) {
            message = message.replace("\\", "\\\\");
            String _json = "{\"success\":" + (success ? "true" : "false")
                    + ",\"message\":\"" + message + "\"}";
            try {
                new JSONObject(_json);
                json = _json;
            } catch (JSONException ignored) {
            }
        }
        return json;
    }
}
