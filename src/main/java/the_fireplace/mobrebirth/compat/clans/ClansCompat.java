package the_fireplace.mobrebirth.compat.clans;

import net.minecraft.world.chunk.IChunk;
import the_fireplace.clans.util.ChunkUtils;
import the_fireplace.mobrebirth.MobRebirth;

public class ClansCompat implements IClansCompat {
    @Override
    public boolean doRebirth(IChunk chunk) {
        if(MobRebirth.cfg.rebirthInClaimedLand)
            return true;
        return ChunkUtils.getChunkOwner(chunk) == null;
    }
}
