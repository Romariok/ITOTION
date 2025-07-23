package pet.notion.fileService.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BlockTypeConversionDTO {
   @NotNull
   private UUID id;

   @NotNull
   private String newType;
}