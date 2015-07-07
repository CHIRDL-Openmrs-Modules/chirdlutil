<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>
<openmrs:require allPrivileges="View Locations" otherwise="/login.htm" redirect="/module/chirdlutil/printerThreadPoolMonitor.form" />
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>   
        <meta http-equiv="Content-Type" content="text/html; charset=utf-8" /> 
        <script type="text/javascript" charset="utf-8">
           var context;
           var xmlHttp;
           var refreshRate
           function loadChart(url, rate) {
               context = url;
               refreshRate = rate * 1000;
               timer();
           }
           
           function timer() {
               var chartDiv = document.getElementById('chartDiv');
               var randomNumber = Math.floor((Math.random() * 10000) + 1);
               chartDiv.innerHTML = '<img src="' + context + 
                  '/moduleResources/chirdlutil/ajax-loader.gif"/>';
               chartDiv.innerHTML = '<img src="' + context + 
                  '/moduleServlet/chirdlutil/threadPoolMonitor?poolType=printer&randomNumber=' + randomNumber + '"/>';
               setTimeout("timer()", refreshRate);
           }
        </script>
        <title>Thread Pool Manager</title>  
    </head>   
    <body onLoad="loadChart('${pageContext.request.contextPath}', ${refreshRate})">  
        <center>
            <div id="chartDiv">
                <img src="${pageContext.request.contextPath}/moduleResources/chirdlutil/ajax-loader.gif">
            </div>
        </center>
    </body>
</html>