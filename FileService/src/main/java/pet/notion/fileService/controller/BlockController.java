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
   public ResponseEntity<BlockDTO> createBlock(@RequestBody @Valid BlockCreationDTO blockCreationDTO){
      Type parsedType = Type.valueOf(blockCreationDTO.getType());
      return ResponseEntity.ok(blockService.createBlock(parsedType, blockCreationDTO.getParentId(), blockCreationDTO.getDimensions()));
   }

   @PatchMapping("/convert")
   public ResponseEntity<BlockDTO> convertBlockType(@RequestParam String newType, @RequestParam UUID id){
      Type parsedType = Type.valueOf(newType);
      return ResponseEntity.ok(blockService.convertBlockToTypeById(id, parsedType));
   }

   @PatchMapping("/title")
   public ResponseEntity<BlockDTO> changeBlockTitle(@RequestParam String newTitle, @RequestParam UUID id){
      return ResponseEntity.ok(blockService.changeTitle(id, newTitle));
   }

   @DeleteMapping("")
   public ResponseEntity<Void> deleteBlock(@RequestParam UUID id){
      blockService.deleteBlock(id);
      return ResponseEntity.ok().build();
   }

   
}
