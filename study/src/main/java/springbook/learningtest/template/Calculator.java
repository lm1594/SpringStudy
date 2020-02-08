package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 토비의스프링
 * @author 이경민
 * @since 2020.02.08
 * @history
 *  3.5.3 템플릿 / 콜백의 응용
 */
public class Calculator {
	
	/**
	 * 합계 구하기 / 템플릿 콜백
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public Integer calcSum(String filepath) throws IOException {
		LineCallback sumCallback = 
				new LineCallback() {
					@Override
					public Integer doSomethingWithLine(String line, Integer value) {
						// TODO Auto-generated method stub
						return value + Integer.valueOf(line);
					}
				};
		return lineReadTemplate(filepath, sumCallback, 0);
	}
	
	/**
	 * 곱 구하기 / 템플릿 콜백 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public Integer calcMultiply(String filepath) throws IOException{
		LineCallback sumCallback = 
				new LineCallback() {
					@Override
					public Integer doSomethingWithLine(String line, Integer value) {
						// TODO Auto-generated method stub
						return value * Integer.valueOf(line);
					}
				};
		return lineReadTemplate(filepath, sumCallback, 1);
	}
	
	/**
	 * 템플릿 메소드
	 * @param filepath
	 * @param callback
	 * @return
	 * @throws IOException
	 */
	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) 
			throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			Integer ret = callback.doSomethingWithReader(br);
			return ret;
		}catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) {
				try{br.close();}
				catch(IOException e) {System.out.println(e.getMessage());} 
			}
		}
	}

	/**
	 * 라인별 작업을 정의한 콜백 메소드
	 * @param filepath
	 * @param callback
	 * @param initVal
	 * @return
	 * @throws IOException
	 *  3.5.3 템플릿 / 콜백의 응용
	 *  	- 템플릿 / 콜백의 재설계
	 */
	public Integer lineReadTemplate(String filepath, LineCallback callback, int initVal) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			Integer res = initVal;
			String line = null;
			while((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		}catch(IOException e) {
			System.out.println(e.getMessage());
			throw e;
		}finally {
			if(br != null) {
				try{br.close();}
				catch(IOException e) {System.out.println(e.getMessage());} 
			}
		}
	}
}
