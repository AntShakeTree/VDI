package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.facade.CommonException.CONFLICT;
import static com.opzoon.vdi.core.facade.CommonException.LDAP_ABNORMAL;
import static com.opzoon.vdi.core.facade.CommonException.LDAP_UNAUTHORIZED;
import static com.opzoon.vdi.core.facade.CommonException.NOT_FOUND;
import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.naming.AuthenticationException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.PartialResultException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import sun.misc.BASE64Encoder;

import com.opzoon.vdi.core.domain.DesktopPoolEntity;
import com.opzoon.vdi.core.domain.Domain;
import com.opzoon.vdi.core.domain.Group;
import com.opzoon.vdi.core.domain.LDAPConfig;
import com.opzoon.vdi.core.domain.LDAPConfigStatus;
import com.opzoon.vdi.core.domain.Organization;
import com.opzoon.vdi.core.domain.User;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.transience.SessionFacade;
import com.opzoon.vdi.core.operation.DeleteDesktopPoolOperation;
import com.opzoon.vdi.core.operations.OperationRegistry;
import com.opzoon.vdi.core.util.LDAPUtils;

/**
 * 域相关 业务接口.
 */
public class DomainFacade implements ApplicationListener<ContextRefreshedEvent> {
	
	private static final Logger log = LoggerFactory.getLogger(DomainFacade.class);
	
	private final ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1);
	
	private DatabaseFacade databaseFacade;
  
  private OrganizationFacade organizationFacade;
  
  private GroupFacade groupFacade;
	
	private UserFacade userFacade;
	
	private SessionFacade sessionFacade;

  private OperationRegistry operationRegistry;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				synchronizeLDAPDomains();
			}
			@Override
			public String toString() {
				return super.toString() + "THREAD synchronizeLDAPDomains";
			}
		};
		com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
	}
	
	public void synchronizeLDAPDomains() {
		databaseFacade.update(
				"update Domain set status = ?, ownerthread = null",
				Domain.DOMAIN_STATUS_NORMAL);
		@SuppressWarnings("unchecked")
		List<Domain> ldapDomains = (List<Domain>) databaseFacade.find(
				"from Domain where iddomain != ?",
				Domain.DEFAULT_DOMAIN_ID);
		for (Domain ldapDomain : ldapDomains) {
			@SuppressWarnings("unchecked")
			List<LDAPConfig> ldapConfigs = (List<LDAPConfig>) databaseFacade.find(
					"from LDAPConfig where domainid = ?",
					ldapDomain.getIddomain());
			for (LDAPConfig ldapConfig : ldapConfigs) {
				scheduleNextSyn(ldapDomain.getIddomain(), ldapConfig.getIdldapconfig(), ldapConfig.getSyncinterval());
			}
		}
	}

	/**
	 * 查询域.
	 * 
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<Domain> findDomains() {
		return (List<Domain>) databaseFacade.find("from Domain");
	}

	public List<LDAPConfig> findLDAPConfigs(int domainid,
			String domainsearchbase, int idldapconfig, int syncinterval,
			PagingInfo pagingInfo, int[] amountContainer) {
		StringBuilder selectClause = new StringBuilder("select c from LDAPConfig c");
		StringBuilder whereClause = new StringBuilder(" where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (domainid > -1) {
			whereClause.append(" and c.domainid = ?");
			params.add(domainid);
		}
		if (domainsearchbase != null) {
			whereClause.append(" and c.domainsearchbase like ?");
			params.add("%" + domainsearchbase + "%");
		}
		if (idldapconfig > -1) {
			whereClause.append(" and c.idldapconfig = ?");
			params.add(idldapconfig);
		}
		if (syncinterval > -1) {
			whereClause.append(" and c.syncinterval = ?");
			params.add(syncinterval);
		}
		selectClause.append(whereClause);
		selectClause.append(FacadeHelper.keyword("c", pagingInfo, params));
		Object[] paramsArray = params.toArray();
		count(databaseFacade, "idldapconfig", selectClause, paramsArray, amountContainer);
		@SuppressWarnings("unchecked")
		List<LDAPConfig> ldapConfigs = pagingFind(databaseFacade, selectClause, paramsArray, pagingInfo);
		for (LDAPConfig ldapConfig : ldapConfigs) {
			ldapConfig.setDomain(databaseFacade.load(Domain.class, ldapConfig.getDomainid()));
		}
		return ldapConfigs;
	}
	
	public void updateLDAPConfigSynchronizingInterval(
			String searchbase,
			int syncinterval) throws CommonException {
		String domainName = this.findDomainName(searchbase);
		Domain domain = (Domain) databaseFacade.findFirst(
				"from Domain where domainname = ?",
				domainName);
		if (domain == null) {
			throw new CommonException(NOT_FOUND);
		}
		databaseFacade.update(
				"update LDAPConfig set syncinterval = ? where domainid = ? and domainsearchbase = ?",
				syncinterval, domain.getIddomain(), searchbase);
	}

	public void verifyLDAPConfig(
			String domainservername,
			int domainserverport,
			String searchbase,
			String binddn,
			String bindpass) throws CommonException {
		String domainName = this.findDomainName(searchbase, domainservername, domainserverport, binddn, bindpass);
		if (!searchbase.toLowerCase().endsWith("dc=" + domainName.replace(".", ",dc="))) {
			log.warn("searchbase unmatched.");
			throw new CommonException(LDAP_ABNORMAL);
		}
		DirContext ctx = this.createDirContext(domainservername, domainserverport, binddn, bindpass);
		try {
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(objectCategory=*)";
			NamingEnumeration<SearchResult> answer = ctx.search(searchbase, filter, ctls);
			try {
				if (answer.hasMore()) {
					answer.next();
				}
			} catch (PartialResultException e) {}
		} catch (Exception e) {
			log.warn("Error on reading LDAP.", e);
			throw new CommonException(CommonException.LDAP_OU_NOT_FOUND);
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				log.warn("Error on closing LDAP.", e);
			}
		}
	}

	public void persistDomain(String domainservername, int domainserverport, String searchbase,
			String binddn, String bindpass) throws CommonException {
		String domainName = this.findDomainName(searchbase);
		final String guid = this.findGUID(
		    domainservername,
		    domainserverport,
		    binddn,
		    bindpass,
		    "dc=" + domainName.replace(".", ",dc="));
		log.debug("Domain {} creating.", domainName);
		Domain domain = new Domain();
		domain.setGuid(guid);
		domain.setDomainbinddn(binddn);
		domain.setDomainbindpass(bindpass);
		domain.setDomainname(domainName);
		domain.setDomainnetworkname(domainName);
		domain.setDomainservername(domainservername);
		domain.setDomainserverport(domainserverport);
		// FIXME
		domain.setDomaintype(Domain.DOMAIN_TYPE_MSAD);
		domain.setNotes("");
		domain.setStatus(Domain.DOMAIN_STATUS_MAINTAINING);
		domain.setOwnerthread((int) Thread.currentThread().getId());
		try {
			databaseFacade.persist(domain);
		} catch (PersistenceException e) {// TODO Return immediately after all PersistenceExceptions of the project.
			log.warn("Domain exists: {}", domainName);
			throw new CommonException(CONFLICT);
		}
	}

  public void fetchDomainLock(String searchbase) {
		String domainName = this.findDomainName(searchbase);
		databaseFacade.update(
				"update Domain set status = ?, ownerthread = ? where domainname = ? and (ownerthread = ? or ownerthread = ?)",
				Domain.DOMAIN_STATUS_MAINTAINING, (int) Thread.currentThread().getId(), domainName, null, (int) Thread.currentThread().getId());
	}

	public boolean checkDomainLock(String searchbase) {
		String domainName = this.findDomainName(searchbase);
		return exists(databaseFacade.findFirst(
				"select count(iddomain) from Domain where domainname = ? and ownerthread = ?",
				domainName, (int) Thread.currentThread().getId()));
	}

	public void releaseDomainLock(String searchbase) {
		String domainName = this.findDomainName(searchbase);
		databaseFacade.update(
				"update Domain set status = ?, ownerthread = null where domainname = ? and ownerthread = ?",
				Domain.DOMAIN_STATUS_NORMAL, domainName, (int) Thread.currentThread().getId());
	}

	public void addLDAPConfig(
			String domainservername,
			int domainserverport,
			String searchbase,
			String binddn,
			String bindpass,
			int syncinterval) throws CommonException {
		String domainName = this.findDomainName(searchbase);
		Domain domain = (Domain) databaseFacade.findFirst(
				"from Domain where domainname = ?",
				domainName);
		log.debug("Creating LDAP config '{}' of domain {}.", searchbase, domainName);
		LDAPConfig ldapConfig = new LDAPConfig();
		ldapConfig.setDomainid(domain.getIddomain());
		ldapConfig.setDomainsearchbase(searchbase);
		ldapConfig.setSyncinterval(syncinterval);
		try {
			databaseFacade.persist(ldapConfig);
		} catch (PersistenceException e) {// TODO
			log.warn("LDAP config '{}' existing in domain {}.", searchbase, domainName);
			throw new CommonException(CONFLICT);
		}
		@SuppressWarnings("unchecked")
		List<LDAPConfig> existingLdapConfigs = (List<LDAPConfig>) databaseFacade.find(
				"from LDAPConfig where domainid = ? and idldapconfig != ?",
				domain.getIddomain(), ldapConfig.getIdldapconfig());
		for (LDAPConfig existingLdapConfig : existingLdapConfigs) {
			if (existingLdapConfig.getDomainsearchbase().toLowerCase().endsWith(searchbase.toLowerCase())
					|| searchbase.toLowerCase().endsWith(existingLdapConfig.getDomainsearchbase().toLowerCase())) {
				throw new CommonException(CONFLICT);
			}
		}
		LDAPConfigStatus ldapConfigStatus = new LDAPConfigStatus();
		ldapConfigStatus.setIdldapconfig(ldapConfig.getIdldapconfig());
		ldapConfigStatus.setStatus(LDAPConfigStatus.LDAP_CONFIG_STATUS_OK);
		databaseFacade.persist(ldapConfigStatus);
		synLDAP(domain.getIddomain(), ldapConfig.getIdldapconfig());
	}

  public void updateLDAPConfig(
      int ldapconfigid,
      String domainservername,
      int domainserverport,
      String searchbase,
      String domainbinddn,
      String domainbindpass,
      int syncinterval) throws CommonException {
    LDAPConfig ldapConfig = databaseFacade.load(LDAPConfig.class, ldapconfigid);
    Domain domain = databaseFacade.load(Domain.class, ldapConfig.getDomainid());
    if (domainbinddn != null)
    {
      domain.setDomainbinddn(domainbinddn);
    } else {
      domainbinddn = domain.getDomainbinddn();
    }
    if (domainbindpass != null)
    {
      domain.setDomainbindpass(domainbindpass);
    } else {
      domainbindpass = domain.getDomainbindpass();
    }
    if (domainservername != null)
    {
      domain.setDomainservername(domainservername);
    } else {
      domainservername = domain.getDomainservername();
    }
    if (domainserverport > -1)
    {
      domain.setDomainserverport(domainserverport);
    } else {
      domainserverport = domain.getDomainserverport();
    }
    if (searchbase != null)
    {
      ldapConfig.setDomainsearchbase(searchbase);
    } else {
      searchbase = ldapConfig.getDomainsearchbase();
    }
    if (syncinterval > -1)
    {
      ldapConfig.setSyncinterval(syncinterval);
    } else {
      syncinterval = ldapConfig.getSyncinterval();
    }
    try {
      this.verifyLDAPConfig(
          domain.getDomainservername(),
          domain.getDomainserverport(),
          ldapConfig.getDomainsearchbase(),
          domain.getDomainbinddn(),
          domain.getDomainbindpass());
    } catch (CommonException e) {
      throw new CommonException(e.getError());
    }
    databaseFacade.merge(domain);
    String domainName = domain.getDomainname();
    log.debug("Updating LDAP config '{}' of domain {}.", searchbase, domainName);
    try {
      databaseFacade.merge(ldapConfig);
    } catch (PersistenceException e) {// TODO
      log.warn("LDAP config '{}' existing in domain {}.", searchbase, domainName);
      throw new CommonException(CONFLICT);
    }
    @SuppressWarnings("unchecked")
    List<LDAPConfig> existingLdapConfigs = (List<LDAPConfig>) databaseFacade.find(
        "from LDAPConfig where domainid = ? and idldapconfig != ?",
        domain.getIddomain(), ldapConfig.getIdldapconfig());
    for (LDAPConfig existingLdapConfig : existingLdapConfigs) {
      if (existingLdapConfig.getDomainsearchbase().toLowerCase().endsWith(searchbase.toLowerCase())
          || searchbase.toLowerCase().endsWith(existingLdapConfig.getDomainsearchbase().toLowerCase())) {
        throw new CommonException(CONFLICT);
      }
    }
    synLDAP(domain.getIddomain(), ldapConfig.getIdldapconfig());
  }

	public void deleteLDAPConfig(
			String domainservername,
			String searchbase) throws CommonException {
		String domainName = this.findDomainName(searchbase);
		Domain domain = (Domain) databaseFacade.findFirst(
				"from Domain where domainname = ?",
				domainName);
		if (domain == null) {
			throw new CommonException(NOT_FOUND);
		}
		@SuppressWarnings("unchecked")
		List<LDAPConfig> ldapConfigs = (List<LDAPConfig>) databaseFacade.find(
				"from LDAPConfig where domainid = ?",
				domain.getIddomain());
		LDAPConfig ldapConfig = null;
		for (LDAPConfig existingLdapConfig : ldapConfigs) {
			if (existingLdapConfig.getDomainsearchbase().equals(searchbase)) {
				ldapConfig = existingLdapConfig;
				break;
			}
		}
		if (ldapConfig == null) {
			throw new CommonException(NOT_FOUND);
		}
		boolean last = ldapConfigs.size() < 2;
		if (exists(databaseFacade.findFirst(
				"select count(iduser) from User where deleted = 0 and iduser = ? and domainid = ?",
				sessionFacade.getCurrentSession().getUserid(), domain.getIddomain()))) {
			throw new CommonException(CONFLICT);
		}
		@SuppressWarnings("unchecked")
		List<User> users = (List<User>) databaseFacade.find(
				"from User where deleted = 0 and domainid = ?",
				domain.getIddomain());
		for (User user : users) {
			if (last || uniqueSupport(user, user.getOrganizationid(), domain.getDomainname(), ldapConfig, ldapConfigs)) {
				try {
					userFacade.forceDeleteUser(user.getIduser());
				} catch (CommonException e) {
					log.warn("Delete user failed.", e);
				}
			}
		}
		@SuppressWarnings("unchecked")
		List<Organization> organizations = (List<Organization>) databaseFacade.find(
				"from Organization where domainid = ?",
				domain.getIddomain());
		for (Organization organization : organizations) {
			if (last || uniqueSupport(null, organization.getIdorganization(), domain.getDomainname(), ldapConfig, ldapConfigs)) {
				organizationFacade.forceDeleteOrganization(organization.getIdorganization());
			}
		}
		databaseFacade.update(
				"delete from LDAPConfigStatus where idldapconfig = ?",
				ldapConfig.getIdldapconfig());
		databaseFacade.update(
				"delete from LDAPConfig where idldapconfig = ?",
				ldapConfig.getIdldapconfig());
		if (last) {
			@SuppressWarnings("unchecked")
			List<Group> groups = (List<Group>) databaseFacade.find(
					"from Group where domainid = ?",
					domain.getIddomain());
			for (Group group : groups) {
			  groupFacade.forceDeleteGroup(group.getIdgroup());
			}
	    @SuppressWarnings("unchecked")
	    List<DesktopPoolEntity> pools = (List<DesktopPoolEntity>) databaseFacade.find(
	        "from DesktopPoolEntity where domainname = ?",
	        domain.getDomainname());
	    for (DesktopPoolEntity pool : pools) {
	      try
	      {
	        operationRegistry.start(new DeleteDesktopPoolOperation(pool.getIddesktoppool(), true));
	      } catch (CommonException e)
	      {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	        continue;
	      }
	    }
      databaseFacade.update(
          "delete from RestrictionStrategyAssignment where domainid = ?",
          domain.getIddomain());
			databaseFacade.update(
					"delete from Domain where iddomain = ?",
					domain.getIddomain());
		}
	}

	public int findLDAPConfigStatus(int idldapconfig) {
		return (Integer) databaseFacade.findFirst(
				"select status from LDAPConfigStatus where idldapconfig = ?",
				idldapconfig);
	}

	public void synLDAP(
			final int domainid,
			final int ldapconfigid) {
		log.trace("synLDAP start: {}.", domainid);
		this.markLDAPConfigAsSynchronizing(ldapconfigid);
		final Domain domain = databaseFacade.load(Domain.class, domainid);
		final LDAPConfig ldapConfig = databaseFacade.load(LDAPConfig.class, ldapconfigid);
		DirContext ctx;
		try {
			ctx = this.createDirContext(domain.getDomainservername(), domain.getDomainserverport(), domain.getDomainbinddn(), domain.getDomainbindpass());
		} catch (CommonException e) {
			log.warn("synLDAP error {} with domain {}.", e.getError(), domainid);
			this.markLDAPConfigAsAbnormal(ldapConfig.getIdldapconfig());
			scheduleNextSyn(domain.getIddomain(), ldapConfig.getIdldapconfig(), ldapConfig.getSyncinterval());
			return;
		}
		Map<DN, Organization> organizations = new HashMap<DN, Organization>();
		Map<DN, User> users = new HashMap<DN, User>();
		try {
			this.findOrganizations(ctx, ldapConfig.getDomainsearchbase(), organizations);
			this.findUsers(domain.getDomainbinddn(), ctx, ldapConfig.getDomainsearchbase(), users);
		} catch (NameNotFoundException e) {
			log.warn("synLDAP error with domain {}.", domainid);
			this.markLDAPConfigAsAbnormal(ldapConfig.getIdldapconfig());
			this.synOrganizationsAndUsers(domain.getIddomain(), ldapConfig.getDomainsearchbase(), organizations, users);
			scheduleNextSyn(domain.getIddomain(), ldapConfig.getIdldapconfig(), ldapConfig.getSyncinterval());
			return;
		} catch (NamingException e) {
			log.warn("Search failed: {}.", e);
			log.warn("synLDAP error with domain {}.", domainid);
			this.markLDAPConfigAsAbnormal(ldapConfig.getIdldapconfig());
			scheduleNextSyn(domain.getIddomain(), ldapConfig.getIdldapconfig(), ldapConfig.getSyncinterval());
			return;
		} finally {
			try {
				ctx.close();
			} catch (NamingException e) {
				log.warn("Error on closing LDAP.", e);
			}
		}
		this.markLDAPConfigAsOK(ldapconfigid);
		this.synOrganizationsAndUsers(domain.getIddomain(), ldapConfig.getDomainsearchbase(), organizations, users);
		scheduleNextSyn(domain.getIddomain(), ldapConfig.getIdldapconfig(), ldapConfig.getSyncinterval());
	}

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

	public void setOrganizationFacade(OrganizationFacade organizationFacade) {
		this.organizationFacade = organizationFacade;
	}

	public void setGroupFacade(GroupFacade groupFacade)
  {
    this.groupFacade = groupFacade;
  }

  public void setUserFacade(UserFacade userFacade) {
		this.userFacade = userFacade;
	}

	public void setSessionFacade(SessionFacade sessionFacade) {
		this.sessionFacade = sessionFacade;
	}

  public void setOperationRegistry(OperationRegistry operationRegistry)
  {
    this.operationRegistry = operationRegistry;
  }
	
	private String findDomainName(String searchbase, String domainservername, int domainserverport, String domainbinddn, String domainbindpass) throws CommonException {
		String rootSearchBase = searchbase.substring(searchbase.toLowerCase().indexOf("dc="));
		SearchControls ctls = new SearchControls();
		String[] attrIDs = { "dNSHostName" };
		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(objectCategory=computer)";
		DirContext ctx = null;
		try {
			ctx = this.createDirContext(domainservername, domainserverport, domainbinddn, domainbindpass);
			NamingEnumeration<SearchResult> answer = ctx.search(rootSearchBase, filter, ctls);
			if (answer.hasMore()) {
				SearchResult sr = answer.next();
				Attributes attrs = sr.getAttributes();
				String dNSHostName = getAttr(attrs, "dNSHostName");
				dNSHostName = dNSHostName.substring(dNSHostName.indexOf(".") + 1);
				return dNSHostName;
			}
		} catch (AuthenticationException e) {
			log.warn("Authentication error on creating LDAP.", e);
			throw new CommonException(LDAP_UNAUTHORIZED);
		} catch (NamingException e) {
			log.warn("Search failed: {}.", e);
			throw new CommonException(CommonException.LDAP_ABNORMAL);
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					log.warn("Error on closing LDAP.", e);
				}
			}
		}
		throw new CommonException(CommonException.LDAP_ABNORMAL);
	}

	private String findDomainName(String searchbase) {
		String rootSearchBase = searchbase.substring(searchbase.toLowerCase().indexOf("dc=") + 3);
		rootSearchBase = rootSearchBase.replace(",dc=", ".");
		return rootSearchBase;
	}

	private void findOrganizations(DirContext ctx, String searchbase, Map<DN, Organization> organizations) throws NamingException {
    Organization rootOrganization = new Organization();
    rootOrganization.setOrganizationname("");
    rootOrganization.setGuid("");
    organizations.put(new DN("DC=" + this.findDomainName(searchbase).replace(".", ",DC=")), rootOrganization);
	  SearchControls ctls = new SearchControls();
		String[] attrIDs = { "objectGUID", "distinguishedName" };
		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(|(objectCategory=groupPolicyContainer)(|(objectCategory=container)(objectCategory=organizationalUnit)))";
		NamingEnumeration<SearchResult> answer = null;
		try {
			answer = ctx.search("DC=" + this.findDomainName(searchbase).replace(".", ",DC="), filter, ctls);
		} catch (NameNotFoundException e) {
			log.warn("{} not found.", searchbase);
			throw e;
		}
		try {
			while (answer.hasMore()) {
				SearchResult sr = answer.next();
				Attributes attrs = sr.getAttributes();
				String dn = getAttr(attrs, "distinguishedName");
				if (onDifferentTwigs(dn, searchbase))
        {
          continue;
        }
//				while (!dn.toLowerCase().startsWith("dc=")) {
					String name = dn.substring(3);
					name = name.substring(0, name.indexOf(","));
					Organization organization = new Organization();
					organization.setOrganizationname(name);
					organization.setGuid(base64((String) getAttr(attrs, "objectGUID")));
					organizations.put(new DN(dn), organization);
//					dn = dn.substring(3);
//					dn = dn.substring(dn.indexOf(",") + 1);
//				}
			}
		} catch (PartialResultException e) {}
	}

	private boolean onDifferentTwigs(String dn, String searchbase)
  {
    if (dn.toLowerCase().equals(searchbase.toLowerCase()))
    {
      return false;
    }
    if (dn.toLowerCase().endsWith(searchbase.toLowerCase())
        || searchbase.toLowerCase().endsWith(dn.toLowerCase()))
    {
      return false;
    }
    return true;
  }

  private void findUsers(String binddn, DirContext ctx, String searchbase, Map<DN, User> users) throws NamingException {
		SearchControls ctls = new SearchControls();
		String[] attrIDs = { "objectGUID", "distinguishedName", "sAMAccountName", "name", "telephoneNumber", "mail", "streetAddress", "description" };
		ctls.setReturningAttributes(attrIDs);
		ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		String filter = "(&(objectCategory=user)(objectClass=user))";
		NamingEnumeration<SearchResult> answer = null;
		try {
			answer = ctx.search(searchbase, filter, ctls);
		} catch (NameNotFoundException e) {
			log.warn("{} not found.", searchbase);
			throw e;
		}
		try {
			while (answer.hasMore()) {
				SearchResult sr = answer.next();
				Attributes attrs = sr.getAttributes();
				User user = new User();
        user.setGuid(base64((String) getAttr(attrs, "objectGUID")));
				user.setUsername(getAttr(attrs, "sAMAccountName"));
				user.setRealname(getAttr(attrs, "name"));
				user.setTelephone(getAttr(attrs, "telephoneNumber"));
				user.setEmail(getAttr(attrs, "mail"));
				user.setAddress(getAttr(attrs, "streetAddress"));
				user.setNotes(getAttr(attrs, "description"));
				users.put(new DN(getAttr(attrs, "distinguishedName")), user);
			}
		} catch (PartialResultException e) {}
	}

	private String getAttr(Attributes attrs, String attrName) throws NamingException {
		Attribute attr = attrs.get(attrName);
		return attr == null ? "" : (String) attr.get();
	}

	private DirContext createDirContext(String domainservername, int domainserverport, String binddn,
			String bindpass) throws CommonException {
		try {
			return LDAPUtils.createDirContext(domainservername, domainserverport, binddn, bindpass);
		} catch (AuthenticationException e) {
			log.warn("Authentication error on creating LDAP.", e);
			throw new CommonException(LDAP_UNAUTHORIZED);
		} catch (NamingException e) {
			log.warn("Error on connect LDAP.", e);
			throw new CommonException(LDAP_ABNORMAL);
		}
	}

	private void scheduleNextSyn(final int domainid, final int idldapconfig, final int syncinterval) {
		pool.schedule(new Runnable() {
			@Override
			public void run() {
				fetchDomainLock(domainid);
				try {
					if (checkDomainLock(domainid)) {
						synLDAP(domainid, idldapconfig);
					} else {
						scheduleNextSyn(domainid, idldapconfig, syncinterval);
					}
				} catch (Exception e) {
					log.warn("Syn error.", e);
					scheduleNextSyn(domainid, idldapconfig, syncinterval);
				} finally {
					releaseDomainLock(domainid);
				}
			}
		}, syncinterval, TimeUnit.SECONDS);
	}

  private String findGUID(String domainservername, int domainserverport,
      String binddn, String bindpass, String distinguishedName)
  {
    DirContext ctx = null;
    try {
      ctx = this.createDirContext(domainservername, domainserverport, binddn, bindpass);
      SearchControls ctls = new SearchControls();
      ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
      String filter = "(distinguishedName=" + distinguishedName + ")";
      NamingEnumeration<SearchResult> answer = ctx.search(distinguishedName, filter, ctls);
      try {
        if (answer.hasMore()) {
          SearchResult searchResult = answer.next();
          Attributes attrs = searchResult.getAttributes();
          Attribute attr = attrs.get("objectGUID");
          return base64((String) attr.get());
        } else {
          return null;
        }
      } catch (PartialResultException e) {
        return null;
      }
    } catch (Exception e) {
      log.warn("Error on reading LDAP.", e);
      return null;
    } finally {
      if (ctx != null)
      {
        try {
          ctx.close();
        } catch (NamingException e) {
          log.warn("Error on closing LDAP.", e);
        }
      }
    }
  }
	
	private String base64(String string)
  {
    try
    {
      return new BASE64Encoder().encode(string.getBytes("ISO-8859-1"));
    } catch (UnsupportedEncodingException e)
    {
      throw new RuntimeException(e);
    }
  }

	private void synOrganizationsAndUsers(
	    int domainid, String searchbase, Map<DN, Organization> organizations, Map<DN, User> users) {
		List<DN> organizationDNList = new ArrayList<DN>(organizations.keySet());
		Collections.sort(organizationDNList, new Comparator<DN>() {
			@Override
			public int compare(DN dn0, DN dn1) {
				return dn0.levels.size() - dn1.levels.size();
			}
		});
		List<Organization> matchedOrganizations = new LinkedList<Organization>();
		@SuppressWarnings("unchecked")
		List<Organization> allExistingOrganizations = (List<Organization>) databaseFacade.find(
				"from Organization where domainid = ? order by level desc",
				domainid);
		List<Organization> existingOrganizations = filterUnrelativeOrganizations(searchbase, allExistingOrganizations);
		for (DN dn : organizationDNList) {
			Organization organization = organizations.get(dn);
			organization.setNotes("");
			organization.setLevel(dn.levelSizeNoDomain());
			organization.setDomainid(domainid);
			// FIXME
			organization.setOrganizationtype(Organization.ORGANIZATION_TYPE_MSAD);
			if (!dn.levels.iterator().next().levelType.equalsIgnoreCase("DC")) {
        System.out.println("qqq"+dn);
        System.out.println("qqq"+dn.parent());
				Organization parent = organizations.get(dn.parent());
				organization.setParent(parent.getIdorganization());
        System.out.println("qqq"+parent.getIdorganization());
			} else {
			  organization.setLevel(0);
				organization.setParent(-1);
			}
			
			Organization organizationConflicted = (Organization) databaseFacade.findFirst(
					"from Organization where guid = ? and domainid = ?",
					organization.getGuid(), domainid);
			if (organizationConflicted != null) {
        System.out.println("qqq"+organization.getGuid()+" "+organizationConflicted.getGuid()+" "+organizationConflicted.getOrganizationname()+"!!!!!!!!!!!!!"+organization.getOrganizationname());
        organizationConflicted.setOrganizationname(organization.getOrganizationname());
        organizationConflicted.setParent(organization.getParent());
				organization = organizationConflicted;
				organizations.put(dn, organization);
        databaseFacade.merge(organization);
			} else {
				databaseFacade.persist(organization);
			}
			for (Organization existingOrganization : existingOrganizations) {
				if (existingOrganization.getGuid().equals(organization.getGuid())) {
					matchedOrganizations.add(existingOrganization);
					break;
				}
			}
		}
		List<Organization> organizationsToRemove = new LinkedList<Organization>();
		for (Organization existingOrganization : existingOrganizations) {
			boolean stillExists = false;
			for (Organization matchedOrganization : matchedOrganizations) {
				if (existingOrganization.getGuid().equals(matchedOrganization.getGuid())) {
					stillExists = true;
					break;
				}
			}
			if (!stillExists) {
				organizationsToRemove.add(existingOrganization);
			}
		}
		for (Organization organizationToRemove : organizationsToRemove) {
			organizationFacade.forceDeleteOrganization(organizationToRemove.getIdorganization());
		}
		List<User> matchedUsers = new LinkedList<User>();
		@SuppressWarnings("unchecked")
		List<User> existingUsers = (List<User>) databaseFacade.find(
				"from User where deleted = 0 and domainid = ?",
				domainid);
		existingUsers = filterUnrelativeUsers(searchbase, existingUsers);
		for (DN dn : users.keySet()) {
			User user = users.get(dn);
			user.setDomainid(domainid);
      user.setDomainname(databaseFacade.load(Domain.class, domainid).getDomainname());
			user.setIdcard("");
			user.setIdcardtype("");
			user.setPassword("");
			// FIXME
			user.setUsertype(Domain.DOMAIN_TYPE_MSAD);
      Organization organization = organizations.get(dn.parent());
      if (organization != null) {
        user.setOrganizationid(organization.getIdorganization());
        user.setOrganizationname(organization.getOrganizationname());
      }
			
			User userConflicted = (User) databaseFacade.findFirst(
					"from User where deleted = 0 and guid = ? and domainid = ?",
					user.getGuid(), domainid);
			if (userConflicted != null) {
        userConflicted.setUsername(user.getUsername());
        userConflicted.setOrganizationid(user.getOrganizationid());
        userConflicted.setOrganizationname(user.getOrganizationname());
				userConflicted.setTelephone(user.getTelephone());
				userConflicted.setEmail(user.getEmail());
				userConflicted.setAddress(user.getAddress());
				userConflicted.setNotes(user.getNotes());
				databaseFacade.merge(userConflicted);
				user = userConflicted;
			} else {
				databaseFacade.persist(user);
			}
			for (User existingUser : existingUsers) {
				if (existingUser.getGuid().equals(user.getGuid())) {
					matchedUsers.add(existingUser);
					break;
				}
			}
		}
		List<User> usersToRemove = new LinkedList<User>();
		for (User existingUser : existingUsers) {
			boolean stillExists = false;
			for (User matchedUser : matchedUsers) {
				if (existingUser.getGuid().equals(matchedUser.getGuid())) {
					stillExists = true;
					break;
				}
			}
			if (!stillExists) {
				usersToRemove.add(existingUser);
			}
		}
		for (User userToRemove : usersToRemove) {
			try {
				userFacade.forceDeleteUser(userToRemove.getIduser());
			} catch (CommonException e) {
				log.warn("Deleting user failed: {}.", e);
			}
		}
		// Deleting empty orgs.
		@SuppressWarnings("unchecked")
		List<Organization> newAllExistingOrganizations = (List<Organization>) databaseFacade.find(
				"from Organization where domainid = ? order by level desc",
				domainid);
		for (Organization existingOrganization : newAllExistingOrganizations) {
			if (organizationsToRemove.contains(existingOrganization)) {
				continue;
			}
			if ((!exists(
					databaseFacade.findFirst(
							"select count(idorganization) from Organization where parent = ?",
							existingOrganization.getIdorganization())))
							&& (!exists(
									databaseFacade.findFirst(
											"select count(iduser) from User where deleted = 0 and organizationid = ?",
											existingOrganization.getIdorganization())))) {
				organizationFacade.forceDeleteOrganization(existingOrganization.getIdorganization());
			}
		}
	}

	// FIXME Clustered lock.
	public void fetchDomainLock(int domainid) {
		databaseFacade.update(
				"update Domain set status = ?, ownerthread = ? where iddomain = ? and (ownerthread = ? or ownerthread = ?)",
				Domain.DOMAIN_STATUS_MAINTAINING, (int) Thread.currentThread().getId(), domainid, null, (int) Thread.currentThread().getId());
	}

	public boolean checkDomainLock(int domainid) {
		return exists(databaseFacade.findFirst(
				"select count(iddomain) from Domain where iddomain = ? and ownerthread = ?",
				domainid, (int) Thread.currentThread().getId()));
	}

	public void releaseDomainLock(int domainid) {
		databaseFacade.update(
				"update Domain set status = ?, ownerthread = null where iddomain = ? and ownerthread = ?",
				Domain.DOMAIN_STATUS_NORMAL, domainid, (int) Thread.currentThread().getId());
	}

	private List<Organization> filterUnrelativeOrganizations(
			String searchbase,
			List<Organization> existingOrganizations) {
		List<Organization> leftOrganizations = new LinkedList<Organization>();
		String domainname = this.findDomainName(searchbase);
		for (Organization organization : existingOrganizations) {
			String dn = this.buildDN(null, organization.getIdorganization(), domainname);
			if (dn.endsWith(searchbase.toLowerCase())) {
				leftOrganizations.add(organization);
			}
		}
		return leftOrganizations;
	}

	private List<User> filterUnrelativeUsers(
			String searchbase,
			List<User> existingUsers) {
		List<User> leftUsers = new LinkedList<User>();
		String domainname = this.findDomainName(searchbase);
		for (User user : existingUsers) {
			String dn = this.buildDN(user, user.getOrganizationid(), domainname);
			if (dn.endsWith(searchbase.toLowerCase())) {
				leftUsers.add(user);
			}
		}
		return leftUsers;
	}

	private boolean uniqueSupport(User user, Integer organizationid, String domainname,
			LDAPConfig ldapConfig, List<LDAPConfig> ldapConfigs) {
		String dn = this.buildDN(user, organizationid, domainname);
		if (!dn.endsWith(ldapConfig.getDomainsearchbase().toLowerCase())) {
			return false;
		}
		for (LDAPConfig existingLdapConfig : ldapConfigs) {
			if (existingLdapConfig.getIdldapconfig().equals(ldapConfig.getIdldapconfig())) {
				continue;
			}
			if (dn.endsWith(existingLdapConfig.getDomainsearchbase().toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	private void fulfillOrganizationDN(List<String> dn, int organizationid) {
		Organization organization = databaseFacade.load(Organization.class, organizationid);
		dn.add("OU=" + organization.getOrganizationname());
		if (organization.getParent() != -1) {
			fulfillOrganizationDN(dn, organization.getParent());
		}
	}

	private String buildDN(User user, Integer organizationid, String domainname) {
		List<String> dn = new LinkedList<String>();
		if (user != null) {
			dn.add("CN=" + user.getRealname());
		}
		if (organizationid != null) {
			fulfillOrganizationDN(dn, organizationid);
		}
		for (String domainPart : domainname.split("\\.")) {
			dn.add("DC=" + domainPart);
		}
		StringBuilder stringBuilderDN = new StringBuilder();
		boolean started = false;
		for (String dnPart : dn) {
			if (started) {
				stringBuilderDN.append(",");
			} else {
				started = true;
			}
			stringBuilderDN.append(dnPart);
		}
		return stringBuilderDN.toString().toLowerCase();
	}

	private void markLDAPConfigAsAbnormal(int idldapconfig) {
		databaseFacade.update(
				"update LDAPConfigStatus set status = ? where idldapconfig = ?",
				LDAPConfigStatus.LDAP_CONFIG_STATUS_ABNORMAL, idldapconfig);
	}

	private void markLDAPConfigAsSynchronizing(int idldapconfig) {
		databaseFacade.update(
				"update LDAPConfigStatus set status = ? where idldapconfig = ?",
				LDAPConfigStatus.LDAP_CONFIG_STATUS_SYNCHRONIZING, idldapconfig);
	}

	private void markLDAPConfigAsOK(int idldapconfig) {
		databaseFacade.update(
				"update LDAPConfigStatus set status = ? where idldapconfig = ?",
				LDAPConfigStatus.LDAP_CONFIG_STATUS_OK, idldapconfig);
	}
	
	private static class DN
	{
	  
	  private List<DNLevel> levels = new LinkedList<DNLevel>();

    public DN()
    {
    }

    public DN(String dn)
    {
      String[] parts = dn.split("\\,");
      for (String part : parts)
      {
        String type = part.substring(0, part.indexOf("="));
        String value = part.substring(part.indexOf("=") + 1);
        levels.add(new DNLevel(type, value));
      }
    }

    public DN parent()
    {
      DN parent = new DN();
      Iterator<DNLevel> iterator = levels.iterator();
      iterator.next();
      for (;iterator.hasNext();)
      {
        parent.levels.add(iterator.next());
      }
      return parent;
    }

    public int levelSizeNoDomain()
    {
      int size = 0;
      for (DNLevel level : levels)
      {
        if (!level.levelType.equalsIgnoreCase("DC"))
        {
          size++;
        } else
        {
          break;
        }
      }
      return size;
    }

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder();
      boolean started = false;
      for (DNLevel level : levels)
      {
        if (started)
        {
          sb.append(",");
        } else
        {
          started = true;
        }
        sb.append(level);
      }
      return sb.toString();
    }

    @Override
    public int hashCode()
    {
      return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      DN other = (DN) obj;
      if (levels == null)
      {
        if (other.levels != null)
          return false;
      } else if (!levels.equals(other.levels))
        return false;
      return true;
    }
	  
	}
	
	private static class DNLevel
	{

    private String levelType;
    private String levelValue;
    
    public DNLevel(String levelType, String levelValue)
    {
      this.levelType = levelType;
      this.levelValue = levelValue;
    }

    @Override
    public String toString()
    {
      return levelType + "=" + levelValue;
    }

    @Override
    public int hashCode()
    {
      return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      DNLevel other = (DNLevel) obj;
      if (levelType == null)
      {
        if (other.levelType != null)
          return false;
      } else if (!levelType.equals(other.levelType))
        return false;
      if (levelValue == null)
      {
        if (other.levelValue != null)
          return false;
      } else if (!levelValue.equals(other.levelValue))
        return false;
      return true;
    }
	  
	}

  public LDAPConfig loadLDAPConfig(Integer idldapconfig)
  {
    return databaseFacade.load(LDAPConfig.class, idldapconfig);
  }

}
