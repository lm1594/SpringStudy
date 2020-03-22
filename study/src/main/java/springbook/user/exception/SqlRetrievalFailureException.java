package springbook.user.exception;
/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.1장 SQL과 DAO의 분리
 *    - 7.1.2 SQL 제공 서비스
 */
public class SqlRetrievalFailureException extends RuntimeException{
	public SqlRetrievalFailureException(String message) {
		super(message);
	}
	
	public SqlRetrievalFailureException(String message, Throwable cause) {
		super(message, cause);			// SQL을 가져오는 데 실패한 근본 원인을 담을 수 있도록 중첩 예외를 저장할 수 있는 생성자를 만들어둔다.
	}
}
