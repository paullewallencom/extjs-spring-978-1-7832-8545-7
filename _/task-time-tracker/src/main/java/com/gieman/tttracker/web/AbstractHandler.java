package com.gieman.tttracker.web;

import com.gieman.tttracker.domain.JsonItem;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides base functionality for all AJAX driven handler classes. Uses JSON
 * syntax that can be consumed by Sencha ExtJS4 and Touch2 clients.
 *
 */
public abstract class AbstractHandler {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Processes the List of results and constructs a JSON representation in the
     * format
     *
     * {success:true, data:[ theResults]}
     *
     * where theResults is an array of JSON objects. Each item in the results
     * List MUST implement JsonItem as the toJson() method is invoked on each
     * item to generate the JSON object
     *
     * @param results
     * @return
     */
    public static String getJsonSuccessData(List<? extends JsonItem> results) {

        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("success", true);

        final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (JsonItem ji : results) {

            arrayBuilder.add(ji.toJson());
        }
        
        builder.add("data", arrayBuilder);

        return toJsonString(builder.build());
    }

    /**
     * Returns a JSON success data array in the format { success: true, data:
     * jsonData} where jsonData could be in the format [ {} , {} ...] for lists
     * or simply { } for a single record
     *
     * @param jsonItem
     * @return
     */
    public static String getJsonSuccessData(JsonItem jsonItem) {

        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("success", true);
        builder.add("data", jsonItem.toJson());

        return toJsonString(builder.build());

    }

    /**
     * Returns a JSON success data array in the format { success:true,
     * total:count, data:jsonData} where jsonData would be in the format [ {} ,
     * {} ...]. The total property would be used by Ext.data.Stores for paging
     * purposes
     *
     * @param jsonItem
     * @param totalCount
     * @return
     */
    public static String getJsonSuccessData(JsonItem jsonItem, int totalCount) {

        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("success", true);
        builder.add("total", totalCount);
        builder.add("data", jsonItem.toJson());

        return toJsonString(builder.build());
    }

    public static String getJsonErrorMsg(String theErrorMessage) {

        return getJsonMsg(theErrorMessage, false);

    }

    public static String getJsonSuccessMsg(String msg) {

        return getJsonMsg(msg, true);
    }

    /**
     * Constructs a JSON representation of the msg with a text payload in the
     * following format:
     *
     * {success:true/false, msg:\"the message\" }
     *
     * @param msg
     * @param success
     * @return
     */
    public static String getJsonMsg(String msg, boolean success) {

        final JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("success", success);
        builder.add("msg", msg);

        return toJsonString(builder.build());

    }

    public static String toJsonString(JsonObject model) {

        final StringWriter stWriter = new StringWriter();

        try (JsonWriter jsonWriter = Json.createWriter(stWriter)) {
            jsonWriter.writeObject(model);
        }

        return stWriter.toString();
    }

    protected JsonObject parseJsonObject(String jsonString) {

        JsonReader reader = Json.createReader(new StringReader(jsonString));
        return reader.readObject();

    }

    protected Integer getIntegerValue(JsonValue jsonValue) {
        
        Integer value = null;
        
        switch (jsonValue.getValueType()) {
            
            case NUMBER:
                JsonNumber num = (JsonNumber) jsonValue;
                value = num.intValue();
                break;
            case NULL:
                break;
        }
        
        return value;
    }
}
