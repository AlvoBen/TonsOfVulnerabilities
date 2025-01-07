<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="p" uri="http://java.sun.com/jsf/portlet/components" %>


        <f:view>
        <p:portletPage>
                <h:form id="services_form">
				<table border="0" cellspacing="0" cellpadding="0" width="100%" bgcolor="#FFFFFF">
  				 <!--1st row-->
  				<tr>	
    				<td width="100%" colspan="2">
	      			<table border="0" cellspacing="1" cellpadding="1" class="urTrcHdBgOpen" bgcolor="#FFFFFF" width="100%">
    	    		<tr>
        		  		<td class="urTrcHdNotch" align="left"><img width="6px" src="resources/common/1x1.gif"></td>
          				<td width="100%" bgcolor="#FFFFFF" class="urTrcHdBgOpen"><font class="urTrcTitHdr"><h:outputText value="Services"/></font></td>
        			</tr>
					</table>
					</td>
				</tr>
				<tr>
				<td colspan="2">
				<div id="urID1034712677991" style="position:relative;height:10em;overflow:auto;">
  						
						<h:dataTable id="outer"  headerClass="hdrClss" columnClasses="stdrwClss" value="#{ServicesBean.services}" var="serviceName" border='0' width="100%">
  							 
						   <h:column>
			                   <f:facet name="header">
			 						<h:outputText value="Service Name"/>
			  				 </f:facet> 
	    		           	<h:commandLink action='#{ServicesBean.view}' value='#{serviceName.name}' actionListener='#{ServicesBean.nameListener}'/>
               				</h:column> 
													
							
							   <h:column>
                                 <f:facet name="header"><h:outputText value="Status" /></f:facet>  
                                 <h:outputText value="#{serviceName.statusString}"/>
                            </h:column>    
							
                            <h:column>
                                <f:facet name="header"><h:outputText value="Core" /></f:facet>   
                               <h:outputText value="#{serviceName.isCore}"/>
                            </h:column>                               
  						</h:dataTable>	
  					</div>
                  </td>
				  </tr>
				  </table>          
                </h:form>
        </p:portletPage>       
        </f:view>