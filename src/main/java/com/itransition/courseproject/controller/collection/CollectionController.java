package com.itransition.courseproject.controller.collection;

import com.itransition.courseproject.controller.CRUDController;
import com.itransition.courseproject.dto.request.collection.CollectionRequest;
import com.itransition.courseproject.exception.FileProcessingException;
import com.itransition.courseproject.service.collection.CollectionService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.itransition.courseproject.controller.ControllerUtils.COLLECTION_URI;


@RestController
@RequestMapping(COLLECTION_URI)
public class CollectionController extends CRUDController<CollectionService, Long, CollectionRequest> {

    protected CollectionController(CollectionService service) {
        super(service);
    }

//    @GetMapping
//    public ResponseEntity<?> getList(
//        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//        @RequestParam(name = "size", required = false, defaultValue = "20") Integer size,
//        @RequestParam(name = "order", required = false, defaultValue = "DESC") String order,
//        @RequestParam(name = "categories", required = false) String[] categories
//    ){
//        return ResponseEntity.ok(service.getList(page, size, order, categories));
//    }

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestCollections(){
        return ResponseEntity.ok(service.getLatestCollections());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserCollections(@PathVariable("userId") Long id){
        return ResponseEntity.ok(service.getUserCollections(id));
    }

    @GetMapping("/csv")
    public ResponseEntity<?> createCSV(
            @RequestParam(name = "collection_id") Long collectionId,
            @RequestParam(name = "lang") String lang
    ) {
        Resource resource = service.loadCSVFile(collectionId, lang);
        try {
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\"" + resource.getFilename() + "\"";
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue).body(resource);

        } finally {
            try {
                service.scheduleForDeletion(resource.getFile().toPath(), 60);
            } catch (IOException e) {
                throw new FileProcessingException("csv file creation error", "ошибка создания файла csv");
            }
        }
    }
}
