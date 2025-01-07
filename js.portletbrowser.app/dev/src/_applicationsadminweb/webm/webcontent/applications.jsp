<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>



        
        <f:view>
        <p:portletPage>
        	 <h:form id="navigation_path">                         				   
        	 <table border="0" cellspacing="0" cellpadding="0" width="100%">
  				 <!--1st row-->
  				<tr>	
    			<td width="100%" colspan="2">
      			<table border="0" cellspacing="1" cellpadding="1" class="urTrcHdBgOpen" bgcolor="#FFFFFF" width="100%">
        		<tr>
          		<td class="urTrcHdNotch" align="left"><img width="6px" src="resources/common/1x1.gif"></td>
          		<td width="100%" bgcolor="#FFFFFF" class="urTrcHdBgOpen"><font class="urTrcTitHdr">
          		     <h:commandLink action="#{ApplicationBean.clearAll}" value="Applications"/> &nbsp;
           				 <h:outputText value=">" rendered="#{ApplicationBean.applicationDetailed != null}" /> &nbsp;
          				 <h:outputText value="#{ApplicationBean.applicationDetailed.name}" rendered="#{ApplicationBean.applicationDetailed != null}"/> &nbsp;          				 
          	  </font></td>
        		</tr>
        		<!--2nd row-->
  					<tr>
    					<td valign="top" bgcolor="#ffffff" width="100%" colspan="2">&nbsp; 
    						<h:commandLink id="startApplication" styleClass="urBtnEmph" value="Start" action="#{ApplicationBean.startApplications}"/> &nbsp;
                			<h:commandLink id="stopApplication"  styleClass="urBtnEmph" value="Stop" action="#{ApplicationBean.stopApplications}"/> &nbsp;
    					</td>
  					</tr>
  					<tr>
  						<td colspan="2">
  						<div id="urID1034712677991" style="position:relative;height:10em;overflow:auto;">
  						<h:dataTable id="outer" headerClass="hdrClss" columnClasses="stdrwClss" value="#{ApplicationBean.app}" var="appname" border='0' width="100%">
  							      <h:column>                               
                               <h:selectBooleanCheckbox value="#{appname.selected}"/>
			                </h:column> 
							
            			    <h:column>
		                   <f:facet name="header">
						   						<h:outputText value="Application Name"/>
						  				 </f:facet> 
						   
						   
        		           	<h:commandLink action='#{ApplicationBean.view}' value="#{appname.name}" actionListener='#{ApplicationBean.applicationNameListener}'/>
               				</h:column> 
							
							
															<h:column>
                                <f:facet name="header"><h:outputText value="State"/></f:facet> 
                                <h:outputText value="#{appname.stateString}"/>
                            </h:column>
  						</h:dataTable>	
  						</div>
  					</td>
    				</tr>
      			</table>
      			
      			<h:panelGrid rendered="#{ApplicationBean.messages}" style="width:100%;">
      					<div class="urMsgBarStd" id="urID1034712677801">
      						<table border="0" cellpadding="0" cellspacing="0">
      							<tbody><tr><td><span id="urID1034712677801_ur_bar_img" class="urMsgBarImgOk">
      							<img height="12" width="12" src="resources/common/1x1.gif"></span></td>
      							<td><span id="urID1034712677801_ur_bar_text" class="urTxtStd"><h:messages></h:messages>
      							</span></td></tr></tbody></table></div>
      								
      			</h:panelGrid>

      		</td>
  				</tr>
  			</table>
  		
                
 				</h:form>   
 				
 				<h:form id="application_details" rendered='#{ApplicationBean.applicationDetailed != null}'>  
 					<table border="0" cellspacing="0" cellpadding="0" class="urGrcWhl" style="width:100%;background-color:#ffffff;">
				  <tbody>
				  <tr>
				  <td>
				  <table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
				  <tbody><tr valign="top"><td>
				  <div id="urID1034712678372scroll" class="urTbsstripScrollDIV">
				  <table id="urID1034712678372table" class="urTbsstripTABLE" border="0" cellpadding="0" cellspacing="0" selectedtab="0" tabcount="3">
				  <tbody >
				  <tr>
				  <td id="urID1034712678372Prev" class="urTbsFirstAngOnPrevoff" valign="top"> </td>
				  <td id="urID1034712678372tab0" nowrap class="urTbsLabelOn"><span id="urID1034712678372tab0_a" onClick="UR_TabChange('urID1034712678372',0,0,event);" title="ApplicationDetailes" class="urTbsTxtOn" style="text-decoration:none;">Application Details</span></td>
				  <!-- 
				  <td valign="top" id="urID1034712678372tab1Ang" valign="top" class="urTbsAngOnOff"> </td>
				  <td id="urID1034712678372tab1" nowrap class="urTbsLabelOff"><span title="Application Descriptors" id="urID1034712678372tab1_a" onClick="UR_TabChange('urID1034712678372',1,0,event);" class="urTbsTxtOff">Application Descrptors</span></td>
				  <td valign="top" id="urID1034712678372tab2Ang" valign="top" class="urTbsAngOffOff"> </td>
				  <td id="urID1034712678372tab2" nowrap class="urTbsLabelOff"><span title="Tab 2" id="urID1034712678372tab2_a" onClick="UR_TabChange('urID1034712678372',2,0,event);" class="urTbsTxtOff">Others ...</span></td>
				  -->
				  <td id="urID1034712678372Next" class="urTbsLastOffNextoff" valign="top"> </td>
				  </tr>
				  </tbody>
				  </table>
				  </div>
				  <div class="urTbsDiv"> </div>
				  <table class="urTbsWhl" width="100%" cellpadding="0" cellspacing="0" border="0">
				  <tbody>
				  <tr>
				  <td align="left" valign="top" class="urTbsCnt">
				  	<!--Application Detail -->
				  	
				    <!-- 
				  		<div id="urID1034712678372content1" class="urTbsDsp" style="width:70ex;height:100;line-height:1em;"><span class="urBtnEmphDsbl" id="button_1" title="Tab Content Button1 Button unavailable Tooltip"><nobr>Under development</nobr></span></div>
						  <div id="urID1034712678372content2" class="urTbsDsp" style="width:70ex;height:100;line-height:1em;"><a href="javascript:void(0);" onclick="UR_ButtonClick('button_2',event);" class="urBtnStd" id="button_2" title="Tab Content Button2 Button  Tooltip"><nobr>To be provided</nobr></a></div>
						-->  
				  </td>
				  </tr>
				  </tbody>
				  </table>
				  <div id="urID1034712678372content0" class="urTbsDspSel" style="width:100%;height:100;line-height:1em;">
				     <h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss">
				    	<f:facet name="caption"><h:outputText  value="#{ApplicationBean.applicationDetailed.name}"/></f:facet>
						<h:outputLabel value="Element Name"/><h:outputText value="#{ApplicationBean.applicationDetailed.elementName}"/>
						<h:outputLabel value="Software type"/><h:outputText value="#{ApplicationBean.applicationDetailed.softwareType}"/>
						<h:outputLabel value="Vendor"/><h:outputText value="#{ApplicationBean.applicationDetailed.vendor}"/>
						<h:outputLabel value="Archive size"/><h:outputText value="#{ApplicationBean.applicationDetailed.archiveSize}"/>
						<h:outputLabel value="Application Failover"/><h:outputText value="#{ApplicationBean.applicationDetailed.applicationFailover}"/>
						<h:outputLabel value="Remote support"/><h:outputText value="#{ApplicationBean.applicationDetailed.remoteSupportString}"/>
				    </h:panelGrid>

                    <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Modules"/></f:facet>                      
                    <h:dataTable id="inner" value="#{ApplicationBean.applicationDetailed.modules}" var="modules" width="100%" border="1" cellpadding="0" cellspacing="0" headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column>
                           <f:facet name="header"><h:outputText value="Type" /></f:facet> 
                           <h:outputText value="#{modules.type}"/>                    
                        </h:column>                           
                        <h:column>
                           <f:facet name="header"><h:outputText value="Name" /></f:facet> 
                           <h:commandLink action='#{ApplicationBean.view}' value='#{modules.name}' actionListener='#{ApplicationBean.webModuleNameListener}' rendered='#{modules.type=="web"}'/>
                           <h:outputText rendered='#{modules.type!="web"}' value='#{modules.name}'/>
                        </h:column>
                    </h:dataTable> 
				  </h:panelGrid>
				  
				  </div>
				  </td>
				  </tr>
				  </tbody>
				  </table>
				  </td>
				  </tr>
				  </tbody>
				  </table>
                    
          </h:form>
                
                    
        </p:portletPage>       
        </f:view>