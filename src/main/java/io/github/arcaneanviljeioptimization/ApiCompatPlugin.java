package io.github.arcaneanviljeioptimization;

import net.minecraftforge.fml.loading.FMLLoader;

import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class ApiCompatPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("arcane-anvil-jei-optimization");
    private static final DefaultArtifactVersion LAST_REVIEWED =
            new DefaultArtifactVersion("15.75.0.242");
    private static final DefaultArtifactVersion NATIVE_LAYOUTS =
            new DefaultArtifactVersion("15.35.0.175");
    private static final DefaultArtifactVersion MENU_FACTORY =
            new DefaultArtifactVersion("15.70.0.221");
    private static final DefaultArtifactVersion VISIBILITY_CHECK =
            new DefaultArtifactVersion("15.62.0.214");
    private static final DefaultArtifactVersion INTERFACE_FACTORY =
            new DefaultArtifactVersion("15.32.0.171");
    private static final DefaultArtifactVersion MENU_LAYOUTS =
            new DefaultArtifactVersion("15.28.1.167");

    public void onLoad(String p) {}

    public String getRefMapperConfig() {
        return null;
    }

    public boolean shouldApplyMixin(String target, String mixin) {
        boolean ok;
        try {
            var jei = FMLLoader.getLoadingModList().getModFileById("jei");
            if (jei == null) return false;
            var version =
                    jei.getMods().stream()
                            .filter(mod -> mod.getModId().equals("jei"))
                            .findFirst()
                            .orElseThrow()
                            .getVersion();
            boolean reviewed = version.compareTo(LAST_REVIEWED) <= 0;
            boolean nativeSort = version.compareTo(NATIVE_LAYOUTS) >= 0;
            boolean menuFactory = version.compareTo(MENU_FACTORY) >= 0;
            boolean visibility = version.compareTo(VISIBILITY_CHECK) >= 0;
            if (mixin.endsWith(".NativeLayoutSortMixin")) return reviewed && menuFactory;
            if (mixin.endsWith(".NativeLayoutSortWithoutMenuMixin"))
                return reviewed && nativeSort && !menuFactory;
            if (mixin.endsWith(".RecipeVisibilityMixin") || mixin.endsWith(".FocusedQueryMixin"))
                return reviewed && visibility;
            boolean interfaceFactory = version.compareTo(INTERFACE_FACTORY) >= 0 && !nativeSort;
            boolean bridgeMixin = mixin.endsWith(".InterfaceFactoryMixin");
            if (interfaceFactory != bridgeMixin) return false;
            boolean menu = version.compareTo(MENU_LAYOUTS) >= 0;
            if (menu != (bridgeMixin || mixin.endsWith(".MenuRecipeGuiLogicMixin"))) return false;
            if (menu && hasNativeLazyLayouts()) {
                LOGGER.info("JEI {}: using native lazy layouts", version);
                return false;
            }
            ok = ApiContract.supported(ApiChecks.LIVE, target, mixin);
        } catch (Exception | LinkageError e) {
            ok = false;
            LOGGER.warn("API inspection failed for {}", mixin, e);
        }
        LOGGER.info(
                "API contract {}: {}", mixin, ok ? "ENABLED" : "DISABLED (incompatible structure)");
        return ok;
    }

    private static boolean hasNativeLazyLayouts() throws Exception {
        try {
            ApiChecks.LIVE.get("mezz/jei/gui/recipes/layouts/LazyRecipeLayoutList");
            return true;
        } catch (ClassNotFoundException absent) {
            return false;
        }
    }

    public void acceptTargets(Set<String> mine, Set<String> others) {}

    public List<String> getMixins() {
        return null;
    }

    public void preApply(String t, ClassNode n, String m, IMixinInfo i) {}

    public void postApply(String t, ClassNode n, String m, IMixinInfo i) {}
}
