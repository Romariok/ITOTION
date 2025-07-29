package pet.itotion.authService.model;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "blocks")
public class Block {
   @Id
   @GeneratedValue
   @Column(columnDefinition = "uuid", updatable = false, nullable = false)
   private UUID id;

   @Enumerated(EnumType.STRING)
   @Column(name = "type", nullable = false)
   private Type type;

   @JdbcTypeCode(SqlTypes.JSON)
   @Column(name = "properties", nullable = false)
   private Map<String, List<String>> properties;

   @Column(name = "content", nullable = false)
   private List<UUID> content;

   @Column(name = "parentId", nullable = true)
   private UUID parentId;
}
