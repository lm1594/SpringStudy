package springbook.user.service;

import java.util.List;

import springbook.user.domain.User;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.1장 트랜잭션 코드의 분리
 *    - 6.1.2 DI를 이용한 클래스의 분리
 *   6.6장 트랜잭션 속성
 *    - 6.6.4 트랜잭션 속성 적용
 */
public interface UserService {
	//DAO 메소드와 1:1 대응되는 CRUD 메소드이지만 add()처럼 단순 위임 이상의 로직을 가질 수 있다.
	void add(User user);
	void upgradeLevels();
	User get(String id);
	List<User> getAll();
	void deleteAll();
	int getCount();
	public void update(User user);
}
