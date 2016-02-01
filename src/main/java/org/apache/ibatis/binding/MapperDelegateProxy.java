package org.apache.ibatis.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

/**
 * 
 * @author lindezhi
 * 2016��2��1�� ����5:37:31
 */
public class MapperDelegateProxy {
	
	/**
	 * Ĭ�Ϸ����ص���ִ��SQL
	 * @param mapper
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	public Object invoke(MapperProxy mapper,Object proxy, Method method, Object[] args) throws Throwable{
		return mapper.invoke(proxy, method, args);
	}
	
	/**
	 * ��̬�������ɶ���
	 * @param sqlSession
	 * @param mapperInterface
	 * @param methodCache
	 * @return
	 */
	<T> T newInstance(SqlSession sqlSession,Class<T> mapperInterface, Map<Method, MapperMethod> methodCache){
		 final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
		 T result = (T)Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return MapperDelegateProxy.this.invoke(mapperProxy, proxy, method, args);
			}
		});
		this.newInstanceCallback(mapperInterface);
		return result;
	}
	
	/**
	 * ���init�ص���֧�ֻ�����ʼ��
	 * �޸� no mapper found for table:partner
	 * @param mapperInterface
	 */
	public void newInstanceCallback(Class<?> mapperInterface){
		
	}
	
}
