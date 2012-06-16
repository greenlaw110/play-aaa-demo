package sys;

import com.google.inject.AbstractModule;
import com.greenlaw110.play.api.IApplication;
import models.User;

public class GuiceBindings extends AbstractModule {

    @Override
    protected void configure() {
        bind(IApplication.class).to(App.class);
    }


}
