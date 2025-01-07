<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="servletMappingsView">
<p:portletPage>

<h:form id="servletMappingsForm"> 
	  <h:panelGrid captionClass="capHdr" columns="1" width="100%">
	    	<f:facet name="caption"><h:outputText  value="Servlet Mappings"/></f:facet> 
        <h:dataTable id="servlet_mappings" value="#{ApplicationSettingsBean.servletMappings}" var="servletMapping" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.servletMappings != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
             <h:column rendered="#{ApplicationSettingsBean.servletMappings != null}" >
                   <f:facet name="header"><h:outputText value="Servlet Name" rendered="#{ApplicationSettingsBean.servletMappings != null}" /></f:facet> 
                   <h:outputText value="#{servletMapping.servletName}"/>                    
             </h:column>                           
	           <h:column rendered="#{ApplicationSettingsBean.servlets != null}" >
	                 <f:facet name="header"><h:outputText value="URL Pattern" rendered="#{ApplicationSettingsBean.servletMappings != null}" /></f:facet> 
	                 <h:outputText value="#{servletMapping.urlPattern}"/>                           
	           </h:column>
						 <h:column rendered="#{ApplicationSettingsBean.servlets != null}" >
							     <f:facet name="header"><h:outputText value="Description" rendered="#{ApplicationSettingsBean.servletMappings != null}" /></f:facet> 
							     <h:outputText value="#{servletMapping.description}"/>                    
						 </h:column>                           
			  </h:dataTable> 
		 </h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>