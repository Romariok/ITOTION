package pet.notion.fileService.model;

import java.util.List;
import java.util.Map;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class Block {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Enumerated(EnumType.STRING)
   private Type type;

   private Map<String, Object> properties;

   private List<String> content;

   private String parentId;
}
