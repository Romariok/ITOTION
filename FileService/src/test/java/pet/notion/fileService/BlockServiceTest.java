package pet.notion.fileService;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.notion.fileService.mapper.BlockMapper;
import pet.notion.fileService.repository.BlockRepository;
import pet.notion.fileService.service.BlockService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.DisplayName;
import pet.notion.fileService.dto.BlockDTO;
import pet.notion.fileService.model.Block;
import pet.notion.fileService.model.Type;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты для BlockService")
public class BlockServiceTest {

   @Mock
   private BlockRepository blockRepository;

   @Mock
   private BlockMapper blockMapper;

   @InjectMocks
   private BlockService blockService;

   static Stream<Type> onlyTitleTypes() {
      return Stream.of(Type.BULLET_LIST, Type.HEADING_1, Type.HEADING_2, Type.HEADING_3,
            Type.NUMBERED_LIST,
            Type.PAGE, Type.QUOTE, Type.TEXT, Type.TO_DO);
   }

   @Nested
   @DisplayName("Тесты создания блоков")
   class BlockCreationTests {

      @ParameterizedTest
      @MethodSource("pet.notion.fileService.BlockServiceTest#onlyTitleTypes")
      @DisplayName("Создание блока с заголовком")
      void blockHaveTitle(Type type) {
         Block block = new Block();
         block.setType(type);
         block.setProperties(Map.of("title", List.of("")));

         BlockDTO blockDTO = new BlockDTO();
         blockDTO.setType(type);
         blockDTO.setProperties(Map.of("title", List.of("")));

         when(blockRepository.save(any(Block.class))).thenReturn(block);
         when(blockMapper.toDTO(any(Block.class))).thenReturn(blockDTO);

         BlockDTO newBlock = blockService.createBlock(type, null, null);
         assertTrue(newBlock.getProperties().containsKey("title"));
      }

      @Test
      @DisplayName("Успешное создание блока таблицы")
      void createTableBlock_Success() {
         Type type = Type.TABLE;
         List<String> dimensions = List.of("2", "3");

         Block block = new Block();
         block.setId(UUID.randomUUID());
         block.setType(type);
         block.setProperties(Map.of("dimensions", dimensions));
         block.setContent(new ArrayList<>());

         BlockDTO blockDTO = new BlockDTO();
         blockDTO.setType(type);
         blockDTO.setProperties(Map.of("dimensions", dimensions));

         when(blockRepository.save(any(Block.class))).thenReturn(block);
         when(blockMapper.toDTO(any(Block.class))).thenReturn(blockDTO);

         BlockDTO newBlock = blockService.createBlock(type, null, dimensions);

         assertEquals(type, newBlock.getType());
         assertTrue(newBlock.getProperties().containsKey("dimensions"));
         assertEquals(dimensions, newBlock.getProperties().get("dimensions"));
      }

      @Test
      @DisplayName("Создание таблицы с нулевыми размерами должно выбрасывать исключение")
      void createTableBlock_throwsException_whenDimensionsAreNull() {
         Type type = Type.TABLE;
         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blockService.createBlock(type, null, null);
         });

         String expectedMessage = "For Table creation provide dimensions array with 2 integers between 1 and 12";
         String actualMessage = exception.getMessage();

         assertTrue(actualMessage.contains(expectedMessage));
      }

      @Test
      @DisplayName("Создание текстового блока с родителем")
      void createTextBlock_withParent() {
         Type type = Type.TEXT;
         UUID parentId = UUID.randomUUID();

         Block block = new Block();
         block.setType(type);
         block.setParentId(parentId);

         BlockDTO blockDTO = new BlockDTO();
         blockDTO.setType(type);
         blockDTO.setParentId(parentId);

         Block parentBlock = new Block();
         parentBlock.setId(parentId);
         parentBlock.setContent(new ArrayList<>());

         when(blockRepository.save(any(Block.class))).thenReturn(block);
         when(blockRepository.findById(parentId)).thenReturn(java.util.Optional.of(parentBlock));
         when(blockMapper.toDTO(any(Block.class))).thenReturn(blockDTO);

         BlockDTO newBlock = blockService.createBlock(type, parentId, null);

         assertEquals(parentId, newBlock.getParentId());
      }
   }

   @Nested
   @DisplayName("Тесты обновления заголовка")
   class TitleUpdateTests {
      @Test
      @DisplayName("Успешное изменение заголовка")
      void changeTitle_Success() {
         UUID blockId = UUID.randomUUID();
         String newTitle = "New Title";

         Block block = new Block();
         block.setId(blockId);
         block.setType(Type.TEXT);
         block.setProperties(new java.util.HashMap<>(Map.of("title", List.of("Old Title"))));

         BlockDTO blockDTO = new BlockDTO();
         blockDTO.setId(blockId);
         blockDTO.setProperties(Map.of("title", List.of(newTitle)));

         when(blockRepository.findById(blockId)).thenReturn(java.util.Optional.of(block));
         when(blockRepository.save(any(Block.class))).thenReturn(block);
         when(blockMapper.toDTO(any(Block.class))).thenReturn(blockDTO);

         BlockDTO updatedBlock = blockService.changeTitle(blockId, newTitle);

         assertEquals(newTitle, updatedBlock.getProperties().get("title").get(0));
      }

      @Test
      @DisplayName("Изменение заголовка для типа DIVIDER должно выбрасывать исключение")
      void changeTitle_throwsException_forDividerType() {
         UUID blockId = UUID.randomUUID();
         String newTitle = "New Title";

         Block block = new Block();
         block.setId(blockId);
         block.setType(Type.DIVIDER);

         when(blockRepository.findById(blockId)).thenReturn(java.util.Optional.of(block));

         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blockService.changeTitle(blockId, newTitle);
         });

         String expectedMessage = "This block type doesn't contain title property";
         String actualMessage = exception.getMessage();

         assertTrue(actualMessage.contains(expectedMessage));
      }
   }

   @Nested
   @DisplayName("Тесты конвертации блоков")
   class BlockConversionTests {

      @Test
      @DisplayName("Успешная конвертация из TEXT в QUOTE")
      void convertBlock_fromTextToQuote_Success() {
         UUID blockId = UUID.randomUUID();
         Type newType = Type.QUOTE;
         String title = "Some text";

         Block block = new Block();
         block.setId(blockId);
         block.setType(Type.TEXT);
         block.setProperties(new java.util.HashMap<>(Map.of("title", List.of(title))));

         BlockDTO blockDTO = new BlockDTO();
         blockDTO.setId(blockId);
         blockDTO.setType(newType);
         blockDTO.setProperties(Map.of("title", List.of(title)));

         when(blockRepository.findById(blockId)).thenReturn(java.util.Optional.of(block));
         when(blockRepository.save(any(Block.class))).thenReturn(block);
         when(blockMapper.toDTO(any(Block.class))).thenReturn(blockDTO);

         BlockDTO convertedBlock = blockService.convertBlockToTypeById(blockId, newType);

         assertEquals(newType, convertedBlock.getType());
         assertTrue(convertedBlock.getProperties().containsKey("title"));
         assertEquals(title, convertedBlock.getProperties().get("title").get(0));
      }

      @Test
      @DisplayName("Конвертация из TEXT в BULLET_LIST добавляет свойство 'checked'")
      void convertBlock_fromTextToBulletList_addsCheckedProperty() {
         UUID blockId = UUID.randomUUID();
         Type newType = Type.BULLET_LIST;
         String title = "List item";

         Block block = new Block();
         block.setId(blockId);
         block.setType(Type.TEXT);
         block.setProperties(new java.util.HashMap<>(Map.of("title", List.of(title))));

         BlockDTO blockDTO = new BlockDTO();
         blockDTO.setId(blockId);
         blockDTO.setType(newType);
         blockDTO.setProperties(Map.of("title", List.of(title), "checked", List.of("No")));

         when(blockRepository.findById(blockId)).thenReturn(java.util.Optional.of(block));
         when(blockRepository.save(any(Block.class))).thenReturn(block);
         when(blockMapper.toDTO(any(Block.class))).thenReturn(blockDTO);

         BlockDTO convertedBlock = blockService.convertBlockToTypeById(blockId, newType);

         assertEquals(newType, convertedBlock.getType());
         assertTrue(convertedBlock.getProperties().containsKey("title"));
         assertTrue(convertedBlock.getProperties().containsKey("checked"));
         assertEquals("No", convertedBlock.getProperties().get("checked").get(0));
      }

      @Test
      @DisplayName("Конвертация в TABLE должна выбрасывать исключение")
      void convertBlock_toTable_throwsException() {
         UUID blockId = UUID.randomUUID();
         Type newType = Type.TABLE;

         Block block = new Block();
         block.setId(blockId);
         block.setType(Type.TEXT);

         when(blockRepository.findById(blockId)).thenReturn(java.util.Optional.of(block));

         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blockService.convertBlockToTypeById(blockId, newType);
         });

         String expectedMessage = "Table block can't be converted to anything and vice versa";
         String actualMessage = exception.getMessage();

         assertTrue(actualMessage.contains(expectedMessage));
      }

      @Test
      @DisplayName("Конвертация из TABLE должна выбрасывать исключение")
      void convertBlock_fromTable_throwsException() {
         UUID blockId = UUID.randomUUID();
         Type newType = Type.TEXT;

         Block block = new Block();
         block.setId(blockId);
         block.setType(Type.TABLE);

         when(blockRepository.findById(blockId)).thenReturn(java.util.Optional.of(block));

         Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            blockService.convertBlockToTypeById(blockId, newType);
         });

         String expectedMessage = "Table block can't be converted to anything and vice versa";
         String actualMessage = exception.getMessage();

         assertTrue(actualMessage.contains(expectedMessage));
      }
   }

   @Nested
   @DisplayName("Тесты удаления блоков")
   class BlockDeletionTests {

      @Test
      @DisplayName("Успешное удаление простого блока")
      void deleteBlock_simpleBlock_Success() {
         UUID blockId = UUID.randomUUID();
         Block block = new Block();
         block.setId(blockId);
         block.setContent(new ArrayList<>());

         when(blockRepository.findById(blockId)).thenReturn(java.util.Optional.of(block));

         assertDoesNotThrow(() -> {
            blockService.deleteBlock(blockId);
         });
      }

      @Test
      @DisplayName("Успешное удаление блока с дочерними элементами")
      void deleteBlock_withChildren_Success() {
         UUID parentId = UUID.randomUUID();
         UUID child1Id = UUID.randomUUID();
         UUID child2Id = UUID.randomUUID();

         Block parentBlock = new Block();
         parentBlock.setId(parentId);
         parentBlock.setContent(new ArrayList<>(List.of(child1Id, child2Id)));

         Block child1 = new Block();
         child1.setId(child1Id);
         child1.setContent(new ArrayList<>());
         child1.setParentId(parentId);

         Block child2 = new Block();
         child2.setId(child2Id);
         child2.setContent(new ArrayList<>());
         child2.setParentId(parentId);

         when(blockRepository.findById(parentId)).thenReturn(java.util.Optional.of(parentBlock));
         when(blockRepository.findById(child1Id)).thenReturn(java.util.Optional.of(child1));
         when(blockRepository.findById(child2Id)).thenReturn(java.util.Optional.of(child2));

         assertDoesNotThrow(() -> {
            blockService.deleteBlock(parentId);
         });
      }
   }
}
