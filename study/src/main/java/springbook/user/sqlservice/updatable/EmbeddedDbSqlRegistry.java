package springbook.user.sqlservice.updatable;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import springbook.user.exception.SqlNotFoundException;
import springbook.user.exception.SqlUpdateFailureException;

/**
 * 토비의스프링
 * @author 이경민
 * @history
 *  7장 스프링 핵심 기술의 응용
 *   7.5장 DI를 이용해 다양한 구현 방법 적용하기
 *    - 7.5.2 내장형 데이터베이스를 이용한 SQL 레지스트리 만들기
 */
public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry{
	SimpleJdbcTemplate jdbc;
	TransactionTemplate transactionTemplate;
	
	public void setDataSource(DataSource dataSource) {
		this.jdbc = new SimpleJdbcTemplate(dataSource);
		transactionTemplate = new TransactionTemplate(
					// dataSource로 TransactionManager를 만들고 이를 이용해 TransactionTemplate을 생성한다.
					new DataSourceTransactionManager(dataSource)
				);
	}
	
	@Override
	public void registerSql(String key, String sql) {
		jdbc.update("insert into sqlmap(key_, sql_) values (?,?)", key, sql);			// DataSource를 DI 받아서 SimpleJdbcTemplate 형태로 저장해두고 사용한다.
	}

	@Override
	public String findSql(String key) throws SqlNotFoundException {		
		try {
			return jdbc.queryForObject("select sql_ from sqlmap where key_ = ?", String.class, key);
		}catch (EmptyResultDataAccessException e ) {	// queryForObject는 쿼리의 결과가 없으면 이 예외를 발생시킨다.
			throw new SqlNotFoundException(key + "를 이용해서 SQL을 찾을 수 없습니다.");
		}
	}

	@Override
	public void updateSql(String key, String sql) throws SqlUpdateFailureException {
		// update()는 SQL 실행 결과로 영향을 받은 레코드의 개수를 리턴한다. 이를 이용하면 주어진 키(key)를 가진 SQL이 존재했는지를 간단히 확인할 수 있다.
		int affected = jdbc.update("update sqlmap set sql_ = ? where key_ = ?", sql, key);
		if(affected == 0) {
			throw new SqlUpdateFailureException(key + "에 해당하는 SQL을 찾을 수 없습니다.");
		}
	}

	@Override
	public void updateSql(final Map<String, String> sqlmap) throws SqlUpdateFailureException {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				for(Map.Entry<String, String> entry : sqlmap.entrySet()) {
					updateSql(entry.getKey(), entry.getValue());
				}
			}
		});
		
	}
}
