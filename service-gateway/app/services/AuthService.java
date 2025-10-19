package services;


import com.encentral.entities.User;
import com.encentral.repositories.UserRepository;
import com.encentral.scaffold.commons.utils.SecurityUtil;

import javax.inject.Inject;
import java.util.Optional;

public class AuthService {
    private final UserRepository userRepo;

    @Inject
    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User signInEmployee(String email, String pinOrPassword) {
        Optional<User> maybe = userRepo.findByEmail(email);
        if(!maybe.isPresent()) throw new RuntimeException("Invalid credentials");
        User u = maybe.get();

        // employees sign in using email + pin OR password if they updated
        boolean ok = false;
        if (u.role == User.Role.EMPLOYEE) {
            if (u.pin != null && u.pin.equals(pinOrPassword)) ok = true;
            if (SecurityUtil.verifyPassword(pinOrPassword, u.passwordHash)) ok = true;
        } else { // admin sign in uses password
            if (SecurityUtil.verifyPassword(pinOrPassword, u.passwordHash)) ok = true;
        }

        if (!ok) throw new RuntimeException("Invalid credentials");
        // return existing token (no new token rotation here). If you want to rotate, generate a new UUID and save.
        return u;
    }
}

