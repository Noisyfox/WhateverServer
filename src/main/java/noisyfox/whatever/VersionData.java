package noisyfox.whatever;

import java.sql.Timestamp;

/**
 * Created by Noisyfox on 6/10/2014.
 */
public class VersionData {
    public static final int OS_ANDROID = 0;
    public static final String OS[] = {"Android"};
    public int id;
    public int os;
    public long version;
    public String versionName;
    public Timestamp updateTime;
    public String versionDescription;
    public boolean isCritical;
    public String fileName;
}
