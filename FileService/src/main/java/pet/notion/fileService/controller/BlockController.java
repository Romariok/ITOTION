package pet.notion.fileService.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import pet.notion.fileService.dto.BlockCreationDTO;
import pet.notion.fileService.dto.BlockDTO;
import pet.notion.fileService.dto.BlockTitleUpdateDTO;
import pet.notion.fileService.dto.BlockTypeConversionDTO;
import pet.notion.fileService.model.Type;
import pet.notion.fileService.service.BlockService;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/file/block")
@RequiredArgsConstructor
public class BlockController {
   @Autowired
   private final BlockService blockService;

   @GetMapping("")
   public ResponseEntity<BlockDTO> getBlock(@RequestParam UUID id) {
      return ResponseEntity.ok(blockService.getBlock(id));
   }

   @PostMapping("/create")
   public ResponseEntity<BlockDTO> createBlock(@RequestBody @Valid BlockCreationDTO blockCreationDTO) {
      Type parsedType = Type.valueOf(blockCreationDTO.getType());
      return ResponseEntity
            .ok(blockService.createBlock(parsedType, blockCreationDTO.getParentId(), blockCreationDTO.getDimensions()));
   }

   @PatchMapping("/convert")
   public ResponseEntity<BlockDTO> convertBlockType(@RequestBody @Valid BlockTypeConversionDTO blockTypeConversionDTO) {
      Type parsedType = Type.valueOf(blockTypeConversionDTO.getNewType());
      return ResponseEntity.ok(blockService.convertBlockToTypeById(blockTypeConversionDTO.getId(), parsedType));
   }

   @PatchMapping("/title")
   public ResponseEntity<BlockDTO> changeBlockTitle(@RequestBody @Valid BlockTitleUpdateDTO blockTitleUpdateDTO) {
      return ResponseEntity
            .ok(blockService.changeTitle(blockTitleUpdateDTO.getId(), blockTitleUpdateDTO.getNewTitle()));
   }

   @DeleteMapping("")
   public ResponseEntity<Void> deleteBlock(@RequestParam UUID id) {
      blockService.deleteBlock(id);
      return ResponseEntity.ok().build();
   }

}
