package pet.notion.fileService.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import java.lang.IllegalArgumentException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pet.notion.fileService.dto.BlockDTO;
import pet.notion.fileService.exception.BlockNotConsistentException;
import pet.notion.fileService.exception.BlockNotFoundException;
import pet.notion.fileService.mapper.BlockMapper;
import pet.notion.fileService.model.Block;
import pet.notion.fileService.model.Type;
import pet.notion.fileService.repository.BlockRepository;

@Service
@RequiredArgsConstructor
public class BlockService {
   private final BlockRepository blockRepository;
   private final BlockMapper blockMapper;

   @Transactional
   public BlockDTO createBlock(Type type, UUID parentId, List<String> dimensions) {
      Block block = new Block();
      block.setType(type);
      block.setParentId(parentId);

      Map<String, List<String>> properties = initProperties(type, dimensions);
      block.setProperties(properties);
      block.setContent(new ArrayList<>());
      block = blockRepository.save(block);

      if (type.equals(Type.TABLE)) {
         try {
            if (dimensions == null) {
               throw new IllegalArgumentException(
                     "For Table creation provide dimensions array with 2 integers between 1 and 12");
            }
            Integer axisX = Integer.parseInt(dimensions.get(0));
            Integer axisY = Integer.parseInt(dimensions.get(1));
            List<UUID> textBlocks = initTextBlocksForTable(axisX * axisY, block.getId());
            block.setContent(textBlocks);
         } catch (NumberFormatException e) {
            throw new NumberFormatException("Dimensions values must be Integers");
         } catch (Exception e) {
            throw e;
         }

      }
      block = blockRepository.save(block);
      if (parentId != null) {
         Block parentBlock = blockRepository.findById(parentId)
               .orElseThrow(() -> new BlockNotFoundException("BLOCK_NOT_FOUND", "Block " + parentId + " not found"));
         List<UUID> parentContent = parentBlock.getContent();
         parentContent.add(block.getId());
         blockRepository.setContentById(parentId, parentContent);
      }

      return blockMapper.toDTO(block);
   }

   @Transactional(readOnly = true)
   public BlockDTO getBlock(UUID id) {
      return blockMapper.toDTO(blockRepository.findById(id)
            .orElseThrow(() -> new BlockNotFoundException("BLOCK_NOT_FOUND", "Block not found")));
   }

   @Transactional
   public BlockDTO changeTitle(UUID id, String newTitle) {
      Block block = blockRepository.findById(id)
            .orElseThrow(() -> new BlockNotFoundException("BLOCK_NOT_FOUND", "Block not found"));
      if (block.getType().equals(Type.DIVIDER))
         throw new IllegalArgumentException("This block type doesn't contain title property");
      Map<String, List<String>> properties = block.getProperties();
      properties.put("title", new ArrayList<>(Collections.nCopies(1, newTitle)));
      block.setProperties(properties);
      block = blockRepository.save(block);
      return blockMapper.toDTO(block);
   }

   @Transactional
   public void deleteBlock(UUID id) {
      Block block = blockRepository.findById(id)
            .orElseThrow(() -> new BlockNotFoundException("BLOCK_NOT_FOUND", "Block not found"));

      for (UUID childUuid : block.getContent()) {
         List<UUID> deletedChildren = deleteBlockRecursive(childUuid, block);
         blockRepository.deleteAllByIdIn(deletedChildren);
      }

      if (block.getParentId() != null) {
         Block parentBlock = blockRepository.findById(block.getParentId())
               .orElseThrow(() -> new BlockNotFoundException("BLOCK_NOT_FOUND", "Parent block not found"));
         List<UUID> parentContentList = parentBlock.getContent();
         if (!parentContentList.remove(block.getId())) {
            // У блока есть родитель, но у родителя нет связи с этим блоком
            // TODO Проработать момент с удалением потерянного блока
            throw new BlockNotConsistentException("BLOCK_LOST", "Block is lost");
         }
         blockRepository.setContentById(parentBlock.getId(), parentContentList);
      }
      blockRepository.delete(block);
   }

   public List<UUID> deleteBlockRecursive(UUID id, Block parentBlock) {
      Block block = blockRepository.findById(id)
            .orElseThrow(() -> new BlockNotFoundException("BLOCK_NOT_FOUND", "Block not found"));

      List<UUID> deletedBlocks = new ArrayList<>();

      for (UUID childUuid : block.getContent()) {
         deletedBlocks.addAll(deleteBlockRecursive(childUuid, block));
      }

      return deletedBlocks;
   }

   @Transactional
   public BlockDTO convertBlockToTypeById(UUID id, Type type) {
      Block block = blockRepository.findById(id)
            .orElseThrow(() -> new BlockNotFoundException("BLOCK_NOT_FOUND", "Block not found"));
      if (block.getType().equals(Type.TABLE) || type.equals(Type.TABLE)) {
         throw new IllegalArgumentException("Table block can't be converted to anything and vice versa");
      }
      block.setType(type);
      Map<String, List<String>> newProperties = initProperties(type, null);
      if (!type.equals(Type.DIVIDER) || !block.getType().equals(Type.DIVIDER)) {
         List<String> oldTitle = block.getProperties().get("title");
         newProperties.put("title", oldTitle);
      }

      block.setProperties(newProperties);
      block = blockRepository.save(block);
      return blockMapper.toDTO(block);
   }

   @Transactional
   private List<UUID> initTextBlocksForTable(int size, UUID parentId) {
      List<UUID> resultList = new ArrayList<>();
      for (int i = 0; i < size; i++) {
         Block block = new Block();
         block.setType(Type.TEXT);
         block.setParentId(parentId);
         block.setProperties(initProperties(Type.TEXT, null));
         block.setContent(new ArrayList<>());
         block = blockRepository.save(block);
         resultList.add(block.getId());
      }
      return resultList;
   }

   private Map<String, List<String>> initProperties(Type type, List<String> dimensions) {
      Map<String, List<String>> properties = new HashMap<>();

      if (!type.equals(Type.DIVIDER))
         properties.put("title", new ArrayList<>(Collections.nCopies(1, "")));

      switch (type) {
         case Type.BULLET_LIST, Type.NUMBERED_LIST, Type.TO_DO:
            properties.put("checked", new ArrayList<>(Collections.nCopies(1, "No")));
            break;
         case Type.TABLE:
            properties.put("dimensions", dimensions);
            break;
         default:
            break;
      }
      return properties;
   }
}
