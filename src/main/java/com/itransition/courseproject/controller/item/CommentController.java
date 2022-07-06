package com.itransition.courseproject.controller.item;

import com.itransition.courseproject.dto.request.comment.CommentCreationRequest;
import com.itransition.courseproject.service.comment.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.itransition.courseproject.controller.ControllerUtils.COMMENT_URI;

@RestController
@RequestMapping(COMMENT_URI)
public class CommentController {

    private final CommentService commentService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public CommentController(CommentService commentService, SimpMessagingTemplate simpMessagingTemplate) {
        this.commentService = commentService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @PostMapping()
    public ResponseEntity<Void> sendMessage(@RequestBody CommentCreationRequest request) {
        simpMessagingTemplate.convertAndSend("/topic/comments/"+request.getItemId(), commentService.create(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
