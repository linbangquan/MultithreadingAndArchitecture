package com.lbq.concurrent.chapter01;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * RecordQuery中的query只负责将数据查询出来，然后调用RowHandler进行数据封装，至于将其封装成什么数据结构，那就得看你自己怎么处理了。
 * 好处是可以用query方法应对任何数据库的查询，返回结果的不同只会因为你传入RowHandler的不同而不同，
 * 同样RecordQuery只负责数据的获取，而RowHandler则负责数据的加工，职责分明，每个类的均功能单一，
 * 相信通过这个简单的示例，大家应该能够清楚Thread和Runnable之间的关系了。
 * @author 14378
 *
 */
public class RecordQuery {

	private final Connection connection;
	
	public RecordQuery(Connection connection) {
		this.connection = connection;
	}
	
	public <T> T query(RowHandler<T> handler, String sql, Object ... params) throws SQLException{
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			int index = 1;
			for(Object param : params) {
				stmt.setObject(index++, param);
			}
			ResultSet resultSet = stmt.executeQuery();
			return handler.handle(resultSet);//调用RowHandler
		}
	}
}
