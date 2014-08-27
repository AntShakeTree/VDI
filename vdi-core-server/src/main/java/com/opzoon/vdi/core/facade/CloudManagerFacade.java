package com.opzoon.vdi.core.facade;

import static com.opzoon.vdi.core.cloud.CloudManagerHelper.initCloudManager;
import static com.opzoon.vdi.core.cloud.CloudManagerHelper.syncWorkWithCloudManager;
import static com.opzoon.vdi.core.facade.CommonException.NO_ERRORS;
import static com.opzoon.vdi.core.util.ConditionUtils.numberNotEquals;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dc.engine.core.d;
import com.opzoon.vdi.core.cloud.CloudManager;
import com.opzoon.vdi.core.cloud.CloudManagerHelper;
import com.opzoon.vdi.core.cloud.CloudManagerHelper.CloudManagerWorker;
import com.opzoon.vdi.core.domain.CloudManagerEntity;
import com.opzoon.vdi.core.domain.CloudManagerStatus;
import com.opzoon.vdi.core.domain.state.CloudManagerState;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;
import com.opzoon.vdi.core.facade.transience.CloudManagerStatusFacade;
import com.opzoon.vdi.core.operation.CreateCloudManagerOperation;
import com.opzoon.vdi.core.operation.DeleteCloudManagerOperation;
import com.opzoon.vdi.core.operations.OperationRegistry;

/**
 * 平台相关业务接口.
 */
public class CloudManagerFacade {
	
	private static final Logger log = LoggerFactory.getLogger(CloudManagerFacade.class);

	private DatabaseFacade databaseFacade;
	private DesktopPoolFacade desktopPoolFacade;
	private CloudManagerStatusFacade cloudManagerStatusFacade;
	private OperationRegistry operationRegistry;

	
	/**
	 * 初始化所有的CloudManager.
	 * 由Spring自动调用, 参考spring-all.xml.
	 */
	public void init() {
    FacadeHelper.waitUntilDatabaseIsReady(databaseFacade);
		@SuppressWarnings("unchecked")
		List<CloudManagerEntity> cloudManagers = (List<CloudManagerEntity>) databaseFacade.find(
				"from CloudManagerEntity");
		for (final CloudManagerEntity cloudManager : cloudManagers) {
			Thread thread = new Thread() {
				@Override
				public void run() {
					int error = initCloudManager(cloudManager, cloudManagerStatusFacade);
					if (numberNotEquals(error, NO_ERRORS)) {
						cloudManagerStatusFacade.markAsAbnormal(cloudManager.getIdcloudmanager());
					}
				}
				@Override
				public String toString() {
					return super.toString() + "THREAD initCloudManager " + cloudManager.getIdcloudmanager() + " " + cloudManager.getBaseurl();
				}
			};
			com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
		}
	}

	/**
	 * 创建平台.
	 * 
	 * @param cloudManagerEntity 平台实体.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 成功.
	 */
	public int createCloudManager(CloudManagerEntity cloudManagerEntity) throws CommonException {
    try
    {
      databaseFacade.persist(cloudManagerEntity);
    } catch (PersistenceException e)
    {
      throw new CommonException(CommonException.CONFLICT);
    }
    CloudManagerStatus cloudManagerStatus = new CloudManagerStatus();
    cloudManagerStatus.setIdcloudmanager(cloudManagerEntity.getIdcloudmanager());
    cloudManagerStatus.setPhase(CloudManagerState.CLOUD_MANAGER_PHASE_START);
//    cloudManagerStatus.setStatus(CloudManagerState.CLOUD_MANAGER_STATUS_UNMANAGED);
    cloudManagerStatus.setStatus(0);
    databaseFacade.persist(cloudManagerStatus);
    int error = initCloudManager(cloudManagerEntity, null);
    operationRegistry.start(new CreateCloudManagerOperation(cloudManagerEntity.getIdcloudmanager()));
    if (numberNotEquals(error, NO_ERRORS)) {
      // TODO
      throw new CommonException(error);
    }
    return NO_ERRORS;
	}

	/**
	 * 删除平台.
	 * 
	 * @param idcloudmanager 平台ID.
	 * @return 错误代码.<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 成功;<br />
	 *         {@link com.opzoon.vdi.core.facade.CommonException#NOT_FOUND}: 平台不存在.
	 * @throws CommonException 
	 */
	public void deleteCloudManager(final int idcloudmanager) throws CommonException {
    operationRegistry.start(new DeleteCloudManagerOperation(idcloudmanager));
//		desktopPoolFacade.deleteAllDesktopPools(idcloudmanager, new Runnable() {
//			@Override
//			public void run() {
//				cloudManagerStatusFacade.deleteStatus(idcloudmanager);
//				databaseFacade.update(
//						"delete from CloudManagerEntity where idcloudmanager = ?",
//						idcloudmanager);
//				dropCloudManager(idcloudmanager);
//			}});
	}

	/**
	 * 查询平台.
	 * 
	 * @param idcloudmanager 平台ID. -1为忽略.
	 * @param type 平台类型.
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<CloudManagerEntity> findCloudManagers(int idcloudmanager, String clouddrivername, PagingInfo pagingInfo) {
		StringBuilder queryClause = new StringBuilder("from CloudManagerEntity where 1 = 1");
		List<Object> params = new ArrayList<Object>();
		if (idcloudmanager > -1) {
			queryClause.append(" and idcloudmanager = ?");
			params.add(idcloudmanager);
		}
		if (clouddrivername != null && clouddrivername.length() > 0) {
			queryClause.append(" and clouddrivername = ?");
			params.add(clouddrivername);
		}
		queryClause.append(FacadeHelper.keyword(pagingInfo, params));
		Object[] paramsArray = params.toArray();
		List<CloudManagerEntity> cloudManagers = (List<CloudManagerEntity>) databaseFacade.find(
				queryClause.toString(),
		        paramsArray);
		for (final CloudManagerEntity cloudManagerEntity : cloudManagers) {
      final CloudManagerEntity cloudManagerEntityCopy = new CloudManagerEntity();
      cloudManagerEntityCopy.setBaseurl(cloudManagerEntity.getBaseurl());
      cloudManagerEntityCopy.setClouddrivername(cloudManagerEntity.getClouddrivername());
      cloudManagerEntityCopy.setCloudname(cloudManagerEntity.getCloudname());
      cloudManagerEntityCopy.setDomain(cloudManagerEntity.getDomain());
      cloudManagerEntityCopy.setIdcloudmanager(cloudManagerEntity.getIdcloudmanager());
      cloudManagerEntityCopy.setNotes(cloudManagerEntity.getNotes());
      cloudManagerEntityCopy.setPassword(cloudManagerEntity.getPassword());
      cloudManagerEntityCopy.setStatus(cloudManagerEntity.getStatus());
      cloudManagerEntityCopy.setUsername(cloudManagerEntity.getUsername());
			cloudManagerEntity.setPassword(null);
			CloudManagerStatus status = (CloudManagerStatus) databaseFacade.findFirst(
	        "from CloudManagerStatus where idcloudmanager = ?",
	        cloudManagerEntity.getIdcloudmanager());
			cloudManagerEntity.setStatus(
			    status.getPhase() == CloudManagerState.CLOUD_MANAGER_PHASE_DELETING ?
			        254 :// FIXME
			          status.getStatus());// FIXME
      Thread thread = new Thread() {
        @Override
        public void run() {
          final CloudManager cloudManager = CloudManagerHelper.findCloudManager(cloudManagerEntityCopy);
          int status = 1;
          try {
            cloudManager.listTemplates();
            status = 0;
          } catch (Exception e) {
            log.warn("Exception", e);
          }
          databaseFacade.update(
              "update CloudManagerStatus set status = ? where idcloudmanager = ?",
              status, cloudManagerEntity.getIdcloudmanager());
        }
      };
      com.opzoon.ohvc.session.ExcecutorUtil.execute(thread);
		}
		return cloudManagers;
	}

	/**
	 * 查询指定平台下的所有模板.
	 * 
	 * @param cloudmanagerid 平台ID.
	 * @param templates 查询出的模板列表.
	 * @return 错误代码.
	 */
	public int findAllTemplates(final int cloudmanagerid, final List<Template> templates,final boolean link) {
		return syncWorkWithCloudManager(
				cloudManagerStatusFacade,
				databaseFacade.load(CloudManagerEntity.class, cloudmanagerid),
				new CloudManagerWorker() {
					@Override
					public int execute(CloudManager cloudManager, Object[] resultContainer, int jobid) throws Exception {
						try {
							List<com.opzoon.vdi.core.domain.Template> cloudTemplates = cloudManager.listTemplates();
							for (com.opzoon.vdi.core.domain.Template cloudTemplate : cloudTemplates) {
								Template template = new Template();
								template.setIdtemplate(cloudTemplate.getTemplateId());
								template.setTemplatename(cloudTemplate.getTemplatename());
								templates.add(template);
							}
						} catch (Exception e) {
							log.warn("listTemplates failed: {}", cloudManager);
							throw e;
						}
						return NO_ERRORS;
					}
				});
	}

	public void setDatabaseFacade(DatabaseFacade databaseFacade) {
		this.databaseFacade = databaseFacade;
	}

	public void setCloudManagerStatusFacade(
			CloudManagerStatusFacade cloudManagerStatusFacade) {
		this.cloudManagerStatusFacade = cloudManagerStatusFacade;
	}
	
	public void setDesktopPoolFacade(DesktopPoolFacade desktopPoolFacade) {
		this.desktopPoolFacade = desktopPoolFacade;
	}

	public void setOperationRegistry(OperationRegistry operationRegistry)
  {
    this.operationRegistry = operationRegistry;
  }

  public static class Template {

		private String idtemplate;
		private String templatename;
		
		public String getIdtemplate() {
			return idtemplate;
		}
		public void setIdtemplate(String idtemplate) {
			this.idtemplate = idtemplate;
		}
		public String getTemplatename() {
			return templatename;
		}
		public void setTemplatename(String templatename) {
			this.templatename = templatename;
		}
		
	}

	public int updateCloudManager(CloudManagerEntity cloudManagerEntity) throws CommonException {
		try
	    {
		  	
			CloudManagerEntity dbCloudManagerEntity=	   databaseFacade.get(CloudManagerEntity.class,cloudManagerEntity.getIdcloudmanager());
//			dbCloudManagerEntity.setBaseurl(cloudManagerEntity.getBaseurl());
			String baseUrl =cloudManagerEntity.getBaseurl();
			String password =cloudManagerEntity.getPassword();
			String domain =cloudManagerEntity.getDomain();
			String username =cloudManagerEntity.getUsername();
			String cloudname =cloudManagerEntity.getCloudname();
			if(StringUtils.isNotEmpty(baseUrl)&&StringUtils.isNotBlank(baseUrl))
				dbCloudManagerEntity.setBaseurl(baseUrl);
			if(StringUtils.isNotEmpty(username)&&StringUtils.isNotBlank(username))
				dbCloudManagerEntity.setUsername(username);
			if(StringUtils.isNotEmpty(password)&&StringUtils.isNotBlank(password))
				dbCloudManagerEntity.setPassword(password);
			if(StringUtils.isNotEmpty(cloudname)&&StringUtils.isNotBlank(cloudname))
				dbCloudManagerEntity.setCloudname(cloudname);
			if(StringUtils.isNotEmpty(domain)&&StringUtils.isNotBlank(domain))
				dbCloudManagerEntity.setDomain(domain);
			databaseFacade.merge(dbCloudManagerEntity);
			cloudManagerEntity=dbCloudManagerEntity;
	    } catch (PersistenceException e)
	    {
	      throw new CommonException(CommonException.CONFLICT);
	    }
		return 0;
}

}
