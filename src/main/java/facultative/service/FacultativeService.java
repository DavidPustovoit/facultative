package facultative.service;

import facultative.entity.*;
import facultative.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FacultativeService {
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final ArchiveRecordRepository archiveRecordRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public FacultativeService(TeacherRepository teacherRepository, StudentRepository studentRepository,
                              CourseRepository courseRepository, ArchiveRecordRepository archiveRecordRepository,
                              UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.archiveRecordRepository = archiveRecordRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public List<ArchiveRecord> getAllArchiveRecords() {
        return archiveRecordRepository.findAll();
    }

    public Optional<Teacher> getTeacherByUsername(String username) {
        return teacherRepository.findByUsername(username);
    }

    public void registerCurrentStudentForCourse(String username, Long courseId) {
        Student student = studentRepository.findByUsername(username).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        if (!course.getRegisteredStudents().contains(student)) {
            course.getRegisteredStudents().add(student);
            courseRepository.save(course);
        }
    }

    public void addGradeToArchive(Long courseId, Long studentId, int grade) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        ArchiveRecord record = new ArchiveRecord();
        record.setStudent(student);
        record.setCourse(course);
        record.setGrade(grade);
        archiveRecordRepository.save(record);

        course.getRegisteredStudents().remove(student);
        courseRepository.save(course);
    }

    public void createTeacher(String name, String username) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        teacher.setUsername(username);
        teacherRepository.save(teacher);
    }

    public void createStudent(String name, String username) {
        Student student = new Student();
        student.setName(name);
        student.setUsername(username);
        studentRepository.save(student);
    }

    public void createCourse(String name, Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow();
        Course course = new Course();
        course.setName(name);
        course.setTeacher(teacher);
        courseRepository.save(course);
    }

    public void registerNewUser(String username, String password, String role, String fullName) {
        if (userRepository.findByUsername(username).isPresent()) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);

        if ("STUDENT".equals(role)) {
            createStudent(fullName, username);
        } else if ("TEACHER".equals(role)) {
            createTeacher(fullName, username);
        }
    }

    public void promoteStudentToTeacher(Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        User user = userRepository.findByUsername(student.getUsername()).orElseThrow();

        user.setRole("TEACHER");
        userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setName(student.getName());
        teacher.setUsername(student.getUsername());
        teacherRepository.save(teacher);

        for (Course course : courseRepository.findAll()) {
            course.getRegisteredStudents().remove(student);
            courseRepository.save(course);
        }
        studentRepository.delete(student);
    }
}