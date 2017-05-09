<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>
<openmrs:require allPrivileges="View Encounters, View Patients, View Concept Classes" otherwise="/login.htm" redirect="/module/atd/logViewer.form" />
<link href="${pageContext.request.contextPath}/moduleResources/chirdlutil/chirdlutil.css" type="text/css" rel="stylesheet" />
<script language="JavaScript" src="${pageContext.request.contextPath}/moduleResources/chirdlutil/chirdlutil.js"></script>
<style>
	/* roScripts
	Table Design by Mihalcea Romeo
	www.roscripts.com
	----------------------------------------------- */
	
	table {
	        border-collapse:collapse;
	        background:#EFF4FB url(${pageContext.request.contextPath}/moduleResources/chirdlutil/eventTable.gif) repeat-x;
	        border-left:1px solid #686868;
	        border-right:1px solid #686868;
	        border-top:1px solid #686868;
	        border-bottom:1px solid #686868;
	        font:0.8em/145% 'Trebuchet MS',helvetica,arial,verdana;
	        color: #333;
	}
	
	td, th {
	        padding:5px;
	}
	
	caption {
	        padding: 0 0 .5em 0;
	        text-align: left;
	        font-size: 1.4em;
	        font-weight: bold;
	        color: #333;
	        background: transparent;
	}
	
	/* =head =foot
	----------------------------------------------- */
	
	thead th, tfoot th, tfoot td {
	        background:#333 url(${pageContext.request.contextPath}/moduleResources/chirdlutil/eventTable.gif) repeat-x;
	        color:#fff
	}
	
	tfoot td {
	        text-align:right
	}
	
	/* =body
	----------------------------------------------- */
	
	tbody th, tbody td {
	        /*border-bottom: dotted 1px #333;
	        border-top: dotted 1px #333;*/
	}
	
	tbody th.dotted, tbody td.dotted {
	        border-bottom: dotted 1px #333;
    }
    
    tbody th.solid, tbody td.solid {
            border-bottom: 1px solid #686868;
    }
	
	tbody th {
	        white-space: nowrap;
	}
</style>
<html>
<body>
<p><h3>Event Log Viewer:</h3></p>
<form name="input" action="logViewer.form" method="get">
    <center>
	    <table width="90%">
	        <tr>
	            <td style="padding: 10px 10px 10px 10px">Location:</td>
	            <td align="left" style="padding: 10px 5px 10px 0px">
	                <select id="locationId" name="locationId" style="width:250px">
	                    <option value="None Selected">None Selected</option>
	                    <c:forEach items="${locations}" var="loc">
	                       <c:choose>
	                           <c:when test="${loc.locationId == initialLocation }">
	                               <option value="${loc.locationId}" selected>${loc.name} (id:${loc.locationId})</option>
	                           </c:when>
	                           <c:otherwise>
	                               <option value="${loc.locationId}">${loc.name} (id:${loc.locationId})</option>
	                           </c:otherwise>
	                       </c:choose>
	                    </c:forEach>
	                </select>
	            </td>
	            <td style="padding: 10px 10px 10px 0px">Form:</td>
	            <td align="left" style="padding: 10px 5px 10px 0px">
	                <select id="formId" name="formId" style="width:250px">
	                    <option value="None Selected">None Selected</option>
	                    <c:forEach items="${forms}" var="form">
	                       <c:choose>
	                           <c:when test="${form.formId == initialForm }">
	                               <option value="${form.formId}" selected>${form.name} (id:${form.formId})</option>
	                           </c:when>
	                           <c:otherwise>
	                               <option value="${form.formId}">${form.name} (id:${form.formId})</option>
	                           </c:otherwise>
	                       </c:choose>
	                    </c:forEach>
	                </select>
	            </td>
	            <td style="padding: 10px 10px 10px 0px">Study:</td>
	            <td align="left" style="padding: 10px 10px 10px 0px">
	                <input type="text" id="studyId" name="studyId" value="${initialStudyId}" style="width:200px; height:15px"/>
	            </td>
	        </tr>
	        <tr>
	            <td style="padding: 10px 10px 10px 10px">Event:</td>
	            <td align="left" style="padding: 10px 5px 10px 0px">
	                <select id="event" name="event" style="width:250px">
	                    <option value="None Selected">None Selected</option>
	                    <c:forEach items="${events}" var="evt">
	                       <c:choose>
	                           <c:when test="${evt == initialEvent }">
	                               <option value="${evt}" selected>${evt}</option>
	                           </c:when>
	                           <c:otherwise>
	                               <option value="${evt}">${evt}</option>
	                           </c:otherwise>
	                       </c:choose>
	                    </c:forEach>
	                </select>
	            </td>
	            <td style="padding: 10px 10px 10px 0px">User:</td>
	            <td align="left" style="padding: 10px 5px 10px 0px">
	                <select id="user" name="user" style="width:250px">
	                    <option value="None Selected">None Selected</option>
	                    <c:forEach items="${users}" var="usr">
	                       <c:choose>
	                           <c:when test="${usr.userId == initialUser }">
	                               <option value="${usr.userId}" selected>${usr.username} (${usr}) (id:${usr.userId })</option>
	                           </c:when>
	                           <c:otherwise>
	                               <option value="${usr.userId}">${usr.username} (${usr}) (id:${usr.userId })</option>
	                           </c:otherwise>
	                       </c:choose>    
	                    </c:forEach>
	                </select>
	            </td>
	            <td></td>
	            <td></td>
	        </tr>
	        <tr>
	            <td nowrap style="padding: 10px 10px 10px 10px">Start Date:</td>
	            <td align="left" style="padding: 10px 5px 10px 0px">
	                <select id="startDateMonth" name="startDateMonth">
                        <c:forEach items="${months}" var="month">
                            <c:choose>
                                <c:when test="${month == initialStartDateMonth }">
                                    <option value="${month}" selected>${month}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${month}">${month}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
	                <select id="startDateDay" name="startDateDay">
                        <c:forEach items="${days}" var="day">
                            <c:choose>
                                <c:when test="${day == initialStartDateDay }">
                                    <option value="${day}" selected>${day}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${day}">${day}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                    <c:set var="setInitialVal" value="false"/>
	                <select id="startDateYear" name="startDateYear">
                        <c:forEach items="${years}" var="year">
                            <c:choose>
                                <c:when test="${year == initialStartDateYear }">
                                    <option value="${year}" selected>${year}</option>
                                    <c:set var="setInitialVal" value="true"/>
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${setInitialVal == 'true'}">
                                            <option value="${year}">${year}</option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${year}" selected>${year}</option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
	            </td>
	            <td nowrap style="padding: 10px 10px 10px 0px">End Date:</td>
	            <td align="left" style="padding: 10px 5px 10px 0px">
	                <select id="endDateMonth" name="endDateMonth">
                        <c:forEach items="${months}" var="month">
                            <c:choose>
                                <c:when test="${month == initialEndDateMonth }">
                                    <option value="${month}" selected>${month}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${month}">${month}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                    <select id="endDateDay" name="endDateDay">
                        <c:forEach items="${days}" var="day">
                            <c:choose>
                                <c:when test="${day == initialEndDateDay }">
                                    <option value="${day}" selected>${day}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${day}">${day}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
                    <select id="endDateYear" name="endDateYear">
                        <c:forEach items="${years}" var="year">
                            <c:choose>
                                <c:when test="${year == initialEndDateYear }">
                                    <option value="${year}" selected>${year}</option>
                                </c:when>
                                <c:otherwise>
                                    <option value="${year}">${year}</option>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </select>
	            </td>
	            <td></td>
	            <td></td>
	        </tr>
	        <tr>
	            <td class="solid" style="padding: 10px 10px 10px 10px">Description:</td>
	            <td class="solid" colspan="5" align="left" style="padding: 10px 10px 10px 0px">
	                <input type="text" id="description" name="description" value="${initialDescription}" style="width:920px; height:15px"/>
	            </td>
	        </tr>
	        <tr>
	            <td class="solid" colspan="6" align="center" style="padding: 20px 10px 20px 0px">
		            <input type="button" name="Reset" value="Reset" style="width:70px" onclick="resetLogForm('${currentMonth}', '${currentDay}', '${currentYear}')">
		            <input type="Submit" name="Search" value="Search" style="width:70px">&nbsp;
	            </td>
	        </tr>
	        <c:if test="${!empty (eventLogs)}">
		        <tr>
		            <td align="center" colspan="6" style="padding: 10px 10px 10px 10px">
			            <div style="overflow:auto; height:300px; width:90%;">
				            <table>
				                <caption><c:out value="${fn:length(eventLogs)}"/> results</caption>
				                <tr>
				                    <th class="solid">Event ID</th>
				                    <th class="solid">Location ID</th>
				                    <th class="solid">Form ID</th>
				                    <th class="solid">Study ID</th>
				                    <th class="solid">Event</th>
				                    <th class="solid">Event Time</th>
				                    <th class="solid">User ID</th>
				                    <th class="solid">Description</th>
				                </tr>
				                <c:forEach items="${eventLogs}" var="eventLog">
			                        <tr>
			                            <td class="dotted">${eventLog.eventId}</td>
			                            <td class="dotted">${eventLog.locationId}</td>
			                            <td class="dotted">${eventLog.formId}</td>
			                            <td class="dotted">${eventLog.studyId}</td>
			                            <td class="dotted">${eventLog.event}</td>
			                            <td class="dotted">${eventLog.eventTime}</td>
			                            <td class="dotted">${eventLog.userId}</td>
			                            <td class="dotted">${eventLog.description}</td>
			                        </tr>
			                    </c:forEach>
				            </table>
			            </div>
		            </td>
		        </tr>
	        </c:if>
	    </table>
    </center>
</form>
</body>
</html>
<%@ include file="/WEB-INF/template/footer.jsp" %>