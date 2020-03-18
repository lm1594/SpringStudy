package springbook.user.sqlservice;
/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.1장 SQL과 DAO의 분리
 *    - 7.1.2 SQL 제공 서비스
 */
public interface SqlService {
	String getSql(String key) throws SqlRetrievalFailureException;
}
