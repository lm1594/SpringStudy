package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;

/**
 * 토비의스프링
 * @author 이경민
 * @since 2020.02.08
 * @history
 *  3.5.3 템플릿 / 콜백의 응용
 */
public class CalcSumTest {
	
	Calculator calculator;
	String numFilepath;

	@Before
	public void setUp() {
		calculator = new Calculator();
		numFilepath = "numbers.txt";
	}
	
	/**
	 * 합계를 구하는 테스트
	 * @throws IOException
	 */
	@Test
	public void sumOfNumbers() throws IOException {
		int sum = calculator.calcSum(getClass().getResource(this.numFilepath).getPath());
		assertThat(sum, is(10));
	}
	
	/**
	 * 곱을 구하는 테스트
	 * @throws IOException
	 */
	@Test
	public void multiplyOfNumbers() throws IOException {
		assertThat(calculator.calcMultiply(getClass().getResource(this.numFilepath).getPath()), is(24));
	}
	
	/**
	 * 제네릭스를 이용한 콜백 인터페이스
	 * @throws IOException
	 */
	@Test
	public void concatenateStrings() throws IOException {
		assertThat(calculator.concatenate(getClass().getResource(this.numFilepath).getPath()), is("1234"));
	}
	
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.learningtest.template.CalcSumTest");
	}
}
