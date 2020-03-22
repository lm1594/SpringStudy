package springbook.user.exception;
/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.4장 인터페이스 상속을 통한 안전한 기능확장
 *    - 7.4.2 인터페이스 상속
 */
public class SqlNotFoundException extends RuntimeException{
	public SqlNotFoundException(String message) {
		super(message);
	}
	
	public SqlNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
