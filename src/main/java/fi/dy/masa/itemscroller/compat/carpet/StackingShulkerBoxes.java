package fi.dy.masa.itemscroller.compat.carpet;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import java.lang.invoke.*;
import java.util.function.IntSupplier;

public class StackingShulkerBoxes {
    public static boolean enabled = false;

    public static void init(){
        if (FabricLoader.getInstance().isModLoaded("carpet")){
            try {
                enabled = true;
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                MethodHandle shulkerBoxStackSizeHandle = lookup.findStaticVarHandle(Class.forName("carpet.CarpetSettings"), "shulkerBoxStackSize", int.class).toMethodHandle(VarHandle.AccessMode.GET);
                shulkerBoxStackSizeGetter = ()-> {
                    try {
                        return (int) shulkerBoxStackSizeHandle.invokeExact();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                };

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
    private static IntSupplier shulkerBoxStackSizeGetter = () -> 1; // avoid init fail cause NullPointerException

    /**
     * @param stack {@link ItemStack}
     * @return Stack size considering empty boxes stacking rule from carpet mod
     * @author <a href="https://github.com/gnembon">gnembon</a>, <a href="https://github.com/vlad2305m">vlad2305m</a>
     * @see <a href="https://github.com/gnembon/fabric-carpet/blob/master/src/main/java/carpet/mixins/ItemStack_stackableShulkerBoxesMixin.java">Original implementation</a>
     */
    public static int getMaxCount(ItemStack stack){
        if (!enabled) return stack.getMaxCount();
        int shulkerBoxStackSize = shulkerBoxStackSizeGetter.getAsInt();
        if (shulkerBoxStackSize > 1
                && stack.getItem() instanceof BlockItem
                && ((BlockItem)stack.getItem()).getBlock() instanceof ShulkerBoxBlock
                && stack.getOrDefault(DataComponentTypes.CONTAINER, ContainerComponent.DEFAULT).stream().findAny().isEmpty()
        ) {
            return shulkerBoxStackSize;
        }
        return stack.getMaxCount();
    }
}
