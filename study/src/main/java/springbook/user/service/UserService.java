package springbook.user.service;

import springbook.user.domain.User;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.1 트랜잭션 코드의 분리
 *    - 6.1.2 DI를 이용한 클래스의 분리
 */
public interface UserService {
	void add(User user);
	void upgradeLevels();
}
