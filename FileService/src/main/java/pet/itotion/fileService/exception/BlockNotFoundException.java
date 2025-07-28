package pet.itotion.fileService.exception;

import lombok.Getter;

@Getter
public class BlockNotFoundException extends RuntimeException {
    private final String reason;

    public BlockNotFoundException(String reason, String message) {
        super(message);
        this.reason = reason;
    }
}
