package springbook.learningtest.jdk;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.3장 다이내믹 프록시와 팩토리 빈
 *    - 6.3.2 다이내믹 프록시
 */
// 리스트 6-19 타깃 클래스
public class HelloTarget implements Hello {

	@Override
	public String sayHello(String name) {
		// TODO Auto-generated method stub
		return "Hello " + name;
	}

	@Override
	public String sayHi(String name) {
		// TODO Auto-generated method stub
		return "Hi " + name;
	}

	@Override
	public String sayThankYou(String name) {
		// TODO Auto-generated method stub
		return "Thank You " + name;
	}

}
