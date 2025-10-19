package services;


import com.encentral.entities.User;
import com.encentral.repositories.UserRepository;
import com.encentral.scaffold.commons.utils.SecurityUtil;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class EmployeeService {
    private final UserRepository userRepo;

    @Inject
    public EmployeeService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String addEmployee(String firstname, String lastname, String email, String defaultPassword) {
        Optional<User> existing = userRepo.findByEmail(email);
        if (existing.isPresent()) throw new RuntimeException("Email already exists");

        String pin = SecurityUtil.generate4DigitPin();
        String hash = SecurityUtil.hashPassword(defaultPassword == null ? pin : defaultPassword);
        User emp = new User(firstname, lastname, email, hash, User.Role.EMPLOYEE, pin);
        userRepo.save(emp);
        return pin;
    }

    public void removeEmployee(Long id) {
        Optional<User> maybe = userRepo.findById(id);
        if (!maybe.isPresent()) throw new RuntimeException("Employee not found");
        User u = maybe.get();
        if (u.role != User.Role.EMPLOYEE) throw new RuntimeException("Not an employee");
        userRepo.delete(u);
    }

    public List<User> getEmployees() {
        return userRepo.getAllEmployees();
    }

    public void updatePassword(User user, String newPassword) {
        user.passwordHash = SecurityUtil.hashPassword(newPassword);
        // clear pin if they set a password? we can keep pin; both will authorize for sign in
        userRepo.save(user);
    }
}

