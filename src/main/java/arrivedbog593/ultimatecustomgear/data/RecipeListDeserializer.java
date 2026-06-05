package arrivedbog593.ultimatecustomgear.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom Gson deserializer that allows the "recipe" field in JSON to be
 * either a single object or an array of objects.
 * <p>
 * Single object:
 *   "recipe": { "type": "shaped", ... }
 * <p>
 * Array:
 *   "recipe": [
 *     { "type": "shaped", ... },
 *     { "type": "shaped", "result_count": 2, ... }
 *   ]
 * <p>
 * Register this in GsonBuilder before parsing GearData, ItemData and BlockData.
 */
public class RecipeListDeserializer implements JsonDeserializer<List<RecipeData>> {

    @Override
    public List<RecipeData> deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {
        List<RecipeData> list = new ArrayList<>();
        if (json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            for (JsonElement element : array) {
                list.add(context.deserialize(element, RecipeData.class));
            }
        } else if (json.isJsonObject()) {
            list.add(context.deserialize(json, RecipeData.class));
        }
        return list;
    }
}