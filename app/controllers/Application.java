package controllers;

import java.util.List;

import models.Question;
import models.User;

public class Application extends BaseController {

	public static void index() {
		List<Question> questions = Question.findAll();
		render(questions);
	}

	public static void showQuestion(String id) {
		Question question = Question.findById(id);
		render(question);
	}

	public static void register() {
		render();
	}

	public static void createUser(String username, String password, String fullName) {
		User user = new User(username, password, fullName);
		user.save();
		index();
	}

}