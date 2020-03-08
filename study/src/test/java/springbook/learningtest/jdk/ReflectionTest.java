package springbook.learningtest.jdk;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.3장 다이내믹 프록시와 팩토리 빈
 *    - 6.3.2 다이내믹 프록시
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class ReflectionTest {

	@Test
	public void invokeMethod() throws Exception {
		String name = "Spring";
		
		//length()
		assertThat(name.length(), is(6));
		
		Method lengthMethod = String.class.getMethod("length");
		assertThat((Integer)lengthMethod.invoke(name), is(6));
		
		//charAt()
		assertThat(name.charAt(0), is('S'));
		
		Method charAtMethod = String.class.getMethod("charAt", int.class);
		assertThat((Character)charAtMethod.invoke(name,0), is('S'));
	}
	
	/**
	 * 리스트 6-20 클라이언트 역할의 테스트
	 */
	@Test
	public void simpleProxy() {
		Hello hello = new HelloTarget();						// 타깃은 인터페이스를 통해 접근하는 습관을 들이자.
		assertThat(hello.sayHello("Toby"), is("Hello Toby"));
		assertThat(hello.sayHi("Toby"), is("Hi Toby"));
		assertThat(hello.sayThankYou("Toby"), is("Thank You Toby"));
		
		// 리스트 6-24 프록시 생성
		Hello proxiedHello = (Hello) Proxy.newProxyInstance(								// 생성된 다이내믹 프록시 오브젝트는 Hello 인터페이스를 구현하고 있으므로 Hello 타입으로 캐스팅해도 안전하다.
												getClass().getClassLoader()					// 동적으로 생성되는 다이내믹 프록시 클래스의 로딩에 사용할 클래스 로더
												, new Class[] {Hello.class}					// 구현할 인터페이스
												, new UppercaseHandler(new HelloTarget()));	// 부가기능과 위임 코드를 담음 invocztionHandler
		assertThat(proxiedHello.sayHello("Toby"), is("HELLO TOBY"));
		assertThat(proxiedHello.sayHi("Toby"), is("HI TOBY"));
		assertThat(proxiedHello.sayThankYou("Toby"), is("THANK YOU TOBY"));
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.learningtest.jdk.ReflectionTest");
	}
}
