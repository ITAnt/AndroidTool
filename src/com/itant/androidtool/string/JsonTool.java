package com.itant.androidtool.string;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Json字符串的相关工具类
 * @author iTant
 *
 */
public class JsonTool {
	
	private JsonTool() {}
	
	private static class ToolProvider {
		private static JsonTool instance = new JsonTool();
	}
	
	public static JsonTool getInstance() {
		return ToolProvider.instance;
	}
	
	/* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */  
	public Object readResolve() {  
		return getInstance();  
	}
	
	/**
	 * 将对象转换为Json字符串
	 * 
	 * @param object 要转换的对象
	 * @return 转换之后的Json字符串
	 */
	public String tojson(Object object) {
		if (null == object)
			return "";
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return jsonString;
	}

	/**
	 * 将Json字符串转换为对象 
	 * 
	 * @param json Json字符串
	 * @param type 目标对象类型
	 * @return 转换之后的对象
	 */
	public <T> T convert(String json, Class<T> type) {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		T result = null;
		try {

			result = mapper.readValue(json, type);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 将Json字符串转换为对象，该对象里面包含有其他对象
	 * 
	 * @param json Json字符串
	 * @param type 目标对象类型
	 * @return 转换之后的对象
	 */
	public <T> T convert(String json, TypeReference<T> type) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
		mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
		T result = null;
		try {

			result = mapper.readValue(json, type);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 将Json字符串转换为对象集合
	 * 
	 * @param json 将要转换的Json字符串
	 * @param type 目标对象类型
	 * @return 转换之后的对象集合
	 */
	public <T> List<T> convertList(String json, Class<T> type) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode;
		List<T> result = null;
		try {
			result = new ArrayList<T>();
			rootNode = mapper.readTree(json);
			if (rootNode.isArray()) {
				for (JsonNode jsonNode : rootNode) {
					T childResult = mapper.readValue(jsonNode.traverse(), type);
					if (childResult != null) {
						result.add(childResult);
					}
				}
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
}
