package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
 *    - 6.3.4 다이내믹 프록시를 위한 팩토리 빈
 *   6.4장 스프링의 프록시 팩토리 빈
 *    - 6.4.2 ProxyFactoryBean 적용
 *   6.5장 스프링 AOP
 *    - 6.5.2 DefaultAdvisorAutoProxyCreator의 적용
 *   6.8장 트랜잭션 지원 테스트
 *    - 6.8.2 트랜잭션 동기화와 테스트
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {

	@Autowired private UserService userService;
	@Autowired private UserService testUserService;							// 같은 타입의 빈이 두 개 존재하기 때문에 필드 이름을 기준으로 주입될 빈이 결정된다. 자동 프록시 생성기에 의해 트랜잭션 부가기능이 testUserService 빈에 적용됐는지를 확인하는 것이 목적이다.
	@Autowired private UserDao userDao;
	@Autowired ApplicationContext context;									// 팩토리 빈을 가져오려면 애플리케이션 컨텍스트가 필요하다.
	@Autowired PlatformTransactionManager transactionManager;
	
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
	 *  리스트 6-37 트랜잭션 프록시 팩토리 빈을 적용한 테스트
	 *  리스트 6-48 ProxyFactoryBean을 이용한 트랜잭션 테스트
	 *  리스트 6-56 testUserService 빈을 사용하도록 수정된 테스트
	 */
	@Test
	public void upgradeAllOrNothing() throws Exception{
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		try {
			// TestUserService는 업그레이드 작업 중에 예외가 발생해야 한다. 정상 종료라면 문제가 있으니 실패
			this.testUserService.upgradeLevels();				// 트랜잭션 기능을 분리한 오브젝트를 통해 예외 발생용 TestUserService가 호출되게 해야한다.
			fail("TestUserServiceException expected");
		}catch(TestUserServiceException e) {
			// TestUserService가 던져주는 예외를 잡아서 계속 진행되도록 한다. 그 외의 예외라면 테스트 실패 
		}
		
		checkLevelUpgraded(users.get(1), false);
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
	
	/**
	 * 리스트 6-82 읽기전용 속성 테스트
	 */
	@Test(expected = TransientDataAccessResourceException.class)
	public void readOnlyTransactionAttribute() {
		testUserService.getAll();				// 트랜잭션 속성이 제대로 적용됐다면 여기서 읽기전용 속성을 위반했기 때문에 예외가 발생해야 한다.
	}
	
	/**
	 * 리스트 6-91 간단한 테스트 메소드
	 * 리스트 6-92 트랜잭션 매니저를 이용해 트랜잭션을 미리 시작하게 만드는 테스트
	 * 리스트 6-93 트랜잭션 동기화 검증용 테스트
	 * 리스트 6-96 롤백 테스트
	 */
	@Test
	public void transactionSync() {
		
		DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();		// 트랜잭션 정의는 기본 값을 사용한다.
		TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);		// 트랜잭션 매니저에게 트랜잭션을 요청한다. 기존에 시작된 트랜잭션이 없으니 새로운 트랜잭션을 시작 시키고 트랜잭션 정보를 돌려준다. 동시에 만들어진 트랜잭션을 다른 곳에서도 사용할 수 있도록 동기화 한다.
		
		try {
			// 앞에서 만들어진 트랜잭션에 모두 참여한다.
			userService.deleteAll();
			userService.add(users.get(0));
			userService.add(users.get(1));
		}finally {
			transactionManager.rollback(txStatus);											// 테스트 결과가 어떻든 상관없이 테스트가 끝나면 무조건 롤백한다. 테스트 중에 발생했던 DB의 변경 사항은 모두 이전 상태로 복구된다.
		}
		
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.user.service.UserServiceTest");
	}
	
	//---------------------------------------------------------------------------------------------------------
	// 스태틱 클래스
	//---------------------------------------------------------------------------------------------------------
	
	/**
	 * 리스트 6-54 수정한 테스트용 UserService 구현 클래스
	 * 리스트 6-81 읽기전용 메소드에 쓰기 작업을 추가한 테스트용 클래스
	 */
	static class TestUserServiceImpl extends UserServiceImpl {
		private String id = "madnite1";
		
		@Override
		protected void upgradeLevel(User user) {
			if(user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
		
		@Override
		public List<User> getAll() {				// 읽기전용 트랜잭션의 대상인 get으로 시작하는 메소드를 오버라이드한다.
			for(User user : super.getAll() ) {
				super.update(user);					// 강제로 쓰기 시도를 한다. 여기서 읽기전용 속성으로 인한 예외가 발생해야 한다.
			}
			return null;							// 메소드가 끝나기 전에 예외가 발생해야 하니 리턴 값은 별 의미 없다. 적당한 값을 넣어서 컴파일만 되게 한다.
		}
	}
	
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
