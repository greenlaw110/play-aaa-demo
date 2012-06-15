package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import play.Logger;
import play.data.validation.Required;
import play.jobs.Job;
import play.modules.aaa.AllowSystemAccount;
import play.modules.aaa.RequireAccounting;
import play.modules.aaa.RequireRight;
import play.modules.morphia.Model;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

@Entity("questions")
public class Question extends Model {

	@Required
	public String title;

	@Required
	public String content;

	@Required
	public User user;

	@Reference
	public List<Answer> answers;

	public Question() {
	}

	public Question(User user, String title, String content) {
		this.user = user;
		this.title = title;
		this.content = content;
	}

	public Date getCreatedAt() {
		return new Date(this._getCreated());
	}

	@OnAdd
	@RequireRight("ask-question")
	void checkAddAccess() {
		if (Logger.isTraceEnabled()) {
			Logger.trace("checking add access to Question");
		}
	}

	@OnUpdate
	@RequireRight("manage-my-question")
	@AllowSystemAccount
	void checkUpdateAccess() {
		if (Logger.isTraceEnabled()) {
			Logger.trace("checking update access to Question");
		}
	}

	@OnDelete
	@RequireRight("manage-my-question")
	@AllowSystemAccount
	@RequireAccounting("delete question")
	void cascadeDelete() {
		new Job() {
			public void doJob() throws Exception {
				if (answers != null) {
					for (Answer answer : new ArrayList<Answer>(answers)) {
						answer.delete();
					}
				}
			};
		}.now();
	}

}
