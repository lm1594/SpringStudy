package springbook.user.sqlservice.updatable;
import java.util.Map;

import springbook.user.exception.SqlUpdateFailureException;
import springbook.user.sqlservice.SqlRegistry;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.4장 인터페이스 상속을 통한 안전한 기능확장
 *    - 7.4.2 인터페이스 상속
 */
public interface UpdatableSqlRegistry extends SqlRegistry {
	// 리스트 7-62 SQL 수정 기능을 가진 확장 인터페이스
	public void updateSql(String key, String sql) throws SqlUpdateFailureException;
	public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException;
}
