package pet.notion.fileService.exception;

import lombok.Getter;

@Getter
public class BlockNotConsistentException extends RuntimeException{
   private final String reason;

   public BlockNotConsistentException(String reason, String message) {
       super(message);
       this.reason = reason;
   }
}
