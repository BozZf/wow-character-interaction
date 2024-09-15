package me.dio.wow_character_interaction.adapter.repository;

import me.dio.wow_character_interaction.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    User findByUsername(@Param("username") String username);

    Boolean existsByUsername(String username);

    @Modifying
    @Query("UPDATE User u u.fullname = :fullname WHERE u.username = :username")
    User updateFullName(@Param("username") String username, @Param("fullname") String fullName);

    @Modifying
    @Query("UPDATE User u u.password = :password WHERE u.username = :username")
    User updatePassword(@Param("username") String username, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u u.accountNonExpired = :state WHERE u.id = :id")
    void updateAccountNonExpired(@Param("id") Long id, @Param("state") Boolean state);

    @Modifying
    @Query("UPDATE User u u.accountNonLocked = :state WHERE u.id = :id")
    void updateAccountNonLocked(@Param("id") Long id, @Param("state") Boolean state);

    @Modifying
    @Query("UPDATE User u u.credentialsNonExpired = :state WHERE u.id = :id")
    void updateCredentialsNonExpired(@Param("id") Long id, @Param("state") Boolean state);

    @Modifying
    @Query("UPDATE User u u.enabled = :state WHERE u.id = :id")
    void updateEnabled(@Param("id") Long id, @Param("state") Boolean state);
}
