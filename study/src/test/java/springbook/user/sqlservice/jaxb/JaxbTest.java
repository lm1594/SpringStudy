package springbook.user.sqlservice.jaxb;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.2장 XML 파일 매핑
 *    - 7.2.1 XML 파일 매핑
 */
public class JaxbTest {

	@Test
	public void readSqlmap() throws JAXBException, IOException{
		String contextPath = Sqlmap.class.getPackage().getName();
		JAXBContext context = JAXBContext.newInstance(contextPath);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		
		Sqlmap sqlmap = (Sqlmap) unmarshaller.unmarshal(getClass().getResourceAsStream("sqlmap.xml"));
		
		List<SqlType> sqlList = sqlmap.getSql();
		
		assertThat(sqlList.size()				, is(3));
		assertThat(sqlList.get(0).getKey()		, is("add"));
		assertThat(sqlList.get(0).getValue()	, is("insert"));
		assertThat(sqlList.get(1).getKey()		, is("get"));
		assertThat(sqlList.get(1).getValue()	, is("select"));
		assertThat(sqlList.get(2).getKey()		, is("delete"));
		assertThat(sqlList.get(2).getValue()	, is("delete"));
	}
	
	public static void main(String args[]) throws Exception {
		JUnitCore.main("springbook.user.sqlservice.jaxb.JaxbTest");
	}
}
