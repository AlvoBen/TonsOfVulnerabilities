<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="responseStatusesView">
<p:portletPage>

<h:form id="responseStatusesForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Response Statuses" /></f:facet> 
                    <h:dataTable id="responseStatuses" value="#{ApplicationSettingsBean.responseStatuses}" var="responseStatus" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.responseStatuses != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.responseStatuses != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.responseStatuses != null}" /></f:facet> 
                           <h:outputText value="#{responseStatus.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.responseStatuses != null}" >
                           <f:facet name="header"><h:outputText value="Status Code" rendered="#{ApplicationSettingsBean.responseStatuses != null}" /></f:facet> 
                           <h:outputText value="#{responseStatus.statusCode}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.responseStatuses != null}" >
                           <f:facet name="header"><h:outputText value="Reason Phrase" rendered="#{ApplicationSettingsBean.responseStatuses != null}" /></f:facet> 
                           <h:outputText value="#{responseStatus.reasonPhrase}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.responseStatuses != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.responseStatuses != null}" /></f:facet> 
                           <h:outputText value="#{responseStatus.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>