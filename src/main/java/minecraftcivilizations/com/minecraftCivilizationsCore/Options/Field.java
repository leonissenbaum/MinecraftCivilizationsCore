package minecraftcivilizations.com.minecraftCivilizationsCore.Options;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.constant.Constable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Field<T extends Constable> {
    private String name;
    private Class<T> valueType;
    private T defaultValue;
}
