package springbook.user.sqlservice;

import java.util.Map;

import springbook.user.exception.SqlRetrievalFailureException;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.1장 SQL과 DAO의 분리
 *    - 7.1.2 SQL 제공 서비스
 */
public class SimpleSqlService implements SqlService {
	// 설정파일에 <map>으로 정의된 SQL 정보를 가져오도록 프로퍼티로 등록해둔다.
	private Map<String, String> sqlMap;
	public void setSqlMap(Map<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
		String sql = sqlMap.get(key);
		if(sql == null) {
			throw new SqlRetrievalFailureException(key + "에 대한 SQL을 찾을 수 없습니다.");
		}else {
			return sql;
		}
	}

}
