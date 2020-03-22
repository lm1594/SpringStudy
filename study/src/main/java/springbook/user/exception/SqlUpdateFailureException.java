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
public class SqlUpdateFailureException extends RuntimeException{
	public SqlUpdateFailureException(String message) {
		super(message);
	}
	
	public SqlUpdateFailureException(String message, Throwable cause) {
		super(message, cause);			// SQL을 가져오는 데 실패한 근본 원인을 담을 수 있도록 중첩 예외를 저장할 수 있는 생성자를 만들어둔다.
	}
}
