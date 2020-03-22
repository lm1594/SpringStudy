package springbook.user.sqlservice.updatable;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

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
 */
public class ConcurrentHashMapSqlRegistryTest {
	UpdatableSqlRegistry sqlRegistry;
	
	@Before
	public void setUp() {
		sqlRegistry = new ConcurrentHashMapSqlRegistry();
		sqlRegistry.registerSql("KEY1", "SQL1");				// 각 테스트 메소드에서 사용할 초기 SQL 정보를 미리 등록해둔다.
		sqlRegistry.registerSql("KEY2", "SQL2");
		sqlRegistry.registerSql("KEY3", "SQL3");
	}
	
	@Test
	public void find() {
		checkFindResult("SQL1", "SQL2", "SQL3");
	}
	
	private void checkFindResult(String expected1, String expected2, String expected3) {
		assertThat(sqlRegistry.findSql("KEY1"), is(expected1));
		assertThat(sqlRegistry.findSql("KEY2"), is(expected2));
		assertThat(sqlRegistry.findSql("KEY3"), is(expected3));
	}
	
	@Test(expected = SqlNotFoundException.class)
	public void unknownKey() {
		sqlRegistry.findSql("SQL9999!@#$");				// 주어진 키에 해당하는 SQL을 찾을 수 없을 때 예외가 발생하는지를 확인한다. 예외상황에 대한 테스트는 빼먹기가 쉽기 때문에 항상 의식적으로 넣으려고 노력해야한다.
	}
	
	@Test
	public void updateSingle() {
		// 하나의 SQL을 변경하는 기능에 대한 테스트다. 검증할 때는 변경된 SQL외의 나머지 SQL은 그대로인지도 확인해주는게 좋다.
		sqlRegistry.updateSql("KEY2", "Modified2");
		checkFindResult("SQL1", "Modified2", "SQL3");
	}
	
	@Test
	public void updateMulti() {
		// 한번에 여러 개의 SQL을 수정하는 기능을 검증한다.
		Map<String, String> sqlmap = new HashMap<String, String>();
		sqlmap.put("KEY1", "Modified1");
		sqlmap.put("KEY3", "Modified3");
		
		sqlRegistry.updateSql(sqlmap);
		checkFindResult("Modified1", "SQL2", "Modified3");
	}
	
	@Test(expected = SqlUpdateFailureException.class)
	public void updateWithNotExistingKey() {
		// 존재하지 않는 키의 SQL을 변경하려고 시도할 때 예외가 발생하는 것을 검증한다.
		sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
	}
	
	public static void main(String args[]) throws Exception {
		JUnitCore.main("springbook.user.sqlservice.updatable.ConcurrentHashMapSqlRegistryTest");
	}
}
