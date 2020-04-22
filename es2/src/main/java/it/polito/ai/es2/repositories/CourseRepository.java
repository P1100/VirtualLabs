package it.polito.ai.es2.repositories;

import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    @Query("SELECT DISTINCT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name= :courseName")
    List<Student> getStudentsInTeams(String courseName);
    
    @Query("SELECT DISTINCT s FROM Student s WHERE s.id NOT IN (SELECT s FROM Student s INNER JOIN s.teams t INNER JOIN t.course c WHERE c.name= :courseName)")
    List<Student> getStudentsNotInTeams(String courseName);
}
