package fm.jihua.kecheng.rest.entities;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import fm.jihua.kecheng.rest.entities.sticker.UserSticker;

public class OfflineData<T> implements Serializable{
	
	
	private static final long serialVersionUID = 3057220390963184002L;

    
	public int id;
	/**
	 * full name of a class, like fm.jihua.kecheng.rest.entities.Course
	 */
	public String content;
	public String category;
	public Operation operation;
	public int semesterId;
	
	private Class<T> classOfT;
	
	public enum Operation{
		ADD, MODIFY, DELETE
	}
	
	//TODO add class refactor relation
	private static String COURSE = "Course";
	private static String PASTER = "Paster";
	
	private static String[] categoryValues = new String[]{COURSE, PASTER};
	private static Class<?>[] categoryRefs = new Class<?>[]{Course.class, UserSticker.class};
	private static Map<String, Class<?>> categoryToClassMap = new HashMap<String, Class<?>>(){{
		for (int i = 0; i < categoryValues.length; i++) {
			put(categoryValues[i], categoryRefs[i]);
		}
	}};
	
	private static Map<Class<?>, String> classToCategoryMap = new HashMap<Class<?>, String>(){{
		for (int i = 0; i < categoryRefs.length; i++) {
			put(categoryRefs[i], categoryValues[i]);
		}
	}};
	
	public static <T>String getCategory(Class<T> classOfT){
		return classToCategoryMap.get(classOfT);
	}
	
	//dumped
	public int type;
	
	public OfflineData() {

	}
	
	public OfflineData(Class<T> classOfT, String content, Operation operation, int semesterId) {
		super();
		this.classOfT = classOfT;
		this.category = classToCategoryMap.get(classOfT);
		this.content = content;
		this.operation = operation;
		this.semesterId = semesterId;
	}
	
	
	/**
	 * for database initialize
	 * @param id
	 * @param category
	 * @param content
	 * @param operation
	 * @param semesterId
	 */
	@SuppressWarnings("unchecked")
	public OfflineData(int id, String category, String content, Operation operation, int semesterId) {
		this.category = category;
		this.classOfT = (Class<T>) categoryToClassMap.get(category);
		this.content = content;
		this.operation = operation;
		this.semesterId = semesterId;
		this.id = id;
	}
	
	//dumped
	public OfflineData(int id, int type, String content) {
		this.type = type;
		this.content = content;
		this.id = id;
	}
	
	public T getObject() {
		Gson gson = new Gson();
		return gson.fromJson(content, classOfT);
	}

}
