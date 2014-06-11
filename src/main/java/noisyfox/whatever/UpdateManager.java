package noisyfox.whatever;

import com.oreilly.servlet.multipart.FileRenamePolicy;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
            "  `is_delete` TINYINT(1) NOT NULL DEFAULT '0'," +
            "  `file_name` TEXT NOT NULL," +
            "  PRIMARY KEY (`id`)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8 AUTO_INCREMENT=1 ;";
    private static final String SQL_QUERY_ALL_W_UPDATE = "SELECT * FROM `" + SQL_TABLE_W_UPDATE + "` WHERE `is_delete`=0;";
    private static final String SQL_INSERT_NEW_W_UPDATE = "INSERT INTO  `whatever`.`" + SQL_TABLE_W_UPDATE + "` (" +
            "`id` ,`os` ,`version` ,`version_name` ,`update_time` ,`version_description` ,`is_critical` ,`is_delete` ,`file_name`)" +
            "VALUES (NULL ,  ?,  ?,  ?, CURRENT_TIMESTAMP ,  ?,  ?,  '0',  ?);";
    private static final String SQL_DELETE_W_UPDATE = "DELETE FROM `" + SQL_TABLE_W_UPDATE + "` WHERE `id` = ?;";

    static {
        reload();
    }

    private static synchronized void reload() {
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

    public static String getUploadDirectory() {
        String path = System.getenv("OPENSHIFT_DATA_DIR");
        File f = new File(path);
        f = new File(f, "whatever/update/");
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    public static synchronized String addNewVersion(int os, long version, String versionName, String versionDescription, boolean isCritical, String fileName) {
        Connection conn = Util.connectDB();
        try {
            PreparedStatement ps = conn.prepareStatement(SQL_INSERT_NEW_W_UPDATE);
            ps.setInt(1, os);
            ps.setLong(2, version);
            ps.setString(3, versionName);
            ps.setString(4, versionDescription);
            ps.setBoolean(5, isCritical);
            ps.setString(6, fileName);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
        reload();
        return null;
    }

    private static synchronized List<VersionData> firstCheck(long currentVersion, int platform) {
        if (!(platform >= 0 && platform < mNewestVersion.length && mNewestVersion[platform] != null && currentVersion < mNewestVersion[platform].version)) {
            return null;
        }
        List<VersionData> vds = getAllVersions();
        vds.add(mNewestVersion[platform]);
        return vds;
    }

    public static String checkUpdate(long currentVersion, int platform) {
        List<VersionData> vds = firstCheck(currentVersion, platform);

        if (vds != null) {
            boolean mustUpdate = false;

            for (VersionData vd : vds) {
                if (vd.os == platform && vd.version > currentVersion && vd.isCritical) {
                    mustUpdate = true;
                    break;
                }
            }

            VersionData nvd = vds.get(vds.size() - 1);

            JSONObject jobj = new JSONObject();
            try {
                jobj.put("ErrorCode", 0);
                jobj.put("n", "1");
                jobj.put("v", nvd.versionName);
                jobj.put("vd", nvd.versionDescription);
                jobj.put("f", mustUpdate ? "1" : "0");
                jobj.put("t", nvd.updateTime);
                return jobj.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return "{\"ErrorCode\":0, \"n\":\"0\"}";
    }

    public static synchronized void deleteVersion(long id) {
        Connection conn = Util.connectDB();
        try {
            PreparedStatement ps = conn.prepareStatement(SQL_DELETE_W_UPDATE);
            ps.setLong(1, id);

            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException ignored) {
            }
        }
        reload();
    }

    public static synchronized String getNewestFile(int platform) {
        return (platform >= 0 && platform < mNewestVersion.length && mNewestVersion[platform] != null) ? mNewestVersion[platform].fileName : null;
    }

    public static class TimestampFileRenamePolicy implements FileRenamePolicy {
        @Override
        public File rename(File file) {
            file = new File(file.getParent(), System.currentTimeMillis() + "_" + file.getName());
            return file;
        }
    }

}
