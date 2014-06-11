package noisyfox.whatever;

import com.oreilly.servlet.MultipartRequest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by Noisyfox on 6/9/2014.
 */
@WebServlet("/management/AdminSrv")
public class AdminSrv extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        boolean isMultipart = Util.isMultipartContent(request);

        if (isMultipart) {
            handleMultipart(request, response);
            return;
        }

        request.setCharacterEncoding("utf-8");
        String request_t = request.getParameter("request");

        if("delete".equals(request_t)){
            String id = request.getParameter("id");
            UpdateManager.deleteVersion(Long.parseLong(id));
        } else {
            Util.msg(response.getWriter(), false, "Known request!");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    private void handleMultipart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String filePath = UpdateManager.getUploadDirectory();
        //文件最大容量 50M
        int fileMaxSize = 50 * 1024 * 1024;
        //文件名
        String fileName;
        //重命名策略
        UpdateManager.TimestampFileRenamePolicy tfrp = new UpdateManager.TimestampFileRenamePolicy();
        //上传文件
        MultipartRequest mulit = new MultipartRequest(request, filePath, fileMaxSize, "UTF-8", tfrp);

        // 获取request类型
        String request_t = mulit.getParameter("request");

        if (!"new".equals(request_t)) {
            Util.msg(response.getWriter(), false, "Known request!");
            return;
        }

        String os = mulit.getParameter("os");
        String version = mulit.getParameter("version");
        String versionName = mulit.getParameter("versionName");
        String versionDescription = mulit.getParameter("versionDescription");
        String isCritical = mulit.getParameter("isCritical");

        Enumeration filesName = mulit.getFileNames();
        if (!filesName.hasMoreElements()) {
            Util.msg(response.getWriter(), false, "Must upload one file!");
            return;
        }

        String name = (String) filesName.nextElement();
        fileName = mulit.getFilesystemName(name);
        if (fileName == null) {
            Util.msg(response.getWriter(), false, "Must upload one file!");
            return;
        }

        String result = UpdateManager.addNewVersion(Integer.parseInt(os), Long.parseLong(version), versionName, versionDescription, !"0".equals(isCritical), fileName);

        if (result == null) {
            Util.msg(response.getWriter(), true, "添加成功");
        } else {
            Util.msg(response.getWriter(), false, result);
        }
    }
}
