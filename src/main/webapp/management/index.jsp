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
    <script type="text/javascript" src="../js/jquery-1.9.0.min.js"></script>
    <script type="text/javascript">
        function requestAdmin(request, callback) {
            $.post("AdminSrv", request, callback);
        }

        function deleteUpdate(_id) {
            if (confirm("确定删除这一条记录吗？")) {
                requestAdmin({
                    request: 'delete',
                    id: _id
                }, function (result) {
                    window.location.reload();
                });
            }
        }

        function addUpdate() {

        }
    </script>
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
        <td></td>
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
        <td>
            <button onclick="deleteUpdate(<%=vd.id%>)">
                <strong>删除</strong>
            </button>
        </td>
    </tr>
    <%
            count++;
        }
    %>
</table>
<a>添加新版本</a>
<br/>
<a>平台:</a>
<label>
<select name="os" id="os">
    <option value="<%=VersionData.OS_ANDROID%>"><%=VersionData.OS[VersionData.OS_ANDROID]%></option>
</select>
</label>
<br/>
<a>版本号:</a>
<label><input name="version" id="version"/></label>
<br/>
<a>显示版本:</a>
<label><input name="versionName" id="versionName"/></label>
<br/>
<a>更新说明:</a>
<label>
<textarea name="versionDescription" id="versionDescription" rows="3" cols="20"></textarea>
</label>
<br/>
<a>关键更新:</a>
<label><input name="isCritical" id="isCritical" type="checkbox"></label>
<br/>
<a>选择文件:</a>
<label><input type="file" name="fileName" id="fileName" value="Browse..." /></label>
</body>
</html>
