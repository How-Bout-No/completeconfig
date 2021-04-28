package io.github.how_bout_no.completeconfig;

import com.google.common.collect.Sets;
import io.github.how_bout_no.completeconfig.extensions.CompleteConfigExtension;
import io.github.how_bout_no.completeconfig.extensions.Extension;
import io.github.how_bout_no.completeconfig.extensions.GuiExtension;
import io.github.how_bout_no.completeconfig.extensions.clothbasicmath.ClothBasicMathExtension;
import io.github.how_bout_no.completeconfig.util.ReflectionUtils;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@UtilityClass
public final class CompleteConfigForge {

    private static final Set<Class<? extends Extension>> validExtensionTypes = Sets.newHashSet(CompleteConfigExtension.class);
    private static final Set<Extension> extensions = new HashSet<>();

    static {
        registerExtensionType(GuiExtension.class, Dist.CLIENT, "cloth-config2");
        registerExtension("cloth-basic-math", ClothBasicMathExtension.class);
        registerExtension("completeconfig-extension", CompleteConfigExtension.class);
    }

    public static void registerExtensionType(Class<? extends Extension> extensionType, Dist environment, String... mods) {
        if (validExtensionTypes.contains(extensionType)) return;
        if (environment != null && FMLEnvironment.dist != environment || Arrays.stream(mods).anyMatch(modID -> !ModList.get().isLoaded(modID)))
            return;
        validExtensionTypes.add(extensionType);
    }

    public static void registerExtensionType(Class<? extends Extension> extensionType, String... mods) {
        registerExtensionType(extensionType, null, mods);
    }

    private static void registerExtension(Extension extension) {
        extensions.add(extension);
        Set<Class<? extends Extension>> children = extension.children();
        if (children == null) return;
        for (Class<? extends Extension> child : children) {
            registerExtension(child);
        }
    }

    private static void registerExtension(Class<? extends Extension> extension) {
        if (Collections.disjoint(ClassUtils.getAllInterfaces(extension), validExtensionTypes)) return;
        try {
            registerExtension(ReflectionUtils.instantiateClass(extension));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("[CompleteConfig] Failed to instantiate extension " + extension, e);
        }
    }

    public static void registerExtension(@NonNull String modID, @NonNull Class<? extends CompleteConfigExtension> extensionType) {
        if (!ModList.get().isLoaded(modID)) return;
        registerExtension(extensionType);
    }

    public static <E extends Extension, T> Collection<T> collectExtensions(Class<E> extensionType, Function<E, T> function) {
        return extensions.stream().filter(extensionType::isInstance).map(extension -> function.apply((E) extension)).filter(Objects::nonNull).collect(Collectors.toSet());
    }

}
