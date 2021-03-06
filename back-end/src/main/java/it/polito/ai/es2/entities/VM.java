package it.polito.ai.es2.entities;

import it.polito.ai.es2.dtos.validators.TeamVmConstrains;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@TeamVmConstrains
public class VM {
  @Id
  @GeneratedValue
  private Long id;
  @PositiveOrZero
  private int vcpu;
  @PositiveOrZero
  private int disk;
  @PositiveOrZero
  private int ram;
  private boolean active = false;
// --> vmModel saved in course

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false)
  private Team team; // --> course

  @ManyToOne()
  @JoinColumn
  private Student creator;

  @ManyToMany()
  @JoinTable
  private List<Student> sharedOwners = new ArrayList<>();

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn
  private Image imageVm;

  public void addSetTeam(Team x) {
    if (team != null)
      throw new RuntimeException("JPA-Team: overriding a OneToOne or ManyToOne field might be an error");
    team = x;
    x.getVms().add(this);
  }

  public void addSetCreator(Student x) {
    if (creator != null)
      throw new RuntimeException("JPA-Team: It's not possible to change course for a team");
    creator = x;
    x.getVmsCreated().add(this);
  }

  public void addShareOwner(Student x) {
    sharedOwners.add(x);
    x.getVmsOwned().add(this);
  }

  public void removeSharedOwner(Student x) {
    sharedOwners.remove(x);
    x.getVmsOwned().add(this);
  }

  public void addSetImage(Image x) {
    if (imageVm != null)
      throw new RuntimeException("JPA-Team: overriding a OneToOne or ManyToOne field might be an error");
    imageVm = x;
    x.setVm(this);
  }
}
