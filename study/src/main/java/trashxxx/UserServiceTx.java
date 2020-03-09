package trashxxx;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;
import springbook.user.service.UserService;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.1 트랜잭션 코드의 분리
 *    - 6.1.2 DI를 이용한 클래스의 분리 : 리스트6-5 위임 기능을 가진 UserServiceTx 클래스
 */
public class UserServiceTx implements UserService {
	
	// UserService를 구현한 다른 오브젝트를 DI 받는다.
	UserService userService;
	PlatformTransactionManager transactionManager;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	// DI 받은 UserService 오브젝트에 모든 기능을 위임한다.
	@Override
	public void add(User user) {
		userService.add(user);
	}

	@Override
	public void upgradeLevels() {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			userService.upgradeLevels();
			this.transactionManager.commit(status);
		}catch(RuntimeException e) {
			this.transactionManager.rollback(status);
			throw e;
		}
	}

}
