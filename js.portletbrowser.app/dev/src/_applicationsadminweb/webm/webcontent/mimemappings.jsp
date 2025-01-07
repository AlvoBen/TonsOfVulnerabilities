<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="mimeMappingsView">
<p:portletPage>

<h:form id="mimeMappingsForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="MIME Mappings" /></f:facet> 
                    <h:dataTable id="mimeMappings" value="#{ApplicationSettingsBean.mimeMappings}" var="mimeMapping" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.mimeMappings != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.mimeMappings != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.mimeMappings != null}" /></f:facet> 
                           <h:outputText value="#{mimeMapping.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.mimeMappings != null}" >
                           <f:facet name="header"><h:outputText value="Extension" rendered="#{ApplicationSettingsBean.mimeMappings != null}" /></f:facet> 
                           <h:outputText value="#{mimeMapping.extension}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.mimeMappings != null}" >
                           <f:facet name="header"><h:outputText value="MIME Type" rendered="#{ApplicationSettingsBean.mimeMappings != null}" /></f:facet> 
                           <h:outputText value="#{mimeMapping.mimeType}"/>                           
                        </h:column>
                        <h:column rendered="#{ApplicationSettingsBean.mimeMappings != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.mimeMappings != null}" /></f:facet> 
                           <h:outputText value="#{mimeMapping.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>