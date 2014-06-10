package noisyfox.whatever;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Noisyfox on 6/11/2014.
 */
@WebServlet("/update")
public class UpdateServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String m = request.getParameter("m");

        if("c".equals(m)){
            String resp;
            // 检查更新
            String currentVersion = request.getParameter("v");
            String platform = request.getParameter("p");
            try{
                long cv = Long.parseLong(currentVersion);
                int p = Integer.parseInt(platform);
                resp = UpdateManager.checkUpdate(cv, p);
            }catch(NumberFormatException e){
                resp = "{\"ErrorCode\":1}";
            }

            response.getWriter().print(resp);
        } else if("d".equals(m)){
            // 下载
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
