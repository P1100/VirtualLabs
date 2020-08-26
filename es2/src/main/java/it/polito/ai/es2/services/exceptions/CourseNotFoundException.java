package it.polito.ai.es2.services.exceptions;

public class CourseNotFoundException extends RuntimeException {
  public CourseNotFoundException() {
  }
  
  public CourseNotFoundException(String courseId) {
    super("Course not found! (" + courseId + ")");
  }
}
