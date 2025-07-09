package pet.notion.fileService.model;

public enum Type {
   TEXT,
   HEADING_1,
   HEADING_2,
   HEADING_3,
   BULLET_LIST,
   NUMBERED_LIST,
   TO_DO,
   QUOTE,
   CODE,
   DIVIDER,
   TABLE;

   public static Type fromString(String type) {
      return Type.valueOf(type.toUpperCase());
   }
   
}
