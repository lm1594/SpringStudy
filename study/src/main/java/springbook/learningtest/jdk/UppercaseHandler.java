package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 토비의 스프링
 * @author 이경민
 * @history
 *  6장 AOP
 *   6.3장 다이내믹 프록시와 팩토리 빈
 *    - 6.3.2 다이내믹 프록시
 */
// 리스트 6-23 InvocationHandler 구현 클래스
// 리스트 6-25 확장된 UppercaseHandler
public class UppercaseHandler implements InvocationHandler{
	
	// 다이내믹 프록시로부터 전달받은 요청을 다시 타깃 오브젝트에 위임해야 하기 때문에 타깃 오브젝트를 주입받아 둔다.
	// 어떤 종류의 인터페이스를 구현한 타깃에도 적용 가능하도록 Object타입으로 수정
	Object target;
	public UppercaseHandler(Object target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		Object ret = method.invoke(target, args);
		if (ret instanceof String) {				// 호출한 메소드의 리턴 타입이 String인 경우만 대문자 변경 기능을 적용하도록 수정
			return ((String)ret).toUpperCase();
		}else {
			return ret;								// 조건이 일치하지 않으면 타깃 오브젝트의 호출 결과를 그대로 리턴한다.
		}
	}
	
	// 리스트 6-26 메소드를 선별해서 부가기능을 적용하는 invoke()
//	@Override
//	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//		
//		Object ret = method.invoke(target, args);
//		if (ret instaceof String && method.getName().startsWith("say")) {	// 리턴 타입과 메소드 이름이 일치하는 경우에만 부가기능을 적용한다.
//			return ((String)ret).toUpperCase();
//		}else {
//			return ret;
//		}
//	}
}
