package controllers;

import controllers.aaa.AAAExceptionHandler;
import play.mvc.With;

@With({Secure.class, AAAExceptionHandler.class})
public class Profiles extends CRUD {
}
