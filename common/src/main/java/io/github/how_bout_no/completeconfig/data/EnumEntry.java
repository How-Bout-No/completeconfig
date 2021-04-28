package io.github.how_bout_no.completeconfig.data;

import com.google.common.base.CaseFormat;
import io.github.how_bout_no.completeconfig.api.ConfigEntry;
import io.github.how_bout_no.completeconfig.data.entry.EntryOrigin;
import lombok.Getter;
import net.minecraft.text.Text;

import java.util.function.Function;

public class EnumEntry<T extends Enum> extends Entry<T> {

    @Getter
    private final DisplayType displayType;

    public EnumEntry(EntryOrigin origin) {
        super(origin);
        this.displayType = origin.getOptionalAnnotation(ConfigEntry.Enum.class).map(ConfigEntry.Enum::displayType).orElse(DisplayType.DEFAULT);
    }

    public Function<Enum, Text> getEnumNameProvider() {
        return enumValue -> getTranslation().append(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, enumValue.name())).toText();
    }

    public enum DisplayType {

        BUTTON, DROPDOWN;

        private static final DisplayType DEFAULT;

        static {
            try {
                DEFAULT = (DisplayType) ConfigEntry.Enum.class.getDeclaredMethod("displayType").getDefaultValue();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

    }

}
