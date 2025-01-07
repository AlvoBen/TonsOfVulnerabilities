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
        <title>Data Sources Module Settings</title>
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
           								<h:commandLink action="application" value="#{DataSourceSettings.applicationName}"/> &nbsp;
           								<h:outputText value=">"/> &nbsp;
          								<h:outputText value="#{DataSourceSettings.dsName}"/> &nbsp;
          						</font></td>
        						</tr>
				    		</table>
        		</h:form>
         
        		<h:form id="ds_properties_form">  
								<h:outputLabel value="Data Source" styleClass="capHdr" style="color:#606060;font-size:0.7em"/>
								<h:outputText value="#{DataSourceSettings.dsName}" styleClass="capHdr" style="font-size:0.7em"/>
        		    
        		 		<table border="0" cellspacing="0" cellpadding="0" class="urGrcWhl" style="width:100%;background-color:#ffffff;">
				  					<tbody>
				  						 <tr><td>
				  								<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
				  										<tbody><tr valign="top"><td>
				  												<div id="urID1034712678372scroll" class="urTbsstripScrollDIV">
				  												<table id="urID1034712678372table" class="urTbsstripTABLE" border="0" cellpadding="0" cellspacing="0" selectedtab="0" tabcount="4">
				  												<tbody><tr>
																	  <td id="urID1034712678372Prev" class="urTbsFirstAngOnPrevoff" valign="top"> </td>
																	  <td id="urID1034712678372tab0" nowrap class="urTbsLabelOn">
																	  			<span id="urID1034712678372tab0_a" onClick="UR_TabChange('urID1034712678372',0,0,event);" title="Settings" class="urTbsTxtOn" style="text-decoration:none;">Settings</span>
																	  </td>
																	  <td id="urID1034712678372tab1Ang" class="urTbsAngOnOff" valign="top"> </td>
																	  <td id="urID1034712678372tab1" nowrap class="urTbsLabelOff">
																	  			<span title="Connection Pooling" id="urID1034712678372tab1_a" onClick="UR_TabChange('urID1034712678372',1,0,event);" class="urTbsTxtOff">Connection Pooling</span>
																	  </td>
																	  <td id="urID1034712678372tab2Ang" class="urTbsAngOffOff" valign="top"> </td>
																	  <td id="urID1034712678372tab2" nowrap class="urTbsLabelOff">
																	  		  <span title="Antecedent JDBC Driver" id="urID1034712678372tab2_a" onClick="UR_TabChange('urID1034712678372',2,0,event);" class="urTbsTxtOff">Antecedent JDBC Driver</span>
																	  </td>
																	  <td id="urID1034712678372tab3Ang" class="urTbsAngOnOff" valign="top"> </td>
																	  <td id="urID1034712678372tab3" nowrap class="urTbsLabelOff">
																	  			<span title="Dependent JDBC Data Source Aliases" id="urID1034712678372tab3_a" onClick="UR_TabChange('urID1034712678372',3,0,event);" class="urTbsTxtOff">Dependent JDBC Data Source Aliases</span>
																	  </td>																	  
																	  <td id="urID1034712678372Next" class="urTbsLastOffNextoff" valign="top"> </td>
				  												</tr></tbody>
				  												</table>
				  												</div>
																  <div class="urTbsDiv"> </div>
				  												<table class="urTbsWhl" width="100%" cellpadding="0" cellspacing="0" border="0">
				  												<tbody>
																  <tr><td align="left" valign="top" class="urTbsCnt">
																  
				  				  									<div id="urID1034712678372content0" class="urTbsDspSel" style="width:100%;height:100;line-height:1em;">
							    												<h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">				  					 														
																								<h:outputLabel value="Driver Name"/><h:outputText value="#{DataSourceSettings.driverName}"/>
																								<h:outputLabel value="SQL Engine"/><h:outputText value="#{DataSourceSettings.sqlEngine}"/>
																								<h:outputLabel value="Isolation Level"/><h:outputText value="#{DataSourceSettings.isolationLevel}"/>
																								<h:outputLabel value="Deployer"/><h:outputText value="#{DataSourceSettings.deployer}"/>
																        </h:panelGrid>				  
																	    </div>
				  
				  
				  														<div id="urID1034712678372content1" class="urTbsDsp" style="width:70ex;height:100;line-height:1em;">
							    												<h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">
							    															<h:outputLabel value="Initial Connections"/><h:outputText  value="#{DataSourceSettings.initConnections}"/>
				  					 														<h:outputLabel value="Max Connections"/><h:outputText  value="#{DataSourceSettings.maxConnections}"/>	
				  					 														<h:outputLabel value="Max Time To Wait Connection"/><h:outputText  value="#{DataSourceSettings.maxTimeToWaitConnection}"/>	
				  					 														<h:outputLabel value="Connection Lifetime (Sec.)"/><h:outputText  value="#{DataSourceSettings.connectionLifetime}"/>
				  					 														<h:outputLabel value="Cleanup Interval (Sec.)"/><h:outputText  value="VILLY ERROR"/>
				  					 														 
				  					 											</h:panelGrid>	
				  														</div>
				  														
				  														
				  														<div id="urID1034712678372content2" class="urTbsDsp" style="width:70ex;height:100;line-height:1em;">
							    												<h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">
				  					 														
																								<h:outputLabel value="tablica "/><h:outputText  value="VILLY ERROR"/>
				  																</h:panelGrid>	
				  														</div>
				  														
				  														
				  														<div id="urID1034712678372content3" class="urTbsDsp" style="width:70ex;height:100;line-height:1em;">
				  																<h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">				  					 														
																								<h:outputLabel value="tablica "/><h:outputText  value="VILLY ERROR"/>
				  																</h:panelGrid>	
				  														</div>
				  												</td></tr>
				  												</tbody>
				  												</table>			  
				  									  </td></tr></tbody>
				  								</table>
				  					</td></tr>
				  				</tbody>
				  		</table>        		
        		</h:form>
         
        		<h:form id="applicationsLink">               
            		<font class="urTrcTitHdr"><h:commandLink action="application" value="Applications"/></font>                
        		</h:form>
        
        </p:portletPage>       
        </f:view>    
    </body>
</html>