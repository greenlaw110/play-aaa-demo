package controllers;

import org.apache.commons.lang.StringUtils;

import models.User;
import play.mvc.With;
import controllers.aaa.AAAExceptionHandler;

@With({ CRUD.class, Secure.class, AAAExceptionHandler.class })
public class Users extends BaseController {

	public static void editMine() {
		User current = current();
		render(current);
	}

	public static void update(String fullName, String oriPassword, String newPassword) {
		User current = current();
		current.fullName = fullName;
		if (StringUtils.isNotBlank(oriPassword)) {
			current.checkPassword(oriPassword);
		}
	}

}
