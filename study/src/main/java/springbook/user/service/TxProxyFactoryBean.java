package springbook.user.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.3장 다이내믹 프록시와 팩토리 빈
 *    - 6.3.4 다이내믹 프록시를 위한 팩토리 빈
 */
public class TxProxyFactoryBean implements FactoryBean<Object> {		// 생성할 오브젝트 타입을 지정할 수도 있지만 범용적으로 사용하기 위해 Object로 했다.
	
	// Transactionhandler를 생성할 때 필요
	Object target;

	PlatformTransactionManager transactionManager;
	String pattern;
	
	Class<?> serviceInterface;											// 다이내믹 프록시를 생성할 때 필요하다. UserService 외의 인터페이스를 가진 타깃에도 적용할 수 있다.

	public void setTarget(Object target) {
		this.target = target;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	
	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}
	
	// FactoryBean 인터페이스 구현 메소드
	public Object getObject() throws Exception {						// DI 받은 정보를 이용해서 TransactionHandler를 사용하는 다이내믹 프록시를 생성한다.
		TransactionHandler txHandler = new TransactionHandler();
		txHandler.setTarget(target);
		txHandler.setTransactionManager(transactionManager);
		txHandler.setPattern(pattern);
		return Proxy.newProxyInstance(
										getClass().getClassLoader(), 
										new Class[] {serviceInterface},
										txHandler);
	}
	
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return serviceInterface;
	}
	
	public boolean isSingleton() {
		return false;													// 싱글톤 빈이 아니라는 뜻이 아니라 getObject()가 매번 같은 오브젝트를 리턴하지 않는다는 의미다.
	}
}
