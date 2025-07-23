package pet.notion.fileService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

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

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
      String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));
      return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(false, errorMessage));
   }

}
