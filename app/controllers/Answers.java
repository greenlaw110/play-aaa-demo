package controllers;

import controllers.filters.AuthentityChecker;
import models.Answer;
import models.Question;
import models.User;
import play.mvc.Controller;
import play.mvc.With;
import controllers.aaa.AAAExceptionHandler;

@With({ Secure.class, AAAExceptionHandler.class, AuthentityChecker.class})
public class Answers extends Controller {

	public static void create(String questionId, String content) {
		Question question = Question.findById(questionId);
		notFoundIfNull(question);
		Answer answer = new Answer(User.current(), question, content);
		if (answer.validateAndCreate()) {
			flash.success("Answered successfully");
			Application.showQuestion(question.getId().toString());
		} else {
			flash.keep();
			validation.keep();
			params.flash();
			Application.showQuestion(questionId);
		}
	}

	public static void edit(String id) {
		Answer answer = Answer.findById(id);
		notFoundIfNull(answer);
		render(answer);
	}

	public static void update(String id, String content) {
		Answer answer = Answer.findById(id);
		notFoundIfNull(answer);
		answer.content = content;
		answer.save();
		flash.success("Answer updated");
		Application.showQuestion(answer.questionId);
	}

	public static void delete(String id) {
		Answer answer = Answer.findById(id);
		notFoundIfNull(answer);
		answer.delete();
		flash.success("Answer deleted");
		Application.showQuestion(answer.questionId);
	}

}
