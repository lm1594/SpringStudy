package springbook.user.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.4장 스프링의 프록시 팩토리 빈
 *    - 6.4.2 ProxyFactoryBean 적용
 */
public class TransactionAdvice implements MethodInterceptor {	// 스프링의 어드바이스 인터페이스 구현
	
	private PlatformTransactionManager transactionManager;		// 트랜잭션 기능을 제공하는 데 필요한 트랜잭션 매니저
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {	// 타깃을 호출하는 기능을 가진 콜백 오브젝트를 프록시로부터 받는다. 덕분에 어드바이스는 특정 타깃에 의존하지 않고 재사용 가능하다.
		TransactionStatus status = 
				this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		try {
			Object ret = invocation.proceed();					// 콜백을 호출해서 타깃의 메소드를 실행한다. 타깃 메소드 호출 전후로 필요한 부가기능을 넣을 수 있다. 경우에 따라서 타깃이 아예 호출되지 않게 하거나 재시도를 위한 반복적인 호출도 가능하다.
			this.transactionManager.commit(status);
			return ret;
		}catch (RuntimeException e) {							// JDK 다이내믹 프록시가 제공하는 Method와는 달리 스프링의 MethodInvocation을 통한 타깃 호출은 예외가 포장되지 않고 타깃에서 보낸 그대로 전달된다.
			this.transactionManager.rollback(status);
			throw e;
		}
	}

}
