package noisyfox.whatever;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * Created by Noisyfox on 6/11/2014.
 */
@WebServlet("/update")
public class UpdateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        String m = request.getParameter("m");

        if ("c".equals(m)) {
            response.setCharacterEncoding("utf-8");
            String resp;
            // 检查更新
            String currentVersion = request.getParameter("v");
            String platform = request.getParameter("p");
            try {
                long cv = Long.parseLong(currentVersion);
                int p = Integer.parseInt(platform);
                resp = UpdateManager.checkUpdate(cv, p);
            } catch (NumberFormatException e) {
                resp = "{\"ErrorCode\":1}";
            }

            response.getWriter().print(resp);
        } else {
            if ("d".equals(m)) {
                // 下载
                String platform = request.getParameter("p");
                int os = 0;
                String file = null;
                try {
                    os = Integer.parseInt(platform);
                    file = UpdateManager.getNewestFile(os);
                } catch (NumberFormatException ignored) {
                }
                if (file == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                File f = new File(UpdateManager.getUploadDirectory(), file);
                response.reset();
                response.setContentType("application/x-msdownload");
                String fileDownload = f.getAbsolutePath();
                String fileDisplay = VersionData.FILE[os];
                fileDisplay = URLEncoder.encode(fileDisplay, "UTF-8");
                response.setHeader("Content-Disposition", "attachment;filename=" + fileDisplay);

                OutputStream out = null;
                FileInputStream in = null;
                try {
                    out = response.getOutputStream();
                    in = new FileInputStream(fileDownload);

                    byte[] b = new byte[1024];
                    int i;

                    while ((i = in.read(b)) > 0) {
                        out.write(b, 0, i);
                    }
                    out.flush();
                } catch (Exception e) {
                    System.out.println("Error!");
                    e.printStackTrace();
                } finally {
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                }
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
