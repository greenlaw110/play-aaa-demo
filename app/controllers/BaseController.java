package controllers;

import models.User;

import org.apache.commons.lang.StringUtils;

import play.mvc.Before;
import play.mvc.Controller;

public abstract class BaseController extends Controller {

	protected static User current() {
		User current = (User) renderArgs.get("current");
		if (current == null) {
			String username = session.get("username");
			if (StringUtils.isNotBlank(username)) {
				current = User.find("username", username).get();
				renderArgs.put("current", current);
			}
		}
		return current;
	}

	@Before
	static void prepareCurrentUser() {
		current();
	}

}
