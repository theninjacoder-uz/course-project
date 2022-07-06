package com.itransition.courseproject.service.collection;

import com.itransition.courseproject.dto.request.collection.ItemRequest;
import com.itransition.courseproject.dto.request.collection.ItemUpdateRequest;
import com.itransition.courseproject.dto.request.field.FieldValueRequest;
import com.itransition.courseproject.dto.response.APIResponse;
import com.itransition.courseproject.dto.response.collection.FieldResponse;
import com.itransition.courseproject.dto.response.comment.CommentResponse;
import com.itransition.courseproject.dto.response.field.FieldValueListResponse;
import com.itransition.courseproject.dto.response.field.FieldValueResponse;
import com.itransition.courseproject.dto.response.item.ItemResponse;
import com.itransition.courseproject.dto.response.item.ItemResponseByTag;
import com.itransition.courseproject.dto.response.tag.TagResponse;
import com.itransition.courseproject.exception.ResourceNotFoundException;
import com.itransition.courseproject.model.entity.collection.Collection;
import com.itransition.courseproject.model.entity.collection.Field;
import com.itransition.courseproject.model.entity.collection.FieldValue;
import com.itransition.courseproject.model.entity.collection.Item;
import com.itransition.courseproject.model.entity.comment.Comment;
import com.itransition.courseproject.model.entity.tag.Tag;
import com.itransition.courseproject.repository.CommentRepository;
import com.itransition.courseproject.repository.collection.CollectionRepository;
import com.itransition.courseproject.repository.collection.FieldRepository;
import com.itransition.courseproject.repository.collection.FieldValueRepository;
import com.itransition.courseproject.repository.collection.ItemRepository;
import com.itransition.courseproject.repository.tag.TagRepository;
import com.itransition.courseproject.service.CRUDService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.itransition.courseproject.util.constant.ResourceConstants.*;

@Service
@RequiredArgsConstructor
public class ItemService implements CRUDService<Long, ItemRequest> {

    private final ModelMapper modelMapper;
    private final ItemRepository itemRepository;
    private final FieldRepository fieldRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final FieldValueRepository fieldValueRepository;
    private final CollectionRepository collectionRepository;

    @Override
    public APIResponse create(ItemRequest itemRequest) {
        List<Long> tagIdList = itemRequest.getTagIdList();
        List<Tag> tagList = tagRepository.findAllById(tagIdList);
        Collection collection = collectionRepository
                .findById(itemRequest.getCollectionId())
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException(COLLECTION_ENG,COLLECTION_RUS, String.valueOf(itemRequest.getCollectionId()));
                });
        Item item = itemRepository.save(new Item(itemRequest.getName(), collection, tagList));
        //// TODO: 06.07.2022 return value changed
        fieldValueRepository.saveAll(getFieldValues(item, itemRequest.getFieldValues()));
        return APIResponse.success(true);
    }

    @Override
    public APIResponse get(Long id) {
        return APIResponse.success(
                itemRepository.findById(id).orElseThrow(()->{
                    throw new ResourceNotFoundException(ITEM_ENG,ITEM_RUS,String.valueOf(id));
                })
        );
    }


    public APIResponse getItemResponse(Long id, Long userId) {
        Item item = itemRepository.findById(id).orElseThrow(() -> {
                    throw new ResourceNotFoundException(ITEM_ENG,ITEM_RUS,String.valueOf(id));
                }
        );

        List<Comment> comments = commentRepository.findAllByItemId(id);
        return APIResponse.success(new ItemResponse(
                id,
                item.getName(),
                item.getLikes(),
                List.of(modelMapper.map(item.getTags(), TagResponse[].class)),
                getFieldValueListResponse(item.getCollection().getId()),
                List.of(modelMapper.map(comments, CommentResponse[].class)),
                itemRepository.existsByIdAndLikedUsers_Id(id, userId))
        );
    }

    public APIResponse modify(Long id, ItemUpdateRequest itemRequest) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ITEM_ENG, ITEM_RUS, String.valueOf(id)));
        List<Tag> tags = tagRepository.findAllById(itemRequest.getTags());
        List<Long> filedIds = itemRequest.getFieldValues().stream().map(f -> {
            return f.getFieldId();
        }).collect(Collectors.toList());
        List<Field> fields = fieldRepository.findAllById(filedIds);
        List<FieldValue> fieldValues = itemRequest.getFieldValues().stream().map(fieldValue -> {
            return new FieldValue(fieldValue.getValue(), item,
                    fields.stream().filter(f -> f.getId() == fieldValue.getFieldId()).findFirst().orElseThrow(
                            () -> new ResourceNotFoundException(FIELD_ENG, FIELD_RUS, String.valueOf(fieldValue.getFieldId())))
            );
        }).collect(Collectors.toList());

        item.setName(itemRequest.getName());
        item.setTags(tags);
        itemRepository.save(item);
        fieldValueRepository.saveAll(fieldValues);

        return getItemResponse(item.getId(),item.getCollection().getUser().getId());
    }

    @Override
    public APIResponse delete(Long aLong) {
        return null;
    }


    @Override
    public APIResponse update(Long aLong, ItemRequest u) {
        return null;
    }

    @Override
    public APIResponse getAll() {
        return null;
    }

    public APIResponse getItemsByTagId(Long tagId) {
        Map<Long, List<ItemResponseByTag>> collect = fieldValueRepository.findAllByItem_Tags_Id(tagId).stream()
                .map(fieldValue -> {
                    return new ItemResponseByTag(fieldValue.getItem().getId(), fieldValue.getField().getName(), fieldValue.getValue());
                })
                .collect(Collectors.groupingBy(ItemResponseByTag::getItemId));
        return APIResponse.success(collect);
    }

    public APIResponse getItemsByCollectionId(Long collectionId) {
        return APIResponse.success(getFieldValueListResponse(collectionId));
    }

    private List<FieldValue> getFieldValues(Item item, List<FieldValueRequest> fieldValues) {
        return fieldValues
                .stream()
                .map((fvr) -> new FieldValue(fvr.getValue(),
                        item,
                        fieldRepository
                                .findById(fvr.getFieldId())
                                .orElseThrow(() -> new ResourceNotFoundException(FIELD_ENG,FIELD_RUS, String.valueOf(fvr.getFieldId())))))
                .collect(Collectors.toList());
    }

    public APIResponse updateItemLike(Long userId, Long itemId, boolean isLiked) {
        if(isLiked){
            itemRepository.insertItemLike(itemId,userId);
        }else {
            itemRepository.deleteItemLike(itemId,userId);
        }
        return APIResponse.success(true);
    }

    private FieldValueListResponse getFieldValueListResponse(Long collectionId){
        FieldValueListResponse fieldList = new FieldValueListResponse();

        fieldList.setTypes(fieldRepository.findAllByCollection_Id(collectionId)
                .stream().map(elm -> modelMapper.map(elm, FieldResponse.class)).collect(Collectors.toList()));
        fieldList.setValues(fieldValueRepository
                .findAllByItem_CollectionId(collectionId)
                .stream()
                .map(item -> new FieldValueResponse(item.getValue(), item.getItem().getId(), item.getField().getId()))
                .collect(Collectors.groupingBy(FieldValueResponse::getItemId)));

        return fieldList;
    }
}
