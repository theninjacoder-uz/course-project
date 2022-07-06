package com.itransition.courseproject.util;

import com.itransition.courseproject.exception.FileProcessingException;
import com.itransition.courseproject.model.entity.collection.Collection;
import com.itransition.courseproject.model.entity.collection.Field;
import com.itransition.courseproject.model.entity.collection.FieldValue;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public final class CSVUtil extends FileUtil {

    //TODO - must fix PATH when deploying
    private static final String PATH = "D:\\PDP\\OnsiteLessons\\collect_in\\collect-in\\src\\main\\resources\\files";

    public static String ofCollection(String lang, Collection collection, List<Field> fieldList, List<FieldValue> fieldValueList) {
        final String FILE_PATH = PATH + collection.getName().replace(" ","") + "_" + collection.getId() +".csv";
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(FILE_PATH));
            List<String[]> data = new ArrayList<>();
            data.addAll(collection.toCSVString(lang));
            data.addAll(collectionItemFieldNameLists(fieldList));
            data.addAll(valuesCollectionItemFields(fieldValueList, fieldList.size()));
            writer.writeAll(data);
            writer.close();
            return FILE_PATH;
        } catch (IOException e) {
            throw new FileProcessingException("csv file creation error", "ошибка создания файла csv");
        }
    }

    public static List<String[]> collectionItemFieldNameLists(List<Field> fieldList) {
        int length = fieldList.size();
        String[] head = new String[length + 4];
        head[0] = "Id";
        head[1] = "Name";
        head[2] = "Tags";
        for (int i = 0; i < length; i++) {
            head[i + 3] = fieldList.get(i).getName();
        }
        return Collections.singletonList(head);
    }

    public static List<String[]> valuesCollectionItemFields(List<FieldValue> fieldValueList, int fieldCount) {
        final List<String[]> fieldData = new ArrayList<>();
        String[] itemContents = new String[fieldCount + 3];
        int columnCount = 0, i = 0;
        while (i < fieldValueList.size()) {
            itemContents[columnCount++] = String.valueOf(fieldValueList.get(i).getItem().getId());
            itemContents[columnCount++] = fieldValueList.get(i).getItem().getName();
            itemContents[columnCount++] = "[" + fieldValueList.get(i).getItem().getTags() + "]";
            for (int j = 0; j < fieldCount; j++) {
                itemContents[columnCount++] = fieldValueList.get(i++).getValue();
            }
            fieldData.add(itemContents);
            columnCount = 0;
        }
        return fieldData;
    }
}
