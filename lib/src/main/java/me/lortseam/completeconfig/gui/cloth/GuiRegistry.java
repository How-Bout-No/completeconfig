package me.lortseam.completeconfig.gui.cloth;

import com.google.common.collect.Lists;
import com.google.common.collect.MoreCollectors;
import com.google.common.reflect.TypeToken;
import me.lortseam.completeconfig.data.*;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.FieldBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.TextColor;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class GuiRegistry {

    private static final List<Consumer<GuiRegistry>> globalRegistrars = Lists.newArrayList(GuiRegistry::registerDefaultProviders);

    public static void addGlobalRegistrar(Consumer<GuiRegistry> registrar) {
        globalRegistrars.add(registrar);
    }

    public static <T, A extends AbstractConfigListEntry> A build(Function<ConfigEntryBuilder, FieldBuilder<T, A>> builder, boolean requiresRestart) {
        FieldBuilder<T, A> fieldBuilder = builder.apply(ConfigEntryBuilder.create());
        fieldBuilder.requireRestart(requiresRestart);
        return fieldBuilder.build();
    }

    private final List<GuiProviderRegistration> registrations = new ArrayList<>();

    GuiRegistry() {
        for (Consumer<GuiRegistry> registrar : globalRegistrars) {
            registrar.accept(this);
        }
    }

    public void registerProvider(GuiProvider<?> provider, Predicate<Entry<?>> predicate, Type... types) {
        registrations.add(0, new GuiProviderRegistration(predicate.and(entry -> {
            if (types.length == 0) {
                return true;
            }
            return ArrayUtils.contains(types, entry.getType());
        }), provider));
    }

    public void registerProvider(GuiProvider<?> provider, Type... types) {
        if (types.length == 0) {
            throw new IllegalArgumentException("Types must not be empty");
        }
        registerProvider(provider, entry -> true, types);
    }

    public void registerBoundedProvider(GuiProvider<? extends BoundedEntry<?>> provider, boolean slider, Type... types) {
        registerProvider(provider, entry -> entry instanceof BoundedEntry && ((BoundedEntry<?>) entry).isSlider() == slider, types);
    }

    public void registerEnumProvider(GuiProvider<? extends EnumEntry<?>> provider, EnumEntry.DisplayType enumDisplayType) {
        registerProvider(provider, entry -> entry instanceof EnumEntry && ((EnumEntry<?>) entry).getDisplayType() == enumDisplayType);
    }

    public void registerColorProvider(GuiProvider<? extends ColorEntry<?>> provider, boolean alphaModeSupported, Type... types) {
        registerProvider(provider, entry -> entry instanceof ColorEntry<?> && (!((ColorEntry<?>) entry).isAlphaMode() || alphaModeSupported), types);
    }

    private void registerDefaultProviders() {
       registerProvider((BooleanEntry entry) -> build(
               builder -> builder
                       .startBooleanToggle(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setYesNoTextSupplier(entry.getValueTextSupplier())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), boolean.class, Boolean.class);
       registerProvider((Entry<Integer> entry) -> build(
               builder -> builder
                       .startIntField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), int.class, Integer.class);
       registerBoundedProvider((BoundedEntry<Integer> entry) -> build(
               builder -> builder
                       .startIntField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getMin())
                       .setMax(entry.getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, int.class, Integer.class);
       registerBoundedProvider((BoundedEntry<Integer> entry) -> build(
               builder -> builder
                       .startIntSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), true, int.class, Integer.class);
       registerColorProvider((ColorEntry<Integer> entry) -> build(
               builder -> builder
                       .startColorField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setAlphaMode(entry.isAlphaMode())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), true, int.class, Integer.class);
       registerProvider((Entry<Long> entry) -> build(
               builder -> builder
                       .startLongField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), long.class, Long.class);
       registerBoundedProvider((BoundedEntry<Long> entry) -> build(
               builder -> builder
                       .startLongField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getMin())
                       .setMax(entry.getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, long.class, Long.class);
       registerBoundedProvider((BoundedEntry<Long> entry) -> build(
               builder -> builder
                       .startLongSlider(entry.getText(), entry.getValue(), entry.getMin(), entry.getMax())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), true, long.class, Long.class);
       registerProvider((Entry<Float> entry) -> build(
               builder -> builder
                       .startFloatField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), float.class, Float.class);
       registerBoundedProvider((BoundedEntry<Float> entry) -> build(
               builder -> builder
                       .startFloatField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getMin())
                       .setMax(entry.getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, float.class, Float.class);
       registerProvider((Entry<Double> entry) -> build(
               builder -> builder
                       .startDoubleField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), double.class, Double.class);
       registerBoundedProvider((BoundedEntry<Double> entry) -> build(
               builder -> builder
                       .startDoubleField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setMin(entry.getMin())
                       .setMax(entry.getMax())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), false, double.class, Double.class);
       registerProvider((Entry<String> entry) -> build(
               builder -> builder
                       .startStrField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), String.class);
       registerEnumProvider((EnumEntry<Enum<?>> entry) -> build(
               builder -> builder
                       .startEnumSelector(entry.getText(), entry.getTypeClass(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setEnumNameProvider(entry.getEnumNameProvider())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), EnumEntry.DisplayType.BUTTON);
       registerEnumProvider((EnumEntry<Enum<?>> entry) -> {
           List<Enum> enumValues = Arrays.asList(((Class<? extends Enum<?>>) entry.getTypeClass()).getEnumConstants());
           return build(
                   builder -> builder
                           .startDropdownMenu(entry.getText(), DropdownMenuBuilder.TopCellElementBuilder.of(
                                   entry.getValue(),
                                   enumTranslation -> enumValues.stream().filter(enumValue -> entry.getEnumNameProvider().apply(enumValue).getString().equals(enumTranslation)).collect(MoreCollectors.toOptional()).orElse(null),
                                   entry.getEnumNameProvider()
                           ), DropdownMenuBuilder.CellCreatorBuilder.of(entry.getEnumNameProvider()))
                           .setSelections(enumValues)
                           .setDefaultValue(entry.getDefaultValue())
                           .setSaveConsumer(entry::setValue),
                   entry.requiresRestart()
           );
       }, EnumEntry.DisplayType.DROPDOWN);
       registerProvider((Entry<List<Integer>> entry) -> build(
               builder -> builder
                       .startIntList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Integer>>() {}.getType());
       registerProvider((Entry<List<Long>> entry) -> build(
               builder -> builder
                       .startLongList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Long>>() {}.getType());
       registerProvider((Entry<List<Float>> entry) -> build(
               builder -> builder
                       .startFloatList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Float>>() {}.getType());
       registerProvider((Entry<List<Double>> entry) -> build(
               builder -> builder
                       .startDoubleList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<Double>>() {}.getType());
       registerProvider((Entry<List<String>> entry) -> build(
               builder -> builder
                       .startStrList(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer(entry::setValue),
               entry.requiresRestart()
       ), new TypeToken<List<String>>() {}.getType());
       registerColorProvider((ColorEntry<TextColor> entry) -> build(
               builder -> builder
                       .startColorField(entry.getText(), entry.getValue())
                       .setDefaultValue(entry.getDefaultValue())
                       .setTooltip(entry.getTooltip())
                       .setSaveConsumer3(entry::setValue),
               entry.requiresRestart()
       ), false, TextColor.class);
    }

    Optional<GuiProvider<Entry<?>>> getProvider(Entry<?> entry) {
        return registrations.stream().filter(registration -> registration.test(entry)).findFirst().map(registration -> {
            return (GuiProvider<Entry<?>>) registration.getProvider();
        });
    }

}