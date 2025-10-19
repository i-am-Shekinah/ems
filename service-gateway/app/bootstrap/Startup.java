package bootstrap;

import com.encentral.entities.User;
import com.encentral.repositories.UserRepository;
import com.encentral.scaffold.commons.utils.SecurityUtil;

import javax.inject.Inject;
import javax.inject.Singleton;
import play.Environment;
import play.Logger;

@Singleton
public class Startup {
    @Inject
    public Startup(UserRepository userRepo, Environment env) {
        // ensure admin exists
        String adminEmail = "admin@encentral.com";
        userRepo.findByEmail(adminEmail).ifPresentOrElse(
                u -> Logger.info("Admin already present: " + adminEmail),
                () -> {
                    String hash = SecurityUtil.hashPassword("admin");
                    User admin = new User("Michael", "Olatunji", adminEmail, hash, User.Role.ADMIN, null);
                    userRepo.save(admin);
                    Logger.info("Default admin created: " + adminEmail + " / password: admin");
                }
        );
    }
}
