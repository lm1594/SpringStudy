package springbook.user.sqlservice;

import springbook.user.exception.SqlRetrievalFailureException;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.2장 인터페이스의 분리와 자기참조 빈
 *    - 7.2.4 변화를 위한 준비: 인터페이스 분리
 */
public interface SqlRegistry {
	void registerSql(String key, String sql);							// SQL을 키와 함께 등록한다.
	String findSql(String key) throws SqlRetrievalFailureException;		// 키로 SQL을 검색한다. 검색이 실패하면 예외를 던진다.
}
