package springbook.user.sqlservice.oxm;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Unmarshaller;								// JAXB API 등에서 사용하는 언마샬러와 클래스 이름이 같으므로 임포트할 때 주의해야 한다.
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

/**
 * 토비의 스프링
 * @author 이경민
 * @since 2020.03.18
 * @history
 * 7장 스프링 핵심기술의 응용
 *   7.3장 서비스 추상화 적용
 *    - 7.3.1 OXM 서비스 추상화
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration														// 클래스 이름 + '-context.xml' 파일을 사용하는 애플리케이션 컨텍스트로 만들어서 테스트가 사용할 수 있게 해준다.
public class OxmTest {

	@Autowired Unmarshaller unmarshaller;									// 스프링 테스트가 테스트용 애플리케이션 컨텍스트에서 Unmarshaller 인터페이스 타입의 빈을 찾아서 테스트가 시작되기 전에 이 변수에 넣어준다.
	
	@Test
	public void readSqlmap() throws XmlMappingException, IOException{
		Source xmlSource = new StreamSource(
					getClass().getResourceAsStream("sqlmap.xml")			// InputStream을 이용하는 Source 타입의 StreamSource를 만든다.
				);
		
		Sqlmap sqlmap = (Sqlmap) this.unmarshaller.unmarshal(xmlSource);	// 어떤 OXM 기술이든 언마샬은 이 한 줄이면 끝이다.
		
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
