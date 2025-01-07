package com.sap.engine.services.dc.cm.params.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.sap.engine.frame.ApplicationServiceContext;
import com.sap.engine.frame.core.configuration.ConfigurationException;
import com.sap.engine.frame.core.configuration.ConfigurationHandler;
import com.sap.engine.frame.core.configuration.ConfigurationHandlerFactory;
import com.sap.engine.frame.core.configuration.ConfigurationLockedException;
import com.sap.engine.frame.core.configuration.CustomParameterMappings;
import com.sap.engine.frame.core.configuration.NameAlreadyExistsException;
import com.sap.engine.frame.core.configuration.NameNotFoundException;
import com.sap.engine.services.dc.cm.params.Param;
import com.sap.engine.services.dc.cm.params.ParamAlreadyExistsException;
import com.sap.engine.services.dc.cm.params.ParamManager;
import com.sap.engine.services.dc.cm.params.ParamNotFoundException;
import com.sap.engine.services.dc.cm.params.ParamsException;
import com.sap.engine.services.dc.cm.params.ParamsFactory;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.logging.DCLogConstants;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-10-4
 * 
 * @author Dimitar Dimitrov, Anton Georgiev
 * @version 1.0
 * @since 7.0
 * 
 */
public class ParamManagerImpl implements ParamManager {
	
	private Location location = DCLog.getLocation(this.getClass());

	private static final String PARAM_PREFIX = "${";
	private static final String PARAM_SUFFIX = "}";

	private static final Map SDM_PARAMS_TO_SYS_PARAMS_MAP = new HashMap();

	static {
		SDM_PARAMS_TO_SYS_PARAMS_MAP.put("com.sap.engine.installdir",
				"${INSTANCE_DIR}/j2ee");
		SDM_PARAMS_TO_SYS_PARAMS_MAP.put("com.sap.systemdir",
				"${SYS_GLOBAL_DIR}");
		SDM_PARAMS_TO_SYS_PARAMS_MAP.put("com.sap.instancedir",
				"${INSTANCE_DIR}");
		SDM_PARAMS_TO_SYS_PARAMS_MAP.put("com.sap.SID", "${SYSTEM_NAME}");
		SDM_PARAMS_TO_SYS_PARAMS_MAP.put("com.sap.InstanceNumber",
				"${INSTANCE_NUMBER}");
		SDM_PARAMS_TO_SYS_PARAMS_MAP.put("com.sap.datasource.default",
				"${SYS_DATASOURCE_NAME}");
	}

	private ConfigurationHandlerFactory cfgHandlerFactory;
	private ConfigurationHandler configurationHandler;

	ParamManagerImpl() {
		this(null);
	}

	ParamManagerImpl(ConfigurationHandlerFactory cfgHandlerFactory) {
		super();
		this.cfgHandlerFactory = cfgHandlerFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#addParam(com.sap.engine
	 * .services.dc.cm.params.Param)
	 */
	public void addParam(Param param) throws ParamAlreadyExistsException,
			ParamsException {
		if (param == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003120 The argument param could not be null.");
		}

		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);

			try {
				addParam(param, paramMappings);
				commit(cfgHandler);
			} catch (ParamsException pe) {
				rollback(cfgHandler);
				throw pe;
			}
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#addParams(com.sap.engine
	 * .services.dc.cm.params.Param[])
	 */
	public void addParams(Param[] params) throws ParamAlreadyExistsException,
			ParamsException {
		if (params == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003121 The argument params could not be null.");
		}

		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);

			try {
				for (int i = 0; i < params.length; i++) {
					addParam(params[i], paramMappings);
				}
				commit(cfgHandler);
			} catch (ParamsException pe) {
				rollback(cfgHandler);
				throw pe;
			}
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#removeParam(com.sap
	 * .engine.services.dc.cm.params.Param)
	 */
	public void removeParam(Param param) throws ParamNotFoundException,
			ParamsException {
		if (param == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003122 The argument param could not be null.");
		}

		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);

			try {
				removeParam(param, paramMappings);
				commit(cfgHandler);
			} catch (ParamsException pe) {
				rollback(cfgHandler);
				throw pe;
			}
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#removeParams(com.sap
	 * .engine.services.dc.cm.params.Param[])
	 */
	public void removeParams(Param[] params) throws ParamNotFoundException,
			ParamsException {
		if (params == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003123 The argument params could not be null.");
		}

		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);

			try {
				for (int i = 0; i < params.length; i++) {
					removeParam(params[i], paramMappings);
				}
				commit(cfgHandler);
			} catch (ParamsException pe) {
				rollback(cfgHandler);
				throw pe;
			}
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#updateParam(com.sap
	 * .engine.services.dc.cm.params.Param)
	 */
	public void updateParam(Param param) throws ParamNotFoundException,
			ParamsException {
		if (param == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003124 The argument param could not be null.");
		}

		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);

			try {
				updateParam(param, paramMappings);
				commit(cfgHandler);
			} catch (ParamsException pe) {
				rollback(cfgHandler);
				throw pe;
			}
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#updateParams(com.sap
	 * .engine.services.dc.cm.params.Param[])
	 */
	public void updateParams(Param[] params) throws ParamNotFoundException,
			ParamsException {
		if (params == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003125 The argument params could not be null.");
		}

		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);

			try {
				for (int i = 0; i < params.length; i++) {
					updateParam(params[i], paramMappings);
				}
				commit(cfgHandler);
			} catch (ParamsException pe) {
				rollback(cfgHandler);
				throw pe;
			}
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.engine.services.dc.cm.params.ParamManager#getAllParams()
	 */
	public Param[] getAllParams() throws ParamsException {
		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);
			final Properties propsParam = paramMappings
					.getCustomParamMappings();
			if (propsParam == null) {
				return new Param[0];
			}

			final Param[] allParams = new Param[propsParam.size()];
			int idx = 0;
			for (Iterator iter = propsParam.entrySet().iterator(); iter
					.hasNext();) {
				final Map.Entry mapEntry = (Map.Entry) iter.next();
				allParams[idx++] = ParamsFactory.getInstance().createParam(
						(String) mapEntry.getKey(),
						(String) mapEntry.getValue());
			}

			return allParams;
		} catch (ConfigurationException ce) {
			throw new ParamsException(
					"ASJ.dpl_dc.003126 An error occurred while getting all the parameters.");
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.engine.services.dc.cm.params.ParamManager#getParamByName(java
	 * .lang.String)
	 */
	public Param getParamByName(String paramName)
			throws ParamNotFoundException, ParamsException {
		if (paramName == null) {
			throw new NullPointerException(
					"ASJ.dpl_dc.003127 The argument paramName could not be null.");
		}

		ConfigurationHandler cfgHandler = null;
		try {
			cfgHandler = getConfigurationHandler();
			final CustomParameterMappings paramMappings = getCustomParameterMappings(cfgHandler);

			final String value = getParamValue(paramName, paramMappings);

			return ParamsFactory.getInstance().createParam(paramName, value);
		} finally {
			releaseCustomParameterMappings(cfgHandler);
		}
	}

	private void addParam(Param param, CustomParameterMappings paramMappings)
			throws ParamAlreadyExistsException, ParamsException {
		final Param validatedParam = getValidatedParam(param);

		try {
			paramMappings.addCustomParameterMapping(validatedParam.getName(),
					validatedParam.getValue());
		} catch (NameAlreadyExistsException naee) {
			throw new ParamAlreadyExistsException(
					"ASJ.dpl_dc.003128 The validated custom parameter '"
							+ validatedParam
							+ "' has already been added. The original custom parameter was '"
							+ param + "'.", naee);
		} catch (ConfigurationLockedException cle) {
			throw new ParamsException(
					"ASJ.dpl_dc.003129 Configuration is locked exception occurred while "
							+ "adding the validated custom parameter '"
							+ validatedParam + "'. "
							+ "The original custom parameter was '" + param
							+ "'.", cle);
		} catch (ConfigurationException ce) {
			throw new ParamsException(
					"ASJ.dpl_dc.003130 Configuration exception occurred while "
							+ "adding the validated custom parameter '"
							+ validatedParam + "'. "
							+ "The original custom parameter was '" + param
							+ "'.", ce);
		}
	}

	private Param getValidatedParam(Param param) {
		final String mappedSysParamValue = (String) SDM_PARAMS_TO_SYS_PARAMS_MAP
				.get(param.getName());
		if (mappedSysParamValue != null) {
			DCLog
					.logInfo(location, 
							"ASJ.dpl_dc.005801",
							"Parameter Manager replaces the parameter''s [{0}] value [{1}] with the one [{2}], because the parameter is part of SDM system parameters",
							new Object[] { param.getName(), param.getValue(),
									mappedSysParamValue });

			return ParamsFactory.getInstance().createParam(param.getName(),
					mappedSysParamValue);
		}

		final Properties systemProps = getCfgHandlerFactory()
				.getSystemProfile();
		if (systemProps == null || systemProps.isEmpty()) {
			return param;
		}

		final String sysParamValue = systemProps.getProperty(param.getName());
		if (sysParamValue != null) {
			DCLog
					.logInfo(location, 
							"ASJ.dpl_dc.005802",
							"Parameter Manager replaces the parameter''s [{0}] value [{1}] with the one [{2}], because the parameter is part of SDM system parameters",
							new Object[] { param.getName(), param.getValue(),
									mappedSysParamValue });

			return ParamsFactory.getInstance().createParam(param.getName(),
					sysParamValue);
		}

		return param;
	}

	private void removeParam(Param param, CustomParameterMappings paramMappings)
			throws ParamNotFoundException, ParamsException {
		try {
			paramMappings.removeCustomParameterMapping(param.getName());
		} catch (NameNotFoundException nnfe) {
			throw new ParamNotFoundException(
					"ASJ.dpl_dc.003131 An error occurred while removing the parameter '"
							+ param + "' because it could not be found.", nnfe);
		} catch (ConfigurationLockedException cle) {
			throw new ParamsException(
					"ASJ.dpl_dc.003132 Configuration is locked exception occurred while "
							+ "removing the custom parameter '" + param + "'.",
					cle);
		} catch (ConfigurationException ce) {
			throw new ParamsException(
					"ASJ.dpl_dc.003133 Configuration exception occurred while "
							+ "removing the custom parameter '" + param + "'.",
					ce);
		}
	}

	private void updateParam(Param param, CustomParameterMappings paramMappings)
			throws ParamNotFoundException, ParamsException {
		final Param validatedParam = getValidatedParam(param);

		try {
			paramMappings.setCustomParameterValue(validatedParam.getName(),
					validatedParam.getValue());
		} catch (NameNotFoundException nnfe) {
			throw new ParamNotFoundException(
					"ASJ.dpl_dc.003134 An error occurred while updating the validated parameter '"
							+ validatedParam
							+ "' because it could not be found. "
							+ "The original custom parameter was '"
							+ param
							+ "'.", nnfe);
		} catch (ConfigurationLockedException cle) {
			throw new ParamsException(
					"ASJ.dpl_dc.003135 Configuration is locked exception occurred while "
							+ "updating the validated custom parameter '"
							+ validatedParam + "'. "
							+ "The original custom parameter was '" + param
							+ "'.", cle);
		} catch (ConfigurationException ce) {
			throw new ParamsException(
					"ASJ.dpl_dc.003136 Configuration exception occurred while "
							+ "updating the validated custom parameter '"
							+ validatedParam + "'. "
							+ "The original custom parameter was '" + param
							+ "'.", ce);
		}

	}

	private String getParamValue(String paramName,
			CustomParameterMappings paramMappings)
			throws ParamNotFoundException, ParamsException {
		final String tagSurroundedParamName;
		if (!paramName.trim().startsWith(PARAM_PREFIX)
				&& !paramName.trim().startsWith(PARAM_SUFFIX)) {
			tagSurroundedParamName = PARAM_PREFIX + paramName + PARAM_SUFFIX;
		} else {
			tagSurroundedParamName = paramName;
		}

		try {
			return paramMappings.substituteParamString(tagSurroundedParamName);
		} catch (NameNotFoundException nnfe) {
			throw new ParamNotFoundException(
					"ASJ.dpl_dc.003137 An error occurred while getting the value for the parameter '"
							+ tagSurroundedParamName
							+ "' because it could not be found."
							+ "The original custom parameter name was '"
							+ paramName + "'.", nnfe);
		} catch (ConfigurationLockedException cle) {
			throw new ParamsException(
					"ASJ.dpl_dc.003138 Configuration is locked exception occurred "
							+ "while getting the value for the custom parameter '"
							+ tagSurroundedParamName
							+ "'. The original custom parameter name was '"
							+ paramName + "'.", cle);
		} catch (ConfigurationException ce) {
			throw new ParamsException(
					"ASJ.dpl_dc.003139 Configuration exception occurred "
							+ "while getting the value for the custom parameter '"
							+ tagSurroundedParamName
							+ "'. The original custom parameter name was '"
							+ paramName + "'.", ce);
		}
	}

	private synchronized ConfigurationHandlerFactory getCfgHandlerFactory() {
		if (this.cfgHandlerFactory == null) {
			final ApplicationServiceContext appServiceCtx = ServiceConfigurer
					.getInstance().getApplicationServiceContext();

			this.cfgHandlerFactory = appServiceCtx.getCoreContext()
					.getConfigurationHandlerFactory();
		}

		return this.cfgHandlerFactory;
	}

	private synchronized ConfigurationHandler getConfigurationHandler()
			throws ParamsException {
		if (this.configurationHandler == null) {
			if (this.cfgHandlerFactory != null) {
				try {
					this.configurationHandler = this.cfgHandlerFactory
							.getConfigurationHandler();
				} catch (ConfigurationException ce) {
					throw new ParamsException(
							"ASJ.dpl_dc.003140 The Configuration exception occurred while "
									+ "getting a ConfigurationHandler from the specified factory.",
							ce);
				}
			} else {
				final ApplicationServiceContext appServiceCtx = ServiceConfigurer
						.getInstance().getApplicationServiceContext();

				final ConfigurationHandlerFactory cfgHandlerFactory = appServiceCtx
						.getCoreContext().getConfigurationHandlerFactory();

				try {
					this.configurationHandler = cfgHandlerFactory
							.getConfigurationHandler();
				} catch (ConfigurationException ce) {
					throw new ParamsException(
							"ASJ.dpl_dc.003141 The Configuration exception occurred while "
									+ "getting a ConfigurationHandler.", ce);
				}
			}
		}

		return this.configurationHandler;
	}

	private CustomParameterMappings getCustomParameterMappings(
			ConfigurationHandler cfgHandler) throws ParamsException {
		try {
			return cfgHandler.getCustomParameterMappings();
		} catch (ConfigurationException ce) {
			throw new ParamsException(
					"ASJ.dpl_dc.003142 The CustomParameterMappings could not be get.",
					ce);
		}
	}

	private void commit(ConfigurationHandler handler) throws ParamsException {
		if (handler != null) {
			try {
				handler.commit();
			} catch (ConfigurationException ce) {
				throw new ParamsException(
						"ASJ.dpl_dc.003143 The Configuration exception occurred while "
								+ "committing the ConfigurationHandler.", ce);
			}
		}
	}

	private void rollback(ConfigurationHandler handler) throws ParamsException {
		if (handler != null) {
			try {
				handler.rollback();
			} catch (ConfigurationException ce) {
				throw new ParamsException(
						"ASJ.dpl_dc.003144 The Configuration exception occurred while "
								+ "rolling back the ConfigurationHandler.", ce);
			}
		}
	}

	private void releaseCustomParameterMappings(ConfigurationHandler handler)
			throws ParamsException {
		if (handler != null) {
			try {
				try {
					handler.releaseCustomParameterMappings();
				} catch (ConfigurationException ce) {
					throw new ParamsException(
							"ASJ.dpl_dc.003145 The Configuration exception occurred while "
									+ "releasing the CustomParameterMappings.",
							ce);
				}
			} finally {
				try {
					handler.closeAllConfigurations();
				} catch (ConfigurationException ce) {
					throw new ParamsException(
							"ASJ.dpl_dc.003146 The Configuration exception occurred while "
									+ "closing all configurations after "
									+ "releasing the CustomParameterMappings.",
							ce);
				}
			}
		}
	}

	// private void close(ConfigurationHandler handler, Configuration cfg)
	// throws ParamsException {
	// if ( handler != null && cfg != null ) {
	// handler.closeConfiguration(cfg);
	// }
	// }
	//  
	//

}
