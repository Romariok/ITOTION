package pet.notion.fileService.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DimensionsValidator.class)
public @interface ValidDimensions {
   String message() default "Invalid dimensions. List must be null or contain two integers between 1 and 12.";

   Class<?>[] groups() default {};

   Class<? extends Payload>[] payload() default {};
}