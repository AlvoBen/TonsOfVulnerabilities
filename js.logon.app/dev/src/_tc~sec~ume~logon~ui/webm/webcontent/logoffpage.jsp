<%@ page session = "false"%>
<%@ taglib uri="/SapLogon" prefix="sap" %>
<jsp:useBean id="logonLocale" class="com.sap.engine.applications.security.logon.beans.ResourceBean" scope="request"/>

<% String webpath = (String) request.getAttribute("logon.application.real.path"); %>

<%
//In case of NWBC we must deliver just the inner content without any frame around it.
//No margins to the left and top of the page is required as well.
//See sap_user_agent.txt about details for distinguishing nwbc
boolean isRTLMode = false;
boolean uiFrameVisible = true;
String uiFrameAllign = "center";
String uiFrameTopMargin = "margin-top:50px;";
%>
<%@ include file="/sap_user_agent.txt" %>

<table id="tblFrmUI" width="790px" valign="middle" dir="ltr" align="<%=uiFrameAllign%>" cellPadding="0" cellSpacing="0" style="background-color:#ffffff;<%=uiFrameTopMargin%>">
  <% if(uiFrameVisible){ %>
	<tr>
		<td><img src="<%=webpath%>layout/lf_tl.png" width="29" height="32"></td>
		<td background="<%=webpath%>layout/lf_st.png"></td>
		<td><img src="<%=webpath%>layout/lf_tr.png" width="27" height="32"></td>
	</tr>
	<% } %>
	<tr>
	  <% if(uiFrameVisible){ %>
		<td background="<%=webpath%>layout/lf_sl.png"></td>
		<% } %>
		<td width="100%">		
			<% if(isRTLMode){ %>
        <table id="tblInnerCnt" dir="rtl" cellpadding="0" cellspacing="0">
      <% } else {%>
        <table id="tblInnerCnt" dir="ltr" cellpadding="0" cellspacing="0">
      <% } %>
				<tr>					
					<td valign="top"><div class="urBrandImage"><sap:brandimage type="main"/></div></td>
					<td width="100%"></td>
					<td>					
						<table height="360px" cellPadding="0" cellSpacing="0">
							<tr>								
								<td valign="top"><div class="urProductName"><sap:brandimage type="second"/></div></td>
							</tr>							
							<tr>
								<td valign="top" align="left">							
										<div class="urLogonData">																					
											<!-- data table starts after this line -->
											<sap:form type="logoff">
											<% if(isRTLMode){ %>
                        <input type="hidden" name="sap_rtl" value="true">
                      <% } %>
											<table class="urLogonTable" cellSpacing="3px" cellPadding="0" valign="top">																									
												<!-- logout label -->
												<tr><td width="80%">
													<div class="urMessageArea">
													  <sap:if display="logoutSuccessMsg">
														  <sap:label type="logout" styleClass="urLblStdNew"/>
													  </sap:if>		
													  <sap:if display="logoutErrorMsg">
													  	<sap:errormsg styleClass="urTxtStd" imageClass="urMsgBarImgError" imageHeight="12" imageWidth="12"/>
													  </sap:if>		
													</div>										
												</td>
												<td width="20%">&nbsp;</td></tr>	
												<!-- button to logon again -->
												<sap:if display="LogonAgainButton">
													<tr><td width="80%" align="right">
													 <sap:if display="logoutSuccessMsg">
														<sap:button type="logonAgain" styleClass="urBtnStdNew"/>
													 </sap:if>   
													 <sap:if display="logoutErrorMsg">
													   <sap:button type="GoBack" styleClass="urBtnStdNew"/>
													 </sap:if>   
													</td>	
													<td width="20%">&nbsp;</td></tr>												
												</sap:if>																													
											</table>
											</sap:form>
											<!-- data table ends before this line -->
										</div>																	
								</td>
							</tr>
							<tr>
								<td valign="bottom">									
									<div class="urCopyrightFrame">									 
										<div class="urCopyrightText"><%=logonLocale.get("COPY_RIGHT")%></div>					
										<div class="urCopyrightImage"><img src="<%=webpath%>layout/sap_logo.png" alt="<%=logonLocale.get("SAPAG")%>" title="<%=logonLocale.get("SAPAG")%>" width="55" height="27"></div>
									</div>				
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>			
		</td>		
		<% if(uiFrameVisible){ %>
		<td background="<%=webpath%>layout/lf_sr.png"></td>
		<% } %>
	</tr>
	<% if(uiFrameVisible){ %>
	<tr>
		<td><img src="<%=webpath%>layout/lf_bl.png" width="29" height="32"></td>
		<td align="center" background="<%=webpath%>layout/lf_sb.png"><span class="clip"><img src="<%=webpath%>layout/lf_clip.png" width="298" height="32"></span></td>
		<td><img src="<%=webpath%>layout/lf_br.png" width="27" height="32"></td>
	</tr>
	<% } %>
</table>

<script type="text/javascript">
<!--    
  var docDir = document.dir;    
  if(docDir=="rtl" || docDir=="RTL"){   
     var elTableCnt = document.getElementById("tblInnerCnt");
     if(elTableCnt!=undefined){
       elTableCnt.style.direction="rtl";        
     }     
  }  
-->
</script>
