package springbook.learningtest.spring.pointcut;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.5장 스프링 AOP
 *    - 6.5.3 포인트컷 표현식을 이용한 포인트컷
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class PointcutExpressionTest {
	
	// 리스트 6-61 메소드 시그니처를 이용한 포인트컷 표현식 테스트
	@Test
	public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException{
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		
		pointcut.setExpression("execution(public int " + 
						"springbook.learningtest.spring.pointcut.Target.minus(int, int) " + 
						"throws java.lang.RuntimeException)");			// Target 클래스 minus()메소드 시그니처
		
		// Target.minus()
		assertThat(pointcut.getClassFilter().matches(Target.class) &&	// 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
					pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), is(true));	// 포인트컷 조건 통과
		
		// Target.plus()
		assertThat(pointcut.getClassFilter().matches(Target.class) &&	// 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
				pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), is(false));		// 포인트컷 조건 통과
		
		// Bean.method()
		assertThat(pointcut.getClassFilter().matches(Bean.class) &&		// 클래스 필터와 메소드 매처를 가져와 각각 비교한다.
				pointcut.getMethodMatcher().matches(Target.class.getMethod("method", int.class, int.class), null), is(false));		// 포인트컷 조건 통과
	}
	
	/**
	 * 리스트 6-62 포인트컷과 메소드를 비교해주는 테스트 헬퍼 메소드
	 */
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>...args) throws Exception {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		
		assertThat(pointcut.getClassFilter().matches(clazz) &&		// 포인트컷의 클래스 필터와 메소드 매처 두 가지를 동시에 만족하는지 확인한다.
				pointcut.getMethodMatcher().matches(clazz.getMethod(methodName , args), null), is(expected));
	}
	
	/**
	 * 리스트 6-63 타깃 클래스의 메소드 6개에 대해 포인트컷 선정 여부를 검사하는 헬퍼 메소드
	 */
	public void targetClassPointcutMatches(String expression, boolean...expected) throws Exception {
		pointcutMatches(expression, expected[0], Target.class	, "hello");
		pointcutMatches(expression, expected[1], Target.class	, "hello", String.class);
		pointcutMatches(expression, expected[2], Target.class	, "plus", int.class, int.class);
		pointcutMatches(expression, expected[3], Target.class	, "minus", int.class, int.class);
		pointcutMatches(expression, expected[4], Target.class	, "method");
		pointcutMatches(expression, expected[5], Bean.class		, "method");
	}
	
	/**
	 * 리스트 6-64 포인트컷 표현식 테스트
	 */
	@Test
	public void pointcut() throws Exception {
		targetClassPointcutMatches("execution(* *(..))", true, true, true, true, true, true);
		// 나머지는 생략 - 표 6-1의 내용과 동일하다.
	}
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.learningtest.spring.pointcut.PointcutExpressionTest");
	}

}
