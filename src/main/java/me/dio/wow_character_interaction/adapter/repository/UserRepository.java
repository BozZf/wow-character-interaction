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
    @Query("UPDATE User u SET u.fullName = :full_name WHERE u.username = :username")
    void updateFullName(@Param("username") String username, @Param("full_name") String fullName);

    @Modifying
    @Query("UPDATE User u SET u.password = :password WHERE u.username = :username")
    void updatePassword(@Param("username") String username, @Param("password") String password);

    @Modifying
    @Query("UPDATE User u SET u.accountNonExpired = :state WHERE u.id = :id")
    void updateAccountNonExpired(@Param("id") Long id, @Param("state") Boolean state);

    @Modifying
    @Query("UPDATE User u SET u.accountNonLocked = :state WHERE u.id = :id")
    void updateAccountNonLocked(@Param("id") Long id, @Param("state") Boolean state);

    @Modifying
    @Query("UPDATE User u SET u.credentialsNonExpired = :state WHERE u.id = :id")
    void updateCredentialsNonExpired(@Param("id") Long id, @Param("state") Boolean state);

    @Modifying
    @Query("UPDATE User u SET u.enabled = :state WHERE u.id = :id")
    void updateEnabled(@Param("id") Long id, @Param("state") Boolean state);
}
