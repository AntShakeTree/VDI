/**
 * 
 */
package test.vdi.dao;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.config.TestConfig;

import com.vdi.dao.Request;
import com.vdi.dao.desktop.DesktopDao;
import com.vdi.dao.desktop.domain.Desktop;
import com.vdi.facade.DesktopService;

/**
 * @author mxc
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class DesktopDaoTest {
	
		private @Autowired DesktopDao desktopDao;

		private @Autowired DesktopService desktopservice;


	/**
	 * Test method for {@link com.vdi.dao.DesktopDao#saveDesktop(com.vdi.dao.domain.Desktop)}.
	 * @throws Exception 
	 */
	@Test
	public void testSaveDesktop() throws Exception {
		Desktop desktop=new Desktop();
		desktop.setPoolid(1l);
		desktop.setPoolname("pool1");
		desktop.setVmname("vm");
		desktop.setVmid("vmid1");
		desktopDao.save(desktop);
//		desktopservice.createDesktops(100, desktop);

	}

	/**
	 * Test method for {@link com.vdi.dao.DesktopDao#getDesktop(long)}.
	 */
	@Test
	public void testGetDesktop() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.vdi.dao.DesktopDao#findAll()}.
	 */
	@Test
	public void testFindAll() {
//		System.out.println();
		long start = System.currentTimeMillis();
		Request<Desktop> x=new Request<Desktop>() {
		};
		desktopDao.listRequest(x);
//		for (int i = 0; i < 10000; i++) {
//			List<Desktop> ss=	desktopDao.listRequest(new Request<>() {
//			});		}
		System.out.println(System.currentTimeMillis()-start);
	}

}
