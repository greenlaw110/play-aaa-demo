package controllers;

import com.greenlaw110.rythm.utils.S;
import controllers.filters.AuthentityChecker;
import org.apache.commons.lang.StringUtils;

import models.User;
import play.modules.aaa.IAccount;
import play.modules.aaa.RequireRight;
import play.modules.aaa.utils.AAAFactory;
import play.mvc.Controller;
import play.mvc.With;
import controllers.aaa.AAAExceptionHandler;
import sys.AAA;

@With({ Secure.class, AAAExceptionHandler.class, AuthentityChecker.class })
public class Users extends Controller {

	public static void editForm() {
		User current = User.current();
		render(current);
	}

	public static void saveUpdate(String fullName, String oriPassword, String newPassword) {
		User user = User.current();
		user.fullName = fullName;
		if (!S.isEmpty(newPassword)) {
            IAccount account = AAAFactory.account().authenticate(user.username, oriPassword);
            if (null == account) {
                flash.error("password not match");
                editForm();
            }
            account.setPassword(newPassword);
		}
		flash.success("user profile updated");
		editForm();
	}

}
