package it.polito.ai.es2.services.interfaces;

import it.polito.ai.es2.dtos.AssignmentDTO;
import it.polito.ai.es2.dtos.ImplementationDTO;
import it.polito.ai.es2.dtos.VmDTO;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface VLService {
  void createVm(@Valid VmDTO vmDTO);

  List<VmDTO> getTeamVms(@NotNull Long teamId);

  void changeStatusVm(@NotNull Long vmId, boolean newStatus);

  void editVm(@Valid VmDTO vmDTO);

  void deleteVm(@NotNull Long vmId);

  @PreAuthorize("hasRole('PROFESSOR')") List<AssignmentDTO> getAllAssignments(@NotNull String courseId);

  void updateImplementation(ImplementationDTO implementationDTO);

  @PreAuthorize("hasRole('STUDENT')") void uploadSubmission(@NotNull Long implId, @NotNull Long imageId);

  @PreAuthorize("hasRole('STUDENT')")
  void setStatusSubmissionToRead(Long assignmentId, Long studentId);
}
