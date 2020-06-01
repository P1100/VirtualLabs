package it.polito.ai.es2;

import it.polito.ai.es2._provecodicelearning.MyTestingService;
import it.polito.ai.es2.repositories.CourseRepository;
import it.polito.ai.es2.repositories.StudentRepository;
import it.polito.ai.es2.repositories.TeamRepository;
import it.polito.ai.es2.repositories.TokenRepository;
import it.polito.ai.es2.services.interfaces.NotificationService;
import it.polito.ai.es2.services.interfaces.TeamService;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

//TODO: passare a JDK14
//TODO: Integrare con es1 (da sessione a jwt), mantenendo doppia struttura single page e multi page
//TODO: fare tutti i test! importante
@SpringBootApplication
@Log
public class VirtualLabsApplication {
  public static void main(String[] args) {
    SpringApplication.run(VirtualLabsApplication.class, args);
  }
  
  @Bean
  ModelMapper modelMapper() {
    return new ModelMapper();
  }
  
  @Autowired
  Environment environment;
  @Autowired
  private ApplicationContext applicationContext;
  @Value("${server.port}")
  String port;
//  @Value("${server.address}")
//  String address;
  
  @Bean
  CommandLineRunner runner(CourseRepository courseRepository, StudentRepository studentRepository,
                           TeamRepository teamRepository, TeamService teamService,
                           ModelMapper modelMapper, MyTestingService testservice, TokenRepository tokenRepository,
                           NotificationService notificationService) {
    return new CommandLineRunner() {
      @Override
      public void run(String... args) {
/*        List<Token> allByTeamId = tokenRepository.findAllByTeamId(Long.valueOf(18));
        System.out.println("allByTeamId-" + allByTeamId);
        
        List<Token> allByExpiryDateBeforeOrderByExpiryDateAsc = tokenRepository.findAllByExpiryDateBeforeOrderByExpiryDate(Timestamp.valueOf(LocalDateTime.now()));
        System.out.println("allByExpiryDateBeforeOrderByExpiryDateAsc-" + allByExpiryDateBeforeOrderByExpiryDateAsc);
        
        List<Token> allByExpiryDateBeforeOrderByExpiryDateDesc = tokenRepository.findAllByExpiryDateBeforeOrderByExpiryDateDesc(Timestamp.valueOf(LocalDateTime.now()));
        System.out.println("allByExpiryDateBeforeOrderByExpiryDateDesc-" + allByExpiryDateBeforeOrderByExpiryDateDesc);
        */

//      notificationService.sendMessage("pibelex285@reqaxv.com", "This is subject2", "Hello,this is body. Last version.\n\n\nTwo new lines were added. Now Finish with one last.\n\nBye\n");
//        notificationService.notifyTeam(null,null);
/*        System.out.println(environment.getProperty("server.port"));
        System.out.println(environment.getProperty("server.address"));
        System.out.println(port);
        System.out.println("------");
        try {
          System.out.println(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        notificationService.notifyTeam(new TeamDTO((long) 999, "Team_test_email", 1), Arrays.asList("S1", "S3"));
  
        teamService.setTeamStatus((long) 14, Team.status_inactive());
        notificationService.cleanUpOldTokens();*/

//        teamService.proposeTeam("C0", "Team_Test_Evict", Arrays.asList("S0", "S1"));
//        teamService.proposeTeam("C0", "Team_Test_Evict2", Collections.singletonList("S2"));
//        teamService.proposeTeam("C0", "Team_Test_Evict3", Collections.singletonList("S3"));
/*
        teamService.setTeamStatus((long) 21, Team.status_active());
        System.out.println(teamService.getTeamsForCourse("C0"));
        System.out.println("------------------------ EVICT TEAM NONEXISTENT TEAM ---------------");
        System.out.println(teamService.evictTeam(Long.valueOf(1203)));
        System.out.println("------------------------ EVICT TEAM TEST MANUAL ID ---------------");
        System.out.println(teamService.evictTeam(Long.valueOf(20)));
        
        System.out.println("-------- TEST EQUALS TEAMS --------");
        System.out.println(tokenRepository.findAll());
        
        System.out.println("---- TEST1 duplicate teams");
        System.out.println(teamRepository.count());
        List<Team> teamList = teamRepository.findAll();
        System.out.println("################ TEST2 duplicate teams #########################");
        for (Team team1 : teamList) {
          for (Team team2 : teamList) {
            if (team1 != team2 && team1.equals(team2))
              System.out.println("Duplicate team:" + team1 + " | " + team2);
          }
        }
        System.out.println("---------------------------------1-----------------------------------");
        List<Team> allByNameAndCourse_name = teamRepository.findAllByNameAndCourse_Idname("Team1", "C0");
        System.out.println("################ TEST3 duplicate teams #########################");
        System.out.println(allByNameAndCourse_name);
        System.out.println("---------------------------------2-----------------------------------");
        ExampleMatcher caseInsensitiveExampleMatcher = ExampleMatcher.matchingAll().withIgnoreCase().withIgnorePaths("status");
        Team team_mapped = modelMapper.map(new TeamDTO(null, "Team1", Team.status_active()), Team.class);
        Example<Team> example = Example.of(team_mapped, caseInsensitiveExampleMatcher);
  
        List<Team> actual = teamRepository.findAll(example);
        System.out.println("team mapped:" + team_mapped);
        System.out.println("List Team di findAll(example) - Team1: " + actual);
//        actual.ifPresentOrElse(System.out::println, () -> System.out.println("------> null value of findOne(example)"));
        System.out.println("---------------------------------3-----------------------------------");
  
        System.out.println("################ ALL TEAMS LIST #########################");
        System.out.println(teamList);
  
        System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
        System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
        System.out.println("§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§");
*/

//        teamService.proposeTeam("C0", "Team1", Collections.singletonList("S1"));
//        teamService.proposeTeam("C0", "Team1", List.of("S1", "S2", "S3"));
//        notificationService.reject("68194f20-877b-49cc-a1a4-c460f5ef8bc2");

//         DONT WORK WITH LAZY LOADING
//                courseRepository.findAll().stream().forEach(i -> System.out.println(i.toString()));
//                studentRepository.findAll().stream().forEach(i -> System.out.println(i.toString()));
        
        // NOT WORKING --> org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: it.polito.ai.es2.entities.Course.teams, could not initialize proxy - no Session
//        testservice.entity_manager_test();
//        if (true) return;
//
//        // --> ATTENZIONE, mettendo le cascade in ordine/combinazione sbagliata (tipo sulla mapped ontomany), NON FUNZIONA PIU NIENTE!! (...)
//        teamRepository.deleteAll();
//        studentRepository.deleteAll();
//        courseRepository.deleteAll();
/*
        System.out.println("############################## BEGIN TEST SERVICE COMMAND LINE RUNNER ####################################");
        teamService.addCourse(new CourseDTO("c_enroll_all", 1, 2, false));
        CourseDTO c0 = new CourseDTO("C0", 1, 100, true);
        teamService.addCourse(c0);
        CourseDTO c1 = new CourseDTO("C1", 1, 100, true);
        teamService.addCourse(c1);
        CourseDTO c2 = new CourseDTO("C2", 1, 100, true);
        teamService.addCourse(c2);
        CourseDTO c3 = new CourseDTO("C3", 1, 99, true);
        teamService.addCourse(c3);
        CourseDTO c4 = new CourseDTO("C4", 1, 99, true);
        teamService.addCourse(c4);
        CourseDTO c5 = new CourseDTO("C5", 2, 3, true);
        teamService.addCourse(c5);
        CourseDTO c6 = new CourseDTO("C6", 1, 99, true);
        teamService.addCourse(c6);
        CourseDTO c7 = new CourseDTO("C7", 1, 99, true);
        teamService.addCourse(c7);
        CourseDTO c8 = new CourseDTO("C8", 1, 99, false);
        teamService.addCourse(c8);
        CourseDTO c9 = new CourseDTO("C9", 1, 99, false);
        teamService.addCourse(c9);
        StudentDTO s0 = new StudentDTO("S0", "S0-name", "S0-FirstName");
        teamService.addStudent(s0);
        StudentDTO s1 = new StudentDTO("S1", "S1-name", "S1-FirstName");
        teamService.addStudent(s1);
        StudentDTO s2 = new StudentDTO("S2", "S2-name", "S2-FirstName");
        teamService.addStudent(s2);
        StudentDTO s3 = new StudentDTO("S3", "S3-name", "S3-FirstName");
        teamService.addStudent(s3);
        StudentDTO s4 = new StudentDTO("S4", "S4-name", "S4-FirstName");
        teamService.addStudent(s4);
        StudentDTO s5 = new StudentDTO("S5", "S5-name", "S5-FirstName");
        teamService.addStudent(s5);
        StudentDTO s6 = new StudentDTO("S6", "S6-name", "S6-FirstName");
        teamService.addStudent(s6);
        StudentDTO s7 = new StudentDTO("S7", "S7-name", "S7-FirstName");
        teamService.addStudent(s7);
        StudentDTO s8 = new StudentDTO("S8", "S8-name", "S8-FirstName");
        teamService.addStudent(s8);
        StudentDTO s9 = new StudentDTO("S9", "S9-name", "S9-FirstName");
        teamService.addStudent(s9);
        
        System.out.println("-------------------------  AddAll ------------------------------------");
        List<StudentDTO> lstudto;
        lstudto = Arrays.asList(new StudentDTO("Student_0", "s0n", "s0fn"),
            new StudentDTO("Student_1", "s1n", "s1fn"),
            new StudentDTO("Student_2", "s2n", "s2fn"),
            new StudentDTO("Student_3", "s3n", "s3fn"),
            new StudentDTO("S0", "errore", "studente_gia_presente"));
        System.out.println(teamService.addAll(lstudto));
        
        System.out.println("-------------------------  getCourse() ------------------------------------");
        System.out.print("#C1 is present():" + teamService.getCourse("C1").isPresent() + "-");
        teamService.getCourse("C1").ifPresent(System.out::println);
        System.out.println("#C99 is present():" + teamService.getCourse("C99").isPresent());
        teamService.getCourse("C99").ifPresent(x -> System.out.println(x));
        
        System.out.println("-------------------------  getStudent() ------------------------------------");
        System.out.print("S5 is present():" + teamService.getStudent("S5").isPresent() + "-");
        teamService.getStudent("S5").ifPresent(System.out::println);
        System.out.println("#S999 is present():" + teamService.getStudent("S999").isPresent());
        teamService.getStudent("S999").ifPresent(System.out::println);
        
        System.out.println("-------------------------  getAllCourses() ------------------------------------");
        teamService.getAllCourses().forEach(System.out::println);
        System.out.println("-------------------------  getAllStudents() ------------------------------------");
        teamService.getAllStudents().forEach(System.out::println);
        
        System.out.println("-------------------------  Enable & disable courses ------------------------------------");
        teamService.enableCourse("c_enroll_all");
        teamService.disableCourse("C7");
        
        System.out.println("-------------------------  EnrollAll - C_enroll_all, C8 ------------------------------------");
        System.out.println(teamService.enrollAll(Arrays.asList("S1", "S4", "S5"), "C_enroll_all"));
        System.out.println(teamService.enrollAll(Arrays.asList("S1", "S4", "S5", "S6"), "C_enroll_all"));
        System.out.println(teamService.enrollAll(Collections.singletonList("S8"), "C8"));
        
        System.out.println("-------------------------  AddStudentToCourse ------------------------------------");
        System.out.println("#-Result_1_duplicate: " + teamService.addStudentToCourse("S1", "C0"));
        System.out.println("#-Result_2_duplicate: " + teamService.addStudentToCourse("S2", "C0"));
//      teamService.disableCourse("C1"); // -> ritornerà falso dopo
        //teamService.addStudentToCourse("S34124124", "C5"); //--> exception student not found
        //teamService.addStudentToCourse("S6", "C13123123"); //--> exception course not found
        teamService.addStudentToCourse("S0", "C0");
        teamService.addStudentToCourse("S1", "C0");
        teamService.addStudentToCourse("S2", "C0");
        teamService.addStudentToCourse("S3", "C0");
        teamService.addStudentToCourse("S3", "C3");
        teamService.addStudentToCourse("S5", "C3");
        teamService.addStudentToCourse("S6", "C3");
        teamService.addStudentToCourse("S9", "C3");
        teamService.addStudentToCourse("S6", "C5");
        teamService.addStudentToCourse("S7", "C5");
        teamService.addStudentToCourse("S8", "C5");
        teamService.addStudentToCourse("S9", "C5");
        System.out.println("----- C0 getCourse() + GetEnrolledStudents() -----");
        System.out.println(teamService.getCourse("C0"));
        System.out.println(teamService.getEnrolledStudents("C0"));
        System.out.println("----- C3 getCourse() + GetEnrolledStudents() -----");
        System.out.println(teamService.getCourse("C3"));
        System.out.println(teamService.getEnrolledStudents("C3"));
        
        System.out.println("------------------------- GetCourses(S3) ------------------------");
        System.out.println(teamService.getCourses("S3"));
        
        System.out.println("-------------------------  GetEnrolledStudents ------------------------------------");
        System.out.println(teamService.getEnrolledStudents("C0"));
        System.out.println(teamService.getEnrolledStudents("C8"));
        
        System.out.println("####################################################################################################");
        
        teamService.enableCourse("C0");
        teamService.enableCourse("C3");
        teamService.enableCourse("C5");
        System.out.println("------------------- proposeTeam: C0, C3, C5 ---------------");
        teamService.proposeTeam("C0", "Team1", Arrays.asList("S0", "S1", "S2", "S3"));
        teamService.proposeTeam("C0", "Team2", Arrays.asList("S1", "S2", "S3"));
        teamService.proposeTeam("C3", "Team3", Arrays.asList("S3", "S6"));
//        teamService.proposeTeam("C9", "TeamException1", Arrays.asList("S0")); // course C9 not enabled
//        teamService.proposeTeam("C33", "TeamException2", Arrays.asList("S0")); // course not found
//        teamService.proposeTeam("C1", "TeamException3", Arrays.asList("S99")); // student not found
//        teamService.proposeTeam("C0", "TeamException4", Arrays.asList("S9")); // non iscritto al corso
//        teamService.proposeTeam("C5", "TeamException5", Arrays.asList("S8","S8")); // duplicati lista partecipanti
//        teamService.proposeTeam("C5", "TeamException6", Arrays.asList("S8")); // vincolo min2
//        teamService.proposeTeam("C5", "TeamException7", Arrays.asList("S6","S7","S8","S9")); // vincolo max3
        teamService.proposeTeam("C5", "TeamExceptionOk", Arrays.asList("S8", "S9")); // ok
        teamService.proposeTeam("C5", "TeamExceptionOk", Arrays.asList("S7", "S8", "S9")); // ?
        teamService.enrollAll(Arrays.asList("S0", "S1", "S2", "S4"), "C3");
        teamService.proposeTeam("C3", "Team3", Arrays.asList("S0", "S1", "S2", "S3", "S6")); // overwritten test
        
        System.out.println("------------------- getTeamsForStudent S1, S3, S6---------------");
        System.out.println(teamService.getTeamsForStudent("S1"));
        System.out.println(teamService.getTeamsForStudent("S3"));
        System.out.println(teamService.getTeamsForStudent("S6"));
        
        System.out.println("------------ getMembers + getTeamForCourse + getStudentsInTeams + getAvailableStudents --------");
        for (CourseDTO courseDTO : teamService.getAllCourses()) {
          System.out.println("## Course " + courseDTO.getName() + " ##");
          System.out.println(teamService.getTeamForCourse(courseDTO.getName()));
          for (StudentDTO studentDTO : teamService.getStudentsInTeams(courseDTO.getName()))
            System.out.println("$$$$inTeam-" + studentDTO);
          try {
            for (StudentDTO studentDTO : teamService.getAvailableStudents(courseDTO.getName()))
              System.out.println("§noTeam-" + studentDTO);
          } catch (Exception e) {
            continue;
          }
          for (TeamDTO teamDTO : teamService.getTeamForCourse(courseDTO.getName())) {
            try {
              for (StudentDTO studentDTO : teamService.getMembers(teamDTO.getId())) {
                System.out.println("---" + studentDTO);
              }
            } catch (Exception e) {
              continue;
            }
          }
        }
        
        System.out.println("------------------- Final Test C9, S9, not being overwritten ---------------");
        CourseDTO c9r = new CourseDTO("C9", 33, 44, false);
        System.out.println(teamService.addCourse(c9r));
        StudentDTO s9r = new StudentDTO("S9", "REWRITE", "REWRITE");
        System.out.println(teamService.addStudent(s9r));
*/
      
      }
    };
  }
}