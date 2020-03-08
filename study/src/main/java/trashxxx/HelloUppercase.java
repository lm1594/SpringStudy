package trashxxx;

import springbook.learningtest.jdk.Hello;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.3장 다이내믹 프록시와 팩토리 빈
 *    - 6.3.2 다이내믹 프록시
 */
// 리스트 6-21 프록시 클래스
public class HelloUppercase implements Hello {
	
	Hello hello;								// 위임할 타깃 오브젝트, 여기서는 타깃 클래스의 오브젝트인 것은 알지만 다른 프록시를 추가할 수도 있으므로 인터페이스로 접근한다.
	public HelloUppercase(Hello hello) {
		this.hello = hello;
	}
	
	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		// TODO Auto-generated method stub
		return hello.sayThankYou(name).toUpperCase();
	}

}
