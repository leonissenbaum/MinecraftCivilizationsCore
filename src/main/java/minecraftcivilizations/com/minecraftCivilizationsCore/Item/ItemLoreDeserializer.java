package minecraftcivilizations.com.minecraftCivilizationsCore.Item;

import com.google.gson.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import minecraftcivilizations.com.minecraftCivilizationsCore.Component.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemLoreDeserializer implements JsonDeserializer<List<Component>> {
    private final GsonComponentSerializer serializer = GsonComponentSerializer.gson();

    @Override
    public List<Component> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray array = jsonElement.getAsJsonArray();
        List<Component> result = new ArrayList<>();
        for (JsonElement element : array) {
            result.add(ComponentUtils.deserializeComponent(element.getAsString()));
        }
        return result;
    }
}
