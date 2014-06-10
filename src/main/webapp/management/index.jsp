<%@ page import="noisyfox.whatever.UpdateManager" %>
<%@ page import="noisyfox.whatever.VersionData" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: Noisyfox
  Date: 6/9/2014
  Time: 11:31 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>Whatever 管理</title>
</head>
<body>
<% List<VersionData> mAllVersions = UpdateManager.getAllVersions();%>
<table border="1">
    <caption>版本列表</caption>
    <tr>
        <td>序号</td>
        <td>系统</td>
        <td>版本号</td>
        <td>显示版本</td>
        <td>更新日期</td>
        <td>更新说明</td>
        <td>关键更新</td>
        <td>文件名</td>
    </tr>
    <%
        int count = 1;
        for (VersionData vd : mAllVersions) {
    %>
    <tr>
        <td><%=count%>
        </td>
        <td><%=VersionData.OS[vd.os]%>
        </td>
        <td><%=vd.version%>
        </td>
        <td><%=vd.versionName%>
        </td>
        <td><%=vd.updateTime%>
        </td>
        <td><%=vd.versionDescription%>
        </td>
        <td><%=vd.isCritical ? "是" : "否"%>
        </td>
        <td><%=vd.fileName%>
        </td>
        <td></td>
    </tr>
    <%
            count++;
        }
    %>
</table>
</body>
</html>
