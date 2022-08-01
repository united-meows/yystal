package pisi.unitedmeows.yystal.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pisi.unitedmeows.yystal.utils.IDisposable;

public class YDatabaseClient implements IDisposable {
	private boolean connected;
	private Connection connection;
	private final java.lang.Object actionLock = new java.lang.Object();
	private HashMap<String, List<String>> tableColumnsCache;

	public YDatabaseClient(String username, String password, String database, String host, int port) {
		try {
			synchronized (this) {
				if (connection != null && !connection.isClosed()) return;
				connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?characterEncoding=latin1&useConfigs=maxPerformance", username, password);
				connected = true;
			}
			tableColumnsCache = new HashMap<>();
		}
		catch (Exception ex) {
			connected = false;
			ex.printStackTrace();
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
		}
		catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean execute(YSQLCommand sql) {
		return execute(sql.getHooked());
	}

	public List<Map<String, Object>> select(YSQLCommand sql, String... columnNames) {
		return select(sql.getHooked(), columnNames);
	}

	public List<String> dbColumnsNoCache(String table) {
		synchronized (actionLock) {
			List<Map<String, Object>> select = select(new YSQLCommand("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=^").putString(table), "TABLE_CATALOG", "TABLE_SCHEMA", "TABLE_NAME",
						"COLUMN_NAME");
			List<String> columns = new ArrayList<>();
			for (int i = 0; i < select.size(); i++) {
				columns.add((String) select.get(i).get("COLUMN_NAME"));
			}
			return columns;
		}
	}

	public List<String> dbColumns(String table) {
		List<String> columns = tableColumnsCache.getOrDefault(table, null);
		if (columns == null) {
			tableColumnsCache.put(table, dbColumnsNoCache(table));
			return columns;
		}
		return tableColumnsCache.get(table);
	}

	public List<List<Object>> select(String sql) {
		synchronized (actionLock) {
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
			}
			catch (SQLException e) {
				e.printStackTrace();
				return new ArrayList<>();
			}
		}
	}

	public List<Map<String, Object>> select(String sql, String... columnNames) {
		synchronized (actionLock) {
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
			}
			catch (SQLException e) {
				e.printStackTrace();
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

	@Override
	public void close() {
		try {
			connection.close();
		}
		catch (Exception ex) {}
		connected = false;
		tableColumnsCache.clear();
	}
	/*	public boolean insertMulti(String tableName, List<List<Object>> dataList, String... columns) {
		String sql = "INSERT INTO " + tableName + "(" + String.join(",", columns) + " VALUES ";
	
	}*/
}
