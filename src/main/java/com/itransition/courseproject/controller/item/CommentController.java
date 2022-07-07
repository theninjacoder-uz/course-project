package com.itransition.courseproject.controller.item;

import com.itransition.courseproject.dto.request.comment.CommentRequest;
import com.itransition.courseproject.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.itransition.courseproject.controller.ControllerUtils.COMMENT_URI;

@RestController
@RequiredArgsConstructor
@RequestMapping(COMMENT_URI)
public class CommentController {

    private final CommentService commentService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping()
    public ResponseEntity<Void> sendMessage(@RequestBody CommentRequest request) {
        simpMessagingTemplate.convertAndSend("/topic/comment/" + request.getItemId(), commentService.create(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @MessageMapping("/comment") // api/comment
    public ResponseEntity<Void> sendComment(@Payload CommentRequest request) {
        simpMessagingTemplate.convertAndSend("/topic/comment/" + request.getItemId(), commentService.create(request));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
