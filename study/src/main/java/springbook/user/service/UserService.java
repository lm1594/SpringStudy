package springbook.user.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

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
 *   6.7장 애노테이션 트랜잭션 속성과 포인트컷
 *    - 6.7.2 트랜잭션 애노테이션 적용
 *    
 */
@Transactional					// <tx:method name="*" />과 같은 설정 효과를 가져온다. 메소드 레벨 @Transactional 애노테이션이 없으므로 대체 정책에 따라 타입 레벨에 부여된 디폴트 속성이 적용된다.
public interface UserService {
	//DAO 메소드와 1:1 대응되는 CRUD 메소드이지만 add()처럼 단순 위임 이상의 로직을 가질 수 있다.
	void add(User user);
	void upgradeLevels();
	void deleteAll();
	int getCount();
	public void update(User user);
	
	// <tx:method name="get*" read-only="true"/>를 애노테이션 방식으로 변경한 것이다. 메소드 단위로 부여된 트랜잭션의 속성이 타입 레벨에 부여된 것에 우선해서 적용된다. 같은 속성을 가졌어도 메소드 레벨에 부여될 때는 메소드마다 반복될 수밖에 없다.
	@Transactional(readOnly = true)
	User get(String id);
	
	@Transactional(readOnly = true)
	List<User> getAll();
}
