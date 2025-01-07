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
											<sap:form type="helpForm">			
											<% if(isRTLMode){ %>
                        <input type="hidden" name="sap_rtl" value="true">
                      <% } %>																			
											<table class="urLogonTable" cellSpacing="3px" cellPadding="0" valign="top">																									
												<tr><td colspan="3">
													<div class="urMessageArea">																		
														<sap:if display="errormsg">
															<!-- display error message if there is one -->
															<div class="urMsgBarErr">
																<sap:errormsg styleClass="urTxtStd" imageClass="urMsgBarImgError" imageHeight="12" imageWidth="12"/>
															</div>
														</sap:if>														
													</div>
												</td></tr>
												<!-- header line -->
												<tr><td align="left" colspan="3">
													<div class="urTxtH3" tabindex=0><%=logonLocale.get("HAVE_TROUBLE")%></div>
												</td></tr>
												<tr><td align="left" colspan="3">
													<div class="urTxtLbl" tabindex=0><%=logonLocale.get("SELECT_HELP")%></div>
												</td></tr>
												<!-- line with select box for help type -->
												<tr><td>
													<label class="urLblStdNew" for="helptypefield">
														<nobr><%=logonLocale.get("HOW_HELP")%></nobr>
													</label>
												</td>
												<td>
													<sap:select type="helpAction" styleClass="urDdlWhl" size="1">
														<sap:option type="requestPasswordReset" />
														<sap:option type="otherLogonProblem" />
													</sap:select>
												</td>
												<td width="100%">&nbsp;</td></tr>												
												<!-- submit button -->
												<tr><td>&nbsp;</td><td align="right">
													<sap:button type="submitHelpPage" styleClass="urBtnStdNew"/>
													<sap:button type="cancelHelpPage" styleClass="urBtnStdNew"/>
												</td>
												<td>&nbsp;</td></tr>																				
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
