package sys;

import com.greenlaw110.utils.S;
import models.IUserProperty;
import models.User;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.modules.aaa.IAccount;
import play.modules.aaa.IPrivilege;
import play.modules.aaa.IRole;
import play.modules.aaa.utils.AAAFactory;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 5/01/12
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
@OnApplicationStart(async = true)
public class AAA extends Job<Object> {

    public static IRole ROLE_ADMIN;
    private static IPrivilege PRV_SUPERUSER;

    @Override
    public void doJob() throws Exception {
        ROLE_ADMIN = AAAFactory.role().getByName("admin");
        PRV_SUPERUSER = AAAFactory.privilege().getByName("superuser");
    }

    public static IAccount currentUser() {
        return AAAFactory.account().getCurrent();
    }

    public static boolean isSuperUser() {
        IAccount account = currentUser();
        if (null == account) return false;
        IPrivilege p = account.getPrivilege();
        if (null == p) return false;
        return PRV_SUPERUSER.compareTo(p) <= 0;
    }

    public static boolean isSuperUser(IAccount account) {
        if (null == account) return false;
        IPrivilege p = account.getPrivilege();
        if (null == p) return false;
        return PRV_SUPERUSER.compareTo(p) <= 0;
    }

    public static boolean isMyProperty(IUserProperty property) {
        IAccount user = currentUser();
        if (null == user) return false;
        User owner = property.owner();
        return S.isEqual(user.getName(), owner.username);
    }
}
