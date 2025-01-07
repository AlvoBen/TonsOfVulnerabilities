/*
 * Created on 02.05.2003
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.sap.jms.util;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.ContextNotEmptyException;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import com.sap.jms.JMSConstants;
import com.sap.jms.util.logging.LogService;
import com.sap.jms.util.logging.LogServiceImpl;

/**
 * @author D040756
 *
 * This class is a singleton that holds methods that are regularly used to
 * access the JNDI. It works only on a replicated context. <b>Be aware that you
 * can't mix replicated and unreplicated contexts on the same branch of the
 * tree! </b>
 */
public final class JNDIHelper {
    private static final String LOG_COMPONENT = "util.JNDIHelper";
    private static final LogService log = LogServiceImpl.getLogService();

    private InitialContext initialContext;

    //The environment setting for the replicated InitialContext
    private final Hashtable replicatedEnv = new Hashtable(3);
    private final Hashtable nonReplicatedEnv = new Hashtable(1);

    private boolean isReplicatedContext = true;
    private static final JNDIHelper instance = null;//new JNDIHelper();

    private JNDIHelper(boolean isReplicatedContext) {
    	this.isReplicatedContext = isReplicatedContext;
    	if (isReplicatedContext) {
	        replicatedEnv.put("Replicate", "true");
	        replicatedEnv.put("domain", "true");
    	} else {
    		nonReplicatedEnv.put("domain", "true");
    	}
    }

    public static JNDIHelper getInstance(boolean isReplicated) {
        return new JNDIHelper(isReplicated);
    }

    /**
     * Return an InitialContext that is replicated.
     *
     * @return A InitialContext that replicates entries on all nodes.
     * @throws NamingException
     */
    public synchronized InitialContext getReplicatedInitialContext() throws NamingException {
        if (initialContext == null) {
            initialContext = new InitialContext(replicatedEnv);
        }
        return initialContext;
    } //getReplicatedInitialContext
    
    
    /**
     * Return an InitialContext that is NOT replicated.
     *
     * @return  InitialContext .
     * @throws NamingException
     */
    public synchronized InitialContext getInitialContext() throws NamingException {
        if (initialContext == null) {
            initialContext = new InitialContext(nonReplicatedEnv);
        }
        return initialContext;
    } //getInitialContext

    /**
     * Return a Context.
     *
     * @param String
     *            Name for the Context
     * @return A Context
     * @throws NamingException
     */
    private Context getContext(String name) throws NamingException {
        Context result = null;
        if (isReplicatedContext) {
	        try {
	            result = (Context) getReplicatedInitialContext().lookup(name);
	        } catch (NameNotFoundException e) {
			    log.infoTrace(LOG_COMPONENT, "lookup failed because context {0} was missing. The contexts will be created.", name);
			    log.exception(LogService.INFO, LOG_COMPONENT, e);
	            result = getReplicatedInitialContext().createSubcontext(name);
	        }
        } else {
        	 try {
 	            result = (Context) getInitialContext().lookup(name);
 	        } catch (NameNotFoundException e) {
 			    log.infoTrace(LOG_COMPONENT, "lookup failed because context {0} was missing. The contexts will be created.", name);
 			    log.exception(LogService.INFO, LOG_COMPONENT, e);
 	            result = getInitialContext().createSubcontext(name);
 	        }
        }
        return result;
    } //getContext

    /**
     * Return the FactoriesContext if not existent create it.
     *
     * @return The FactoryContext
     * @throws NamingException
     */
    public Context getFactoriesContext() throws NamingException {
        return getContext(JMSConstants.JMS_FACTORY_SUBCONTEXT);
    } //getFactoriesContext

    /**
     * Return the FactoriesContext if not existent create it.
     *
     * @return The FactoryContext
     * @throws NamingException
     */
    public Context getQueuesContext() throws NamingException {
        return getContext(JMSConstants.JMS_QUEUES_SUBCONTEXT);
    } //getFactoriesContext

    /**
     * Return the FactoriesContext if not existent create it.
     *
     * @return The FactoryContext
     * @throws NamingException
     */
    public Context getTopicsContext() throws NamingException {
        return getContext(JMSConstants.JMS_TOPICS_SUBCONTEXT);
    } //getFactoriesContext

    /**
     * Binds the given Object to the given Name
     *
     * @param contextName
     * @param object
     * @throws NamingException
     */
    public void bind(String contextName, Object object) throws NamingException {
        InitialContext context = null;
        try {
        	if (isReplicatedContext) {
        		context = getReplicatedInitialContext();
        	} else {
        		context = getInitialContext();
        	}
            contextName = convertSlashes(contextName);
            context.bind(contextName, object);
        }catch(NameAlreadyBoundException e){
		    log.infoTrace(LOG_COMPONENT, "bind failed because name {0} is already bound. Rebind will be used instead.", contextName);
		    log.exception(LogService.INFO, LOG_COMPONENT, e);
            context.rebind(contextName, object);
        }catch(NameNotFoundException e){
		    log.infoTrace(LOG_COMPONENT, "bind failed because a subcontext of {0} was missing. Intermediate contexts will be created.", contextName);
		    log.exception(LogService.INFO, LOG_COMPONENT, e);
            //Create the contexts
            String[] contexts = contextName.split("/");
            String subContext = "/";
            for (int i = 0; i < contexts.length-1; i++) {
                subContext += contexts[i] + '/';
                // getContext creates the context if it doesn't exist.
                getContext(subContext);
            }
            context.bind(contextName, object);
        }
    }

 
     public boolean isExistingContext(String name) {
     	boolean isExisting  = false;
     	try {
     		if (isReplicatedContext) {
     			getReplicatedInitialContext().lookup(name);
     		} else {
     			getInitialContext().lookup(name);
     		}
            isExisting = true;
        } catch (NamingException e) {
		    isExisting = false;
        }
        return isExisting;
     }
 
    
    /**
     * Unbinds the Object that was bound under the given name.
     * If the name contains "/" tries to destroy subscontexts if they are empty
     *
     * @param contextName
     * @throws NamingException
     */
    public void unbind(String contextName) throws NamingException {
      int index = contextName.lastIndexOf('/');
         
      if (isReplicatedContext) {
	      if (index == -1) {
	      	getReplicatedInitialContext().unbind(contextName);
	      } else {
	       getContext(contextName.substring(0, index)).unbind(contextName.substring(index + 1));
	        destroyContext(getReplicatedInitialContext(), contextName.substring(0, index));
	      }
      } else {
    	  if (index == -1) {
    	      	getInitialContext().unbind(contextName);
    	      } else {
    	       getContext(contextName.substring(0, index)).unbind(contextName.substring(index + 1));
    	        destroyContext(getInitialContext(), contextName.substring(0, index));
    	      }
      }
    }
    
    public void unbindSubContext(String jmsSubContext, String instance_name, NamingEnumeration/*<Binding>*/ enumeration) throws NamingException{
    	while(enumeration.hasMoreElements()) {
            Binding name = (Binding) enumeration.nextElement();
            if(name.getObject() instanceof Context){
            	unbindSubTree(jmsSubContext + instance_name + '/' + name.getName());
            }else{
            	unbindOnlyName(jmsSubContext + instance_name + '/' + name.getName());
            }
        }
    	if (isReplicatedContext) {
    		getReplicatedInitialContext().destroySubcontext(jmsSubContext + instance_name);
    	} else {
    		getInitialContext().destroySubcontext(jmsSubContext + instance_name);
    	}
    }
    
    public void unbindObject(String lookupName, String context) throws NamingException{
		if(lookupName.indexOf("/") != -1){
			unbindOnlyName(context + lookupName);
			while(lookupName.indexOf("/") != -1){
				int lastIndex = lookupName.lastIndexOf("/");
				String subcontextName = lookupName.substring(0, lastIndex);
				if (isReplicatedContext) {
					NamingEnumeration/*<Binding>*/ bindings = getReplicatedInitialContext().listBindings(context + subcontextName);				
					if(!bindings.hasMoreElements()){
						getReplicatedInitialContext().destroySubcontext(context + subcontextName);
					}else{
						break;
					}
				} else {
					NamingEnumeration/*<Binding>*/ bindings = getInitialContext().listBindings(context + subcontextName);				
					if(!bindings.hasMoreElements()){
						getInitialContext().destroySubcontext(context + subcontextName);
					} else { 
						break;
					}	
				}
				lookupName = subcontextName;
			}
		}else{
			unbindOnlyName(context + lookupName);
		}
	}

    private String validateLastSlash(String string) {
    	String lastChar = String.valueOf(string.charAt(string.length() - 1));
    	if(!lastChar.equals("/")){
    		return string + "/";
    	}else{
    		return string;
    	}
	}
    
    private void unbindSubTree(String contextName) throws NamingException {
    	NamingEnumeration/*<Binding>*/ enumeration = null;
    	if (isReplicatedContext) {
    		 enumeration = getReplicatedInitialContext().listBindings(contextName);
    	} else {
    		 enumeration = getInitialContext().listBindings(contextName);
    	}
		while(enumeration.hasMoreElements()) {
            Binding name = (Binding) enumeration.nextElement();
            contextName = validateLastSlash(contextName);
            if(!(name.getObject() instanceof Context)){
            	unbindOnlyName(contextName + name.getName());                	
            }else{
            	unbindSubTree(contextName + name.getName());
            }
		}
		if (isReplicatedContext) {
			getReplicatedInitialContext().destroySubcontext(contextName);
		} else {
			getInitialContext().destroySubcontext(contextName);
		}
    }
    
    private void unbindOnlyName(String contextName) throws NamingException {
        int index = contextName.lastIndexOf('/');       
        if (index == -1) {
        	if (isReplicatedContext) {
        		getReplicatedInitialContext().unbind(contextName);
        	} else {
        		getInitialContext().unbind(contextName);
        	}
        } else {
         getContext(contextName.substring(0, index)).unbind(contextName.substring(index + 1));
        }
    }

	/**
     * Destroys context with the given name
     * and all of the belonging subContexts if they are not empty
     *
     * @param   root Context which contains Context with name  contextName
     * @param   contextName
     * @exception   javax.naming.NamingException
     */
    private static void destroyContext(Context root, String contextName) throws NamingException {
      if (contextName.equals("")) {
        return;
      }

      int index = contextName.indexOf('/');
      try {
        if (index == -1) {
          root.destroySubcontext(contextName);
        } else {
          String s = contextName.substring(0, index);
          destroyContext((Context)root.lookup(contextName.substring(0, index)), contextName.substring(index + 1));
          root.destroySubcontext(s);
        }
      } catch (ContextNotEmptyException e) {
        log.debug(LOG_COMPONENT, "destroySubcontext failed because name {0} is not empty.", contextName);
//	    log.exception(LogService.WARNING,LOG_COMPONENT,e);
      }
    }
    
    /**
     * Return the prefix of a name consisting of one of the jms contexts + the instancename.
     * @param name
     * @return The prefix
     */
    public String getPrefix(String name) {
        String prefix = null;
        if (name.startsWith(JMSConstants.JMS_QUEUES_SUBCONTEXT)) {
            prefix = JMSConstants.JMS_QUEUES_SUBCONTEXT + getInstanceName(name);
        } else if (name.startsWith(JMSConstants.JMS_TOPICS_SUBCONTEXT)) {
            prefix = JMSConstants.JMS_TOPICS_SUBCONTEXT + getInstanceName(name);
        } else if (name.startsWith(JMSConstants.JMS_FACTORY_SUBCONTEXT)) {
            prefix = JMSConstants.JMS_FACTORY_SUBCONTEXT + getInstanceName(name);
        }
        return prefix;
    }

    private String getInstanceName(String contextName) {
        String temp = "";
        if (contextName.startsWith(JMSConstants.JMS_QUEUES_SUBCONTEXT)) {
            temp = contextName.substring(JMSConstants.JMS_QUEUES_SUBCONTEXT.length());
        } else if (contextName.startsWith(JMSConstants.JMS_TOPICS_SUBCONTEXT)) {
            temp = contextName.substring(JMSConstants.JMS_TOPICS_SUBCONTEXT.length());
        } else if (contextName.startsWith(JMSConstants.JMS_FACTORY_SUBCONTEXT)) {
            temp = contextName.substring(JMSConstants.JMS_FACTORY_SUBCONTEXT.length());
        }
        return temp.substring(0, temp.indexOf('/'));
    }

    /**
     * Converts the SLASH_REPLACE_CHAR with slash if any
     *
     * @param name
     * @return
     */
    public static String convertSlashes(String name) {
        if (name.indexOf((JMSConstants.SLASH_REPLACE_CHAR)) != -1) {
        	name = name.replace(JMSConstants.SLASH_REPLACE_CHAR, '/');
        }
        while(name.indexOf((JMSConstants.SLASH_REPLACE_CHAR)) != -1) {
        	name = convertSlashes(name);
        }
        return name;
    }
    
}