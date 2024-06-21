package me.dio.wow_character_interaction.domain.repository;

import me.dio.wow_character_interaction.domain.model.WowCharacter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WowCharacterRepository extends JpaRepository<WowCharacter, Long> {

    boolean existsByName(String name);
}
