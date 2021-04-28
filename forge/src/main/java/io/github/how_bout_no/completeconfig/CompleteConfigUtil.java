package io.github.how_bout_no.completeconfig;

import lombok.extern.log4j.Log4j2;
import net.minecraftforge.fml.ModList;

@Log4j2
public class CompleteConfigUtil {
    public static boolean isModLoaded(String modid) {
        if (modid == null) return false;
        try {
            ModList.get().isLoaded(modid);
        } catch (NullPointerException e) {
            log.error("Error loading mod");
            return false;
        }
        return ModList.get().isLoaded(modid);
    }
}