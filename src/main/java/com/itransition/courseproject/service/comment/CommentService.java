package com.itransition.courseproject.service.comment;

import com.itransition.courseproject.dto.request.comment.CommentCreationRequest;
import com.itransition.courseproject.dto.response.APIResponse;
import com.itransition.courseproject.dto.response.comment.CommentResponse;
import com.itransition.courseproject.exception.ResourceNotFoundException;
import com.itransition.courseproject.exception.user.UserNotFoundException;
import com.itransition.courseproject.model.entity.collection.Item;
import com.itransition.courseproject.model.entity.comment.Comment;
import com.itransition.courseproject.model.entity.user.User;
import com.itransition.courseproject.repository.CommentRepository;
import com.itransition.courseproject.repository.UserRepository;
import com.itransition.courseproject.repository.collection.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.itransition.courseproject.util.constant.ResourceConstants.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ModelMapper modelMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    public CommentResponse create(CommentCreationRequest commentCreationRequest) {

        User user = userRepository.findById(commentCreationRequest.getUserId()).orElseThrow(() -> {
            throw new UserNotFoundException(commentCreationRequest.getUserId());
        });
        Item item = itemRepository.findById(commentCreationRequest.getItemId()).orElseThrow(() -> {
            throw new ResourceNotFoundException(ITEM_ENG,ITEM_RUS,commentCreationRequest.getItemId());
        });

        return modelMapper.map(commentRepository
                        .save(new Comment(commentCreationRequest.getText(), user, item)),
                        CommentResponse.class);
    }

    public APIResponse get(Long id) {
        return APIResponse.success(commentRepository
                .findById(id)
                .orElseThrow(()-> {throw new ResourceNotFoundException(COMMENT_ENG,COMMENT_RUS, id);}));
    }

    public APIResponse getCommentsByItemId(Long itemId) {
        return APIResponse.success(List.of(modelMapper.map(commentRepository.findAllByItemId(itemId), CommentResponse[].class)));
    }
}
