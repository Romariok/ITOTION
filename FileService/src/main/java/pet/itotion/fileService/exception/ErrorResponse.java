package pet.itotion.fileService.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
   private boolean success;
   private String message;
}
