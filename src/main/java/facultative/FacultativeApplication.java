package facultative;

import facultative.entity.Course;
import facultative.entity.Student;
import facultative.entity.Teacher;
import facultative.entity.User;
import facultative.repository.CourseRepository;
import facultative.repository.StudentRepository;
import facultative.repository.TeacherRepository;
import facultative.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class FacultativeApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacultativeApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(TeacherRepository teacherRepository,
                                      StudentRepository studentRepository,
                                      CourseRepository courseRepository,
                                      UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User studentUser = new User();
                studentUser.setUsername("david");
                studentUser.setPassword(passwordEncoder.encode("1111"));
                studentUser.setRole("STUDENT");
                userRepository.save(studentUser);

                User teacherUser = new User();
                teacherUser.setUsername("teacher");
                teacherUser.setPassword(passwordEncoder.encode("2222"));
                teacherUser.setRole("TEACHER");
                userRepository.save(teacherUser);

                Teacher teacher1 = new Teacher();
                teacher1.setName("Коваленко О.В.");
                teacher1.setUsername("teacher");
                teacherRepository.save(teacher1);

                Teacher teacher2 = new Teacher();
                teacher2.setName("Сидоренко М.І.");
                teacher2.setUsername("unknown_teacher");
                teacherRepository.save(teacher2);

                Student student1 = new Student();
                student1.setName("Давид");
                student1.setUsername("david");
                studentRepository.save(student1);

                Student student2 = new Student();
                student2.setName("Анна");
                student2.setUsername("anna");
                studentRepository.save(student2);

                Course course1 = new Course();
                course1.setName("Об'єктно-орієнтоване програмування");
                course1.setTeacher(teacher1);
                courseRepository.save(course1);

                Course course2 = new Course();
                course2.setName("Бази даних");
                course2.setTeacher(teacher2);
                courseRepository.save(course2);
            }
        };
    }
}