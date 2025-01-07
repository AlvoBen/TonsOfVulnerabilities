<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>
		
<f:subview id="taglibsView">
<p:portletPage>

<h:form id="taglibsForm">          
		 <h:panelGrid captionClass="capHdr" columns="1" width="100%">
				    	<f:facet name="caption"><h:outputText  value="Taglibs" /></f:facet> 
                    <h:dataTable id="taglibs" value="#{ApplicationSettingsBean.taglibs}" var="taglib" width="100%" border="1" cellpadding="0" cellspacing="0" rendered="#{ApplicationSettingsBean.taglibs != null}"  headerClass="hdrClss" columnClasses="stdrwClss">
                        <h:column >
                           <f:facet name="header"><h:outputText value="Element Name" /></f:facet> 
                           <h:outputText value="#{taglib.elementName}"/>                    
                        </h:column>                           
                        <h:column>
                           <f:facet name="header"><h:outputText value="Location"  /></f:facet> 
                           <h:outputText value="#{taglib.location}"/>                           
                        </h:column>
                        <h:column>
                           <f:facet name="header"><h:outputText value="URI" /></f:facet> 
                           <h:outputText value="#{taglib.uri}"/>                    
                        </h:column>                           
                        <h:column>
                           <f:facet name="header"><h:outputText value="Description"/></f:facet> 
                           <h:outputText value="#{taglib.description}"/>                           
                        </h:column>                        
                    </h:dataTable>
					</h:panelGrid>
</h:form>
</p:portletPage>
</f:subview>