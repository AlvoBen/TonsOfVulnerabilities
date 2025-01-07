<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="filterMappingsView">
<p:portletPage>

<h:form id="filterMappingsForm">  
    <h:panelGrid captionClass="capHdr" columns="1" width="100%">
		    	<f:facet name="caption"><h:outputText  value="Filter Mappings"/></f:facet> 
                <h:dataTable id="filter_mappings" value="#{ApplicationSettingsBean.filterMappings}" var="filterMapping" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.filterMappings != null}" headerClass="hdrClss" columnClasses="stdrwClss">
                      <h:column rendered="#{ApplicationSettingsBean.filterMappings != null}" >
											     <f:facet name="header"><h:outputText value="Filter Name" rendered="#{ApplicationSettingsBean.filterMappings != null}" /></f:facet> 
											     <h:outputText value="#{filterMapping.filterName}"/>                    
											</h:column>                           
											<h:column rendered="#{ApplicationSettingsBean.filterMappings != null}" >
											      <f:facet name="header"><h:outputText value="Servlet Name" rendered="#{ApplicationSettingsBean.filterMappings != null}" /></f:facet> 
											      <h:outputText value="#{filterMapping.servletName}"/>                           
											</h:column>
											<h:column rendered="#{ApplicationSettingsBean.filterMappings != null}" >
												    <f:facet name="header"><h:outputText value="URL Pattern" rendered="#{ApplicationSettingsBean.filterMappings != null}" /></f:facet> 
												    <h:outputText value="#{filterMapping.urlPattern}"/>                           
										  </h:column>
											<h:column rendered="#{ApplicationSettingsBean.filterMappings != null}" >
											      <f:facet name="header"><h:outputText value="Dispatcher Request" rendered="#{ApplicationSettingsBean.filterMappings != null}" /></f:facet> 
											      <h:selectBooleanCheckbox value="#{filterMapping.dispatcherRequest}" disabled = "true" />                           
											</h:column>
											<h:column rendered="#{ApplicationSettingsBean.filterMappings != null}" >
											      <f:facet name="header"><h:outputText value="Dispatcher Include" rendered="#{ApplicationSettingsBean.filterMappings != null}" /></f:facet> 
											      <h:selectBooleanCheckbox value="#{filterMapping.dispatcherInclude}" disabled = "true" />                           
											</h:column>
											<h:column rendered="#{ApplicationSettingsBean.filterMappings != null}" >
											      <f:facet name="header"><h:outputText value="Dispatcher Forward" rendered="#{ApplicationSettingsBean.filterMappings != null}" /></f:facet> 
												    <h:selectBooleanCheckbox value="#{filterMapping.dispatcherForward}" disabled = "true" />                           
											</h:column>
											<h:column rendered="#{ApplicationSettingsBean.filterMappings != null}" >
											      <f:facet name="header"><h:outputText value="Dispatcher Error" rendered="#{ApplicationSettingsBean.filterMappings != null}" /></f:facet> 
											      <h:selectBooleanCheckbox value="#{filterMapping.dispatcherError}" disabled = "true" />                           
											</h:column>                        
								</h:dataTable>
		</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>