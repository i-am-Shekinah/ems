package modules;

import com.google.inject.AbstractModule;
import bootstrap.Startup;

public class StartupModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Startup.class).asEagerSingleton();
    }
}
