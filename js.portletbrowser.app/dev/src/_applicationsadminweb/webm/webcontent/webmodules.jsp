<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Web Module Settings</title>
    </head>
    <body>
        <f:view>
        <p:portletPage>
		
        <h:form id="navigation_path">                         				   
            <table border="0" cellspacing="1" cellpadding="1" class="urTrcHdBgOpen" bgcolor="#FFFFFF" width="100%">
        				<tr>
        						<td class="urTrcHdNotch" align="left"><img width="6px" src="resources/common/1x1.gif"></td>
          					<td width="100%" bgcolor="#FFFFFF" class="urTrcHdBgOpen"><font class="urTrcTitHdr">
          								<h:commandLink action="application" value="Applications"/> &nbsp;
           								<h:outputText value=">"/> &nbsp;
           								<h:commandLink action="application" value="#{ApplicationSettingsBean.applicationName}"/> &nbsp;
           								<h:outputText value=">"/> &nbsp;
          								<h:outputText value="#{ApplicationSettingsBean.name}"/> &nbsp;
          					</font></td>
        				</tr>
				    </table>
        </h:form>
               
        <h:form id="applications_properties_form">
		         <h:outputLabel value="Web" styleClass="capHdr" style="color:#606060;font-size:0.7em"/>
						 <h:outputText value="#{ApplicationSettingsBean.name}" styleClass="capHdr" style="font-size:0.7em"/>
				</h:form>      		 
         		 
        <table border="0" cellspacing="0" cellpadding="0" class="urGrcWhl" style="width:100%;background-color:#ffffff;">
					 <tbody><tr><td>
								<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
										<tbody><tr valign="top"><td>
												<div id="urID1034712678372scroll" class="urTbsstripScrollDIV">
				 								     <table id="urID1034712678372table" class="urTbsstripTABLE" border="0" cellpadding="0" cellspacing="0" selectedtab="0" tabcount="5">
				  											<tbody><tr>
																	  <td id="urID1034712678372Prev" class="urTbsFirstAngOnPrevoff" valign="top"> </td>
																	  <td id="urID1034712678372tab0" nowrap class="urTbsLabelOn">
																	  			<span id="urID1034712678372tab0_a" onClick="UR_TabChange('urID1034712678372',0,0,event);" title="General" class="urTbsTxtOn" style="text-decoration:none;">General</span>
																	  </td>
																	  <td id="urID1034712678372tab1Ang" class="urTbsAngOnOff" valign="top"> </td>
																	  <td id="urID1034712678372tab1" nowrap class="urTbsLabelOff">
																	  		  <span title="Web Objects" id="urID1034712678372tab1_a" onClick="UR_TabChange('urID1034712678372',1,0,event);" class="urTbsTxtOff">Web Objects</span>
																	  </td>
																	  <td id="urID1034712678372tab2Ang" class="urTbsAngOffOff" valign="top"> </td>
																	  <td id="urID1034712678372tab2" nowrap class="urTbsLabelOff">
																	  		  <span title="Mappings" id="urID1034712678372tab2_a" onClick="UR_TabChange('urID1034712678372',2,0,event);" class="urTbsTxtOff">Mappings</span>
																	  </td>																	  
																	  <td id="urID1034712678372tab3Ang" class="urTbsAngOffOff" valign="top"> </td>
																	  <td id="urID1034712678372tab3" nowrap class="urTbsLabelOff">
																	  		  <span title="References" id="urID1034712678372tab3_a" onClick="UR_TabChange('urID1034712678372',3,0,event);" class="urTbsTxtOff">References</span>
																	  </td>
																	  <td id="urID1034712678372tab4Ang" class="urTbsAngOffOff" valign="top"> </td>
																	  <td id="urID1034712678372tab4" nowrap class="urTbsLabelOff">
																	  		  <span title="Others" id="urID1034712678372tab4_a" onClick="UR_TabChange('urID1034712678372',4,0,event);" class="urTbsTxtOff">Others</span>
																	  </td>																	  																	  
																	  <td id="urID1034712678372Next" class="urTbsLastOffNextoff" valign="top"> </td>
				  											 </tr></tbody>
				  										</table>
				  							</div>
												<div class="urTbsDiv"> </div>
				  									 <table class="urTbsWhl" width="100%" cellpadding="0" cellspacing="0" border="0">
				  										   <tbody>
															   <tr><td align="left" valign="top" class="urTbsCnt">																  
				  				  									<div id="urID1034712678372content0" class="urTbsDspSel" style="width:100%;height:400;line-height:1em;overflow:auto">
				  				  									<h:form id="GeneralTab">
																				  <h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">																						   	
																								<h:outputLabel value="Display Name"/><h:outputText  value="#{ApplicationSettingsBean.displayName}"/>
																								<h:outputLabel value="Distributable"/><h:selectBooleanCheckbox value="#{ApplicationSettingsBean.distributable}" disabled="true" />
																								<h:outputLabel value="Failover Message"/><h:outputText value="#{ApplicationSettingsBean.failoverMessage}"/>
																								<h:outputLabel value="Failover Timeout"/><h:outputText value="#{ApplicationSettingsBean.failoverTimeout}"/>
																								<h:outputLabel value="URL Session Tracking"/><h:selectBooleanCheckbox value="#{ApplicationSettingsBean.urlSessionTracking}"/>
																								<h:outputLabel value="Max sessions"/><h:inputText value="#{ApplicationSettingsBean.maxSessions}"/>
																								<h:outputLabel value="Session Timeout"/><h:inputText value="#{ApplicationSettingsBean.sessionTimeOut}"/>
																			    </h:panelGrid>
																			    
																			   	<h:panelGrid captionClass="capHdr" columns="1" width="100%">
																					    	<f:facet name="caption"><h:outputText  value="Context Params"/></f:facet> 
														                    <h:dataTable id="contectParams" value="#{ApplicationSettingsBean.contextParams}" var="contextParam" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.contextParams != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        														<h:column rendered="#{ApplicationSettingsBean.contextParams != null}" >
															                            <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.contextParams != null}" /></f:facet> 
                           															  <h:outputText value="#{contextParam.elementName}"/>                    
																                    </h:column>                           
														                        <h:column rendered="#{ApplicationSettingsBean.contextParams != null}" >
																                          <f:facet name="header"><h:outputText value="Name" rendered="#{ApplicationSettingsBean.contextParams != null}" /></f:facet> 
																                          <h:outputText value="#{contextParam.name}"/>                           
														                        </h:column>
														                        <h:column rendered="#{ApplicationSettingsBean.contextParams != null}" >
																                          <f:facet name="header"><h:outputText value="Value" rendered="#{ApplicationSettingsBean.contextParams != null}" /></f:facet> 
																	                        <h:outputText value="#{contextParam.value}"/>                    
														                        </h:column>                           
														                        <h:column rendered="#{ApplicationSettingsBean.contextParams != null}" >
																                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.contextParams != null}" /></f:facet> 
																                           <h:outputText value="#{contextParam.description}"/>                           
														                        </h:column>                        
														                   </h:dataTable>	
														              </h:panelGrid>
																			    <BR> 	
																			    
																			    <h:panelGrid captionClass="capHdr" columns="1" width="100%">
																					  	<f:facet name="caption"><h:outputText  value="Cookies"/></f:facet>  
                    
														                  <h:dataTable id="cookies" value="#{ApplicationSettingsBean.cookies}" var="cookies" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.cookies != null}" headerClass="hdrClss" columnClasses="stdrwClss" >
                        														<h:column rendered="#{ApplicationSettingsBean.cookies != null}" >
												                          			<f:facet name="header"><h:outputText value="Type" rendered="#{ApplicationSettingsBean.cookies != null}" /></f:facet> 
												                          			<h:selectOneMenu id="selectNewResType" value='#{cookies.type}' rendered="#{ApplicationSettingsBean.cookies != null}" >
																														<f:selectItems  value="#{cookies.typesList}" />
																												</h:selectOneMenu>                         													   		
																                    </h:column>                           
														                        <h:column rendered="#{ApplicationSettingsBean.cookies != null}" >
														                           <f:facet name="header"><h:outputText value="Path" rendered="#{ApplicationSettingsBean.cookies != null}" /></f:facet> 
														                           <h:inputText value="#{cookies.path}" rendered="#{ApplicationSettingsBean.cookies != null}" />                           
														                        </h:column>
														                        <h:column rendered="#{ApplicationSettingsBean.cookies != null}" >
														                           <f:facet name="header"><h:outputText value="Domain" rendered="#{ApplicationSettingsBean.cookies != null}"  /></f:facet> 
														                           <h:inputText value="#{cookies.domain}" rendered="#{ApplicationSettingsBean.cookies != null}" />                    
														                        </h:column>                           
														                        <h:column rendered="#{ApplicationSettingsBean.cookies != null}" >
														                           <f:facet name="header"><h:outputText value="Max-Age" rendered="#{ApplicationSettingsBean.cookies != null}"  /></f:facet> 
														                           <h:inputText value="#{cookies.maxAge}" rendered="#{ApplicationSettingsBean.cookies != null}" />                           
														                        </h:column>                        
														                    </h:dataTable> 
																					</h:panelGrid>				
																					
																					<BR>				    
          															  <h:commandLink id="updateWebModule" styleClass="urBtnEmph" value="Update" action="#{ApplicationSettingsBean.updateWebModule}"/>
																				  
																				  <BR>
																				  <h:panelGrid rendered="#{ApplicationSettingsBean.messages}" style="width:100%;">
					      															<div class="urMsgBarStd" id="urID1034712677801">
																      				<table border="0" cellpadding="0" cellspacing="0">
																      				 	<tbody><tr>
																      				 	    <td><span id="urID1034712677801_ur_bar_img" class="urMsgBarImgOk">
																		      							<img height="12" width="12" src="resources/common/1x1.gif">
																		      					</span></td>
																		      					<td><span id="urID1034712677801_ur_bar_text" class="urTxtStd">
																		      					   <h:messages styleClass="stdrwClss"></h:messages>
																	      					  </span></td>
																	      				</tr></tbody>
																	      			</table>
																	      			</div>      								
																	      	</h:panelGrid>																			  
																	    </h:form>  	
																	    </div>			  
				  
				  														<div id="urID1034712678372content1" class="urTbsDsp" style="width:100%;height:400;line-height:1em;">
				  														<h:form id="WebObjectsTab">
																	       <h:panelGrid captionClass="capHdr" columns="1" width="100%">
																					    	<f:facet name="caption"><h:outputText  value="Servlets"/></f:facet>  
																	                    <h:dataTable id="servlets" value="#{ApplicationSettingsBean.servlets}" var="servlet" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.servlets != null}" headerClass="hdrClss" columnClasses="stdrwClss">
																	                        <h:column rendered="#{ApplicationSettingsBean.servlets != null}" >
																	                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.servlets != null}" /></f:facet> 
																	                           <h:outputText value="#{servlet.elementName}"/>                    
																	                        </h:column>                           
																	                        <h:column rendered="#{ApplicationSettingsBean.servlets != null}" >
																	                           <f:facet name="header"><h:outputText value="Display Name" rendered="#{ApplicationSettingsBean.servlets != null}" /></f:facet> 
																	                           <h:outputText value="#{servlet.displayName}"/>                    
																	                        </h:column>                                                   
																	                        <h:column rendered="#{ApplicationSettingsBean.servlets != null}" >
																	                           <f:facet name="header"><h:outputText value="Servlet class / Jsp File" rendered="#{ApplicationSettingsBean.servlets != null}" /></f:facet> 
																	                           <h:outputText value="#{servlet.source}"/>                           
																	                        </h:column>
																	                        <h:column rendered="#{ApplicationSettingsBean.servlets != null}" >
																	                           <f:facet name="header"><h:outputText value="Load-on-Startup" rendered="#{ApplicationSettingsBean.servlets != null}" /></f:facet> 
																	                           <h:outputText value="#{servlet.loadOnStartup}"/>                    
																	                        </h:column>                           
																	                        <h:column rendered="#{ApplicationSettingsBean.servlets != null}" >
																	                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.servlets != null}" /></f:facet> 
																	                           <h:outputText value="#{servlet.description}"/>                    
																	                        </h:column>                                                                         
																	                    </h:dataTable> 
																					</h:panelGrid>	
																	
														                <BR> 
																	          <h:panelGrid captionClass="capHdr" columns="1" width="100%">
																					    	<f:facet name="caption"><h:outputText  value="Filters"/></f:facet> 
																	                    <h:dataTable id="filters" value="#{ApplicationSettingsBean.filters}" var="filter" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.filters != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
																	                        <h:column rendered="#{ApplicationSettingsBean.filters != null}" >
																	                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.filters != null}" /></f:facet> 
																	                           <h:outputText value="#{filter.elementName}"/>                    
																	                        </h:column>                                               
																	                        <h:column rendered="#{ApplicationSettingsBean.filters != null}" >
																	                           <f:facet name="header"><h:outputText value="Display Name" rendered="#{ApplicationSettingsBean.filters != null}" /></f:facet> 
																	                           <h:outputText value="#{filter.displayName}"/>                    
																	                        </h:column>                           
																	                        <h:column rendered="#{ApplicationSettingsBean.filters != null}" >
																	                           <f:facet name="header"><h:outputText value="Class Name" rendered="#{ApplicationSettingsBean.filters != null}" /></f:facet> 
																	                           <h:outputText value="#{filter.className}"/>                           
																	                        </h:column>
																	                        <h:column rendered="#{ApplicationSettingsBean.filters != null}" >
																	                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.filters != null}" /></f:facet> 
																	                           <h:outputText value="#{filter.description}"/>                           
																	                        </h:column>                        
																	                    </h:dataTable>
																						</h:panelGrid>	
																					
																	          <BR>
																	          <h:panelGrid captionClass="capHdr" columns="1" width="100%">
																					    	<f:facet name="caption"><h:outputText  value="Listeners"/></f:facet> 
																	                    <h:dataTable id="listeners" value="#{ApplicationSettingsBean.listeners}" var="listener" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.listeners != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
																	                        <h:column rendered="#{ApplicationSettingsBean.listeners != null}" >
																	                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.listeners != null}" /></f:facet> 
																	                           <h:outputText value="#{listener.elementName}"/>                    
																	                        </h:column>                           
																	                        <h:column rendered="#{ApplicationSettingsBean.listeners != null}" >
																	                           <f:facet name="header"><h:outputText value="Class Name" rendered="#{ApplicationSettingsBean.listeners != null}" /></f:facet> 
																	                           <h:outputText value="#{listener.className}"/>                           
																	                        </h:column>
																	                        <h:column rendered="#{ApplicationSettingsBean.listeners != null}" >
																	                           <f:facet name="header"><h:outputText value="Display Name" rendered="#{ApplicationSettingsBean.listeners != null}" /></f:facet> 
																	                           <h:outputText value="#{listener.displayName}"/>                    
																	                        </h:column>                           
																	                        <h:column rendered="#{ApplicationSettingsBean.listeners != null}" >
																	                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.listeners != null}" /></f:facet> 
																	                           <h:outputText value="#{listener.description}"/>                           
																	                        </h:column>                        
																	                    </h:dataTable>
																						</h:panelGrid>
                                      </h:form>																								
				  														</div>		

				  														<div id="urID1034712678372content2" class="urTbsDsp" style="width:100%;height:400;line-height:1em;">				  														
																				  	<jsp:include page="servletMappings.jsp" />																			
																						<jsp:include page="filterMappings.jsp" />																			
  																					<jsp:include page="jspPropGroups.jsp" />
																		        <jsp:include page="localeEncodingMappings.jsp" />		
																		        <jsp:include page="mimeMappings.jsp" />			  	
				  														</div>
				  														
				  														<div id="urID1034712678372content3" class="urTbsDsp" style="width:100%;height:400;line-height:1em;">
																	        <jsp:include page="ejbRemoteReferences.jsp" />				                  
																	        <jsp:include page="ejbLocalReferences.jsp" />		
																	        <jsp:include page="environmentEntries.jsp" />
																	        <jsp:include page="messageDestinations.jsp" />		        
																	        <jsp:include page="messageDestinationReferences.jsp" />		  
																	        <jsp:include page="resourceEnvironmentReferences.jsp" />		  
																	        <jsp:include page="resourceReferences.jsp" />		  
																	        <jsp:include page="serviceReferences.jsp" />	
				  														</div>
				  														
				  														<div id="urID1034712678372content4" class="urTbsDsp" style="width:100%;height:400;line-height:1em;">   																       
				  														<h:form id="OthersTab">
   																       <h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss" width="100%" border="1" cellpadding="0" cellspacing="0">
			   																      <h:outputLabel value="Welcome Files"/><h:outputText  value="#{ApplicationSettingsBean.welcomeFilesString}"/>
																				 </h:panelGrid>
																				 
																				<jsp:include page="errorPages.jsp" />				
																        <jsp:include page="responseStatuses.jsp" />				          
																				<jsp:include page="taglibs.jsp" />	
																			</h:form>
				  														</div>				  																  														
				  												</td></tr>
				  												</tbody>
				  												</table>			  
				  									  </td></tr></tbody>
				  								</table>
				  				</td></tr></tbody>
				  	</table>        		
        		

		        <h:form id="applicationsLink">               
    	            <font class="urTrcTitHdr"><h:commandLink action="application" value="Applications"/></font>                
      		  </h:form>
        
        </p:portletPage>       
        </f:view>
    
    </body>
</html>
