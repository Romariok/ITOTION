package pet.notion.fileService.dto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import pet.notion.fileService.model.Type;
import lombok.Data;

@Data
public class BlockDTO {
   private UUID id;
   private Type type;
   private Map<String, List<String>> properties;
   private List<UUID> content;
   private UUID parentId;
}
