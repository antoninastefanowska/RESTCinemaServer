package utils;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Converter {
	private static final Gson gson = new GsonBuilder().create();
	
	public static <T> T toEntity(Document document, Class<T> entityClass) {
		return gson.fromJson(document.toJson(), entityClass);
	}
	
	public static <T> List<T> toEntityList(Iterable<Document> documents, Class<T> entityClass) {
		List<T> result = new ArrayList<>();
		for (Document document : documents)
			result.add(toEntity(document, entityClass));
		return result;
	}
	
	public static <T> Document toDocument(T entity) {
		return Document.parse(gson.toJson(entity));
	}
	
	public static <T> List<Document> toDocumentList(List<T> entityList) {
		List<Document> result = new ArrayList<>();
		for (T entity : entityList)
			result.add(toDocument(entity));
		return result;
	}
}
