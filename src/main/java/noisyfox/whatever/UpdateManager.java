package noisyfox.whatever;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Noisyfox on 6/10/2014.
 */
public class UpdateManager {

    private static VersionData mNewestVersion[];
    private static List<VersionData> mAllVersion;

    private static final String SQL_TABLE_W_UPDATE = "w_update";
    private static final String SQL_CREATE_TABLE_W_UPDATE = "CREATE TABLE IF NOT EXISTS `" + SQL_TABLE_W_UPDATE + "` (" +
            "  `id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT," +
            "  `os` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0'," +
            "  `version` INT(10) UNSIGNED NOT NULL DEFAULT '0'," +
            "  `version_name` TEXT NOT NULL," +
            "  `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "  `version_description` TEXT NOT NULL," +
            "  `is_critical` TINYINT(1) NOT NULL DEFAULT '0'," +
            "  `file_name` TEXT NOT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
    private static final String SQL_QUERY_ALL_W_UPDATE = "SELECT * FROM `" + SQL_TABLE_W_UPDATE + "`;";

    static {
        //init
        mAllVersion = new ArrayList<VersionData>();
        mNewestVersion = new VersionData[1];
        //load data
        Connection conn = Util.connectDB();
        try {
            Statement statement = conn.createStatement();
            statement.execute(SQL_CREATE_TABLE_W_UPDATE);

            ResultSet result = statement.executeQuery(SQL_QUERY_ALL_W_UPDATE);
            if (result.first()) {
                do {
                    int id = result.getInt("id");
                    int os = result.getInt("os");
                    long version = result.getLong("version");
                    String versionName = result.getString("version_name");
                    Timestamp updateTime = result.getTimestamp("update_time");
                    String versionDescription = result.getString("version_description");
                    boolean isCritical = result.getBoolean("is_critical");
                    String fileName = result.getString("file_name");

                    VersionData vd = new VersionData();
                    vd.id = id;
                    vd.os = os;
                    vd.version = version;
                    vd.versionName = versionName;
                    vd.updateTime = updateTime;
                    vd.versionDescription = versionDescription;
                    vd.isCritical = isCritical;
                    vd.fileName = fileName;

                    mNewestVersion[os] = vd;
                    mAllVersion.add(vd);
                } while (result.next());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }

    }

    public static synchronized List<VersionData> getAllVersions() {
        ArrayList<VersionData> vd = new ArrayList<VersionData>();
        vd.addAll(mAllVersion);
        return vd;
    }
}
