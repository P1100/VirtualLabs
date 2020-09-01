package it.polito.ai.es2.controllers;

import it.polito.ai.es2.controllers.hateoas.ModelHelper;
import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.dtos.TeamDTO;
import it.polito.ai.es2.services.interfaces.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/courses")
@PreAuthorize("isAuthenticated()") // doesnt override WebSecurityConfig settings
public class APICourses_RestController {
  @Autowired
  private CourseService courseService;
  @Autowired
  private ModelHelper modelHelper;

  @GetMapping()
  @PreAuthorize("isAuthenticated()") // overrides class roles
  public CollectionModel<CourseDTO> getAllCourses() {
    List<CourseDTO> courses = courseService.getAllCourses().stream().map(modelHelper::enrich).collect(Collectors.toList());
    return CollectionModel.of(courses,
        linkTo(methodOn(APICourses_RestController.class).getAllCourses()).withSelfRel());
  }

  //   {"name":"C33","min":1,"max":100,"enabled":true,"professor":"malnati"} - ContentType: application/json
  @GetMapping("/{courseId}")
  @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR')")
  public CourseDTO getCourse(@PathVariable String courseId) {
    Optional<CourseDTO> courseDTO = courseService.getCourse(courseId);
    if (courseDTO.isEmpty())
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found (" + courseId + ")");
    return modelHelper.enrich(courseDTO.get());
  }

  @PostMapping()
  @PreAuthorize("hasRole('PROFESSOR')")
  public void addCourse(@Valid @RequestBody CourseDTO courseDTO) {
    courseService.addCourse(courseDTO);
  }

  @PutMapping()
  @PreAuthorize("hasRole('PROFESSOR')")
  public void updateCourse(@Valid @RequestBody CourseDTO courseDTO) {
    courseService.updateCourse(courseDTO);
  }

  @DeleteMapping("/{courseId}")
  @PreAuthorize("hasRole('PROFESSOR')")
  public void deleteCourse(@PathVariable String courseId) {
    courseService.deleteCourse(courseId);
  }

  @RequestMapping(value = "/{courseId}/enable", method = {RequestMethod.POST, RequestMethod.PUT})
  @PreAuthorize("hasRole('PROFESSOR')")
  public void enableCourse(@PathVariable String courseId) {
    courseService.enableCourse(courseId);
  }

  @RequestMapping(value = "/{courseId}/disable", method = {RequestMethod.POST, RequestMethod.PUT})
  @PreAuthorize("hasRole('PROFESSOR')")
  public void disableCourse(@PathVariable String courseId) {
    courseService.disableCourse(courseId);
  }

  @GetMapping("/{courseId}/enrolled")
  @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR')")
  public CollectionModel<StudentDTO> getEnrolledStudents(@PathVariable String courseId) {
    List<StudentDTO> students = courseService.getEnrolledStudents(courseId);
    for (StudentDTO student : students) {
      modelHelper.enrich(student);
    }
    return CollectionModel.of(students,
        linkTo(methodOn(APICourses_RestController.class).getEnrolledStudents(courseId)).withSelfRel());
  }

  // TODO: remove team if no more students in it?
  @PutMapping("/{courseId}/disenroll/{studentId}")
  @PreAuthorize("hasRole('PROFESSOR')")
  public void disenrollStudent(@PathVariable Long studentId, @PathVariable String courseId) {
    courseService.disenrollStudent(studentId, courseId);
  }

  // ContentType:json. Body:{"id":"S33","name":"S33-name","firstName":"S33-FirstName"}
  @RequestMapping(value = "/{courseId}/enroll", method = {RequestMethod.PUT, RequestMethod.POST})
  @PreAuthorize("hasRole('PROFESSOR')")
  public void enrollStudent(@PathVariable String courseId, @RequestBody Map<String, String> studentMap) { // StudentDTO
    Long studentId;
    if (studentMap == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "null student");
    if (studentMap.containsKey("id"))
      studentId = Long.valueOf(studentMap.get("id"));
    else
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "missing student id");
    courseService.enrollStudent(studentId, courseId);
  }

  //["S33","S44"]
  @RequestMapping(value = "/{courseId}/enroll-all", method = {RequestMethod.PUT, RequestMethod.POST})
  @PreAuthorize("hasRole('PROFESSOR')") // and @mySecurityChecker.isCourseOwner(#courseName,authentication.principal.username))")
  public List<Boolean> enrollStudents(@RequestBody List<Long> studentIds, @PathVariable String courseId) {
    return courseService.enrollStudents(studentIds, courseId);
  }

  @RequestMapping(value = "/{courseId}/enroll-csv", method = {RequestMethod.PUT, RequestMethod.POST})
  @PreAuthorize("hasRole('PROFESSOR')")
  public List<Boolean> enrollStudentsCSV(@PathVariable String courseId, @RequestParam("file") @NotNull MultipartFile file) {
    List<Boolean> booleanList;
    if (file == null || file.isEmpty() || file.getContentType() == null)
      throw new ResponseStatusException(HttpStatus.CONFLICT, "null or empty file");
    if (!(file.getContentType().equals("text/csv") || file.getContentType().equals("application/vnd.ms-excel")))
      throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, courseId + " - file Type:" + file.getContentType());
    Reader reader;
    try {
      reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
      booleanList = courseService.enrollStudentsCSV(reader, courseId);
    } catch (IOException e) {
      e.printStackTrace();
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "file IOException");
    }
    return booleanList;
  }

  @GetMapping("/{courseId}/teams")
  @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR')")
  public CollectionModel<TeamDTO> getTeamsForCourse(@PathVariable String courseId) {
    List<TeamDTO> teams = courseService.getTeamsForCourse(courseId);
    for (TeamDTO team : teams) {
      modelHelper.enrich(team);
    }
    return CollectionModel.of(teams,
        linkTo(methodOn(APICourses_RestController.class).getTeamsForCourse(courseId)).withSelfRel());
  }

  @GetMapping("/{courseId}/students-in-teams")
  @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR')")
  public CollectionModel<StudentDTO> getStudentsInTeams(@PathVariable String courseId) {
    List<StudentDTO> students = courseService.getStudentsInTeams(courseId);
    for (StudentDTO student : students) {
      modelHelper.enrich(student);
    }
    return CollectionModel.of(students,
        linkTo(methodOn(APICourses_RestController.class).getStudentsInTeams(courseId)).withSelfRel());
  }

  @GetMapping("/{courseId}/students-available")
  @PreAuthorize("hasRole('STUDENT') or hasRole('PROFESSOR')")
  public CollectionModel<StudentDTO> getAvailableStudents(@PathVariable String courseId) {
    List<StudentDTO> students = courseService.getAvailableStudents(courseId);
    for (StudentDTO student : students) {
      modelHelper.enrich(student);
    }
    return CollectionModel.of(students,
        linkTo(methodOn(APICourses_RestController.class).getAvailableStudents(courseId)).withSelfRel());
  }
}
