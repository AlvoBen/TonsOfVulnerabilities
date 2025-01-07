package com.sap.security.api.util;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Title:        User Management 60
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:      SAP Portals
 * @author d037363
 * @version 1.0
 */

public interface IUMParameters {

	public static final String LOGIN_AUTHSCHEMES_DEFAULT = "login.authschemes.default";
	public static final String LOGIN_AUTHSCHEMES_DEFINITION_FILE = "login.authschemes.definition.file";
	public static final String LOGIN_TICKET__KEYALIAS = "login.ticket_keyalias";
	public static final String LOGIN_TICKET__KEYSTORE = "login.ticket_keystore";
	public static final String LOGIN_TICKET__LIFETIME = "login.ticket_lifetime";
	public static final String UME_ACL_VALIDATE__CACHED__ACLS = "ume.acl.validate_cached_acls";
	public static final String UME_ADMIN_ACCOUNT__PRIVACY = "ume.admin.account_privacy";
	public static final String UME_ADMIN_ADDATTRS = "ume.admin.addattrs";
	public static final String UME_ADMIN_AUTO__PASSWORD = "ume.admin.auto_password";
	public static final String UME_ADMIN_DEBUG__INTERNAL = "ume.admin.debug_internal";
	public static final String UME_ADMIN_EXPORT_PWDHASH = "ume.admin.export.pwdhash";
	public static final String UME_ADMIN_NOCACHE = "ume.admin.nocache";
	public static final String UME_ADMIN_ORGUNIT_ADAPTERID = "ume.admin.orgunit.adapterid";
	public static final String UME_ADMIN_PHONE__CHECK = "ume.admin.phone_check";
	public static final String UME_ADMIN_SEARCH__MAXHITS__WARNINGLEVEL = "ume.admin.search_maxhits_warninglevel";
	public static final String UME_ADMIN_SEARCH__MAXHITS = "ume.admin.search_maxhits";
	public static final String UME_ADMIN_SELFREG__COMPANY = "ume.admin.selfreg_company";
	public static final String UME_ADMIN_SELF_ADDATTRS = "ume.admin.self.addattrs";
	public static final String UME_ADMIN_PUBLIC_ADDATTRS = "ume.admin.public.addattrs";
	public static final String UME_ADMIN_SELFREG__GUEST = "ume.admin.selfreg_guest";
	public static final String UME_ADMIN_SELFREG__SUS_ADAPTERID = "ume.admin.selfreg_sus.adapterid";
	public static final String UME_ADMIN_SELFREG__SUS_ADMINROLE = "ume.admin.selfreg_sus.adminrole";
	public static final String UME_ADMIN_SELFREG__SUS_DELETECALL = "ume.admin.selfreg_sus.deletecall";
	public static final String UME_ADMIN_SELFREG__SUS = "ume.admin.selfreg_sus";
	public static final String UME_ADMIN_WD_TENANT_IDENTIFIER_ALL = "ume.admin.wd.tenant.identifier.all";
	public static final String UME_ADMIN_WD_TENANT_IDENTIFIER_NONE = "ume.admin.wd.tenant.identifier.none";
	public static final String UME_ADMIN_WD_COMPONENTS_UMEADMINAPP = "ume.admin.wd.components.umeadminapp";
	public static final String UME_ADMIN_WD_TABLE_SIZE_LARGE = "ume.admin.wd.table.size.large";
	public static final String UME_ADMIN_WD_TABLE_SIZE_MEDIUM = "ume.admin.wd.table.size.medium";
	public static final String UME_ADMIN_WD_TABLE_SIZE_SMALL = "ume.admin.wd.table.size.small";
	public static final String UME_ALLOW__NESTED__GROUPS = "ume.allow_nested_groups";
	public static final String UME_ALLOW__NESTED__ROLES = "ume.allow_nested_roles";
	public static final String UME_AUTHENTICATIONFACTORY = "ume.authenticationFactory";
	public static final String UME_CACHE_ACL_DEFAULT__CACHING__TIME = "ume.cache.acl.default_caching_time";
	public static final String UME_CACHE_ACL_INITIAL__CACHE__SIZE = "ume.cache.acl.initial_cache_size";
	public static final String UME_CACHE_ACL_PERMISSIONS_DEFAULT__CACHING__TIME = "ume.cache.acl.permissions.default_caching_time";
	public static final String UME_CACHE_ACL_PERMISSIONS_INITIAL__CACHE__SIZE = "ume.cache.acl.permissions.initial_cache_size";
	public static final String UME_CACHE_DEFAULT__CACHE = "ume.cache.default_cache";
	public static final String UME_CACHE_GROUP_DEFAULT__CACHING__TIME = "ume.cache.group.default_caching_time";
	public static final String UME_CACHE_GROUP_INITIAL__CACHE__SIZE = "ume.cache.group.initial_cache_size";
	public static final String UME_CACHE_NOTIFICATION__TIME = "ume.cache.notification_time";
	public static final String UME_CACHE_PRINCIPAL_DEFAULT__CACHING__TIME = "ume.cache.principal.default_caching_time";
	public static final String UME_CACHE_PRINCIPAL_INITIAL__CACHE__SIZE = "ume.cache.principal.initial_cache_size";
	public static final String UME_CACHE_ROLE_DEFAULT__CACHING__TIME = "ume.cache.role.default_caching_time";
	public static final String UME_CACHE_ROLE_INITIAL__CACHE__SIZE = "ume.cache.role.initial_cache_size";
	public static final String UME_CACHE_USER_DEFAULT__CACHING__TIME = "ume.cache.user.default_caching_time";
	public static final String UME_CACHE_USER_INITIAL__CACHE__SIZE = "ume.cache.user.initial_cache_size";
	public static final String UME_CACHE_USER__ACCOUNT_DEFAULT__CACHING__TIME = "ume.cache.user_account.default_caching_time";
	public static final String UME_CACHE_USER__ACCOUNT_INITIAL__CACHE__SIZE = "ume.cache.user_account.initial_cache_size";
	public static final String UME_COMPANY__GROUPS_DESCRIPTION__TEMPLATE = "ume.company_groups.description_template";
	public static final String UME_COMPANY__GROUPS_DISPLAYNAME__TEMPLATE = "ume.company_groups.displayname_template";
	public static final String UME_COMPANY__GROUPS_ENABLED = "ume.company_groups.enabled";
	public static final String UME_COMPANY__GROUPS_GUESTUSERCOMPANY__ENABLED = "ume.company_groups.guestusercompany_enabled";
	public static final String UME_COMPANY__GROUPS_GUESTUSERCOMPANY__NAME = "ume.company_groups.guestusercompany_name";
	public static final String UME_DB_CONNECTION__POOL_J2EE_IS__UNICODE = "ume.db.connection_pool.j2ee.is_unicode";
	public static final String UME_DB_CONNECTION__POOL_J2EE_XATRANSACTIONS__USED = "ume.db.connection_pool.j2ee.xatransactions_used";
	public static final String UME_DB_CONNECTION__POOL__TYPE = "ume.db.connection_pool_type";
	public static final String UME_DB_OR__SEARCH_MAX__ARGUMENTS = "ume.db.or_search.max_arguments";
	public static final String UME_DB_USE__DEFAULT__TRANSACTION__ISOLATION = "ume.db.use_default_transaction_isolation";
	public static final String UME_LDAP_ACCESS_ACTION__RETRIAL = "ume.ldap.access.action_retrial";
	public static final String UME_LDAP_ACCESS_ADDITIONAL__PASSWORD_1 = "ume.ldap.access.additional_password.1";
	public static final String UME_LDAP_ACCESS_ADDITIONAL__PASSWORD_2 = "ume.ldap.access.additional_password.2";
	public static final String UME_LDAP_ACCESS_ADDITIONAL__PASSWORD_3 = "ume.ldap.access.additional_password.3";
	public static final String UME_LDAP_ACCESS_ADDITIONAL__PASSWORD_4 = "ume.ldap.access.additional_password.4";
	public static final String UME_LDAP_ACCESS_ADDITIONAL__PASSWORD_5 = "ume.ldap.access.additional_password.5";
	public static final String UME_LDAP_ACCESS_AUXILIARY__NAMING__ATTRIBUTE_GRUP = "ume.ldap.access.auxiliary_naming_attribute.grup";
	public static final String UME_LDAP_ACCESS_AUXILIARY__NAMING__ATTRIBUTE_UACC = "ume.ldap.access.auxiliary_naming_attribute.uacc";
	public static final String UME_LDAP_ACCESS_AUXILIARY__NAMING__ATTRIBUTE_USER = "ume.ldap.access.auxiliary_naming_attribute.user";
	public static final String UME_LDAP_ACCESS_AUXILIARY__OBJECTCLASS_GRUP = "ume.ldap.access.auxiliary_objectclass.grup";
	public static final String UME_LDAP_ACCESS_AUXILIARY__OBJECTCLASS_UACC = "ume.ldap.access.auxiliary_objectclass.uacc";
	public static final String UME_LDAP_ACCESS_AUXILIARY__OBJECTCLASS_USER = "ume.ldap.access.auxiliary_objectclass.user";
	public static final String UME_LDAP_ACCESS_BASE__PATH_GRUP = "ume.ldap.access.base_path.grup";
	public static final String UME_LDAP_ACCESS_BASE__PATH_UACC = "ume.ldap.access.base_path.uacc";
	public static final String UME_LDAP_ACCESS_BASE__PATH_USER = "ume.ldap.access.base_path.user";
	public static final String UME_LDAP_ACCESS_CONTEXT__FACTORY = "ume.ldap.access.context_factory";
	public static final String UME_LDAP_ACCESS_CREATION__PATH_GRUP = "ume.ldap.access.creation_path.grup";
	public static final String UME_LDAP_ACCESS_CREATION__PATH_UACC = "ume.ldap.access.creation_path.uacc";
	public static final String UME_LDAP_ACCESS_CREATION__PATH_USER = "ume.ldap.access.creation_path.user";
	public static final String UME_LDAP_ACCESS_DYNAMIC__GROUP__ATTRIBUTE = "ume.ldap.access.dynamic_group_attribute";
	public static final String UME_LDAP_ACCESS_DYNAMIC__GROUPS = "ume.ldap.access.dynamic_groups";
	public static final String UME_LDAP_ACCESS_FLAT__GROUP__HIERACHY = "ume.ldap.access.flat_group_hierachy";
	public static final String UME_LDAP_ACCESS_MSADS_CONTROL__ATTRIBUTE = "ume.ldap.access.msads.control_attribute";
	public static final String UME_LDAP_ACCESS_MSADS_CONTROL__VALUE = "ume.ldap.access.msads.control_value";
	public static final String UME_LDAP_ACCESS_NAMING__ATTRIBUTE_GRUP = "ume.ldap.access.naming_attribute.grup";
	public static final String UME_LDAP_ACCESS_NAMING__ATTRIBUTE_UACC = "ume.ldap.access.naming_attribute.uacc";
	public static final String UME_LDAP_ACCESS_NAMING__ATTRIBUTE_USER = "ume.ldap.access.naming_attribute.user";
	public static final String UME_LDAP_ACCESS_OBJECTCLASS_GRUP = "ume.ldap.access.objectclass.grup";
	public static final String UME_LDAP_ACCESS_OBJECTCLASS_UACC = "ume.ldap.access.objectclass.uacc";
	public static final String UME_LDAP_ACCESS_OBJECTCLASS_USER = "ume.ldap.access.objectclass.user";
	public static final String UME_LDAP_ACCESS_PASSWORD = "ume.ldap.access.password";
	public static final String UME_LDAP_ACCESS_SERVER__NAME = "ume.ldap.access.server_name";
	public static final String UME_LDAP_ACCESS_SERVER__PORT = "ume.ldap.access.server_port";
	public static final String UME_LDAP_ACCESS_SERVER__TYPE = "ume.ldap.access.server_type";
	public static final String UME_LDAP_ACCESS_SIZE__LIMIT = "ume.ldap.access.size_limit";
	public static final String UME_LDAP_ACCESS_SSL__SOCKET__FACTORY = "ume.ldap.access.ssl_socket_factory";
	public static final String UME_LDAP_ACCESS_SSL = "ume.ldap.access.ssl";
	public static final String UME_LDAP_ACCESS_TIME__LIMIT = "ume.ldap.access.time_limit";
	public static final String UME_LDAP_ACCESS_USER__AS__ACCOUNT = "ume.ldap.access.user_as_account";
	public static final String UME_LDAP_ACCESS_USER = "ume.ldap.access.user";
	public static final String UME_LDAP_BLOCKED__ACCOUNTS = "ume.ldap.blocked_accounts";
	public static final String UME_LDAP_BLOCKED__GROUPS = "ume.ldap.blocked_groups";
	public static final String UME_LDAP_BLOCKED__USERS = "ume.ldap.blocked_users";
	public static final String UME_LDAP_CACHE__LIFETIME = "ume.ldap.cache_lifetime";
	public static final String UME_LDAP_CACHE__SIZE = "ume.ldap.cache_size";
	public static final String UME_LDAP_CONNECTION__POOL_MAX__CONNECTION__USAGE__TIME__CHECK__INTERVAL = "ume.ldap.connection_pool.max_connection_usage_time_check_interval";
	public static final String UME_LDAP_CONNECTION__POOL_MAX__IDLE__CONNECTIONS = "ume.ldap.connection_pool.max_idle_connections";
	public static final String UME_LDAP_CONNECTION__POOL_MAX__IDLE__TIME = "ume.ldap.connection_pool.max_idle_time";
	public static final String UME_LDAP_CONNECTION__POOL_MAX__SIZE = "ume.ldap.connection_pool.max_size";
	public static final String UME_LDAP_CONNECTION__POOL_MAX__WAIT__TIME = "ume.ldap.connection_pool.max_wait_time";
	public static final String UME_LDAP_CONNECTION__POOL_MIN__SIZE = "ume.ldap.connection_pool.min_size";
	public static final String UME_LDAP_CONNECTION__POOL_MONITOR__LEVEL = "ume.ldap.connection_pool.monitor_level";
	public static final String UME_LDAP_CONNECTION__POOL_RETRIAL__INTERVAL = "ume.ldap.connection_pool.retrial_interval";
	public static final String UME_LDAP_CONNECTION__POOL_RETRIAL = "ume.ldap.connection_pool.retrial";
	public static final String UME_LDAP_CONNECTION__POOL_CONNECT__TIMEOUT = "ume.ldap.connection_pool.connect_timeout";
	public static final String UME_LDAP_DEFAULT__GROUP__MEMBER_ENABLED = "ume.ldap.default_group_member.enabled";
	public static final String UME_LDAP_DEFAULT__GROUP__MEMBER = "ume.ldap.default_group_member";
	public static final String UME_LDAP_RECORD__ACCESS = "ume.ldap.record_access";
	public static final String UME_LDAP_UNIQUE__GRUP__ATTRIBUTE = "ume.ldap.unique_grup_attribute";
	public static final String UME_LDAP_UNIQUE__UACC__ATTRIBUTE = "ume.ldap.unique_uacc_attribute";
	public static final String UME_LDAP_UNIQUE__USER__ATTRIBUTE = "ume.ldap.unique_user_attribute";
	public static final String UME_LOCKING_ENABLED = "ume.locking.enabled";
	public static final String UME_LOCKING_MAX__WAIT__TIME = "ume.locking.max_wait_time";
	public static final String UME_LOGIN_ANONYMOUS__USER_MODE = "ume.login.anonymous_user.mode";
	public static final String UME_LOGIN_BASICAUTHENTICATION = "ume.login.basicauthentication";
	public static final String UME_LOGIN_CONTEXT_DEFAULT = "ume.login.context.default";
	public static final String UME_LOGIN_CONTEXT = "ume.login.context";
	public static final String UME_LOGIN_GUEST__USER_UNIQUEIDS = "ume.login.guest_user.uniqueids";
	public static final String UME_LOGIN_MDC_HOSTS = "ume.login.mdc.hosts";
	public static final String UME_LOGON_ALLOW__CERT = "ume.logon.allow_cert";
	public static final String UME_LOGON_BRANDING__IMAGE = "ume.logon.branding_image";
	public static final String UME_LOGON_BRANDING__TEXT = "ume.logon.branding_text";
	public static final String UME_LOGON_HTTPONLYCOOKIE = "ume.logon.httponlycookie";
	public static final String UME_LOGON_LOCALE = "ume.logon.locale";
	public static final String UME_LOGON_R3MASTER_ADAPTERID = "ume.logon.r3master.adapterid";
	public static final String UME_LOGON_SECURITY_ENFORCE__SECURE__COOKIE = "ume.logon.security.enforce_secure_cookie";
	public static final String UME_LOGON_SECURITY_RELAX__DOMAIN_LEVEL = "ume.logon.security.relax_domain.level";
	public static final String UME_LOGON_SECURITY__POLICY_AUTO__UNLOCK__TIME = "ume.logon.security_policy.auto_unlock_time";
	public static final String UME_LOGON_SECURITY__POLICY_ENFORCE__POLICY__AT__LOGON = "ume.logon.security_policy.enforce_policy_at_logon";
	public static final String UME_LOGON_SECURITY__POLICY_CERT__LOGON__REQUIRED = "ume.logon.security_policy.cert_logon_required";
	public static final String UME_LOGON_SECURITY__POLICY_LOCK__AFTER__INVALID__ATTEMPTS = "ume.logon.security_policy.lock_after_invalid_attempts";
	public static final String UME_LOGON_SECURITY__POLICY_LOG__CLIENT__HOSTADDRESS = "ume.logon.security_policy.log_client_hostaddress";
	public static final String UME_LOGON_SECURITY__POLICY_LOG__CLIENT__HOSTNAME = "ume.logon.security_policy.log_client_hostname";
	public static final String UME_LOGON_SECURITY__POLICY_OLDPASS__IN__NEWPASS__ALLOWED = "ume.logon.security_policy.oldpass_in_newpass_allowed";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__ALPHA__NUMERIC__REQUIRED = "ume.logon.security_policy.password_alpha_numeric_required";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__CHANGE__ALLOWED = "ume.logon.security_policy.password_change_allowed";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__EXPIRE__DAYS = "ume.logon.security_policy.password_expire_days";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__HISTORY = "ume.logon.security_policy.password_history";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__IMPERMISSIBLE = "ume.logon.security_policy.password_impermissible";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__LAST__CHANGE__DATE = "ume.logon.security_policy.password_last_change_date_default";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__SUCCESSFUL__CHECK__DATE = "ume.logon.security_policy.password_successful_check_date_default";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__MAX__LENGTH = "ume.logon.security_policy.password_max_length";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__MAX__IDLE__TIME = "ume.logon.security_policy.password_max_idle_time";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__MIN__LENGTH = "ume.logon.security_policy.password_min_length";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__MIX__CASE__REQUIRED = "ume.logon.security_policy.password_mix_case_required";
	public static final String UME_LOGON_SECURITY__POLICY_PASSWORD__SPECIAL__CHAR__REQUIRED = "ume.logon.security_policy.password_special_char_required";
	public static final String UME_LOGON_SECURITY__POLICY_USERID__DIGITS = "ume.logon.security_policy.userid_digits";
	public static final String UME_LOGON_SECURITY__POLICY_USERID__IN__PASSWORD__ALLOWED = "ume.logon.security_policy.userid_in_password_allowed";
	public static final String UME_LOGON_SECURITY__POLICY_USERID__LOWERCASE = "ume.logon.security_policy.userid_lowercase";
	public static final String UME_LOGON_SECURITY__POLICY_USERID__SPECIAL__CHAR__REQUIRED = "ume.logon.security_policy.userid_special_char_required";
	public static final String UME_LOGON_SECURITY__POLICY_USERIDMAXLENGTH = "ume.logon.security_policy.useridmaxlength";
	public static final String UME_LOGON_SECURITY__POLICY_USERIDMINLENGTH = "ume.logon.security_policy.useridminlength";
	public static final String UME_LOGON_SELFREG = "ume.logon.selfreg";
	public static final String UME_LOGONAUTHENTICATIONFACTORY = "ume.logonAuthenticationFactory";
	public static final String UME_NOTIFICATION_ADMIN__EMAIL = "ume.notification.admin_email";
	public static final String UME_NOTIFICATION_CREATE__APPROVAL = "ume.notification.create_approval";
	public static final String UME_NOTIFICATION_CREATE__BY__BATCH__PERFORMED = "ume.notification.create_by_batch_performed";
	public static final String UME_NOTIFICATION_CREATE__DENIED = "ume.notification.create_denied";
	public static final String UME_NOTIFICATION_CREATE__PERFORMED = "ume.notification.create_performed";
	public static final String UME_NOTIFICATION_CREATE__REQUEST = "ume.notification.create_request";
	public static final String UME_NOTIFICATION_DELETE__PERFORMED = "ume.notification.delete_performed";
	public static final String UME_NOTIFICATION_EMAIL__ASYNCH = "ume.notification.email_asynch";
	public static final String UME_NOTIFICATION_LOCK__PERFORMED = "ume.notification.lock_performed";
	public static final String UME_NOTIFICATION_MAIL__HOST = "ume.notification.mail_host";
	public static final String UME_NOTIFICATION_PSWD__RESET__PERFORMED = "ume.notification.pswd_reset_performed";
	public static final String UME_NOTIFICATION_PSWD__RESET__REQUEST = "ume.notification.pswd_reset_request";
	public static final String UME_NOTIFICATION_SELFREG__PERFORMED = "ume.notification.selfreg_performed";
	public static final String UME_NOTIFICATION_SYSTEM__EMAIL = "ume.notification.system_email";
	public static final String UME_NOTIFICATION_UNLOCK__PERFORMED = "ume.notification.unlock_performed";
	public static final String UME_NOTIFICATION_UNLOCK__REQUEST = "ume.notification.unlock_request";
	public static final String UME_NOTIFICATION_UPDATE__BY__BATCH__PERFORMED = "ume.notification.update_by_batch_performed";
	public static final String UME_NOTIFICATION_WORKFLOW__EMAIL = "ume.notification.workflow_email";
	public static final String UME_PERSISTENCE_BATCH_PAGE__SIZE = "ume.persistence.batch.page_size";
	public static final String UME_PERSISTENCE_DATA__SOURCE__CONFIGURATION = "ume.persistence.data_source_configuration";
	public static final String UME_PERSISTENCE_PCD__ROLES__DATA__SOURCE__CONFIGURATION = "ume.persistence.pcd_roles_data_source_configuration";
	public static final String UME_PERSISTENCE_UME__ROLES__DATA__SOURCE__CONFIGURATION = "ume.persistence.ume_roles_data_source_configuration";
	public static final String UME_PRINCIPAL_CACHE__GROUP__HIERARCHY = "ume.principal.cache_group_hierarchy";
	public static final String UME_PRINCIPAL_CACHE__INDIRECT__PARENTS = "ume.principal.cache_indirect_parents";
	public static final String UME_PRINCIPAL_CACHE__ROLE__HIERARCHY = "ume.principal.cache_role_hierarchy";
	public static final String UME_R3_CONNECTION_001_ASHOST = "ume.r3.connection.001.ashost";
	public static final String UME_R3_CONNECTION_001_CLIENT = "ume.r3.connection.001.client";
	public static final String UME_R3_CONNECTION_001_GROUP = "ume.r3.connection.001.group";
	public static final String UME_R3_CONNECTION_001_GWHOST = "ume.r3.connection.001.gwhost";
	public static final String UME_R3_CONNECTION_001_GWSERV = "ume.r3.connection.001.gwserv";
	public static final String UME_R3_CONNECTION_001_LANG = "ume.r3.connection.001.lang";
	public static final String UME_R3_CONNECTION_001_MSGHOST = "ume.r3.connection.001.msghost";
	public static final String UME_R3_CONNECTION_001_PASSWD = "ume.r3.connection.001.passwd";
	public static final String UME_R3_CONNECTION_001_POOLMAXSIZE = "ume.r3.connection.001.poolmaxsize";
	public static final String UME_R3_CONNECTION_001_POOLMAXWAIT = "ume.r3.connection.001.poolmaxwait";
	public static final String UME_R3_CONNECTION_001_R3NAME = "ume.r3.connection.001.r3name";
	public static final String UME_R3_CONNECTION_001_RECEIVERID__GUEST = "ume.r3.connection.001.receiverid_guest";
	public static final String UME_R3_CONNECTION_001_RECEIVERID = "ume.r3.connection.001.receiverid";
	public static final String UME_R3_CONNECTION_001_SNC__LIB = "ume.r3.connection.001.snc_lib";
	public static final String UME_R3_CONNECTION_001_SNC__MODE = "ume.r3.connection.001.snc_mode";
	public static final String UME_R3_CONNECTION_001_SNC__MYNAME = "ume.r3.connection.001.snc_myname";
	public static final String UME_R3_CONNECTION_001_SNC__PARTNERNAME = "ume.r3.connection.001.snc_partnername";
	public static final String UME_R3_CONNECTION_001_SNC__QOP = "ume.r3.connection.001.snc_qop";
	public static final String UME_R3_CONNECTION_001_SYSNR = "ume.r3.connection.001.sysnr";
	public static final String UME_R3_CONNECTION_001_USER = "ume.r3.connection.001.user";
	public static final String UME_R3_CONNECTION_001_USEROLE = "ume.r3.connection.001.userole";
	public static final String UME_R3_CONNECTION_002_ASHOST = "ume.r3.connection.002.ashost";
	public static final String UME_R3_CONNECTION_002_CLIENT = "ume.r3.connection.002.client";
	public static final String UME_R3_CONNECTION_002_GROUP = "ume.r3.connection.002.group";
	public static final String UME_R3_CONNECTION_002_GWHOST = "ume.r3.connection.002.gwhost";
	public static final String UME_R3_CONNECTION_002_GWSERV = "ume.r3.connection.002.gwserv";
	public static final String UME_R3_CONNECTION_002_LANG = "ume.r3.connection.002.lang";
	public static final String UME_R3_CONNECTION_002_MSGHOST = "ume.r3.connection.002.msghost";
	public static final String UME_R3_CONNECTION_002_PASSWD = "ume.r3.connection.002.passwd";
	public static final String UME_R3_CONNECTION_002_POOLMAXSIZE = "ume.r3.connection.002.poolmaxsize";
	public static final String UME_R3_CONNECTION_002_POOLMAXWAIT = "ume.r3.connection.002.poolmaxwait";
	public static final String UME_R3_CONNECTION_002_R3NAME = "ume.r3.connection.002.r3name";
	public static final String UME_R3_CONNECTION_002_RECEIVERID__GUEST = "ume.r3.connection.002.receiverid_guest";
	public static final String UME_R3_CONNECTION_002_RECEIVERID = "ume.r3.connection.002.receiverid";
	public static final String UME_R3_CONNECTION_002_SNC__LIB = "ume.r3.connection.002.snc_lib";
	public static final String UME_R3_CONNECTION_002_SNC__MODE = "ume.r3.connection.002.snc_mode";
	public static final String UME_R3_CONNECTION_002_SNC__MYNAME = "ume.r3.connection.002.snc_myname";
	public static final String UME_R3_CONNECTION_002_SNC__PARTNERNAME = "ume.r3.connection.002.snc_partnername";
	public static final String UME_R3_CONNECTION_002_SNC__QOP = "ume.r3.connection.002.snc_qop";
	public static final String UME_R3_CONNECTION_002_SYSNR = "ume.r3.connection.002.sysnr";
	public static final String UME_R3_CONNECTION_002_USER = "ume.r3.connection.002.user";
	public static final String UME_R3_CONNECTION_002_USEROLE = "ume.r3.connection.002.userole";
	public static final String UME_R3_CONNECTION_003_ASHOST = "ume.r3.connection.003.ashost";
	public static final String UME_R3_CONNECTION_003_CLIENT = "ume.r3.connection.003.client";
	public static final String UME_R3_CONNECTION_003_GROUP = "ume.r3.connection.003.group";
	public static final String UME_R3_CONNECTION_003_GWHOST = "ume.r3.connection.003.gwhost";
	public static final String UME_R3_CONNECTION_003_GWSERV = "ume.r3.connection.003.gwserv";
	public static final String UME_R3_CONNECTION_003_LANG = "ume.r3.connection.003.lang";
	public static final String UME_R3_CONNECTION_003_MSGHOST = "ume.r3.connection.003.msghost";
	public static final String UME_R3_CONNECTION_003_PASSWD = "ume.r3.connection.003.passwd";
	public static final String UME_R3_CONNECTION_003_POOLMAXSIZE = "ume.r3.connection.003.poolmaxsize";
	public static final String UME_R3_CONNECTION_003_POOLMAXWAIT = "ume.r3.connection.003.poolmaxwait";
	public static final String UME_R3_CONNECTION_003_R3NAME = "ume.r3.connection.003.r3name";
	public static final String UME_R3_CONNECTION_003_RECEIVERID__GUEST = "ume.r3.connection.003.receiverid_guest";
	public static final String UME_R3_CONNECTION_003_RECEIVERID = "ume.r3.connection.003.receiverid";
	public static final String UME_R3_CONNECTION_003_SNC__LIB = "ume.r3.connection.003.snc_lib";
	public static final String UME_R3_CONNECTION_003_SNC__MODE = "ume.r3.connection.003.snc_mode";
	public static final String UME_R3_CONNECTION_003_SNC__MYNAME = "ume.r3.connection.003.snc_myname";
	public static final String UME_R3_CONNECTION_003_SNC__PARTNERNAME = "ume.r3.connection.003.snc_partnername";
	public static final String UME_R3_CONNECTION_003_SNC__QOP = "ume.r3.connection.003.snc_qop";
	public static final String UME_R3_CONNECTION_003_SYSNR = "ume.r3.connection.003.sysnr";
	public static final String UME_R3_CONNECTION_003_USER = "ume.r3.connection.003.user";
	public static final String UME_R3_CONNECTION_003_USEROLE = "ume.r3.connection.003.userole";
	public static final String UME_R3_CONNECTION_MASTER_ABAP__DEBUG = "ume.r3.connection.master.abap_debug";
	public static final String UME_R3_CONNECTION_MASTER_ASHOST = "ume.r3.connection.master.ashost";
	public static final String UME_R3_CONNECTION_MASTER_CLIENT = "ume.r3.connection.master.client";
	public static final String UME_R3_CONNECTION_MASTER_GROUP = "ume.r3.connection.master.group";
	public static final String UME_R3_CONNECTION_MASTER_GWHOST = "ume.r3.connection.master.gwhost";
	public static final String UME_R3_CONNECTION_MASTER_GWSERV = "ume.r3.connection.master.gwserv";
	public static final String UME_R3_CONNECTION_MASTER_LANG = "ume.r3.connection.master.lang";
	public static final String UME_R3_CONNECTION_MASTER_MSGHOST = "ume.r3.connection.master.msghost";
	public static final String UME_R3_CONNECTION_MASTER_MSSERV = "ume.r3.connection.master.msserv";
	public static final String UME_R3_CONNECTION_MASTER_PASSWD = "ume.r3.connection.master.passwd";
	public static final String UME_R3_CONNECTION_MASTER_POOLMAXSIZE = "ume.r3.connection.master.poolmaxsize";
	public static final String UME_R3_CONNECTION_MASTER_POOLMAXWAIT = "ume.r3.connection.master.poolmaxwait";
	public static final String UME_R3_CONNECTION_MASTER_R3NAME = "ume.r3.connection.master.r3name";
	public static final String UME_R3_CONNECTION_MASTER_RECEIVERID__GUEST = "ume.r3.connection.master.receiverid_guest";
	public static final String UME_R3_CONNECTION_MASTER_RECEIVERID = "ume.r3.connection.master.receiverid";
	public static final String UME_R3_CONNECTION_MASTER_SNC__LIB = "ume.r3.connection.master.snc_lib";
	public static final String UME_R3_CONNECTION_MASTER_SNC__MODE = "ume.r3.connection.master.snc_mode";
	public static final String UME_R3_CONNECTION_MASTER_SNC__MYNAME = "ume.r3.connection.master.snc_myname";
	public static final String UME_R3_CONNECTION_MASTER_SNC__PARTNERNAME = "ume.r3.connection.master.snc_partnername";
	public static final String UME_R3_CONNECTION_MASTER_SNC__QOP = "ume.r3.connection.master.snc_qop";
	public static final String UME_R3_CONNECTION_MASTER_SYSNR = "ume.r3.connection.master.sysnr";
	public static final String UME_R3_CONNECTION_MASTER_TRACE = "ume.r3.connection.master.trace";
	public static final String UME_R3_CONNECTION_MASTER_USER = "ume.r3.connection.master.user";
	public static final String UME_R3_CONNECTION_TPD_ADAPTERID = "ume.r3.connection.tpd.adapterid";
	public static final String UME_R3_CONNECTION_TPD_SYSTEMID = "ume.r3.connection.tpd.systemid";
	public static final String UME_R3_MASTERSYSTEM = "ume.r3.mastersystem";
	public static final String UME_R3_SYNC_SENDER = "ume.r3.sync.sender";
	public static final String UME_R3_USE_ROLE = "ume.r3.use.role";
	public static final String UME_REPLICATION_ADAPTERS_001_COMPANIES = "ume.replication.adapters.001.companies";
	public static final String UME_REPLICATION_ADAPTERS_001_SCOPE = "ume.replication.adapters.001.scope";
	public static final String UME_REPLICATION_ADAPTERS_002_COMPANIES = "ume.replication.adapters.002.companies";
	public static final String UME_REPLICATION_ADAPTERS_002_SCOPE = "ume.replication.adapters.002.scope";
	public static final String UME_REPLICATION_ADAPTERS_003_COMPANIES = "ume.replication.adapters.003.companies";
	public static final String UME_REPLICATION_ADAPTERS_003_SCOPE = "ume.replication.adapters.003.scope";
	public static final String UME_REPLICATION_ADAPTERS_INDEX__1 = "ume.replication.adapters.index_1";
	public static final String UME_REPLICATION_ADAPTERS_INDEX__2 = "ume.replication.adapters.index_2";
	public static final String UME_REPLICATION_ADAPTERS_INDEX__3 = "ume.replication.adapters.index_3";
	public static final String UME_REPLICATION_ADAPTERS_MASTER_COMPANIES = "ume.replication.adapters.master.companies";
	public static final String UME_REPLICATION_ADAPTERS_MASTER_SCOPE = "ume.replication.adapters.master.scope";
	public static final String UME_REPLICATION_MESSAGING_ACTIVE = "ume.replication.messaging.active";
	public static final String UME_REPLICATION_SYNC_DISPLAY__ALL__DOC = "ume.replication.sync.display_all_doc";
	public static final String UME_ROLES_XML__FILES = "ume.roles.xml_files";
	public static final String UME_SECAUDIT_GET__OBJECT__NAME = "ume.secaudit.get_object_name";
	public static final String UME_SECAUDIT_LOG__ACTOR = "ume.secaudit.log_actor";
	public static final String UME_SUPERADMIN_ACTIVATED = "ume.superadmin.activated";
	public static final String UME_SUPERADMIN_PASSWORD = "ume.superadmin.password";
	public static final String UME_SUPERGROUPS_ANONYMOUS__GROUP_DESCRIPTION = "ume.supergroups.anonymous_group.description";
	public static final String UME_SUPERGROUPS_ANONYMOUS__GROUP_DISPLAYNAME = "ume.supergroups.anonymous_group.displayname";
	public static final String UME_SUPERGROUPS_ANONYMOUS__GROUP_UNIQUENAME = "ume.supergroups.anonymous_group.uniquename";
	public static final String UME_SUPERGROUPS_AUTHENTICATED__GROUP_DESCRIPTION = "ume.supergroups.authenticated_group.description";
	public static final String UME_SUPERGROUPS_AUTHENTICATED__GROUP_DISPLAYNAME = "ume.supergroups.authenticated_group.displayname";
	public static final String UME_SUPERGROUPS_AUTHENTICATED__GROUP_UNIQUENAME = "ume.supergroups.authenticated_group.uniquename";
	public static final String UME_SUPERGROUPS_EVERYONE_DESCRIPTION = "ume.supergroups.everyone.description";
	public static final String UME_SUPERGROUPS_EVERYONE_DISPLAYNAME = "ume.supergroups.everyone.displayname";
	public static final String UME_SUPERGROUPS_EVERYONE_UNIQUENAME = "ume.supergroups.everyone.uniquename";
	public static final String UME_TESTUM = "ume.testum";
	public static final String UME_TPD_CLASSLOADER = "ume.tpd.classloader";
	public static final String UME_TPD_COMPANIES = "ume.tpd.companies";
	public static final String UME_TPD_IMP_CLASS = "ume.tpd.imp.class";
	public static final String UME_TPD_PREFIX = "ume.tpd.prefix";
	public static final String UME_TRACE_DEBUG__EXCEPTION__LEVEL = "ume.trace.debug_exception_level";
	public static final String UME_TRACE_EXTERNAL__TRACE__CLASS = "ume.trace.external_trace_class";
	public static final String UME_USERMAPPING_UNSECURE = "ume.usermapping.unsecure";
    public static final String UME_USERMAPPING_ADMIN_PWD_PROTECTION = "ume.usermapping.admin.pwdprotection";
    public static final String UME_USERMAPPING_REFSYS_MAPPING_TYPE = "ume.usermapping.refsys.mapping.type";
    public static final String UME_USERMAPPING_REFSYS_MAPPING_TYPE_INTERNAL = "internal";
    public static final String UME_USERMAPPING_REFSYS_MAPPING_TYPE_ATTRIBUTE = "attribute";
	public static final String UME_USERS_DISPLAYNAME__TEMPLATE = "ume.users.displayname_template";

    /**
     * returns the String value of a property
     *
     * @param  property the property name
     *
     * @return  the string value of that property
     */
    public String get( String property );

    /**
     * returns the String value of a password property.
     * If the value starts with {base64} (e.g. {base64}abcdef==), the base64 decoded
     * value (UTF8-String) will be returned.
     * If the value starts with {encrypted}, the decrypted value will be returned.
     * Clear text passwords have no prefix or {text}.
     * @param  property the property name
     * @return throws always an <code>java.lang.UnsupportedOperationException</code>
     * @deprecated
     */
    public String getPassword( String property );

    /**
     *  same as {@link #getPassword(String)} except that you already have a String
     *  and not a property name.
     *  @param passwd password that should be decrypted
     *  @return throws always an <code>java.lang.UnsupportedOperationException</code>
     *  @deprecated
     */
    public String getPasswordDecode( String passwd );

    /**
     *  returns the String passwd encoded and with prefix as required for a property
     *  parameter
     *  @param passwd that should be encrypted
     *  @return throws always an <code>java.lang.UnsupportedOperationException</code>
     *  @deprecated
     */
    public String returnPasswordEncode(String passwd);


    /**
     * returns the String value of a property if this property exists, otherwise
     * the default value
     *
     * @param property the property name
     * @param dflt the default value
     *
     * @return the string value of that property
     */
    public String get( String property, String dflt );

    /**
     * returns the String value of a property if this property exists, otherwise
     * the default value
     *
     * @param property the property name
     * @param dflt the default value
     *
     * @return the string value of that property
     */
    public String getDynamic( String property, String dflt );
    

    /**
     * returns the a number as a value for a property or thedeafutl, if the
     * property does not exist
     *
     * @param property the property name
     * @param dflt the default value
     *
     * @return a number as a property value
     */
    public int getNumber( String property, int dflt );


    /**
     * returns a boolean value as the value of a property or the default value,
     * if the property does not exist
     *
     * @param property the property name
     * @param dflt the default value
     *
     * @return a boolean value as value for the property
     */
    public boolean getBoolean( String property, boolean dflt );

    /**
     * returns a boolean value as the value of a property or false, if the property
     * does not exist
     *
     * @param property the property name
     *
     * @return a boolean value as value for the property
     */
    public boolean getBoolean( String property);

    /**
     * returns the name of a file as value of a property. If the property
     * does not exist the default value is used for
     * the lookup of the file. The plain filename without path is returned.
     * Does not check for the existence of the file.
     *
     * @param property the property name
     * @param dflt the default value
     *
     * @return file name with complete path, if path found otherwise plain filename.
     */
    public String getFile( String property, String dflt );

    /**
     * returns all properties
     *
     * @return all properties
     */
    public Properties getProperties();


    /**
     * this method initialites the <code>UMParameters</code> object with properties and a fileset
     * @param prop Property object that contains all properties
     * (e.g. from <code>sapum.properties</code>). Must not be <code>null</code>.
     * @param files This is a {@link java.util.HashMap} containing
     * <code>name/byte[]</code> pairs of files which can be
     * read with {@link #getInputStream(String)}. Can be empty should not be <code>null</code>.
     */
    public void init(Properties prop, Map files);

//    public void addFile();


    /**
     * return stream for requested file. Caller must close stream after usage.
     * @param filename name for requested file (without path)
     * @return InputStream of file, or <code>null</code> if not found
     */
    public InputStream getInputStream(String filename);


    /**
     * Same as {@link #getInputStream(String)}, but no trace is written.
     * This should be called only during initialization, if the
     * <code>InternalUMFactory</code> is not yet initialized.
     * @param filename name for requested file (without path)
     * @return InputStream of file, or <code>null</code> if not found
     */
    public InputStream getInputStream_noTrace(String filename);

    /**
     * add a file, so that {@link #getInputStream(String)} can find it later
     * @param name filename (without path)
     * @param data <code>byte[]</code> of file data
     */
    public void addFile(String name, byte[] data);

    /**
     * list all known files which can be accessed by {@link #getInputStream(String)}
     * @return string array of all filenames
     */
    public String[] listFiles();

    /**
     * change an existing property (there is a limited set of properties which can be changed!)
     * @param name property name
     * @param value new property value
     */
    public void changeProperty(String name, Object value);
}
