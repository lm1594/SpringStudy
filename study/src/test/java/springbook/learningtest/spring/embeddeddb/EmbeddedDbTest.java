package springbook.learningtest.spring.embeddeddb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * 토비의스프링
 * @author 이경민
 * @history
 *  7장 스프링 핵심 기술의 응용
 *   7.5장 DI를 이용해 다양한 구현 방법 적용하기
 *    - 7.5.2 내장형 데이터베이스를 이용한 SQL 레지스트리 만들기
 */
public class EmbeddedDbTest {
	EmbeddedDatabase db;
	
	SimpleJdbcTemplate template;				// JdbcTemplate을 더 편리하게 사용할 수 있게 확장한 템플릿
	
	@Before
	public void setUp() {
		db = new EmbeddedDatabaseBuilder()
		.setType(EmbeddedDatabaseType.HSQL)		// HSQL, DERBY, H2 세 가지 중 하나를 선택할 수 있다. 초기화 SQL이 호환만 된다면 DB 종류는 언제든지 바꿀 수 있다.
		.addScript("classpath:/springbook/learningtest/spring/embeddeddb/schema.sql")
		.addScript("classpath:/springbook/learningtest/spring/embeddeddb/data.sql")
		.build();
		
		template = new SimpleJdbcTemplate(db);	// EmbeddedDatabase는 DataSource의 서브 인터페이스이므로 DataSource를 필요로 하는 SimpleJdbcTemplate을 만들 때 사용할 수 있다.
	}
	
	@After
	public void tearDown() {
		// 매 테스트를 진행한 뒤에 DB를 종료한다. 내장형 메모리 DB는 따로 저장하지 않는 한 애플리케이션과 함께 매번 새롭게 DB가 만들어지고 제거되는 생명주기를 갖는다.
		db.shutdown();
	}
	
	@Test
	public void initData() {
		// 초기화 스크립트를 통해 등록된 데이터를 검증하는 테스트다.
		assertThat(template.queryForInt("select count(*) from sqlmap"), is(2));
		
		List<Map<String, Object>> list = template.queryForList("select * from sqlmap order by key_");
		assertThat((String)list.get(0).get("key_"), is("KEY1"));
		assertThat((String)list.get(0).get("sql_"), is("SQL1"));
		assertThat((String)list.get(1).get("key_"), is("KEY2"));
		assertThat((String)list.get(1).get("sql_"), is("SQL2"));
	}
	
	@Test
	public void insert() {
		template.update("insert into sqlmap(key_, sql_) values(?,?)", "KEY3", "SQL3");
		assertThat(template.queryForInt("select count(*) from sqlmap"), is(3));
	}
	
	public static void main(String args[]) throws Exception {
		JUnitCore.main("springbook.learningtest.spring.embeddeddb.EmbeddedDbTest");
	}

}
