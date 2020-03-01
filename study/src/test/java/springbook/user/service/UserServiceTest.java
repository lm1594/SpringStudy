package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;

import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.23
 * @history
 *  5장 서비스 추상화
 *   5.1장 사용자 레벨 관리 기능 추가
 *    - 5.1.3 UserService.upgradeLevels()
 *    - 5.1.4 UserService.add()
 *   5.2장 트랜잭션 서비스 추상화
 *    5.2.1 모 아니면 도
 *     - 강제 예외 발생을 통한 테스트
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired private DataSource dataSource;
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
					new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0),	// 테스트에서는 가능한 한 경계 값을 사용하는 것이 좋다.
					new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0),
					new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1),
					new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD),
					new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE)
				);
	}
	
	/**
	 * 5.1.3 사용자레벨 업그레이드 테스트
	 */
	@Test
	public void upgradeLevels() throws Exception{
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
		// 각 사용자별로 업그레이드 후의 예상 레벨을 검증한다.
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
	}
	
	/**
	 * 5.1.3 DB에서 사용자 정보를 가져와 레벨을 확인하는 코드가 중복되므로 헬퍼 메소드로 분리했다.
	 */
	private void checkLevelUpgraded(User user, boolean upgraded) {			// boolean upgraded -> 어떤 레벨로 바뀔것인가가 아니라, 다음 레벨로 업그레이드될 것인가 아닌가를 지정한다.
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));		// 업그레이드가 일어났는지 확인
		}else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));					// 업그레이드가 일어나지 않았는지 확인
		}
		
	}
	
	/**
	 * 5.1.4 add() 메소드의 테스트
	 */
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);					// GOLD 레벨 -> GOLD 레벨이 이미 지정된 User라면 레벨을 초기화하지 않아야 한다.
		
		User userWithOutLevel = users.get(0);
		userWithOutLevel.setLevel(null);					// 레벨이 비어 있는 사용자, 로직에 따라 등록중에 BASIC 레벨도 설정돼야 한다.
		
		userService.add(userWithLevel);
		userService.add(userWithOutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithOutLevelRead = userDao.get(userWithOutLevel.getId());
		// -> DB에 저장된 결과를 가져와 확인한다.
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithOutLevelRead.getLevel(), is(Level.BASIC));
	}
	
	/**
	 * 5.2장 트랜잭션 서비스 추상화
	 *  - 강제 예외 발생을 통한 테스트
	 */
	@Test
	public void upgradeAllOrNothing() throws Exception{
		UserService testUserService = new TestUserService(users.get(3).getId());		// 예외를 발생시킬 네 번째 사용자의 id를 넣어서 테스트용 UserService 대역 오브젝트를 생성한다.
		testUserService.setUserDao(this.userDao); 		// userDao를 수동 DI해준다.
		testUserService.setDataSource(this.dataSource);
		
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		try {
			// TestUserService는 업그레이드 작업 중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e) {
			// TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패 
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.service.UserServiceTest");
	}
}
