package minecraftcivilizations.com.minecraftCivilizationsCore.Options;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Option<K, V> {
    private K key;
    private V value;

}
