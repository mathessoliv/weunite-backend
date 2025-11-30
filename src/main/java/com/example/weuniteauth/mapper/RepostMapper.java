package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.post.Repost;
import com.example.weuniteauth.dto.PostDTO;
import com.example.weuniteauth.dto.RepostDTO;
import com.example.weuniteauth.dto.ResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface RepostMapper {

    @Mapping(target = "id", source = "repost.id", resultType = String.class)
    @Mapping(target = "user", source = "repost.user")
    @Mapping(target = "post", source = "repost.post", qualifiedByName = "mapPostWithoutReposts")
    RepostDTO toRepostDTO(Repost repost);

    @Named("mapPostWithoutReposts")
    @Mapping(target = "id", source = "post.id", resultType = String.class)
    @Mapping(target = "text", source = "post.text")
    @Mapping(target = "imageUrl", source = "post.imageUrl")
    @Mapping(target = "videoUrl", source = "post.videoUrl")
    @Mapping(target = "likes", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "reposts", ignore = true)
    @Mapping(target = "createdAt", source = "post.createdAt")
    @Mapping(target = "updatedAt", source = "post.updatedAt")
    @Mapping(target = "user", source = "post.user")
    @Mapping(target = "repostedBy", ignore = true)
    @Mapping(target = "repostedAt", ignore = true)
    PostDTO mapPostWithoutReposts(Post post);

    default ResponseDTO<RepostDTO> toResponseDTO(String message, Repost repost) {
        RepostDTO repostDTO = toRepostDTO(repost);
        return new ResponseDTO<>(message, repostDTO);
    }

    default ResponseDTO<List<RepostDTO>> toResponseDTO(String message, Set<Repost> reposts) {
        List<RepostDTO> repostDTOs = mapReposts(reposts);
        return new ResponseDTO<>(message, repostDTOs);
    }

    default ResponseDTO<List<RepostDTO>> toResponseDTO(String message, List<Repost> reposts) {
        List<RepostDTO> repostDTOs = mapReposts(reposts);
        return new ResponseDTO<>(message, repostDTOs);
    }

    @Named("mapReposts")
    default List<RepostDTO> mapReposts(Set<Repost> reposts) {
        if (reposts == null || reposts.isEmpty()) {
            return List.of();
        }

        return reposts.stream()
                .map(this::toRepostDTO)
                .collect(Collectors.toList());
    }

    @Named("mapRepostsToList")
    default List<RepostDTO> mapReposts(List<Repost> reposts) {
        if (reposts == null || reposts.isEmpty()) {
            return List.of();
        }

        return reposts.stream()
                .map(this::toRepostDTO)
                .collect(Collectors.toList());
    }
}
