package pet.notion.fileService.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pet.notion.fileService.model.Block;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface BlockRepository extends JpaRepository<Block, UUID> {

   @Modifying
   @Query("UPDATE Block b SET b.content = :content WHERE b.id = :id")
   @Transactional
   void setContentById(UUID id, List<UUID> content);

   @Modifying
   @Query("UPDATE Block b SET b.properties = :properties WHERE b.id = :id")
   @Transactional
   void setPropertiesById(UUID id, Map<String, List<String>> properties);

   @Modifying
   @Query("DELETE FROM Block b WHERE b.id IN :ids")
   @Transactional
   void deleteAllByIdIn(List<UUID> ids);
}
