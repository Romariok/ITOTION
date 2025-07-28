package pet.itotion.fileService.model;

public enum Type {
   TEXT,
   HEADING_1,
   HEADING_2,
   HEADING_3,
   BULLET_LIST,
   NUMBERED_LIST,
   TO_DO,
   QUOTE,
   DIVIDER,
   TABLE,
   PAGE;

   public Type fromString(String type) {
      return Type.valueOf(type.toUpperCase());
   }

   public String toString() {
      return this.name();
   }

}
