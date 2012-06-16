package models;

import com.google.code.morphia.annotations.Entity;
import play.Logger;
import play.modules.aaa.AllowSystemAccount;
import play.modules.aaa.RequireAccounting;
import play.modules.aaa.RequirePrivilege;
import play.modules.aaa.RequireRight;
import play.modules.morphia.Model;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/06/12
 * Time: 6:11 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(value="a", noClassnameStored = true)
public class Answer extends Model implements IUserProperty {
    @Column("ctnt")
    public String content;
    @Column("q")
    public String questionId;

    public Answer(User user, Question question, String content) {
        ownerId = user.getId().toString();
        questionId = question.getId().toString();
        this.content = content;
    }

    public Question question() {
        return Question.findById(questionId);
    }
    public String ownerId;
    @Override
    public User owner() {
        return User.findById(ownerId);
    }

    @OnAdd
    @RequireRight("answer-question")
    void checkAddAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking add access to Answer");
    }

    @OnUpdate
    @RequireRight("manage-my-answer")
    void checkUpdateAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking update access to Answer");
    }

    @RequireAccounting("delete answer")
    @RequirePrivilege("sys-admin")
    @RequireRight("manage-my-answer")
    @AllowSystemAccount
    @OnDelete
    void checkDeleteAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking delete access to Answer");
    }

}
