package minecraftcivilizations.com.minecraftCivilizationsCore.Options;

import java.util.List;

public class OptionUtils {
    public static List<Option<?, ?>> getOptionsOfType(Class<?> key, Class<?> value, List<Option<?, ?>> options) {
        List<Option<?, ?>> returnOptions = new java.util.ArrayList<>(0);
        for (Option<?, ?> option : options) {
            if (option.getKey().getClass().equals(key) && option.getValue().getClass().equals(value)) {
                returnOptions.add(new Option<>(option.getKey(), option.getValue()));
            }
        }
        return returnOptions;
    }
}
