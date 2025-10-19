package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.encentral.entities.User;
import play.libs.Json;
import play.mvc.*;
import com.encentral.repositories.UserRepository;
import services.EmployeeService;
import services.AttendanceService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AdminController extends Controller {

    private final UserRepository userRepo;
    private final EmployeeService employeeService;
    private final AttendanceService attendanceService;

    @Inject
    public AdminController(UserRepository userRepo, EmployeeService employeeService, AttendanceService attendanceService) {
        this.userRepo = userRepo;
        this.employeeService = employeeService;
        this.attendanceService = attendanceService;
    }

    private Optional<User> userFromToken(String token) { return userRepo.findByToken(token); }

    private Result ensureAdmin(String token) {
        Optional<User> maybe = userFromToken(token);
        if (!maybe.isPresent()) return badRequest(Json.newObject().put("error", "Invalid token"));
        if (maybe.get().role != User.Role.ADMIN) return badRequest(Json.newObject().put("error", "You are not an Admin"));
        return null;
    }

    public Result addEmployee(Http.Request req) {
        ObjectNode body = (ObjectNode) req.body().asJson();
        if (body == null) return badRequest("json required");
        String token = req.getHeaders().get("X-User-Token").orElse("");
        Result adminCheck = ensureAdmin(token);
        if (adminCheck != null) return adminCheck;

        String firstname = body.get("firstname").asText();
        String lastname = body.get("lastname").asText();
        String email = body.get("email").asText();
        String defaultPassword = body.has("password") ? body.get("password").asText() : null;
        try {
            String pin = employeeService.addEmployee(firstname, lastname, email, defaultPassword);
            return ok(Json.newObject().put("pin", pin));
        } catch (RuntimeException ex) {
            return badRequest(Json.newObject().put("error", ex.getMessage()));
        }
    }

    public Result removeEmployee(Http.Request req, Long id) {
        String token = req.getHeaders().get("X-User-Token").orElse("");
        Result adminCheck = ensureAdmin(token);
        if (adminCheck != null) return adminCheck;
        try {
            employeeService.removeEmployee(id);
            return ok(Json.newObject().put("status", "deleted"));
        } catch (RuntimeException ex) {
            return badRequest(Json.newObject().put("error", ex.getMessage()));
        }
    }

    public Result getEmployees(Http.Request req) {
        String token = req.getHeaders().get("X-User-Token").orElse("");
        Result adminCheck = ensureAdmin(token);
        if (adminCheck != null) return adminCheck;

        List<User> employees = employeeService.getEmployees();
        return ok(Json.toJson(employees));
    }

    public Result getEmployeeDailyAttendance(Http.Request req, Long employeeId, String ymd) {
        String token = req.getHeaders().get("X-User-Token").orElse("");
        Result adminCheck = ensureAdmin(token);
        if (adminCheck != null) return adminCheck;

        Optional<User> maybe = userRepo.findById(employeeId);
        if (!maybe.isPresent()) return badRequest(Json.newObject().put("error", "Employee not found"));

        LocalDate date = LocalDate.parse(ymd); // expect yyyy-MM-dd
        List<?> list = attendanceService.getEmployeeAttendanceByDate(maybe.get(), date);
        return ok(Json.toJson(list));
    }
}
