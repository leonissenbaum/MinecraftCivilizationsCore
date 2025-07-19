package minecraftcivilizations.com.minecraftCivilizationsCore.API;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.constant.Constable;
import java.util.Optional;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Field<T extends Constable> {
    private String name;
    private Class<T> valueType;
    private Optional<Class<T>> value;
}
