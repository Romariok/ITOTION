package pet.notion.fileService.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BlockCreationDTO {
   @NotEmpty
   String type;

   UUID parentId;
   List<String> dimensions;
}
