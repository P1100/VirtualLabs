package it.polito.ai.es2.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import it.polito.ai.es2.controllers.CourseRestController;
import it.polito.ai.es2.controllers.TeamRestController;
import it.polito.ai.es2.domains.StudentViewModel;
import it.polito.ai.es2.dtos.CourseDTO;
import it.polito.ai.es2.dtos.StudentDTO;
import it.polito.ai.es2.dtos.TeamDTO;
import it.polito.ai.es2.entities.Course;
import it.polito.ai.es2.entities.Student;
import it.polito.ai.es2.entities.Team;
import it.polito.ai.es2.repositories.CourseRepository;
import it.polito.ai.es2.repositories.StudentRepository;
import it.polito.ai.es2.repositories.TeamRepository;
import it.polito.ai.es2.repositories.TokenRepository;
import it.polito.ai.es2.services.exceptions.*;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Politica di sovrascrittura adottata: in quasi tutti i metodi add, se un id era già presente nel database non sovrascrivo i dati
 * già esistenti (tranne nel caso di proposeTeam, che poichè ha un id autogenerato, si è deciso di aggiornare il team vecchio usando
 * sempre la proposeTeam).
 */
@Service
@Transactional
@Log
@PreAuthorize("permitAll()") // doesnt override httpsecurity settings!
public class TeamServiceImpl implements TeamService {
  @Autowired
  ModelMapper modelMapper;
  @Autowired
  CourseRepository courseRepository;
  @Autowired
  StudentRepository studentRepository;
  @Autowired
  TeamRepository teamRepository;
  @Autowired
  TokenRepository tokenRepository;
  @Autowired
  NotificationService notificationService;
  
  /**
   * GET {@link CourseRestController#getAllCourses()}
   */
  @Override
  public List<CourseDTO> getAllCourses() {
    return courseRepository.findAll().stream().map(x -> modelMapper.map(x, CourseDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.CourseRestController#getCourse(String)}
   */
  @Override
  @PreAuthorize("authenticated") // overrides class roles
  public Optional<CourseDTO> getCourse(String courseName) {
    if (courseName == null) throw new TeamServiceException("getCourse() - null parameter");
    return courseRepository.findById(courseName).map(x -> modelMapper.map(x, CourseDTO.class));
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.CourseRestController#getEnrolledStudents(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('PROFESSOR')")
  public List<StudentDTO> getEnrolledStudents(String courseName) throws CourseNotFoundException {
    if (courseName == null) throw new TeamServiceException("getEnrolledStudents() - null parameters");
    if (!courseRepository.existsById(courseName))
      throw new CourseNotFoundException("getEnrolledStudents() - StudentNotFoundException: (" + courseName + ")");
    Course c = courseRepository.getOne(courseName);
    return c.getStudents().stream().map(x -> modelMapper.map(x, StudentDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.CourseRestController#getStudentsInTeams(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('PROFESSOR')")
  public List<StudentDTO> getStudentsInTeams(String courseName) {
    if (courseName == null) throw new TeamServiceException("null parameter");
    if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("getTeamForCourse - course not found");
    return courseRepository.getStudentsInTeams(courseName).stream().map(x -> modelMapper.map(x, StudentDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.CourseRestController#getAvailableStudents(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT') or hasRole('PROFESSOR')")
  public List<StudentDTO> getAvailableStudents(String courseName) {
    if (courseName == null) throw new TeamServiceException("null parameter");
    if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("getTeamForCourse - course not found");
    return courseRepository.getStudentsNotInTeams(courseName).stream().map(x -> modelMapper.map(x, StudentDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.CourseRestController#getTeamsForCourse(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
  public List<TeamDTO> getTeamsForCourse(String courseName) {
    if (courseName == null) throw new TeamServiceException("null parameter");
    Optional<Course> co = courseRepository.findById(courseName);
    if (!co.isPresent()) throw new CourseNotFoundException("getTeamForCourse - course not found");
    return co.get().getTeams().stream().map(x -> modelMapper.map(x, TeamDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.StudentRestController#getAllStudents()}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR')")
  public List<StudentDTO> getAllStudents() {
    return studentRepository.findAll().stream().map(x -> modelMapper.map(x, StudentDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.StudentRestController#getStudent(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or @mySecurityChecker.isOwner(#studentId,authentication.principal.username)")
  public Optional<StudentDTO> getStudent(String studentId) {
    if (studentId == null) throw new TeamServiceException("getStudent() - null parameter");
    return studentRepository.findById(studentId).map(x -> modelMapper.map(x, StudentDTO.class));
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.StudentRestController#getCourses(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or @mySecurityChecker.isOwner(#studentId,authentication.principal.username)")
  public List<CourseDTO> getCourses(String studentId) {
    if (studentId == null) throw new TeamServiceException("null parameters");
    return studentRepository.getOne(studentId).getCourses().stream().map(x -> modelMapper.map(x, CourseDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.StudentRestController#getTeamsForStudent(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or @mySecurityChecker.isOwner(#studentId,authentication.principal.username)")
  public List<TeamDTO> getTeamsForStudent(String studentId) {
    if (studentId == null) throw new TeamServiceException("getTeamsForStudent() - null parameters");
    return studentRepository.getOne(studentId).getTeams().stream().map(x -> modelMapper.map(x, TeamDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link TeamRestController#getAllTeams()}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public List<TeamDTO> getAllTeams() {
    return teamRepository.findAll().stream().map(x -> modelMapper.map(x, TeamDTO.class)).collect(Collectors.toList());
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.TeamRestController#getTeam(Long)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or @mySecurityChecker.isTeamOwner(#teamId,authentication.principal.username)")
  public Optional<TeamDTO> getTeam(Long teamId) {
    if (teamId == null) throw new TeamServiceException("getTeam() - null parameter");
    return teamRepository.findById(teamId).map(x -> modelMapper.map(x, TeamDTO.class));
  }
  /**
   * GET {@link it.polito.ai.es2.controllers.TeamRestController#getMembers(Long)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('PROFESSOR') or @mySecurityChecker.isTeamOwner(#teamId,authentication.principal.username)")
  public List<StudentDTO> getMembers(Long teamId) {
    if (teamId == null) throw new TeamServiceException("getMembers() - null parameters");
    Optional<Team> team = teamRepository.findById(teamId);
    if (team.isPresent())
      return team.get().getMembers().stream().filter(Objects::nonNull).map(y -> modelMapper.map(y, StudentDTO.class)).collect(Collectors.toList());
    else
      throw new TeamServiceException("getMembers() - team not found");
  }
  /**
   * POST {@link it.polito.ai.es2.controllers.CourseRestController#addCourse(CourseDTO)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public boolean addCourse(CourseDTO course) {
    if (course == null || course.getName() == null) return false;
    Course c = modelMapper.map(course, Course.class);
    try {
      if (!courseRepository.existsById(course.getName())) {
        courseRepository.save(c);
        return true;
      }
      return false;
    } catch (IllegalArgumentException e) {
      log.warning("IllegalArgumentException:" + e.toString());
      e.printStackTrace();
      return false;
    } catch (Exception e) {
      log.warning("###### Other Exception:" + e.toString());
      e.printStackTrace();
      return false;
    }
  }
  /**
   * POST {@link it.polito.ai.es2.controllers.StudentRestController#addStudent(StudentDTO)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public boolean addStudent(StudentDTO student) {
    if (student == null || student.getId() == null) return false;
    Student s = modelMapper.map(student, Student.class);
    try {
      if (!studentRepository.existsById(student.getId())) {
        studentRepository.save(s);
        return true;
      }
      return false;
    } catch (IllegalArgumentException e) {
      log.warning("###### IllegalArgumentException:" + e.toString());
      e.printStackTrace();
      return false;
    } catch (Exception e) {
      log.warning("###### Other Exception:" + e.toString());
      e.printStackTrace();
      return false;
    }
  }
  
  /**
   * POST {@link it.polito.ai.es2.controllers.StudentRestController#addAll(List<StudentDTO>)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN')")
  public List<Boolean> addAll(List<StudentDTO> students) {
    if (students == null) throw new TeamServiceException("null parameters");
    return students.stream()
               .map(x -> modelMapper.map(x, Student.class))
               .peek(e -> log.info("addAll(List<StudentDTO> - size:" + students.size() + " - Entità corrente:" + e))
               .map(
                   e -> {
                     boolean b = studentRepository.existsById(e.getId());
                     // Se studente esisteva già...
                     if (b)
                       return Boolean.FALSE;
                     // Se invece non esisteva...
                     studentRepository.save(e);
                     return Boolean.TRUE;
                   }).collect(Collectors.toList());
  }
  
  /**
   * {@link it.polito.ai.es2.controllers.CourseRestController#enableCourse(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or (hasRole('PROFESSOR') and @mySecurityChecker.isCourseOwner(#courseName,authentication.principal.username))")
  public void enableCourse(String courseName) throws CourseNotFoundException {
    if (courseName == null) throw new CourseNotFoundException("null parameter");
    if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("course not found");
    
    courseRepository.getOne(courseName).setEnabled(true);
  }
  
  /**
   * {@link it.polito.ai.es2.controllers.CourseRestController#disableCourse(String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or (hasRole('PROFESSOR') and @mySecurityChecker.isCourseOwner(#courseName,authentication.principal.username))")
  public void disableCourse(String courseName) throws CourseNotFoundException {
    if (courseName == null) throw new CourseNotFoundException("null parameter");
    if (!courseRepository.existsById(courseName)) throw new CourseNotFoundException("course not found");
    
    courseRepository.getOne(courseName).setEnabled(false);
  }
  
  /**
   * {@link it.polito.ai.es2.controllers.CourseRestController#addStudentToCourse(String, Map)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or (hasRole('PROFESSOR') and @mySecurityChecker.isCourseOwner(#courseName,authentication.principal.username))")
  public boolean addStudentToCourse(String studentId, String courseName) throws StudentNotFoundException, CourseNotFoundException {
    if (studentId == null || courseName == null) throw new TeamServiceException("addStudentToCourse() - null parameters");
    if (!studentRepository.existsById(studentId)) throw new StudentNotFoundException("addStudentToCourse() - student not found:" + studentId);
    Optional<Course> courseOptional = courseRepository.findById(courseName);
    if (!courseOptional.isPresent()) throw new CourseNotFoundException("addStudentToCourse() - course not found:" + courseName);
    
    Course c = courseOptional.get();
    // Controllo che corso sia enabled
    if (!c.isEnabled())
      return false;
    Student s = studentRepository.getOne(studentId);
    // Controllo che studente non sia già iscritto al corso
    if (c.getStudents().stream().anyMatch(x -> x.getId().equals(studentId)))
      return false;
    
    log.info("addStudentToCourse ---> BEFORE ADD");
    c.addStudent(s);
    log.info("addStudentToCourse(" + studentId + "," + courseName + "). " + c.getName() + "->ListStudents: " + c.getStudents());
    return true;
  }
  
  /**
   * {@link it.polito.ai.es2.controllers.CourseRestController#enrollAll(List, String)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or (hasRole('PROFESSOR') and @mySecurityChecker.isCourseOwner(#courseName,authentication.principal.username))")
  public List<Boolean> enrollAll(List<String> studentIds, String courseName) {
    if (studentIds == null || courseName == null)
      throw new TeamServiceException("enrollAll(List<String> studentIds, String courseName) - null parameters");
    if (!courseRepository.existsById(courseName))
      throw new CourseNotFoundException("enrollAll(List<String> studentIds, String courseName) - course not found");
    
    Course course = courseRepository.getOne(courseName);
    List<Boolean> lb = new ArrayList<>();
    for (String id : studentIds) {
      if (id == null) { // null was put by AddAndReroll function
        lb.add(false);
        continue;
      }
      if (!studentRepository.existsById(id))
        throw new StudentNotFoundException("enrollAll(List<String> studentIds, String courseName) - student in list not found");
      Student student = studentRepository.getOne(id);
      // Controllo che lo studente corrente (id) non sià già presente nella lista degli studenti iscritti al corso
      if (course.getStudents().stream().anyMatch(x -> x.getId().equals(id))) {
        lb.add(false);
        continue;
      }
      lb.add(true);
      course.addStudent(student);
//      student.addCourse(course); // -> creerei duplicati
    }
    log.info("enrollAll List<Boolean> ritornata:" + lb);
    return lb;
  }
  
  /**
   * {@link it.polito.ai.es2.controllers.CourseRestController#enrollStudents(String, MultipartFile)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or (hasRole('PROFESSOR') and @mySecurityChecker.isCourseOwner(#courseName,authentication.principal.username))")
  public List<Boolean> addAndEroll(Reader reader, String courseName) {
    if (reader == null || courseName == null) throw new TeamServiceException("addAndEroll(Reader reader, String courseName) - null parameters");
    Optional<Course> courseOptional = courseRepository.findById(courseName);
    if (!courseOptional.isPresent()) throw new CourseNotFoundException("addAndEroll(Reader reader, String courseName) - course not found");
  
    CsvToBean<StudentViewModel> csvToBean = new CsvToBeanBuilder(reader)
                                                .withType(StudentViewModel.class)
                                                .withIgnoreLeadingWhiteSpace(true)
                                                .build();
    List<StudentViewModel> users = csvToBean.parse();
    log.info(String.valueOf(users));
    List<String> list_ids = users.stream()
                                .map(new_studentViewModel -> modelMapper.map(new_studentViewModel, StudentDTO.class))
                                .map(new_studentDTO ->
                                {
                                  Optional<Student> optionalStudent_fromDb = studentRepository.findById(new_studentDTO.getId());
                                  if (optionalStudent_fromDb.isPresent())
                                    return null;
                                  Student newStudent = modelMapper.map(new_studentDTO, Student.class);
                                  return studentRepository.save(newStudent);
                                })
                                .map(y -> y != null ? y.getId() : null).collect(Collectors.toList());
    log.info(courseName + " - CSV_Valid_Students: " + list_ids);
    return enrollAll(list_ids, courseName);
  }
  
  /**
   * {@link it.polito.ai.es2.controllers.TeamRestController#proposeTeam(String, String, List)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or hasRole('STUDENT')")
  public TeamDTO proposeTeam(String courseName, String team_name, List<String> memberIds) throws TeamServiceException {
    if (courseName == null || team_name == null || memberIds == null) throw new TeamServiceException("null parameter");
    Optional<Course> oc = courseRepository.findById(courseName);
    if (!oc.isPresent()) throw new CourseNotFoundException("proposeTeam - course not found");
    if (!oc.get().isEnabled()) throw new CourseNotEnabledException("proposeTeam() - course not enabled");
    List<Optional<Student>> streamopt_listStudentsProposal = memberIds.stream().map(x -> studentRepository.findById(x)).collect(Collectors.toList());
    if (!streamopt_listStudentsProposal.stream().allMatch(Optional::isPresent))
      throw new StudentNotFoundException("proposeTeam() - student not found");
    
    Course course = oc.get();
    List<Student> listStudentsProposal = streamopt_listStudentsProposal.stream().map(Optional::get).collect(Collectors.toList());
    if (!course.getStudents().containsAll(listStudentsProposal)) // !listStudentsProposal.stream().allMatch(x -> course.getStudents().contains(x))
      throw new StudentNotEnrolledException("proposeTeam() - non tutti gli studenti sono iscritti al corso " + course.getName());
    // Controllo se tra gli studenti dei vari teams del corso, ce n'è qualcuno tra quelli presenti nella proposta
    if (course.getTeams().size() != 0 &&
            !course.getTeams()
                 .stream()
                 .map(Team::getMembers)
                 .flatMap(List::stream)
                 .distinct()
                 .noneMatch(student -> {
                   return listStudentsProposal.stream().anyMatch(student::equals);
                 })
    )
      throw new StudentInMultipleTeamsException("proposeTeam() - studenti fanno parte di altri gruppi nell’ambito dello stesso corso");
    if (listStudentsProposal.size() < course.getMin() || listStudentsProposal.size() > course.getMax())
      throw new CourseCardinalConstrainsException("proposeTeam() - non rispettati i vincoli di cardinalità del corso su dimensioni team");
    if (!listStudentsProposal.stream().allMatch(new HashSet<>()::add))
      throw new StudentDuplicatesInProposalException("proposeTeam() - duplicati nell'elenco dei partecipanti della proposta team");
    if (teamRepository.findFirstByNameAndCourse_name(team_name, courseName) != null)
      throw new TeamAlreayCreatedException("proposeTeam() - team già creato");
    
    TeamDTO teamDTO = new TeamDTO(null, team_name, Team.status_inactive());
    Team new_team = modelMapper.map(teamDTO, Team.class);
    // aggiungo nuovo team, a studenti e al corso
    for (Student student : new ArrayList<>(listStudentsProposal)) {
      student.addTeam(new_team); // add su studenti
    }
    course.addTeam(new_team); // add sul singolo corso
    TeamDTO return_teamDTO = modelMapper.map(teamRepository.save(new_team), TeamDTO.class);
    notificationService.notifyTeam(return_teamDTO, memberIds);
    return return_teamDTO;
  }
  
  /**
   * {@link it.polito.ai.es2.controllers.TeamRestController#evictTeam(Long)}
   */
  @Override
  @PreAuthorize("hasRole('ADMIN') or (hasRole('PROFESSOR') and @mySecurityChecker.isTeamOwner(#teamId,authentication.principal.username))")
  public boolean evictTeam(Long teamId) {
    Optional<Team> optionalTeam = teamRepository.findById(teamId);
    if (!optionalTeam.isPresent())
      return false;
    Team team_to_delete = optionalTeam.get();
    
    for (Student student : team_to_delete.getMembers()) {
      // usare "student.removeTeam()" rimuoverebbe studenti da team, il che creerebbe problemi in quanto modificherebbe il ciclo foreach enhanced in corso (java.util.ConcurrentModificationException)
      student.getTeams().remove(team_to_delete);
    }
    // --> non serve rimuovere students e course da team, perchè tanto lo cancello
    team_to_delete.getCourse().getTeams().remove(team_to_delete);
    teamRepository.delete(team_to_delete);
    return true;
  }
  
  /**
   * @param status - Team.status_active() or Team.status_inactive()
   */
  @Override
  public boolean setTeamStatus(Long teamId, int status) {
    Team team = teamRepository.findById(teamId).orElse(null);
    if (team == null || team.getStatus() == status)
      return false;
    team.setStatus(status); // no need to save, will be flushed automatically at the end of transaction (since not a new entity)
    return true;
  }
}
