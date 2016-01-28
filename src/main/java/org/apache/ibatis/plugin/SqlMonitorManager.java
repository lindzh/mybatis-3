package org.apache.ibatis.plugin;

import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * 提供统计使用 SQL
 * @author lindezhi
 * 2016年1月28日 上午11:51:28
 */
@Intercepts({
	@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,ResultHandler.class }),
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class,ResultHandler.class, CacheKey.class, BoundSql.class }) })

public class SqlMonitorManager implements Interceptor {

	private static final Log sqlStatLogger = LogFactory.getLog("mysqlStatLogger");
	
	private boolean showSql = true;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		if (!showSql) {
			return invocation.proceed();
		}

		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		if (mappedStatement == null) {
			return invocation.proceed();
		}

		String sqlId = mappedStatement.getId();
		
		String sourceSql = null;
		BoundSql boundSql = mappedStatement.getBoundSql();
		if(boundSql!=null){
			sourceSql = boundSql.getSql();
		}
		
		Object returnValue = null;
		int resultCode = 0;
		long start = System.currentTimeMillis();
		try {
			returnValue = invocation.proceed();
		} catch (Exception e) {
			resultCode = 1;
			throw e;
		} finally {
			long end = System.currentTimeMillis();
			long time = end - start;
			if(sourceSql!=null){
				sqlStatLogger.info(sqlId + "," + resultCode + "," + time+"  [SQL]:{"+sourceSql+"}");
			}else{
				sqlStatLogger.info(sqlId + "," + resultCode + "," + time);
			}
		}
		return returnValue;
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}
	
	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	@Override
	public void setProperties(Properties properties) {
		if (properties == null) {
			return;
		}
		if (properties.containsKey("show_sql")) {
			String value = properties.getProperty("show_sql");
			if (Boolean.TRUE.toString().equals(value)) {
				this.showSql = true;
			}
		}
	}

}
