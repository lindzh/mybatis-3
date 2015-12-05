package org.apache.ibatis.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

public class MapperDelegateProxy {
	
	public Object invoke(MapperProxy mapper,Object proxy, Method method, Object[] args) throws Throwable{
		return mapper.invoke(proxy, method, args);
	}
	
	<T> T newInstance(SqlSession sqlSession,Class<T> mapperInterface, Map<Method, MapperMethod> methodCache){
		 final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
		 return (T)Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return MapperDelegateProxy.this.invoke(mapperProxy, proxy, method, args);
			}
		});
	}
	
}
