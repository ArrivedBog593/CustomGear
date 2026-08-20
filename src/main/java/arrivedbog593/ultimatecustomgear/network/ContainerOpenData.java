package arrivedbog593.ultimatecustomgear.network;

import net.minecraft.network.RegistryFriendlyByteBuf;

/**
 * What the server tells the client when a container opens.
 * <p>
 * VERSIONED, and that is the point of this class existing at all. The fields
 * used to be written and read inline in two different files, field by field,
 * with no check of any kind: adding one on the server without touching the
 * client did not fail, it silently shifted every value after it — a container
 * with two thousand columns, a sort criterion that is not a criterion. Reading
 * the version first turns that into a clean refusal.
 * <p>
 * To add a field: append it at the END, bump VERSION, and read it only when the
 * incoming version is high enough. Never reorder, never remove.
 */
public record ContainerOpenData(int size, int columns, byte sortCriterion,
                                boolean sortDescending, boolean rejectsContainers,
                                int lockedSlot) {

    /** 1: size, columns, criterion, descending. 2: + rejectsContainers. 3: + lockedSlot. */
    private static final int VERSION = 3;

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(VERSION);
        buf.writeVarInt(size);
        buf.writeVarInt(columns);
        buf.writeByte(sortCriterion);
        buf.writeBoolean(sortDescending);
        buf.writeBoolean(rejectsContainers);
        buf.writeVarInt(lockedSlot + 1);   // shifted: VarInt does not like -1
    }

    /**
     * Reads whatever the other side sent, filling anything it is too old to know
     * about with a safe default. An unknown FUTURE version is refused outright:
     * carrying on would read fields that are not there.
     */
    public static ContainerOpenData read(RegistryFriendlyByteBuf buf) {
        int version = buf.readVarInt();
        if (version > VERSION) {
            throw new IllegalStateException(
                    "[CustomGear] Container open packet is version " + version
                            + ", this side only understands " + VERSION
                            + ". The server and the client are running different mod versions.");
        }

        int size = buf.readVarInt();
        int columns = buf.readVarInt();
        byte criterion = buf.readByte();
        boolean descending = buf.readBoolean();

        // Default false: a container that never said it rejects anything is
        // treated as accepting everything, which is what version 1 did.
        boolean rejects = version >= 2 && buf.readBoolean();
        // Shifted by one on the wire so the "no locked slot" case is 0 rather
        // than a negative, which VarInt encodes in five bytes.
        int locked = version >= 3 ? buf.readVarInt() - 1 : -1;

        return new ContainerOpenData(size, columns, criterion, descending, rejects, locked);
    }
}