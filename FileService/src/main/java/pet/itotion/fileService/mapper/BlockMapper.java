package pet.itotion.fileService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import pet.itotion.fileService.dto.BlockDTO;
import pet.itotion.fileService.model.Block;

@Mapper(componentModel = "spring")
public interface BlockMapper {
   BlockMapper INSTANCE = Mappers.getMapper(BlockMapper.class);

   BlockDTO toDTO(Block block);

   Block toEntity(BlockDTO blockDTO);
}
