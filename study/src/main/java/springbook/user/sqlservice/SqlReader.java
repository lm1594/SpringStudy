package springbook.user.sqlservice;
/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.2장 인터페이스의 분리와 자기참조 빈
 *    - 7.2.4 변화를 위한 준비: 인터페이스 분리
 */
public interface SqlReader {
	void read(SqlRegistry sqlRegistry);			// SQL을 외부에서 가져와 SqlRegistry에 등록한다. 다양한 예외가 발생할 수 있겠지만 대부분 복구 불가능한 예외이므로 굳이 예외를 선언해두지 않았다.
}
