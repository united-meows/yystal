package pisi.unitedmeows.yystal.sql;


import pisi.unitedmeows.yystal.YYStal;
import pisi.unitedmeows.yystal.logger.impl.YLogger;
import pisi.unitedmeows.yystal.parallel.IState;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YDatabaseClient {

	private boolean connected;
	private Connection connection;
	private final java.lang.Object actionLock = new java.lang.Object();


	public YDatabaseClient(String username, String password, String database, String host, int port) {
		try {
			synchronized(this) {
				if (connection != null && !connection.isClosed()) {
					return;
				}

				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=latin1&useConfigs=maxPerformance", username, password);
				connected = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			connected = false;
		}
	}

	public YDatabaseClient(String username, String password, String database, String host) {
		this(username, password, database, host, 3306);
	}

	public YDatabaseClient(String username, String password, String database) {
		this(username, password, database, "localhost", 3306);
	}

	public boolean execute(String sql) {
		PreparedStatement command = null;
		try {
			command = connection.prepareStatement(sql);
			return command.execute();
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean execute(YSQLCommand sql) {
		return execute(sql.getHooked());
	}

	public List<Map<String, Object>> select(YSQLCommand sql, String... columnNames) {
		return select(sql.getHooked(), columnNames);
	}

	public List<List<Object>> select(String sql) {
		synchronized(actionLock) {
			try {
				PreparedStatement command = connection.prepareStatement(sql);
				ResultSet resultSet = command.executeQuery();
				List<List<Object>> list = new ArrayList<>();
				final int columnCount = command.getMetaData().getColumnCount();
				while (resultSet.next()) {
					List<Object> dataList = new ArrayList<>();
					for (int i = 0; i < columnCount; i++) {
						dataList.add(i, resultSet.getObject(i));
					}
					list.add(dataList);
				}
				return list;
			} catch (SQLException e) {
				return new ArrayList<>();
			}
		}
	}

	public List<Map<String, Object>> select(String sql, String... columnNames) {
		synchronized(actionLock) {
			try {
				PreparedStatement command = connection.prepareStatement(sql);
				ResultSet resultSet = command.executeQuery();
				List<Map<String, Object>> list = new ArrayList<>();
				while (resultSet.next()) {
					Map<String, Object> dataMap = new HashMap<>();
					for (String column : columnNames) {
						dataMap.put(column, resultSet.getObject(column));
					}
					list.add(dataMap);
				}
				return list;
			} catch (SQLException e) {
				return new ArrayList<>();
			}
		}
	}

	public boolean connected() {
		return connected;
	}

	public Connection connection() {
		return connection;
	}

	/*	public boolean insertMulti(String tableName, List<List<Object>> dataList, String... columns) {
		String sql = "INSERT INTO " + tableName + "(" + String.join(",", columns) + " VALUES ";

	}*/
}
