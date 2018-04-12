package pl.dmcs.gpslocationsystem.dao;

import java.util.List;

import pl.dmcs.gpslocationsystem.model.Coordinates;
import pl.dmcs.gpslocationsystem.model.User;

public interface UserDAO {

	public void insertNewSession(User user);

	public List<List<Coordinates>> getCoordinatesFromSession(String username, int idSession);

	public void registerUser(User user);

	public List<User> getSessionsList(String username);

}