package com.opzoon.vdi.core.facade.transience;

import static com.opzoon.vdi.core.facade.FacadeHelper.count;
import static com.opzoon.vdi.core.facade.FacadeHelper.exists;
import static com.opzoon.vdi.core.facade.FacadeHelper.pagingFind;
import static com.opzoon.vdi.core.util.StringUtils.randomString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.opzoon.vdi.core.domain.Session;
import com.opzoon.vdi.core.facade.StorageFacade;
import com.opzoon.vdi.core.facade.FacadeHelper.PagingInfo;

/**
 * 会话相关业务接口.
 */
public class SessionFacade {
	
	private StorageFacade storageFacade;
	private ThreadLocal<Session> threadLocalCurrentSession;

	/**
	 * 获取当前用户的会话.
	 * 
	 * @return 当前用户的会话. 未登录则为null.
	 */
	public Session getCurrentSession() {
		if (threadLocalCurrentSession == null) {
			return null;
		}
		return threadLocalCurrentSession.get();
	}

	/**
	 * 将用户会话作为当前用户的会话保存在线程本地变量中.
	 * 
	 * @param currentSession 用户会话
	 */
	public void setCurrentSession(Session currentSession) {
		if (threadLocalCurrentSession == null) {
			threadLocalCurrentSession = new ThreadLocal<Session>();
		}
		threadLocalCurrentSession.set(currentSession);
	}

	@SuppressWarnings("unchecked")
	public List<Session> findExpiredSessions() {
		return (List<Session>) storageFacade.find(
				"from Session where expire < ?",
				new Date());
	}

	@SuppressWarnings("unchecked")
	public List<Session> findAllSessions() {
		return (List<Session>) storageFacade.find(
				"from Session");
	}
	
	@SuppressWarnings("unchecked")
	public List<Integer> findSessionsByUserId(int ownerid) {
		return (List<Integer>) storageFacade.find(
				"select idsession from Session where userid = ?",
				ownerid);
	}

	public Integer findUserBySession(int sessionid) {
		return (Integer) storageFacade.findFirst(
				"select userid from Session where idsession = ?",
				sessionid);
	}

	public Session loadSession(int idsession) {
		return storageFacade.load(Session.class, idsession);
	}

	/**
	 * 分页查询用户会话.
	 * 
	 * @param userid 用户ID. -1为忽略.
	 * @param logintype 登录角色. -1为忽略.
	 * @param pagingInfo 分页信息.
	 * @param amountContainer 查询结果的总数量的容器.
	 * @return 查询结果列表.
	 */
	@SuppressWarnings("unchecked")
	public List<Session> findSessions(int userid, int logintype, PagingInfo pagingInfo, int[] amountContainer) {
		StringBuilder whereClause = new StringBuilder("from Session where 1 = 1 and deleted = ?");
		List<Object> params = new ArrayList<Object>();
		params.add(0);
		if (userid > -1) {
			whereClause.append(" and userid = ?");
			params.add(userid);
		}
		if (logintype > -1) {
			whereClause.append(" and logintype = ?");
			params.add(logintype);
		}
		Object[] paramsArray = params.toArray();
		count(storageFacade, "idsession", whereClause, paramsArray, amountContainer);
		return pagingFind(storageFacade, whereClause, paramsArray, pagingInfo);
	}

	/**
	 * 根据令牌查找角色.
	 * 
	 * @param ticket 令牌.
	 * @return 角色. 令牌无效则为null.
	 */
	public Session findSession(String ticket) {
		Session session = (Session) storageFacade.findFirst(
				"from Session where ticket = ? and expire > ?",
				ticket, new Date());
		if (session == null || session.getDeleted() != 0) {
			this.setCurrentSession(null);
			return session;
		}
		this.setCurrentSession(session);
		return session;
	}

	/**
	 * 延后会话的过期时间.
	 * 
	 * @param ticket 令牌.
	 */
	public void renewSession(String ticket) {
		storageFacade.update(
				"update Session set expire = ? where ticket = ?",
				this.calculateNewExpire(), ticket);
	}

	public void createNewSession(Session session) {
		session.setTicket(randomString(64));
		storageFacade.persist(session);
	}

	public void removeSession(int iduser, Integer idsession, int invalidatingReasonCode) {
		if (idsession == null) {
			storageFacade.update(
					"update Session set deleted = idsession, invalidatingreasoncode = ? where userid = ? and logintype = ?",
					invalidatingReasonCode, iduser, Session.LOGIN_TYPE_USER);
		} else {
			storageFacade.update(
					"update Session set deleted = idsession, invalidatingreasoncode = ? where idsession = ?",
					invalidatingReasonCode, idsession);
		}
	}

	public void removeAllSessions(int iduser) {
		storageFacade.update(
				"update Session set deleted = idsession, invalidatingreasoncode = ? where userid = ?",
				Session.INVALIDATING_REASON_CODE_USER_DELETED, iduser);
	}

//	public boolean checkIfSessionIsOK() {
//		return exists(storageFacade.findFirst(
//				"select count(idsession) from Session where idsession = ?",
//				this.getCurrentSession().getIdsession()));
//	}

	public void setStorageFacade(StorageFacade storageFacade) {
		this.storageFacade = storageFacade;
	}
	
	private Date calculateNewExpire() {
		return new Date(System.currentTimeMillis() + 1000 * 60 * 30);// 30 minutes.
	}

}
