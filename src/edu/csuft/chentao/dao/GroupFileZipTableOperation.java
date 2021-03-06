package edu.csuft.chentao.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import edu.csuft.chentao.pojo.req.FileZip;
import edu.csuft.chentao.util.OperationUtil;

/**
 * @author csuft.chentao
 *
 * 2017年5月1日 上午9:52:50
 */
/**
 * group_file_zip表处理类
 * 
 * @author csuft.chentao
 *
 *         2017年5月1日 上午11:58:45
 */
public class GroupFileZipTableOperation {

	/**
	 * 插入数据
	 */
	public static void insert(GroupFileZipTable table) {
		Connection connection = DaoConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "insert into " + GroupFileZipTable.INSERT_ALL_FIELD
					+ " values(?,?,?,?,?,?)";
			ps = connection.prepareStatement(sql);
			// filename,serial_number,group_id,user_id,nickname,time
			ps.setString(1, table.getFileName());
			ps.setString(2, table.getSerialNumber());
			ps.setInt(3, table.getGroupId());
			ps.setInt(4, table.getUserId());
			ps.setString(5, table.getNickname());
			ps.setString(6, table.getTime());

			if (ps.executeUpdate() > 0) { // 插入成功
				// TODO
				System.out.println("文件上传成功，文件名->" + table.getFileName()
						+ "序列号->" + table.getSerialNumber());
			} else {
				// TODO
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OperationUtil.closeDataConnection(ps, rs);
		}
	}

	/**
	 * 根据群id，查询该群下所有的文件列表
	 * 
	 * @param groupId
	 *            群id
	 */
	public static List<FileZip> queryAll(int groupId) {
		List<FileZip> fileZipList = new ArrayList<FileZip>();
		Connection connection = DaoConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			// filename,serial_number,group_id,user_id,nickname,time
			String sql = "select " + GroupFileZipTable.QUERY_ALL_FIELD
					+ " from " + GroupFileZipTable.TABLE_NAME + " where "
					+ GroupFileZipTable.GROUP_ID + " = ? order by "
					+ GroupFileZipTable.TIME + " desc";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, groupId);
			rs = ps.executeQuery();
			// 查询得到所有数据
			while (rs.next()) {
				/*
				 * 构造成GroupFileZipTable对象
				 */
				GroupFileZipTable table = new GroupFileZipTable();
				table.setFileName(rs.getString(1));
				table.setSerialNumber(rs.getString(2));
				table.setGroupId(rs.getInt(3));
				table.setUserId(rs.getInt(4));
				table.setNickname(rs.getString(5));
				table.setTime(rs.getString(6));

				// 再转换成FileZip对象
				FileZip fileZip = GroupFileZipTable.copyToFileZip(table, null,
						false);
				fileZipList.add(fileZip);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OperationUtil.closeDataConnection(ps, rs);
		}

		return fileZipList;
	}

	/**
	 * 根据serialNumber得到数据表中信息
	 */
	public static FileZip queryBySerialNumber(String serialNumber) {
		FileZip fz = new FileZip();
		Connection connection = DaoConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String keys = String.format("%s,%s", GroupFileZipTable.FILENAME,
					GroupFileZipTable.GROUP_ID);
			String sql = "select " + keys + " from "
					+ GroupFileZipTable.TABLE_NAME + " where "
					+ GroupFileZipTable.SERIAL_NUMBER + " = ?";
			ps = connection.prepareStatement(sql);
			ps.setString(1, serialNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				String fileName = rs.getString(1);
				int groupId = rs.getInt(2);

				fz.setFileName(fileName);
				fz.setGroupId(groupId);
				fz.setSerialNumber(serialNumber);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OperationUtil.closeDataConnection(ps, rs);
		}

		return fz;
	}

	/**
	 * 根据SerialNumber删除数据
	 */
	public static boolean deleteBySerialNumber(String serialNumber, int groupId) {
		Connection connection = DaoConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "delete from " + GroupFileZipTable.TABLE_NAME
					+ " where " + GroupFileZipTable.GROUP_ID + " = ? and "
					+ GroupFileZipTable.SERIAL_NUMBER + " = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, groupId);
			ps.setString(2, serialNumber);
			//执行删除操作
			if (ps.executeUpdate() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OperationUtil.closeDataConnection(ps, rs);
		}

		return false;
	}

	/**
	 * 根据群id和序列号，得到该文件的上传者id
	 * 
	 * @param serialNumber
	 *            文件的序列号
	 * @param groupId
	 *            群id
	 * @return 用户id，文件的上传者
	 */
	public static int getFileOwnerBySerialNumber(String serialNumber,
			int groupId) {
		Connection connection = DaoConnection.getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		int userId = -1;

		try {
			String sql = "select " + GroupFileZipTable.USER_ID + " from "
					+ GroupFileZipTable.TABLE_NAME + " where "
					+ GroupFileZipTable.GROUP_ID + " = ? and "
					+ GroupFileZipTable.SERIAL_NUMBER + " = ?";
			ps = connection.prepareStatement(sql);
			ps.setInt(1, groupId);
			ps.setString(2, serialNumber);
			rs = ps.executeQuery();
			if (rs.next()) {
				userId = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			OperationUtil.closeDataConnection(ps, rs);
		}

		return userId;
	}

}
