<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" pageEncoding="GBK" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    Map<String, String> result = (null == request.getAttribute("result")) ? new HashMap<String, String>() : (Map<String, String>) request.getAttribute("result");
    Map<String, String> result1 = (null == request.getAttribute("result1")) ? new HashMap<String, String>() : (Map<String, String>) request.getAttribute("result1");
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>近似文本检测 张鹏飞的毕业设计 </title>
    <meta http-equiv="Content-Type" content="text/html; charset=gbk"/>
    <meta name="KEYWords" content="-,css,xhtml,effect"/>
    <meta name="DEscription" content="-"/>
    <meta content="all" name="robots"/>

    <style type="text/css">
        * {
            margin: 0;
            padding: 0;
        }

        body {
            font-family: Verdana, Arial, Helvetica, sans-serif;
            min-width: 620px;
        }

        #header, #footer {
            clear: both;
            padding: 10px;
            text-align: center;

        }

        #left,
        #right {
            float: left;
            width: 50%;
            margin: 0 0 0 -16px;
        }

        #innerLeft,
        #innerRight {
            margin: 0 0 0 16px;
            background-color: #efefef;
        }

        #middle {
            float: left;
            width: 30px;
            background-color: #ffffff;
        }

        .inner {
            padding: 12px;
        }

        .query {
            margin-top: 10px;
            margin-bottom: 20px;
        }

        .result {
            margin-left: 20px;
            margin-right: 20px;
            padding: 10px;
        }

        #uploadprogressbar {
            display: none;
        }
    </style>
    <script type="text/javascript" src="<%=basePath%>js/jquery.js"></script>
    <script type="text/javascript" src="<%=basePath%>js/jquery.progressbar.min.js"></script>
    <script type="text/javascript">
        function beginUpload() {
            $("#uploadprogressbar").fadeIn();

            var i = setInterval(function() {
                $.getJSON("<%=basePath%>sample/status", function(data) {
                    if (data == null) {
                        clearInterval(i);
                        location.reload(true);
                        return;
                    }

                    var percentage = Math.floor(100 * parseInt(data.readSize)
                            / parseInt(data.totalSize));
                    $("#uploadprogressbar").progressBar(percentage);
                });
            }, 500);

            return true;
        }
    </script>
</head>
<body>
<div id="header">
    <h1>近似文本检测demo,<a href="<%=basePath%>all.zip">下载文档样本集</a></h1>

    <div style="color:blue">结论:改进后检出结果更加精准</div>
    <div class="query">
        <form name="queryform" action="<%=basePath%>sample/output" method="post"
              enctype="multipart/form-data" onsubmit="beginUpload();">
            <input type="file" title="Search" value="" name="q" autocomplete="off"/>
            <input type="submit" value="simhash算法检测"/> <br/>
            <span class="progressbar" id="uploadprogressbar">0%</span>
        </form>
    </div>
</div>
<div id="left">
    <div id="innerLeft" class="inner">
        <div style="color:red">64-bit simhash算法检测结果:</div>

        <div class="result">
            <ol>
                <%for (Map.Entry<String, String> entry : result.entrySet()) {%>
                <li>
                    <a href="<%=basePath%>sample/download?file=<%=java.net.URLEncoder.encode(entry.getKey(),"gbk")%>"><%
                        out.println(entry.getKey() + "\t" + entry.getValue());%></a></li>
                <%}%>
            </ol>
        </div>

    </div>
</div>
<div id="middle">
    <div id="innerMiddle" class="inner">


    </div>
</div>
<div id="right">
    <div id="innerRight" class="inner">
        <div style="color:red">改进后simhash算法检测结果:</div>

        <div class="result">
            <ol>
                <%for (Map.Entry<String, String> entry : result1.entrySet()) {%>
                <li>
                    <a href="<%=basePath%>sample/download?file=<%=java.net.URLEncoder.encode(entry.getKey(),"gbk")%>"><%
                        out.println(entry.getKey() + "\t" + entry.getValue());%></a></li>
                <%}%>
            </ol>
        </div>

    </div>
</div>
<div id="footer">


</div>

</body>
</html>
