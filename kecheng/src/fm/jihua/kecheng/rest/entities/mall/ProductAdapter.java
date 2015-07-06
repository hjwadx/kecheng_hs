package fm.jihua.kecheng.rest.entities.mall;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ProductAdapter implements JsonDeserializer<Product>{

    @Override
    public Product deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
    	JsonObject jsonObject = json.getAsJsonObject();
    	int type = jsonObject.get("type").getAsInt();
    	//TODO 如果新增加一种类型，会不会crash
    	String className = "";
    	switch (type) {
		case Product.STICKER_SET:
			className = StickerSetProduct.class.getName();
			break;
		case Product.THEME:
			className = ThemeProduct.class.getName();
		default:
			break;
		}
        try {
            return context.deserialize(json, Class.forName(className));
        } catch (ClassNotFoundException cnfe) {
            throw new JsonParseException("Unknown element type: " + type, cnfe);
        }
    }
}
