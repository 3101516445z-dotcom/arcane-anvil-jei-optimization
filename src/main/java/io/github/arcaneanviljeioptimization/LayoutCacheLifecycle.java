package io.github.arcaneanviljeioptimization;

import java.lang.ref.WeakReference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 只追踪一个查询，弱引用避免生命周期管理器持有已废弃的 JEI 界面。 */
@Mod.EventBusSubscriber(modid = "arcane_anvil_jei_optimization", value = Dist.CLIENT)
public final class LayoutCacheLifecycle {
    private static WeakReference<PagedRecipeLayouts> active = new WeakReference<>(null);
    private static WeakReference<Screen> owner = new WeakReference<>(null);
    private LayoutCacheLifecycle() {}
    static void activate(PagedRecipeLayouts layouts) {
        Screen screen = Minecraft.getInstance().screen;
        if (active.get() != layouts || owner.get() != screen) {
            clear();
            active = new WeakReference<>(layouts);
            owner = new WeakReference<>(screen);
        }
    }
    static boolean isActive(PagedRecipeLayouts layouts) {
        if (owner.get() != Minecraft.getInstance().screen) clear();
        return active.get() == layouts;
    }
    public static void clear() {
        PagedRecipeLayouts previous = active.get();
        active.clear();
        owner.clear();
        if (previous != null) previous.clearCache();
    }
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END &&
                (Minecraft.getInstance().level == null || owner.get() != Minecraft.getInstance().screen)) clear();
    }
    @SubscribeEvent
    public static void closing(ScreenEvent.Closing event) {
        if (owner.get() == event.getScreen()) clear();
    }
    @SubscribeEvent
    public static void logout(ClientPlayerNetworkEvent.LoggingOut event) { clear(); }
    @SubscribeEvent
    public static void recipesUpdated(RecipesUpdatedEvent event) { clear(); }
}
