package controllers;

import controllers.aaa.AAAExceptionHandler;
import controllers.filters.AuthentityChecker;
import models.Question;
import models.User;
import play.mvc.Controller;
import play.mvc.With;

@With({ Secure.class, AAAExceptionHandler.class, AuthentityChecker.class})
public class Questions extends Controller {

	public static void ask() {
		render();
	}

	public static void create(String title, String content) {
		Question question = new Question(User.current(), title, content);
		if (question.validateAndCreate()) {
			flash.success("Question created");
			Application.showQuestion(question.getId().toString());
		} else {
			params.flash();
			validation.keep();
			flash.keep();
			ask();
		}
	}

	public static void edit(String id) {
		Question question = Question.findById(id);
		render(question);
	}

	public static void update(String id, String title, String content) {
		Question question = Question.findById(id);
		question.title = title;
		question.content = content;
		question.save();
		flash.success("Question updated");
		Application.showQuestion(question.getId().toString());
	}

	public static void delete(String id) {
		Question question = Question.findById(id);
		question.delete();
		flash.success("Deleted successfully");
		Application.index();
	}

}
