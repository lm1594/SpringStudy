package springbook.learningtest.template;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.08
 * @history
 *  3.5.3 템플릿 / 콜백의 응용
 *  	- 템플릿 / 콜백의 재설계
 *  	- 제네릭스를 이용한 콜백 인터페이스
 */
public interface LineCallback<T> {
	T doSomethingWithLine(String line, T value);
}
