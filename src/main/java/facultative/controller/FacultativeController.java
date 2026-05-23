package facultative.controller;

import facultative.entity.Teacher;
import facultative.service.FacultativeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.security.Principal;
import java.util.Optional;

@Controller
public class FacultativeController {

    private final FacultativeService service;

    public FacultativeController(FacultativeService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        model.addAttribute("currentUsername", username);
        model.addAttribute("students", service.getAllStudents());
        model.addAttribute("courses", service.getAllCourses());
        model.addAttribute("teachers", service.getAllTeachers());
        model.addAttribute("archive", service.getAllArchiveRecords());

        Optional<Teacher> teacher = service.getTeacherByUsername(username);
        if (teacher.isPresent()) {
            model.addAttribute("teacherCourses", teacher.get().getCourses());
        }

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String fullName) {
        service.registerNewUser(username, password, "STUDENT", fullName);
        return "redirect:/login?success";
    }

    @PostMapping("/enroll")
    public String register(Principal principal, @RequestParam Long courseId) {
        service.registerCurrentStudentForCourse(principal.getName(), courseId);
        return "redirect:/";
    }

    @PostMapping("/grade")
    public String grade(@RequestParam Long courseId, @RequestParam Long studentId, @RequestParam int grade) {
        service.addGradeToArchive(courseId, studentId, grade);
        return "redirect:/";
    }

    @PostMapping("/admin/teacher")
    public String addTeacher(@RequestParam String name, @RequestParam String username) {
        service.createTeacher(name, username);
        return "redirect:/";
    }

    @PostMapping("/admin/student")
    public String addStudent(@RequestParam String name, @RequestParam String username) {
        service.createStudent(name, username);
        return "redirect:/";
    }

    @PostMapping("/admin/course")
    public String addCourse(@RequestParam String name, @RequestParam Long teacherId) {
        service.createCourse(name, teacherId);
        return "redirect:/";
    }

    @PostMapping("/admin/promote")
    public String promoteStudent(@RequestParam Long studentId) {
        service.promoteStudentToTeacher(studentId);
        return "redirect:/";
    }
}