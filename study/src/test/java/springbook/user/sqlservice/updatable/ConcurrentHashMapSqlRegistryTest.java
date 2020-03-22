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
 *    - 7.5.2 내장형 데이터베이스를 이용한 SQL 레지스트리 만들기
 */
public class ConcurrentHashMapSqlRegistryTest extends ApstractUpdatableSqlRegistryTest{
	protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
		return new ConcurrentHashMapSqlRegistry();
	};
	
	public static void main(String args[]) throws Exception {
		JUnitCore.main("springbook.user.sqlservice.updatable.ConcurrentHashMapSqlRegistryTest");
	}
}
