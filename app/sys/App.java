package sys;

import com.greenlaw110.play.api.IApplication;
import com.greenlaw110.play.api.IUser;
import controllers.Security;
import models.User;
import play.modules.aaa.IAccount;
import play.mvc.Http;
import play.mvc.Scope;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 15/06/12
 * Time: 10:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class App implements IApplication {
    @Override
    public IUser currentUser() {
        IUser user = (IUser)Http.Request.current().args.get("user");
        if (null == user) {
            String username = Scope.Session.current().get("username");
            if (null == username) return null;
            user = User.findByUsername(username);
            if (null != user) Http.Request.current().args.put("user", user);
        }
        return user;
    }
}
