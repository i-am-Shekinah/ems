package services;


import com.encentral.entities.Attendance;
import com.encentral.entities.User;
import com.encentral.repositories.UserRepository;
import com.encentral.repositories.AttendanceRepository;

import javax.inject.Inject;
import java.time.*;
import java.util.List;
import java.util.Optional;

public class AttendanceService {
    private final AttendanceRepository attendanceRepo;
    private final UserRepository userRepo;
    private final ZoneId zoneId;
    private final int startHour;
    private final int endHour;

    @Inject
    public AttendanceService(AttendanceRepository attendanceRepo, UserRepository userRepo, com.typesafe.config.Config config) {
        this.attendanceRepo = attendanceRepo;
        this.userRepo = userRepo;
        String tz = config.hasPath("attendance.timezone") ? config.getString("attendance.timezone") : "UTC";
        this.zoneId = ZoneId.of(tz);
        this.startHour = config.hasPath("attendance.startHour") ? config.getInt("attendance.startHour") : 9;
        this.endHour = config.hasPath("attendance.endHour") ? config.getInt("attendance.endHour") : 17;
    }

    /**
     * Example requested method signature
     */
    public boolean markAttendance(String userToken) {
        Optional<User> maybe = userRepo.findByToken(userToken);
        if (!maybe.isPresent()) throw new RuntimeException("Invalid token");
        User user = maybe.get();

        if (user.role != User.Role.EMPLOYEE) throw new RuntimeException("Only employees can mark attendance");

        ZonedDateTime now = ZonedDateTime.now(zoneId);
        DayOfWeek day = now.getDayOfWeek();
        // Weekend check
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new RuntimeException("Not a work day");
        }

        LocalTime time = now.toLocalTime();
        LocalTime start = LocalTime.of(startHour, 0);
        LocalTime end = LocalTime.of(endHour, 0);

        if (time.isBefore(start)) {
            throw new RuntimeException("Too early to mark attendance");
        }
        if (time.isAfter(end)) {
            throw new RuntimeException("Too late to mark attendance");
        }

        // Now persist attendance for today's date
        LocalDate date = now.toLocalDate();
        // Prevent duplicate marks for same user/date — check if already marked
        List<Attendance> existing = attendanceRepo.findByUserAndDate(user, date);
        if (!existing.isEmpty()) {
            // Already marked
            return true;
        }

        Attendance a = new Attendance(user, date, now.toLocalDateTime());
        attendanceRepo.save(a);
        return true;
    }

    public List<Attendance> getDailyAttendance(String userToken, LocalDate date) {
        Optional<User> maybe = userRepo.findByToken(userToken);
        if (!maybe.isPresent()) throw new RuntimeException("Invalid token");
        User requester = maybe.get();
        // only admin or same employee allowed to fetch employee daily attendance; caller decides which
        return attendanceRepo.findByUserAndDate(requester, date); // this returns for requester - admin controller will fetch a specific employee
    }

    // helper to get attendance for any employee by admin
    public List<Attendance> getEmployeeAttendanceByDate(User employee, LocalDate date) {
        return attendanceRepo.findByUserAndDate(employee, date);
    }
}
