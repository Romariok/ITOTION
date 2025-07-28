package pet.itotion.fileService.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import pet.itotion.fileService.validation.ValidDimensions;

@Data
public class BlockCreationDTO {
   @NotEmpty
   String type;

   UUID parentId;

   @ValidDimensions
   List<String> dimensions;
}
