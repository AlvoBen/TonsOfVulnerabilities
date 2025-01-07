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
        <title>Service Properties</title>
    </head>
    <body>
    <f:view>
    <p:portletPage>
   
    		 <h:form id="navigationPath"> 
	 				 	  <table border="0" cellspacing="1" cellpadding="1" class="urTrcHdBgOpen" bgcolor="#FFFFFF" width="100%">
    	    				<tr>
        		  				<td class="urTrcHdNotch" align="left"><img width="6px" src="resources/common/1x1.gif"></td>
		          				<td width="100%" bgcolor="#FFFFFF" class="urTrcHdBgOpen"><font class="urTrcTitHdr">
		          				 		<h:commandLink action="services" value="Service" /> &nbsp;
		          				 		<h:outputLabel value=">" />&nbsp;
		          						<h:outputLabel value="#{ServicePropertiesBean.serviceName}"/>
		          				</font></td>
        					</tr>
							</table>
				 </h:form>
				 
				 <h:form id="serviceProps"> 						 		
						<h:dataTable id="outer" value = '#{ServicePropertiesBean.servicePropertiesList}' var="prop" width="100%" headerClass="hdrClss" columnClasses="stdrwClss" >
              	<h:column> 
										<f:facet name="header"><h:outputText value="Property" /></f:facet>  
										<h:outputText value="#{prop.key}"/>
						    </h:column> 
		            <h:column> 
										<f:facet name="header"><h:outputText value="Value" /></f:facet>  
										<h:inputText value="#{prop.value}" valueChangeListener="#{prop.setFlagValueChanged}"  rendered="#{prop.onlineModifable}" />
										<h:outputText rendered="#{!prop.onlineModifable}" value="#{prop.value}"/>
						    </h:column>           
              	<h:column> 
										<f:facet name="header"><h:outputText value="Description" /></f:facet>  
										<h:outputText value="#{prop.description}"/>
						    </h:column> 						    
    		    </h:dataTable>	
    		    	
    		    <h:commandLink id="updateServicePropes" styleClass="urBtnEmph" value="Update" action='#{ServicePropertiesBean.update}' />
    		    
    		    <h:panelGrid rendered="#{ServicePropertiesBean.messages}" style="width:100%;">
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
         
         <h:form id="servicesLink">               
    	          <font class="urTrcTitHdr"><h:commandLink action="services" value="Service" /></font>                
      	 </h:form>
                        
    </p:portletPage>       
    </f:view>    
    </body>
</html>
