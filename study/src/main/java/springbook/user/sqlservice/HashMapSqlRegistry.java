package springbook.user.sqlservice;

import java.util.HashMap;
import java.util.Map;

import springbook.user.exception.SqlNotFoundException;
import springbook.user.exception.SqlRetrievalFailureException;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.2장 인터페이스의 분리와 자기참조 빈
 *    - 7.2.6 디폴트 의존관계
 */
public class HashMapSqlRegistry implements SqlRegistry {
	
	/**
	 * 리스트 7-38 HashMap을 이용하는 SqlRegistry 클래스
	 */
	private Map<String, String> sqlMap = new HashMap<String, String>();
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
		}else {
			return sql;
		}
	}

}
