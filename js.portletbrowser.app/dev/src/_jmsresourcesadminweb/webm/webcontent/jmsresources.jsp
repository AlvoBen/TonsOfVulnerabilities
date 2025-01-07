<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components"%>

<script>
	<!--
		function getit(){
			var test = document.getElementById("hidden_tab");
			var d1;
			if (test){
				test = test.firstChild;
		//		alert("test:"+test.value);
				if (test.value == "1"){
					UR_TabChange('urID1034712678372',0,0,event);
				}
			  if (test.value == "2"){
			  	UR_TabChange('urID1034712678372',1,0,event);
				}
			  if (test.value == "3"){
			  	UR_TabChange('urID1034712678372',2,0,event);
				}
			}	
		}
		
		
		function setTab(obj,tab){
			if (obj){
			//	alert("td:"+obj.id);
				var name = obj.id + ":tab_name";
				//alert(document.getElementById(name) + ":" + tab);
				document.getElementById(name).value=tab;
	    }
	  }  
	-->
</script>



<body onLoad="getit();">

<f:view>
	<p:portletPage>
		<h:form id="jms">
			<div id="hidden_tab">
				     				<h:inputHidden id="tab_name" value="#{JMSResources.mode}"/>
				  			</div>
			<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<!--1st row: navigation path -->
				<tr><td width="100%" colspan="2">
					<table border="0" cellspacing="1" cellpadding="1" class="urTrcHdBgOpen" bgcolor="#FFFFFF" width="100%">
						<tr>
							<td class="urTrcHdNotch" align="left"><img width="6px" src="resources/common/1x1.gif"></td> 
							<td width="100%" bgcolor="#FFFFFF" class="urTrcHdBgOpen"><font class="urTrcTitHdr">
							      <h:commandLink id="jms_root" value="JMS Resources" action="#{JMSResources.clearAll}" /> &nbsp;
							      <h:outputText value=" > " rendered="#{JMSResources.selectedResouceName != null}" /> &nbsp;
							      <h:outputText value="#{JMSResources.selectedResouceType}" rendered="#{JMSResources.selectedResouceName != null}" /> &nbsp;
							      <h:outputText value=" > " rendered="#{JMSResources.selectedResouceName != null}" /> &nbsp;
							      <h:outputText value="#{JMSResources.selectedResouceName}" rendered="#{JMSResources.selectedResouceName != null}" />
							</td>
						</tr>
					</table>	
					</td></tr>					
					
					<!--2nd row: Create / Remove -->
					<tr>
							<td valign="top" bgcolor="#ffffff" width="100%" colspan="2">&nbsp;
							    <h:commandLink id="createJMSResources" styleClass="urBtnEmph" value="Create" action="#{JMSResources.create}" /> &nbsp;
							    <h:commandLink id="removeJMSResources" styleClass="urBtnEmph" value="Remove" action="#{JMSResources.remove}" /> &nbsp;
						  </td>
					</tr>
					
					<tr>
					  	<td>
					  	 		<h:panelGrid captionClass="capHdr" columns="5" columnClasses="stdrwClss" rendered='#{JMSResources.createSelected}' border="0"  >
  											<h:outputText value="Create new jms resouce of type"/>
												<h:selectOneMenu id="selectNewResType" value='#{JMSResources.newResouceType}'>
													<f:selectItems  value="#{JMSResources.resouceTypesList}" />
												</h:selectOneMenu>  											
  											<h:outputText value=" and name "/>
  											<h:inputText value='#{JMSResources.newResouceName}'  />
  											<h:commandLink styleClass="urBtnEmph" style="font-size:13px;" value="Do Create" action='#{JMSResources.createResouce}' />
  							  </h:panelGrid>	 
					  	</td>
  			  </tr>
					<!-- 3rd row: Properties for default Virtual Provider -->
					<!-- TODO -->
						
					<!--  4th row: tabs with Topics, Queues, ConnectionFactories -->
					<tr valign="top"><td>
				  		<div id="urID1034712678372scroll" class="urTbsstripScrollDIV">
				  		<table id="urID1034712678372table" class="urTbsstripTABLE" border="0" cellpadding="0" cellspacing="0" selectedtab="0" tabcount="3">
				  		<tbody>
				  		<tr>
				  			<td id="urID1034712678372Prev" class="urTbsFirstAngOnPrevoff" valign="top"> </td>
				  			<td id="urID1034712678372tab0" nowrap class="urTbsLabelOn">
				  				<span title="Topics" id="urID1034712678372tab0_a" onClick="UR_TabChange('urID1034712678372',0,0,event);" class="urTbsTxtOn" style="text-decoration:none;">Topics</span>
				  		  </td>
				  			<td valign="top" id="urID1034712678372tab1Ang" valign="top" class="urTbsAngOnOff"> </td>
				  			<td id="urID1034712678372tab1" nowrap class="urTbsLabelOff">
				  				<span title="Queues" id="urID1034712678372tab1_a" onClick="UR_TabChange('urID1034712678372',1,0,event);" class="urTbsTxtOff">Queues</span>
				  		  </td>
				  		  <td valign="top" id="urID1034712678372tab2Ang" valign="top" class="urTbsAngOffOff"> </td>
				  			<td id="urID1034712678372tab2" nowrap class="urTbsLabelOff">
				  				<span title="ConnectionFactories" id="urID1034712678372tab2_a" onClick="UR_TabChange('urID1034712678372',2,0,event);" class="urTbsTxtOff">Connection Factories</span>
				  		  </td>
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
									  	<div id="urID1034712678372content0" class="urTbsDspSel" style="width:70ex;height:150;line-height:1em;overflow:auto"><span class="urBtnEmphDsbl" id="TopicsList" title="Topics">
									  	<h:dataTable id="outer1" headerClass="hdrClss" columnClasses="stdrwClss" value="#{JMSResources.topics}" var="topic" border='0' width="100%">
									  	  	<h:column>                               
                               <h:selectBooleanCheckbox value="#{topic.selected}"/>
			                		</h:column>
						     			    <h:column>
		                          <f:facet name="header">	<h:outputText value="Topic"/></f:facet> 
		                          <h:commandLink value="#{topic.name}" actionListener='#{JMSResources.topicPropertiesListener}' onclick="setTab(this,\"1\");"/> 		                 	
               				    </h:column> 
														
													<h:column>
                              <f:facet name="header"><h:outputText value="Lookup Name"/></f:facet> 
                              <h:outputText value="#{topic.lookupName}" />
                          </h:column>
  						        </h:dataTable>	 						        
  						        </span></div>		
									  			  
									  	<div id="urID1034712678372content1" class="urTbsDsp" style="width:70ex;height:150;line-height:1em;"><span class="urBtnEmphDsbl" id="QueuesList" title="Queues">
									  	<h:dataTable id="outer2" headerClass="hdrClss" columnClasses="stdrwClss" value="#{JMSResources.queues}" var="queue" border='0' width="100%">
									  	  	<h:column>                               
                               <h:selectBooleanCheckbox value="#{queue.selected}"/>
			                		</h:column>									  	
						     			    <h:column>
		                          <f:facet name="header">	<h:outputText value="Queue"/> 	</f:facet> 
		                          <h:commandLink value="#{queue.name}" actionListener='#{JMSResources.queuePropertiesListener}' onclick="setTab(this,\"2\");"/>						   						    
               				    </h:column> 
														
													<h:column>
                              <f:facet name="header"><h:outputText value="Lookup Name"/></f:facet> 
                              <h:outputText value="#{queue.lookupName}" />
                          </h:column>
  						        </h:dataTable>	  						        
  								  	</span></div>
  								  	  								  	
							  			<div id="urID1034712678372content2" class="urTbsDsp" style="width:70ex;height:150;line-height:1em;"><span class="urBtnEmphDsbl" id="ConnectionFactoriesList" title="Connection Factories">
							  			<h:dataTable id="outer3" headerClass="hdrClss" columnClasses="stdrwClss" value="#{JMSResources.connectionFactories}" var="connectionFactory" border='0' width="100%">
							  					<h:column>                               
                               <h:selectBooleanCheckbox value="#{connectionFactory.selected}"/>
			                		</h:column>
						     			    <h:column>
		                          <f:facet name="header">	<h:outputText value="connection Factory"/> 	</f:facet> 
		                          <h:commandLink value="#{connectionFactory.name}" actionListener='#{JMSResources.cfPropertiesListener}' onclick="setTab(this,\"3\");"/>	  		                 	
               				    </h:column> 
														
													<h:column>
                              <f:facet name="header"><h:outputText value="Lookup Name"/></f:facet> 
                              <h:outputText value="#{connectionFactory.lookupName}" />
                          </h:column>
  						        </h:dataTable>  						        
							   			</span></div>
				  				</td>
				  			</tr>
				  			

				  		</tbody>
				  	  </table>
				    
					  </td></tr>
					  
				  	<!--  5th row: resouces properties -->					  		
            <tr> <td>                
							        <!-- topic's properties -->
				  			  		<h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss" rendered='#{JMSResources.selectedJMSTopic != null}'>
				    						<f:facet name="caption">
				    						   <h:outputText id="currentTopicName" value='Topic > #{JMSResources.selectedJMSTopic.name}' rendered='#{JMSResources.selectedJMSTopic != null}'/>
				    						</f:facet>
				    						
												<h:outputLabel value="AgentKeepAliveTimeSeconds"/><h:inputText value="#{JMSResources.selectedJMSTopic.agentKeepAliveTimeSeconds}"/>
												<h:outputLabel value="AverageMessageSize"/><h:inputText value="#{JMSResources.selectedJMSTopic.averageMessageSize}"/>												
												<h:outputLabel value="ConnectionId"/><h:outputText value="#{JMSResources.selectedJMSTopic.connectionId}"/>
												<h:outputLabel value="Description"/><h:outputText value="#{JMSResources.selectedJMSTopic.description}"/>
												<h:outputLabel value="DestinationName"/><h:outputText value="#{JMSResources.selectedJMSTopic.destinationName}"/>
												<h:outputLabel value="DestinationType"/><h:outputText value="#{JMSResources.selectedJMSTopic.destinationType}"/>
												<h:outputLabel value="Id"/><h:outputText value="#{JMSResources.selectedJMSTopic.id}"/>
												<h:outputLabel value="IsTemprorary"/><h:outputText value="#{JMSResources.selectedJMSTopic.isTemprorary}"/>
												<h:outputLabel value="JMSDeliveryCountEnabled"/><h:inputText value="#{JMSResources.selectedJMSTopic.jmsDeliveryCountEnabled}"/>
												<h:outputLabel value="MemoryQueueMaxRowsOnStartup"/><h:inputText value="#{JMSResources.selectedJMSTopic.memoryQueueMaxRowsOnStartup}"/>
												<h:outputLabel value="MemoryQueueMaxRowsToSelect"/><h:outputText value="#{JMSResources.selectedJMSTopic.memoryQueueMaxRowsToSelect}"/>
												<h:outputLabel value="MemoryQueueSize"/><h:outputText value="#{JMSResources.selectedJMSTopic.memoryQueueSize}"/>
												<h:outputLabel value="WorkListBufferSize"/><h:outputText value="#{JMSResources.selectedJMSTopic.workListBufferSize}"/>
												<h:outputLabel value="WorkListMaxRowsToSelect"/><h:outputText value="#{JMSResources.selectedJMSTopic.workListMaxRowsToSelect}"/>
         				      </h:panelGrid>			  
         				      <h:commandLink id="updateTopicProperties" styleClass="urBtnEmph" value="Update" action="#{JMSResources.updateTopicProperties}" rendered='#{JMSResources.selectedJMSTopic != null}' onclick="setTab(this,\"1\");"/> &nbsp;  
							                  
                  		<!-- queue's properties -->
					     				<h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss" rendered='#{JMSResources.selectedJMSQueue != null}'>
		  		  						<f:facet name="caption">
		    								   <h:outputText value='Queue > #{JMSResources.selectedJMSQueue.name}' rendered='#{JMSResources.selectedJMSQueue != null}'/>
		    								</f:facet>   
				    						
												<h:outputLabel value="AgentKeepAliveTimeSeconds"/><h:inputText value="#{JMSResources.selectedJMSQueue.agentKeepAliveTimeSeconds}"/>
												<h:outputLabel value="AverageMessageSize"/><h:inputText value="#{JMSResources.selectedJMSQueue.averageMessageSize}"/>												
												<h:outputLabel value="ConnectionId"/><h:outputText value="#{JMSResources.selectedJMSQueue.connectionId}"/>
												<h:outputLabel value="DeliveryAttemptsLimited"/><h:inputText value="#{JMSResources.selectedJMSQueue.deliveryAttemptsLimited}"/>												
												<h:outputLabel value="Description"/><h:outputText value="#{JMSResources.selectedJMSQueue.description}"/>
												<h:outputLabel value="DestinationName"/><h:outputText value="#{JMSResources.selectedJMSQueue.destinationName}"/>
												<h:outputLabel value="DestinationType"/><h:outputText value="#{JMSResources.selectedJMSQueue.destinationType}"/>												
												<h:outputLabel value="Id"/><h:outputText value="#{JMSResources.selectedJMSQueue.id}"/>
												<h:outputLabel value="IsTemprorary"/><h:outputText value="#{JMSResources.selectedJMSQueue.isTemprorary}"/>
												<h:outputLabel value="JMSDeliveryCountEnabled"/><h:inputText value="#{JMSResources.selectedJMSQueue.jmsDeliveryCountEnabled}"/>
												<h:outputLabel value="LoadBalanceBehavior"/>
														<h:selectOneMenu id="loadBalancingType" value='#{JMSResources.selectedJMSQueue.loadBalanceBehavior}'>
																<f:selectItems  value="#{JMSResources.selectedJMSQueue.loadBalanceBehaviorList}" />
														</h:selectOneMenu>
												<h:outputLabel value="MaxDeliveryAttempts"/><h:inputText value="#{JMSResources.selectedJMSQueue.maxDeliveryAttempts}"/>
												<h:outputLabel value="MemoryQueueMaxRowsToSelect"/><h:outputText value="#{JMSResources.selectedJMSQueue.memoryQueueMaxRowsToSelect}"/>
												<h:outputLabel value="MemoryQueueSize"/><h:outputText value="#{JMSResources.selectedJMSQueue.memoryQueueSize}"/>
												<h:outputLabel value="WorkListBufferSize"/><h:outputText value="#{JMSResources.selectedJMSQueue.workListBufferSize}"/>
												<h:outputLabel value="WorkListMaxRowsToSelect"/><h:outputText value="#{JMSResources.selectedJMSQueue.workListMaxRowsToSelect}"/>
       				    	  </h:panelGrid>			  
       				    		<h:commandLink id="updateQueueProperties" styleClass="urBtnEmph" value="Update" action="#{JMSResources.updateQueueProperties}" rendered='#{JMSResources.selectedJMSQueue != null}' onclick="setTab(this,\"2\");"/>
                
  						        <!-- connection factory's properties -->
                  		<h:panelGrid captionClass="capHdr" columns="2" columnClasses="stdrwClss" rendered='#{JMSResources.selectedJMSConnectionFacotry != null}'>
				    						<f:facet name="caption">
				    						   <h:outputText value='ConnectionFactory > #{JMSResources.selectedJMSConnectionFacotry.name}' rendered='#{JMSResources.selectedJMSConnectionFacotry != null}'/>
				    						</f:facet>   
				    						
												<h:outputLabel value="ClientID"/><h:inputText value="#{JMSResources.selectedJMSConnectionFacotry.clientID}"/>
												<h:outputLabel value="ConnectionType"/><h:outputText value="#{JMSResources.selectedJMSConnectionFacotry.connectionType}"/>												
												<h:outputLabel value="Description"/><h:outputText value="#{JMSResources.selectedJMSConnectionFacotry.description}"/>
												<h:outputLabel value="LookupName"/><h:outputText value="#{JMSResources.selectedJMSConnectionFacotry.lookupNameSettings}"/>
         				    	</h:panelGrid>			  
         				      <h:commandLink id="updateCFProperties" styleClass="urBtnEmph" value="Update" action="#{JMSResources.updateConnectionFactoryProperties}" rendered='#{JMSResources.selectedJMSConnectionFacotry != null}' onclick="setTab(this,\"3\");"/> &nbsp;                  
				  	</td></tr>
				  	
				  	<tr><td>
		        <h:panelGrid rendered="#{JMSResources.messages}" style="width:100%;">
    	  					<div class="urMsgBarStd" id="urID1034712677801">
      						<table border="0" cellpadding="0" cellspacing="0">
      							<tbody><tr><td><span id="urID1034712677801_ur_bar_img" class="urMsgBarImgOk">
      							<img height="12" width="12" src="resources/common/1x1.gif"></span></td>
      							<td><span id="urID1034712677801_ur_bar_text" class="urTxtStd"><h:messages styleClass="stdrwClss"></h:messages>
      							</span></td></tr></tbody></table></div>     								
      			</h:panelGrid>
            
			  		</td></tr>
			</table>
			
			</h:form>
	 </p:portletPage>       
 </f:view>
</body>
