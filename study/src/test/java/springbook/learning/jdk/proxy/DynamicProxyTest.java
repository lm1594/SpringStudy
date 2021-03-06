package springbook.learning.jdk.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.learningtest.jdk.UppercaseHandler;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.4장 스프링의 프록시 팩토리 빈
 *    - 6.4.1 ProxyFactoryBean
 *    리스트 6-41 스프링 ProxyFactoryBean을 이용한 다이내믹 프록시 테스트
 *    리스트 6-42 포인트컷까지 적용한 ProxyFactoryBean
 *   6.5장 스프링 AOP
 *    - 6.5.1 자동 프록시 생성
 *    리스트 6-50 확장 포인트컷 테스트
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class DynamicProxyTest {

	@Test
	public void simpleProxy() {
		// jdk 다이내믹 프록시 생성
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(
				getClass().getClassLoader()
				, new Class[] {Hello.class}
				, new UppercaseHandler(new HelloTarget()));
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankyou("Toby"), is("THANK YOU TOBY"));
	}
	
	@Test
	public void proxyFactoryBean() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());									// 타깃 설정
		pfBean.addAdvice(new UppercaseAdvice());								// 부가기능을 담은 어드바이스를 추가한다. 여러 개를 추가할 수도 있다.
		
		Hello proxiedHello = (Hello) pfBean.getObject();						// FactoryBean이므로 getObject()로 생성된 프록시를 가져온다.
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankyou("Toby"), is("THANK YOU TOBY"));
	}
	
	static class UppercaseAdvice implements MethodInterceptor {
		public Object invoke(MethodInvocation invocation) throws Throwable{
			String ret = (String) invocation.proceed();							// 리플렉션의 Method와 달리 메소드 실행 시 타깃 오브젝트를 전달할 필요가 없다. MethodInvocation은 메소드 정보와 함께 타깃 오브젝트를 알고 있기 때문이다.
			return ret.toUpperCase();											// 부가기능 적용	
		}
	}
	
	public static interface Hello {													// 타깃과 프록시가 구현할 인터페이스
		String sayHello(String name);
		String sayHi(String name);
		String sayThankyou(String name);
	}
	
	public static class HelloTarget implements Hello {									// 타깃 클래스
		@Override
		public String sayHello(String name) { return "Hello " + name;}
		public String sayHi(String name) { return "Hi " + name; }
		public String sayThankyou(String name) { return "Thank You " + name; }
	}
	
	@Test
	public void pointcutAdviser() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");
		
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankyou("Toby"), is("Thank You Toby"));
	}

	//------------ 리스트 6-50 확장 포인트컷 테스트
	@Test
	public void classnamePointcutAdvisor() {
		// 포인트컷 준비
		NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {	// 익명 내부 클래스 방식으로 클래스를 정의한다.
			public ClassFilter getClassFilter() {										// 익명 내부 클래스 방식으로 클래스를 정의한다.
				return new ClassFilter() {
					public boolean matches(Class<?> clazz) {
						return clazz.getSimpleName().startsWith("HelloT");				// 클래스 이름이 HelloT로 시작하는 것만 선정한다.
					}
				};
			}
		};
		classMethodPointcut.setMappedName("sayH*"); 									// sayH로 시작하는 메소드 이름을 가진 메소드만 선정한다.
		
		//테스트
		checkAdviced(new HelloTarget(), classMethodPointcut, true);						// 적용 클래스다.
		
		class HelloWorld extends HelloTarget{};
		checkAdviced(new HelloWorld(), classMethodPointcut, false);						// 적용 클래스가 아니다!
		
		class HelloToby extends HelloTarget{};
		checkAdviced(new HelloToby(), classMethodPointcut, true);						// 적용 클래스다.
	}
	
	private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {		// adviced는 적용대상인가 ?
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(target);
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
		Hello proxiedHello = (Hello) pfBean.getObject();
		
		if(adviced) {
			// 메소드 선정방식을 통해 어드바이스 적용
			assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
			assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
			assertThat(proxiedHello.sayThankyou("Toby"), is("Thank You Toby"));
		}else {
			// 어드바이스 적용 대상 후보에서 아예 탈락
			assertThat(proxiedHello.sayHello("Toby"), is("Hello Toby"));
			assertThat(proxiedHello.sayHi("Toby"), is("Hi Toby"));
			assertThat(proxiedHello.sayThankyou("Toby"), is("Thank You Toby"));
		}
	}
	
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.learning.jdk.proxy.DynamicProxyTest");
	}
}
