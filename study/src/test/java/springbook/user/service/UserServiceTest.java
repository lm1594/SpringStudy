package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;

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
 *    - 5.2.1 모 아니면 도
 *     - 강제 예외 발생을 통한 테스트
 *    - 5.2.4 트랜잭션 서비스 추상화 : 스프링의 트랜잭션 서비스 추상화, 트랜잭션 기술 설정의 분리
 *  5장 서비스 추상화
 *   5.4장 메일서비스 추상화
 *    - 5.4.3 테스트를 위한 서비스 추상화 : 테스트용 메일발송 오브젝트
 *    - 5.4.4 테스트 대역 : 목 오브젝트를 이용한 테스트
 *  6장 AOP
 *   6.1장 트랜잭션 코드의 분리
 *    - 6.1.2 DI를 이용한 클래스의 분리
 *   6.2장 고립된 단위 테스트
 *    - 6.2.2 테스트 대상 오브젝트 고립시키기ㄷ
 *    - 6.2.4 목 프레임워크
 *   6.3장 다이내믹 프록시와 팩토리 빈
 *    - 6.3.2 다이내믹 프록시
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {

	@Autowired private UserService userService;
	@Autowired private UserDao userDao;
	@Autowired private PlatformTransactionManager transactionManager;
	@Autowired private MailSender mailSender;
	
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
					new User("bumjin", "박범진", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "lm1595@naver.com"),	// 테스트에서는 가능한 한 경계 값을 사용하는 것이 좋다.
					new User("joytouch", "강명성", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "lm1595@naver.com"),
					new User("erwins", "신승한", "p3", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD-1, "lm1595@naver.com"),
					new User("madnite1", "이상호", "p4", Level.SILVER, 60, MIN_RECCOMEND_FOR_GOLD, "lm1595@naver.com"),
					new User("green", "오민규", "p5", Level.GOLD, 100, Integer.MAX_VALUE, "lm1595@naver.com")
				);
	}
	
	/**
	 * @history
	 *  - 5.1.3 사용자레벨 업그레이드 테스트
	 *  - 리스트 5-57 목 오브젝트로 만든 메일 전송 확인용 클래스
	 *  6장
	 *   - 리스트 6-8 목 오브젝트 설정이 필요한 테스트 코드 수정
	 *   - 리스트 6-10 upgradeLevels() 테스트
	 *   - 리스트 6-13 MockUserDao를 사용해서 만든 고립된 테스트
	 */
	@Test
	public void upgradeLevels() throws Exception{
		UserServiceImpl userServiceImpl = new UserServiceImpl();					// 고립된 테스트에서는 테스트대상 오브젝트를 직접 생성하면 된다.
		
		MockUserDao mockUserDao = new MockUserDao(this.users);					// 목 오브젝트로 만든 UserDao를 직접 DI해준다.
		userServiceImpl.setUserDao(mockUserDao);
		
		// 메일 발송 여부 확인을 위해 목 오브젝트 DI
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		// 테스트 대상 실행 
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();							// MockUserDao로부터 업데이트 결과를 가져온다.
		assertThat(updated.size(), is(2));										// 업데이트 횟수와 정보를 확인한다.
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		// 목 오브젝트를 이용한 결과 확인
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
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
	 *  - 5.2.4 트랜잭션 서비스 추상화 : 스프링의 트랜잭션 서비스 추상화, 트랜잭션 기술 설정의 분리
	 * 6장
	 *  리스트 6-9 분리된 테스트 기능이 포함되도록 수정한 upgradeAllOrNothing()
	 *  리스트 6-28 다이내믹 프록시를 이용한 트랜잭션 테스트
	 */
	@Test
	public void upgradeAllOrNothing() throws Exception{
		UserServiceImpl testUserService = new TestUserService(users.get(3).getId());// 예외를 발생시킬 네 번째 사용자의 id를 넣어서 테스트용 UserService 대역 오브젝트를 생성한다.
		testUserService.setUserDao(this.userDao); 									// userDao를 수동 DI해준다.
		testUserService.setMailSender(mailSender);									// 리스트 5-56 테스트용 UserService를 위한 메일 전송 오브젝트의 수동 DI
		
		TransactionHandler txHandler = new TransactionHandler();
		// 트랜잭션 핸들러가 필요한 정보와 오브젝트를 DI해준다.
		txHandler.setTarget(testUserService);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern("upgradeLevels");
		UserService txUserService = (UserService)Proxy.newProxyInstance(			// UserService 인터페이스 타입의 다이내믹 프록시 생성
				getClass().getClassLoader()
				, new Class[] {UserService.class}
				, txHandler);
		
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		try {
			// TestUserService는 업그레이드 작업 중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패
			txUserService.upgradeLevels();				// 트랜잭션 기능을 분리한 오브젝트를 통해 예외 발생용 TestUserService가 호출되게 해야한다.
			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e) {
			// TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패 
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.service.UserServiceTest");
	}
	
	//---------------------------------------------------------------------------------------------------------
	// 스태틱 클래스
	//---------------------------------------------------------------------------------------------------------
	
	/**
	 * 리스트 5-57 목 오브젝트로 만든 메일 전송 확인용 클래스
	 */
	static class MockMailSender implements MailSender {
		
		// UserService로부터 전송 요청을 받은 메일 주소를 저장해두고 이를 읽을 수 있게 한다.
		private List<String> requests = new ArrayList<String>();
		public List<String> getRequests() {
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage simpleMessage) throws MailException {
			// TODO Auto-generated method stub
			requests.add(simpleMessage.getTo()[0]);				// 전송 요청을 받은 이메일 주소를 저장해둔다. 간단하게 첫 번째 수신자 메일 주소만 저장했다.
		}
		@Override
		public void send(SimpleMailMessage... simpleMessages) throws MailException {
			// TODO Auto-generated method stub
			
		}
	}
	
	/**
	 * 리스트 6-12 UserDao 목 오브젝트
	 */
	static class MockUserDao implements UserDao {
		private List<User> users;								// 레벨 업그레이드 후보 User 오브젝트 목록
		private List<User> updated = new ArrayList<User>();		// 업그레이드 대상 오브젝트를 저장해둘 목록
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return this.updated;
		}
		
		// 스텁기능 제공
		public List<User> getAll() {
			return this.users;
		}
		
		// 목 오브젝트 기능 제공
		public void update(User user) {
			updated.add(user);
		}

		// 테스트에 사용되지 않는 메소드
		@Override public void setDataSource(DataSource dataSource) {throw new UnsupportedOperationException();}
		@Override public void add(User user) {throw new UnsupportedOperationException();}
		@Override public User get(String id) {throw new UnsupportedOperationException();}
		@Override public void deleteAll() {throw new UnsupportedOperationException();}
		@Override public int getCount() {throw new UnsupportedOperationException();}
	}
	
}
