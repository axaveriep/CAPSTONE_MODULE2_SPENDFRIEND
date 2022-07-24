package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tenmo_user")
public class User {

   @Id
   @Column(name="USER_ID")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   @Column(name="USERNAME")
   private String username;
   @Column(name="PASSWORD_HASH")
   private String password;

   /* We used the transient annotation to tell JPA this variable doesn't have a column in the table */
   @Transient
   private boolean activated = true;

   @Transient
   private Set<Authority> authorities = new HashSet<>();

   /* Joining an Account and User with a relationship annotation we can see a user's account when we call them */
   @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
   @JsonIgnoreProperties("balance")
   private Account account;

   @Autowired
   public User(Long id, String username, String password, boolean activated, Set<Authority> authorities, Account account) {
      this.id = id;
      this.username = username;
      this.password = password;
      this.activated = activated;
      this.authorities = authorities;
      this.account = account;
   }

   public void setAuthorities(String authorities) {
      String[] roles = authorities.split(",");
      for(String role : roles) {
         this.authorities.add(new Authority("ROLE_" + role));
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      User user = (User) o;
      return id == user.id &&
              activated == user.activated &&
              Objects.equals(username, user.username) &&
              Objects.equals(password, user.password) &&
              Objects.equals(authorities, user.authorities);
   }

   @Override
   public int hashCode() {
      return Objects.hash(id, username, password, activated, authorities);
   }

   @Override
   public String toString() {
      return "User{" +
              "id=" + id +
              ", username='" + username + '\'' +
              ", activated=" + activated +
              ", authorities=" + authorities +
              '}';
   }
}
