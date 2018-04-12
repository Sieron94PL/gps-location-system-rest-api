package pl.dmcs.gpslocationsystem.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import pl.dmcs.gpslocationsystem.model.Coordinates;
import pl.dmcs.gpslocationsystem.model.User;

public class UserDAOImpl implements UserDAO {

	private JdbcTemplate jdbcTemplate;

	public UserDAOImpl(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<List<Coordinates>> getCoordinatesFromSession(String username, int idSession) {
		String sql = "SELECT LAT, LNG FROM USERS, TRACKS, COORDINATES " + "WHERE USERS.USERNAME = '" + username
				+ "' AND TRACKS.ID_TRACK = COORDINATES.ID_TRACK " + "AND TRACKS.ID_SESSION = " + idSession
				+ " AND USERS.USERNAME = TRACKS.USERNAME";
		List<Coordinates> coordinates = jdbcTemplate.query(sql, new RowMapper<Coordinates>() {

			@Override
			public Coordinates mapRow(ResultSet rs, int rowNum) throws SQLException {
				Coordinates coordinate = new Coordinates();
				coordinate.setLat(rs.getDouble("lat"));
				coordinate.setLng(rs.getDouble("lng"));
				return coordinate;
			}

		});

		List<List<Coordinates>> coordinatesList = new ArrayList<List<Coordinates>>();
		coordinatesList.add(coordinates);

		return coordinatesList;

	}

	@Override
	public List<User> getSessionsList(String username) {
		String sql = "SELECT ID_SESSION, SESSION_DATE FROM USERS, TRACKS WHERE TRACKS.USERNAME = USERS.USERNAME AND USERS.USERNAME = '"
				+ username + "'";
		List<User> users = jdbcTemplate.query(sql, new RowMapper<User>() {

			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				User user = new User();
				user.setIdSession(rs.getInt("id_session"));
				user.setDate(rs.getString("session_date"));
				return user;
			}
		});
		return users;
	}

	@Override
	public void insertNewSession(final User user) {

		String sqlGetLastSessionId = "SELECT IFNULL(MAX(ID_SESSION), 0) FROM USERS, TRACKS WHERE USERS.USERNAME = ? AND USERS.USERNAME = TRACKS.USERNAME";
		int lastSessionId = jdbcTemplate.queryForObject(sqlGetLastSessionId, Integer.class, user.getUsername());
		int currentSessionId = lastSessionId + 1;

		String sqlGetLastTrackId = "SELECT IFNULL(MAX(ID_TRACK),0) FROM TRACKS";
		int lastTrackId = jdbcTemplate.queryForObject(sqlGetLastTrackId, Integer.class);
		final int currentTrackId = lastTrackId + 1;

		String sqlInsertNewSession = "INSERT INTO TRACKS (ID_SESSION, USERNAME, SESSION_DATE) VALUES (?,?,?)";
		jdbcTemplate.update(sqlInsertNewSession, currentSessionId, user.getUsername(), user.getDate());

		String sql = "INSERT INTO COORDINATES (ID_TRACK, LNG, LAT) VALUES (?,?,?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {

				Coordinates coordinate = user.getCoordinates().get(i);
				ps.setInt(1, currentTrackId);
				ps.setDouble(2, coordinate.getLng());
				ps.setDouble(3, coordinate.getLat());
			}

			@Override
			public int getBatchSize() {
				return user.getCoordinates().size();
			}
		});
	}

	@Override
	public void registerUser(User user) {
		String sqlRegisterUser = "INSERT INTO USERS (USERNAME, PASS, ENABLED, MAIL) VALUES (?,?,?,?)";
		jdbcTemplate.update(sqlRegisterUser, user.getUsername(), user.getPassword(), user.getEnabled(), user.getMail());
		String sqlAddRoleToUser = "INSERT INTO USER_ROLES (USERNAME, ROLE) VALUES (?,'ROLE_USER')";
		jdbcTemplate.update(sqlAddRoleToUser, user.getUsername());
	}

}
