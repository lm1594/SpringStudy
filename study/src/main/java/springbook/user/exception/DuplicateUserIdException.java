package springbook.user.exception;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.02.12
 * @history
 *  4장 예외
 *   4.1.4 예외처리 전략
 */
public class DuplicateUserIdException extends  RuntimeException{
	public DuplicateUserIdException(Throwable cause) {
		// TODO Auto-generated constructor stub
		super(cause);
	}
}
