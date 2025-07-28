package pet.itotion.fileService.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class DimensionsValidator implements ConstraintValidator<ValidDimensions, List<String>> {

   @Override
   public boolean isValid(List<String> dimensions, ConstraintValidatorContext context) {
      if (dimensions == null) {
         return true;
      }

      if (dimensions.size() != 2) {
         return false;
      }

      try {
         int first = Integer.parseInt(dimensions.get(0));
         int second = Integer.parseInt(dimensions.get(1));

         return first >= 1 && first <= 12 && second >= 1 && second <= 12;
      } catch (NumberFormatException e) {
         return false;
      }
   }
}