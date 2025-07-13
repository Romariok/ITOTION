package pet.notion.fileService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler
   public ResponseEntity<ErrorResponse> handleBlockNotFoundException(BlockNotFoundException ex) {
      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(false, ex.getMessage()));
   }

   @ExceptionHandler
   public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(false, ex.getMessage()));
   }

   @ExceptionHandler
   public ResponseEntity<ErrorResponse> handleNumberFormatException(NumberFormatException ex) {
      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(false, ex.getMessage()));
   }

   @ExceptionHandler
   public ResponseEntity<ErrorResponse> handleBlockNotConsistentException(BlockNotConsistentException ex) {
      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(false, ex.getMessage()));
   }

}
