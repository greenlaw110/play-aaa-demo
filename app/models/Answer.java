package models;

import java.util.ArrayList;

import play.Logger;
import play.data.validation.Required;
import play.jobs.Job;
import play.modules.aaa.AllowSystemAccount;
import play.modules.aaa.RequireAccounting;
import play.modules.aaa.RequireRight;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;

@Entity("answers")
public class Answer extends Model {

	@Required
	public String content;

	@Required
	public Question question;

	@Required
	public User user;

	public Answer() {
	}

	public Answer(User user, Question question, String content) {
		this.user = user;
		this.question = question;
		this.content = content;
	}

	@OnAdd
	@RequireRight("answer")
	void onAdd() {
		if (Logger.isTraceEnabled()) {
			Logger.trace("checking add access to Answer");
		}
	}

	@Added
	void cascadeAdd() {
		final Answer self = this;
		new Job() {
			@Override
			public void doJob() throws Exception {
				if (question.answers == null) {
					question.answers = new ArrayList<Answer>();
				}
				if (!question.answers.contains(self)) {
					question.answers.add(self);
					question.save();
				}
			}
		}.now();
	}

	@OnUpdate
	@RequireRight("manage-my-answer")
	void onUpdate() {
		if (Logger.isTraceEnabled()) {
			Logger.trace("checking update access to Answer");
		}
	}

	@OnDelete
	@RequireRight("manage-my-answer")
	@AllowSystemAccount
	@RequireAccounting("delete answer")
	void cascadeDelete() {
		new Job() {
			public void run() {
				question.answers.remove(this);
				question.save();
			};
		}.now();
	}

}
