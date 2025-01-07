/*
 * Created on Jan 24, 2006
 */
package com.sap.engine.services.textcontainer.deployment.test;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.services.textcontainer.context.Context;
import com.sap.engine.services.textcontainer.deployment.TextComponentProcessorDeployment;
import com.sap.engine.services.textcontainer.module.TextComponentContext;
import com.sap.engine.services.textcontainer.module.TextComponentException;
import com.sap.engine.services.textcontainer.module.TextComponentS2XText;
import com.sap.engine.services.textcontainer.security.TextContainerMessageDigest;

/**
 * @author d029702
 */
public class TextComponentProcessorDeploymentDummy extends TextComponentProcessorDeployment
{

	public TextComponentProcessorDeploymentDummy(String component) throws TextComponentException
	{
		super(component);
		check_init();
	}
	
	private void check_init() {
		if( m_init ) {
			return;
		}
		m_componentMap = new HashMap<String, byte[]>();
		m_contextMap = new HashMap<Context, Integer>();
		m_bundleMap = new HashMap<String, byte[]>();
		m_init = true;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessor#setContext(com.sap.engine.textcontainer.TextComponentContext)
	 */
	public void setContext(TextComponentContext oContext) throws TextComponentException
	{
		System.out.println("    setContext('"+oContext.m_sIndustry+"','"+oContext.m_sRegion+"','"+oContext.m_sExtension+"')");
		super.setContext(oContext);
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessorDeployment#deploy(java.lang.String, java.lang.String, com.sap.engine.textcontainer.TextComponentS2XText[])
	 */
	public void deploy(String sBundle, String sRecipient, String sSourceLang, String sLang, TextComponentS2XText[] aoText) throws TextComponentException
	{
		System.out.println("    deploy.Begin");
		System.out.println("    Bundle: "+sBundle + "(" + sSourceLang + ":" + sLang + ")" + " Recipient: " + sRecipient);
		super.deploy(sBundle, sRecipient, sSourceLang, sLang, aoText);
		System.out.println("    deploy.End");
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessorDeployment#undeploy(java.lang.String)
	 */
	public void undeploy() throws TextComponentException
	{
		System.out.println("    undeploy.Begin");
		super.undeploy();
		System.out.println("    undeploy.End");
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessorDeployment#getComponentHash(java.lang.String)
	 */
	protected byte[] getComponentHash(String component) throws TextComponentException
	{
		System.out.print("      getComponentHash('"+component+"'): ");
		check_init();
		byte[] componentHash = null;
		try
		{
			componentHash = TextContainerMessageDigest.getDigestOptimizeAscii(component);
			m_componentMap.put(component, componentHash);
		}
		catch (Exception e)
		{
	    	throw new TextComponentException("Failed with Exception:\n"+e.getMessage(), e);
		}
		System.out.println(ByteArrayToString(componentHash));
		return componentHash;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessorDeployment#getBundleHash(byte[], byte[], java.lang.String, java.lang.String)
	 */
	protected void getBundleHash(byte[] bundleHash, byte[] componentHash, String bundle, String recipient) throws TextComponentException
	{
		System.out.print("      getBundleHash('" + bundleHash + ":" + componentHash + ":" + bundle+"',"+recipient+"'): ");
		try
		{
			m_bundleMap.put(componentHash + ":" + bundle, bundleHash);
		}
		catch (Exception e)
		{
	    	throw new TextComponentException("Failed with Exception:\n"+e.getMessage(), e);
		}
		System.out.println(ByteArrayToString(bundleHash));
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessorDeployment#getContextId(com.sap.engine.textcontainer.context.Context, java.lang.String, boolean)
	 */
	protected int getContextId(Context context,  boolean createNewId) throws TextComponentException
	{
		System.out.print("      getContextId('"+context.getLocale()+"','"+context.getIndustry()+"','"+context.getRegion()+"','"+context.getExtension()+"'): ");
		Integer contextId = m_contextMap.get(context);
		if ((contextId == null) && (createNewId))
		{
			contextId = new Integer(++m_maxContextId);
			m_contextMap.put(context, contextId);
		}
		System.out.println(contextId == null ? 0 : contextId.intValue());
		return (contextId == null ? 0 : contextId.intValue());
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessorDeployment#insertTextContainer(int, int, java.lang.String, java.lang.String, byte[], java.lang.String, int, java.lang.String, com.sap.engine.services.textcontainer.module.TextComponentS2XText[])
	 */
	protected int insertTextContainer(byte[] deployComponentHash, int sequenceNumber, byte[] originalComponentHash,
			byte[] bundleHash, int contextId, TextComponentS2XText[] text, String originalLocale) throws TextComponentException
	{
		for (int i = 0; i < text.length; i++)
		{
			System.out.println("      insertTextContainer("+deployComponentHash+","+ ++sequenceNumber+","+originalComponentHash+",'"+
					ByteArrayToString(bundleHash)+"',"+contextId+",'"+text[i].m_sElKey+"','"+text[i].m_sText+"','"+originalLocale+"')");
		}
		return sequenceNumber;
	}

	/* (non-Javadoc)
	 * @see com.sap.engine.textcontainer.TextComponentProcessorDeployment#modifyDirty(int, byte[], java.lang.String)
	 */
	protected void modifyDirty(byte[] originalComponentHash, byte[] bundleHash) throws TextComponentException
	{
		System.out.println("      modifyDirty("+originalComponentHash+",'"+ByteArrayToString(bundleHash)+"')");
	}

	protected Map<String, byte[]> m_componentMap;

	protected Map<Context, Integer> m_contextMap;
	protected int m_maxContextId;

	protected Map<String, byte[]> m_bundleMap;
	private boolean m_init = false;

}
