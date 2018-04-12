package pl.dmcs.gpslocationsystem.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import pl.dmcs.gpslocationsystem.dao.UserDAO;
import pl.dmcs.gpslocationsystem.model.Coordinates;
import pl.dmcs.gpslocationsystem.model.User;

@Controller
public class HomeController {

	@Autowired
	private UserDAO userDAO;

	@RequestMapping(value = "/")
	public ModelAndView hello(ModelAndView model) throws IOException {
		model.setViewName("hello");
		return model;
	}

	@RequestMapping(value = "/authentication", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<Boolean> login() {
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);

	}

	@RequestMapping(value = "/insertNewSession", method = RequestMethod.POST, headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<Boolean> insertNewSession(@RequestBody User user) {
		try {
			userDAO.insertNewSession(user);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/registerUser", method = RequestMethod.POST, 
			headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<Boolean> registerUser(@RequestBody User user) {
		try {
			userDAO.registerUser(user);
			return new ResponseEntity<Boolean>(true, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/getCoordinatesFromSession/{username}/{idSession}", method = RequestMethod.GET, headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<List<List<Coordinates>>> getCoordinatesFromSession(@PathVariable("username") String username,
			@PathVariable("idSession") int idSession) {
		return new ResponseEntity<List<List<Coordinates>>>(userDAO.getCoordinatesFromSession(username, idSession),
				HttpStatus.OK);
	}

	@RequestMapping(value = "getSessionList/{username}", method = RequestMethod.GET, 
			headers = "Accept=application/json")
	@ResponseBody
	public ResponseEntity<List<User>> getSessionList(@PathVariable("username") 
	String username) {
		return new ResponseEntity<List<User>>(userDAO.getSessionsList(username), 
				HttpStatus.OK);
	}

}
