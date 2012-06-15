package controllers;

import models.Answer;
import models.Question;
import play.mvc.With;
import controllers.aaa.AAAExceptionHandler;

@With({ Secure.class, AAAExceptionHandler.class })
public class Answers extends BaseController {

	public static void create(String questionId, String content) {
		Question question = Question.findById(questionId);
		Answer answer = new Answer(current(), question, content);
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
		render(answer);
	}

	public static void update(String id, String content) {
		Answer answer = Answer.findById(id);
		answer.content = content;
		answer.save();
		flash.success("Answer updated");
		Application.showQuestion(answer.question.getId().toString());
	}

	public static void delete(String id) {
		Answer answer = Answer.findById(id);
		answer.delete();
		flash.success("Answer deleted");
		Application.showQuestion(answer.question.getId().toString());
	}

}
