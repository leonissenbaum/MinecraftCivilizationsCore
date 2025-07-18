package minecraftcivilizations.com.minecraftCivilizationsCore.API;

import java.util.function.Consumer;

interface ConfigAPI {
    public void save();
    public void save(String comment);
    public void load();
    public boolean isEmpty();
    public void reload();
    public String getString(String key);
    public void setString(String key, String value);
    public Integer getInteger(String key);
    public void setInteger(String key, Integer value);
    public Double getDouble(String key);
    public void setDouble(String key, Double value);
    public Boolean getBoolean(String key);
    public void setBoolean(String key, Boolean value);
}
