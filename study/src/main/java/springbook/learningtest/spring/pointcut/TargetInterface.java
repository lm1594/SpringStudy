package springbook.learningtest.spring.pointcut;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.5장 스프링 AOP
 *    - 6.5.3 포인트컷 표현식을 이용한 포인트컷
 */
public interface TargetInterface {
	void hello();
	void hello(String a);
	int plus(int a, int b);
	int minus(int a, int b);
}
