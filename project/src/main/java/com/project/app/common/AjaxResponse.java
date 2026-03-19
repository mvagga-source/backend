package com.project.app.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AjaxResponse extends HashMap<String, Object> {
	//private boolean success;
    //private String message;

    public AjaxResponse() {
    	put("success", true);
        put("message", "");
    }

    public AjaxResponse(boolean success) {
    	put("success", success);
        put("message", "");
    }

    public AjaxResponse(Model model) {
    	put("success", true);
        put("message", "");
        if (model != null) {
            this.putAll(model.asMap());
        }
    }

    private AjaxResponse(boolean success, Model model) {
    	put("success", success);
        if (model != null) {
            this.putAll(model.asMap());
        }
    }

    private AjaxResponse(boolean success, Map<String, Object> map) {
    	put("success", success);
        if (map != null) {
            this.putAll(map);
        }
    }

    public static AjaxResponse success() {
        return new AjaxResponse();
    }

    public static AjaxResponse success(Object data) {
    	Map<String, Object> param = new HashMap<>();
    	param.put("data", data);
        return new AjaxResponse(true, param);
    }

    public static AjaxResponse success(Model model) {
    	return new AjaxResponse(true, model);
    }

    public static AjaxResponse success(Map<String, Object> map) {
    	return new AjaxResponse(true, map);
    }

    public static AjaxResponse successTui(Map<String, Object> map) {
    	Map<String, Object> param = new HashMap<>();
    	param.put("result", true);
        param.put("data", map);
    	return new AjaxResponse(true, map);
    }

    public static AjaxResponse success(boolean success) {
        return new AjaxResponse(success);
    }

    public AjaxResponse message(String message) {
        put("message", message);
        return this;
    }
}
