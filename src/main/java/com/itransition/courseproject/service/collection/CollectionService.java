package com.itransition.courseproject.service.collection;

import com.itransition.courseproject.dto.request.collection.CollectionRequest;
import com.itransition.courseproject.dto.request.field.FieldRequest;
import com.itransition.courseproject.dto.response.APIResponse;
import com.itransition.courseproject.dto.response.collection.CollectionResponse;
import com.itransition.courseproject.dto.response.collection.FieldResponse;
import com.itransition.courseproject.exception.FileProcessingException;
import com.itransition.courseproject.exception.ResourceNotFoundException;
import com.itransition.courseproject.exception.user.UserNotFoundException;
import com.itransition.courseproject.model.entity.collection.Collection;
import com.itransition.courseproject.model.entity.collection.Field;
import com.itransition.courseproject.model.entity.collection.FieldValue;
import com.itransition.courseproject.model.entity.collection.Topic;
import com.itransition.courseproject.model.entity.user.User;
import com.itransition.courseproject.repository.UserRepository;
import com.itransition.courseproject.repository.collection.*;
import com.itransition.courseproject.service.CRUDService;
import com.itransition.courseproject.util.CSVUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.itransition.courseproject.util.constant.ResourceConstants.*;


@Service
@RequiredArgsConstructor
public class CollectionService implements CRUDService<Long, CollectionRequest> {

    private final CollectionRepository collectionRepository;
    private final TopicRepository topicRepository;
    private final FieldRepository fieldRepository;
    private final FieldValueRepository fieldValueRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private static final TimeUnit UNITS = TimeUnit.SECONDS;
    private static final  String DEFAULT_SORT_FIELD = "creationDate";


    @Override
    public APIResponse create(CollectionRequest collectionRequest) {
        User user = userRepository.findById(collectionRequest.getUserId()).orElseThrow(() -> {
            throw new UserNotFoundException(String.valueOf(collectionRequest.getUserId()));
        });
        Topic topic = topicRepository.findById(collectionRequest.getTopicId()).orElseThrow(() -> {
            throw new ResourceNotFoundException(TOPIC_ENG, TOPIC_RUS, String.valueOf(collectionRequest.getTopicId()));
        });
        Collection collection = collectionRepository.save(
                new Collection(
                        collectionRequest.getName(),
                        collectionRequest.getDescription(),
                        collectionRequest.getImageUrl(),
                        topic,
                        user
                )
        );
        return APIResponse.success(getCollectionResponse(collection,
                collectionRequest.getCustomFields()));
    }

    @Override
    public APIResponse get(Long id) {
        final Collection collection = collectionRepository.findById(id).orElseThrow(() -> {
            throw new ResourceNotFoundException(COLLECTION_ENG, COLLECTION_RUS, String.valueOf(id));
        });
        final FieldResponse[] fieldList = modelMapper.map(fieldRepository.findAllByCollection(collection), FieldResponse[].class);
        final CollectionResponse response = modelMapper.map(collection, CollectionResponse.class);

        response.setFields(fieldList);
        return APIResponse.success(response);
    }

//    public APIResponse getList(int page, int size, String order, String[] categories) {
//        categories = (categories == null || categories.length == 0) ? new String[]{DEFAULT_SORT_FIELD} : categories;
//        Page<Collection> list = collectionRepository
//                .findAll(PageRequest.of(page, size, Sort.Direction.valueOf(order), categories));
//        return APIResponse.success(getCollectionResponseList(list.getContent()));
//    }

    @Override
    public APIResponse update(Long id, CollectionRequest collectionRequest) {
        User user = userRepository.findById(collectionRequest.getUserId()).orElseThrow(() -> {
            throw new UserNotFoundException(String.valueOf(collectionRequest.getUserId()));
        });
        Topic topic = topicRepository.findById(collectionRequest.getTopicId()).orElseThrow(() -> {
            throw new ResourceNotFoundException(TOPIC_ENG, TOPIC_RUS, String.valueOf(collectionRequest.getTopicId()));
        });
        Collection collection = collectionRepository.findById(id).orElseThrow(() -> {
            throw new ResourceNotFoundException(COLLECTION_ENG, COLLECTION_RUS, String.valueOf(id));
        });
        collection.setImageUrl(collectionRequest.getImageUrl());
        collection.setDescription(collection.getDescription());
        collection.setName(collectionRequest.getName());
        collection.setTopic(topic);
        collection.setUser(user);
        collection = collectionRepository.save(collection);
        return APIResponse.success(getCollectionResponse(collection,
                collectionRequest.getCustomFields()));
    }

    @Override
    public APIResponse delete(Long id) {
        collectionRepository.deleteById(id);
        fieldRepository.deleteAllByCollection_Id(id);
        itemRepository.deleteAllByCollection_Id(id);
        return APIResponse.success(true);
    }

    @Override
    public APIResponse getAll() {
        return APIResponse.success(getCollectionResponseList(collectionRepository.findAll()));
    }

    public APIResponse getLatestCollections() {
        List<Long> collectionIds = fieldRepository.findTopCollectionIds();
        return APIResponse.success(getCollectionResponseList(collectionRepository.findAllById(collectionIds)));
    }

    public APIResponse getUserCollections(Long userId) {
        List<Collection> collectionList = collectionRepository.findAllByUser_Id(userId);
        return APIResponse.success(getCollectionResponseList(collectionList));
    }

    private CollectionResponse getCollectionResponse(Collection collection, FieldRequest[] fieldRequests) {
        CollectionResponse response = modelMapper.map(collection, CollectionResponse.class);
        response.setFields(saveCollectionFields(collection, fieldRequests));
        return response;
    }

    private FieldResponse[] saveCollectionFields(Collection collection, FieldRequest[] fieldRequests) {
        List<Field> fields = List.of(modelMapper.map(fieldRequests, Field[].class));
        fields.forEach(field -> field.setCollection(collection));
        fields = fieldRepository.saveAll(fields);
        return modelMapper.map(fields, FieldResponse[].class);
    }

    private List<CollectionResponse> getCollectionResponseList(List<Collection> collectionList) {
        return collectionList
                .stream()
                .map(collection -> {
                    CollectionResponse response = modelMapper.map(collection, CollectionResponse.class);
                    return response;
                }).collect(Collectors.toList());
    }

    public Resource loadCSVFile(long collectionId, String lang) {
        Collection collection = collectionRepository.findById(collectionId).orElseThrow(() -> {
            throw new ResourceNotFoundException(COLLECTION_ENG, COLLECTION_RUS, String.valueOf(collectionId));
        });
        List<Field> fieldList = fieldRepository.findAllByCollection_IdOrderByIdAsc(collectionId);
        List<FieldValue> fieldValueList = fieldValueRepository.findAllByItem_CollectionIdOrderByItem_IdAscField_IdAsc(collectionId);
        String filePath = CSVUtil.ofCollection(lang, collection, fieldList, fieldValueList);
        return CSVUtil.load(filePath);
    }

    public void scheduleForDeletion(Path path, long delay) {
        executor.schedule(() -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new FileProcessingException("csv file creation error", "ошибка создания файла csv");
            }
        }, delay, UNITS);
    }
}
