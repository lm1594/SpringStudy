package springbook.user.service;

import java.util.List;

import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;
/**
 * 리스트 6-54 수정한 테스트용 UserService 구현 클래스
 * 리스트 6-81 읽기전용 메소드에 쓰기 작업을 추가한 테스트용 클래스
 */
public class TestUserService extends UserServiceImpl {
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
