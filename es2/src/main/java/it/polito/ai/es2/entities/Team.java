package it.polito.ai.es2.entities;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "course_id"}))
@Data
public class Team {
  @Id
  @GeneratedValue
  Long id;
  String name;
  int status;
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinColumn(name = "course_id")
  @ToString.Exclude
  Course course;
  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
  @JoinTable(name = "teams_students", joinColumns = @JoinColumn(name = "team_id"),
      inverseJoinColumns = @JoinColumn(name = "student_id"))
  @ToString.Exclude
  List<Student> members = new ArrayList<>();
  
  public void setCourse(Course new_course) {
    if (this.course != null)
      this.course.getTeams().remove(this);
    if (new_course == null) {
      this.course = null;
    } else {
      new_course.getTeams().add(this);
      this.course = new_course;
    }
  }
  
  public void addStudent(Student new_student) {
    members.add(new_student);
    new_student.getTeams().add(this);
  }
  
  public void removetudent(Student old_student) {
    old_student.getTeams().remove(this);
    this.members.remove(old_student);
  }
}