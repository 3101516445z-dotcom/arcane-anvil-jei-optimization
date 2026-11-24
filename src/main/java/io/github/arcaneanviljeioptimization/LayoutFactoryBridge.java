package io.github.arcaneanviljeioptimization;

import mezz.jei.api.gui.IRecipeLayoutDrawable;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtons;
import mezz.jei.gui.recipes.IRecipeLayoutWithButtonsFactory;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

// 从 JEI 15.32.0.171 起，工厂方法的返回类型由具体类变为接口。
public final class LayoutFactoryBridge {
    private static final MethodHandle CREATE;

    static {
        try {
            CREATE =
                    MethodHandles.publicLookup()
                            .findVirtual(
                                    IRecipeLayoutWithButtonsFactory.class,
                                    "create",
                                    MethodType.methodType(
                                            IRecipeLayoutWithButtons.class,
                                            IRecipeLayoutDrawable.class));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private LayoutFactoryBridge() {}

    public static Object create(
            IRecipeLayoutWithButtonsFactory factory, IRecipeLayoutDrawable<?> layout) {
        try {
            return (IRecipeLayoutWithButtons<?>) CREATE.invokeExact(factory, layout);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
