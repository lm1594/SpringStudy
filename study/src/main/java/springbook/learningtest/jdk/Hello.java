package springbook.learningtest.jdk;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.3장 다이내믹 프록시와 팩토리 빈
 *    - 6.3.2 다이내믹 프록시
 */
// 리스트 6-18 Hello 인터페이스
public interface Hello {
	String sayHello(String name);
	String sayHi(String name);
	String sayThankYou(String name);
}
