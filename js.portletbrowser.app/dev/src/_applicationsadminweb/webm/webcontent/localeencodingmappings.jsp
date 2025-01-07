<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="localeEncodingMappingsView">
<p:portletPage>

<h:form id="localeEncodingMappingsForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Locale Encoding Mappings" /></f:facet> 
                    <h:dataTable id="localeEncodingMappings" value="#{ApplicationSettingsBean.localeEncodingMappings}" var="localeEncodingMapping" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" >
                           <f:facet name="header"><h:outputText value="Element Name" rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" /></f:facet> 
                           <h:outputText value="#{localeEncodingMapping.elementName}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" >
                           <f:facet name="header"><h:outputText value="Encoding" rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" /></f:facet> 
                           <h:outputText value="#{localeEncodingMapping.encoding}"/>                    
                        </h:column>                           
                        <h:column rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" >
                           <f:facet name="header"><h:outputText value="Location" rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" /></f:facet> 
                           <h:outputText value="#{localeEncodingMapping.location}"/>                    
                        </h:column>                                                   
                        <h:column rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" >
                           <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.localeEncodingMappings != null}" /></f:facet> 
                           <h:outputText value="#{localeEncodingMapping.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>