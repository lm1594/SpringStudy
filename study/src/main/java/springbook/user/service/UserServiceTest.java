package springbook.user.service;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import springbook.user.dao.UserDao;
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
 *    - 6.8.3 테스트를 위한 트랜잭션 애노테이션
 *  7장 스프링 핵심 기술의 응용
 *   7.6 스프링 3.1의 DI
 *    - 7.6.1 자바 코드를 이용한 빈 설정
 */
public class UserServiceTest {	
	//---------------------------------------------------------------------------------------------------------
	// 스태틱 클래스
	//---------------------------------------------------------------------------------------------------------
	
	/**
	 * 리스트 6-54 수정한 테스트용 UserService 구현 클래스
	 * 리스트 6-81 읽기전용 메소드에 쓰기 작업을 추가한 테스트용 클래스
	 */
	public static class TestUserService extends UserServiceImpl {
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
