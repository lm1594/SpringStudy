package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.29
 * @history
 *  5장 서비스 추상화
 *   5.2장 트랜잭션 서비스 추상화
 *    - 테스트용 UserService 대역
 */
public class TestUserService extends UserService{
	
	private String id;
	
	public TestUserService(String id) {	// 예외를 발생시킬 User 오브젝트의 id를 지정할 수 있게 만든다.
		this.id = id;
	}

	@Override
	protected void upgradeLevel(User user) {	// UserService의 메소드를 오버라이드 한다.
		// TODO Auto-generated method stub
		if(user.getId().equals(this.id)) {		// 지정된 id의  User 오브젝트가 발견되면 예외를 던져서 작업을 강제로 중단시킨다.
			throw new TestUserServiceException();
		}
		super.upgradeLevel(user);
	}
}
