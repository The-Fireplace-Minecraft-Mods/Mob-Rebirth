package the_fireplace.mobrebirth.forge.compat.clans;

import net.minecraft.world.chunk.IChunk;

public class ClansCompatDummy implements IClansCompat {
    @Override
    public boolean doRebirth(IChunk chunk) {
        return true;
    }
}
