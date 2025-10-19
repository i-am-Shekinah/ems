package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.encentral.entities.User;
import play.libs.Json;
import play.mvc.*;
import com.encentral.repositories.UserRepository;
import services.AttendanceService;
import services.EmployeeService;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmployeeController extends Controller {
    private final UserRepository userRepo;
    private final AttendanceService attendanceService;
    private final EmployeeService employeeService;

    @Inject
    public EmployeeController(UserRepository userRepo, AttendanceService attendanceService, EmployeeService employeeService) {
        this.userRepo = userRepo;
        this.attendanceService = attendanceService;
        this.employeeService = employeeService;
    }

    public Result markAttendance(Http.Request req) {
        String token = req.getHeaders().get("X-User-Token").orElse("");
        try {
            boolean ok = attendanceService.markAttendance(token);
            return ok ? Results.ok(Json.newObject().put("status", "attendance recorded")) :
                    Results.badRequest(Json.newObject().put("error", "could not record"));
        } catch (RuntimeException ex) {
            return badRequest(Json.newObject().put("error", ex.getMessage()));
        }
    }

    public Result updatePassword(Http.Request req) {
        String token = req.getHeaders().get("X-User-Token").orElse("");
        Optional<User> maybe = userRepo.findByToken(token);
        if (!maybe.isPresent()) return badRequest(Json.newObject().put("error", "Invalid token"));
        User u = maybe.get();
        ObjectNode body = (ObjectNode) req.body().asJson();
        if (body == null || !body.has("password")) return badRequest(Json.newObject().put("error", "password required"));
        String newPassword = body.get("password").asText();
        employeeService.updatePassword(u, newPassword);
        return ok(Json.newObject().put("status", "password updated"));
    }

    public Result getMyDailyAttendance(Http.Request req, String ymd) {
        String token = req.getHeaders().get("X-User-Token").orElse("");
        Optional<User> maybe = userRepo.findByToken(token);
        if (!maybe.isPresent()) return badRequest(Json.newObject().put("error", "Invalid token"));
        User u = maybe.get();
        LocalDate date = LocalDate.parse(ymd);
        return ok(Json.toJson(attendanceService.getEmployeeAttendanceByDate(u, date)));
    }
}
