package models;

import com.google.code.morphia.Key;
import com.google.code.morphia.annotations.Entity;
import play.Logger;
import play.jobs.Job;
import play.modules.aaa.AllowSystemAccount;
import play.modules.aaa.RequireAccounting;
import play.modules.aaa.RequireRight;
import play.modules.morphia.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/06/12
 * Time: 6:11 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity(value="q", noClassnameStored = true)
public class Question extends Model implements IUserProperty {
    @Column("ttl")
    public String title;
    @Column("desc")
    public String content;
    @Column("oid")
    public String ownerId;

    public Question(User user, String title, String content) {
        ownerId = user.getId().toString();
        this.title = title;
        this.content = content;
    }

    @Override
    public User owner() {
        return User.findById(ownerId);
    }
    public List<Answer> answers() {
        return Answer.q("questionId", getId().toString()).asList();
    }

    @OnAdd
    @RequireRight("ask-question")
    public void checkAddAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking add access to Question");
    }

    @OnUpdate
    @RequireRight("manage-my-question")
    public void checkUpdateAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking update access to Question");
    }

    @RequireAccounting("delete profile")
    @RequireRight("manage-my-question")
    @AllowSystemAccount
    @OnDelete
    public void checkDeleteAccess() {
        if (Logger.isTraceEnabled()) Logger.trace("checking delete access to Question");
    }

    @Deleted
    public void cascadeDelete() {
        // create job to cascade delete so that @AllowSystemAccount could be leveraged
        new Job(){
            @Override
            public void doJob() throws Exception {
                Object id = Question.this.getId();
                Answer.q("questionId", id.toString()).delete();
            }
        }.now();
    }

    /**
     * Cannot use @OnBatchDeleted hook as by that time the MorphiaQuery object will always
     * return empty list since they are all deleted
     *
     * In product environment this kind of job is more tend to be exeucted by a background cron job
     * instead of built in system.
     *
     * No Access control on batch delete questions because we assume this is always triggered by system instead
     * of individual user. The same is true for Profile and Answers
     *
     * @param q
     */
    @OnBatchDelete
    public static void cascadeDelete(MorphiaQuery q) {
        List<Key<Question>> keys = q.asKeyList();
        final List<String> qidList = new ArrayList<String>();
        for (Key<Question> key: keys) {
            qidList.add(key.getId().toString());
        }
        // warn there might cause in-consistent data
        // as batch delete Questions might failed in which
        // case the answer shall not be removed. this another
        // reason it prefers to use a background cron job to
        // remove all orphan answers
        new Job() {
            @Override
            public void doJob() throws Exception {
                Answer.o().removeAll("questionId", qidList);
            }
        }.in(1); // do it in one second to allow Questions been deleted
    }

}
