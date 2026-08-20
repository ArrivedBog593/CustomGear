package arrivedbog593.ultimatecustomgear.data;

/**
 * The {@code container} content type: storage that exists either as a placeable
 * block (chest) or as a carried item (backpack).
 * <p>
 * WHY IT EXTENDS BlockData: Java has no multiple inheritance, and it does not
 * need it here — BlockData already carries every field both variants use
 * ({@code names}, {@code texture}, {@code tags}, {@code fire_resistant},
 * {@code recipe}) plus the block-only ones a chest needs. What ItemData adds on
 * top is food and mob drops, and neither applies to a container.
 * <p>
 * The cost of that choice is that a BACKPACK inherits block fields that mean
 * nothing to it — {@code destroy_time}, {@code sound}, {@code harvest_level},
 * {@code map_color}, {@code light_level}, {@code required_tool},
 * {@code directional}, {@code gravity}. Those are reported through the same
 * unused-field warning path as everywhere else: a field a type ignores gets a
 * warning, never a rejection.
 */
public class ContainerContentData extends BlockData {

    /** Present by definition — a container without this object is not a container. */
    public ContainerData container;

    public boolean isBarrel() {
        return container != null && ContainerData.KIND_BARREL.equals(container.kind());
    }

    public boolean isChest() {
        return container != null && ContainerData.KIND_CHEST.equals(container.kind());
    }

    public boolean isShulker() {
        return container != null && ContainerData.KIND_SHULKER.equals(container.kind());
    }

    public boolean isBackpack() {
        return container != null && ContainerData.KIND_BACKPACK.equals(container.kind());
    }

}
