package springbook.user.sqlservice.updatable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import springbook.user.exception.SqlNotFoundException;
import springbook.user.exception.SqlUpdateFailureException;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.5장 DI를 이용해 다양한 구현 방법 적용하기
 *    - 7.5.1 ConcurrentHashMap을 이용한 수정 가능 SQL 레지스트리
 *    - 7.5.2 내장형 데이터베이스를 이용한 SQL 레지스트리 만들기
 */
public class EmbeddedDbSqlRegistryTest extends ApstractUpdatableSqlRegistryTest{
	EmbeddedDatabase db;
	
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		db = new EmbeddedDatabaseBuilder()
				.setType(EmbeddedDatabaseType.HSQL)		// HSQL, DERBY, H2 세 가지 중 하나를 선택할 수 있다. 초기화 SQL이 호환만 된다면 DB 종류는 언제든지 바꿀 수 있다.
				.addScript("classpath:/springbook/user/sqlservice/updatable/sqlRegistrySchema.sql")
				.build();
		
		EmbeddedDbSqlRegistry embeddedDbSqlRegistry = new EmbeddedDbSqlRegistry();
		embeddedDbSqlRegistry.setDataSource(db);
		
		return embeddedDbSqlRegistry;
	};
	
	@After
	public void tearDown() {
		db.shutdown();
	}
	
	@Test
	public void transactionalUpdate() {
		checkFindResult("SQL1", "SQL2", "SQL3");	// 초기 상태를 확인한다. 이미 슈퍼클래스의 다른 테스트 메소드에서 확인하긴 했지만 트랜잭션 롤백 후의 결과와 비교돼서 이 테스트의 목적인 롤백 후의 상태는 처음과 동일하다는 것을 비교해서 보여주려고 넣었다.
		
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY9999!@#$", "Modified9999");	// 두 번째 SQL의 키를 존재하지 않는 것으로 지정한다. 이 때문에 테스트는 실패할 것이고, 그때 과연 롤백이 일어나는지 확인한다.
		
		try {
			sqlRegistry.updateSql(sqlmap);
			fail();
		}catch (SqlUpdateFailureException e) {
			
		}
		
		// 첫번째 SQL은 정상적으로 수정했지만 트랜잭션이 롤백되기 때문에 다시 변경 이전 상태로 돌아와야한다. 트랜잭션이 적용되지 않는다면 변경된 채로 남아서 테스트는 실패할 것이다.
		checkFindResult("SQL1", "SQL2", "SQL3");
	}
	
	public static void main(String args[]) throws Exception {
		JUnitCore.main("springbook.user.sqlservice.updatable.EmbeddedDbSqlRegistryTest");
	}
}
