package springbook.learningtest.spring.pointcut;


/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.5장 스프링 AOP
 *    - 6.5.3 포인트컷 표현식을 이용한 포인트컷
 */
// 리스트 6-59 포인트컷 테스트용 클래스
public class Target implements TargetInterface{
	public void hello() {}
	public void hello(String a) {}
	public int plus(int a, int b) { return 0; }
	public int minus(int a, int b) throws RuntimeException { return 0;}
	public void method() {}
	
	public static void main(String args[]) throws Exception {
		System.out.println(Target.class.getMethod("minus", int.class, int.class));
	}
}
