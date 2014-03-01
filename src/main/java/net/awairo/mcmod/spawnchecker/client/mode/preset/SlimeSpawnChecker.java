/*
 * SpawnChecker.
 * 
 * (c) 2014 alalwww
 * https://github.com/alalwww
 * 
 * This mod is distributed under the terms of the Minecraft Mod Public License 1.0, or MMPL.
 * Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 * 
 * この MOD は、Minecraft Mod Public License (MMPL) 1.0 の条件のもとに配布されています。
 * ライセンスの内容は次のサイトを確認してください。 http://www.mod-buildcraft.com/MMPL-1.0.txt
 */

package net.awairo.mcmod.spawnchecker.client.mode.preset;

import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;

import net.awairo.mcmod.spawnchecker.SpawnChecker;
import net.awairo.mcmod.spawnchecker.client.common.ConstantsConfig;
import net.awairo.mcmod.spawnchecker.client.common.MultiServerWorldSeedConfig;
import net.awairo.mcmod.spawnchecker.client.mode.core.CopiedLogics;

/**
 * スライムチャンクの判定.
 * 
 * @author alalwww
 */
public class SlimeSpawnChecker
{
    private static final Logger LOGGER = LogManager.getLogger(SpawnChecker.MOD_ID);
    private static final Minecraft game = Minecraft.getMinecraft();

    private static final ConstantsConfig CONSTS = ConstantsConfig.instance();
    private static final MultiServerWorldSeedConfig MULTI = MultiServerWorldSeedConfig.instance();
    private static final SlimeSpawnChecker SEED_UNKNOWN = new SlimeSpawnChecker();

    /**
     * 現在のワールド用の新たなスライムスポーン判定を取得します.
     * 
     * @return スライムスポーン判定
     */
    public static SlimeSpawnChecker newCheckerOfCurrentWorld()
    {
        if (game.getIntegratedServer() != null)
        {
            final MinecraftServer ms = game.getIntegratedServer();
            final WorldServer ws = ms.worldServerForDimension(game.thePlayer.dimension);
            final long seed = ws.getSeed();
            LOGGER.info("current world is single player world. world seed is {}", seed);
            return new SlimeSpawnChecker(seed);
        }

        // TODO: リファクタリング
        final InetSocketAddress address = Refrection.getServerAddress();
        if (address != null)
        {
            final String host = address.getAddress().getHostName();
            final Integer port = address.getPort();
            if (MULTI.worldSeeds.contains(host, port))
            {
                final long seed = MULTI.worldSeeds.get(host, port);
                LOGGER.info("current world is multi player world. world seed is {}", seed);
                return new SlimeSpawnChecker(seed);
            }

            final String ip = address.getAddress().getHostAddress();
            if (MULTI.worldSeeds.contains(host, port))
            {
                final long seed = MULTI.worldSeeds.get(ip, port);
                LOGGER.info("current world is multi player world. world seed is {}", seed);
                return new SlimeSpawnChecker(seed);
            }
        }

        LOGGER.info("current world is multi player world. world seed is {}", "unknown");
        return SEED_UNKNOWN;
    }

    //---------------------

    private static final CopiedLogics copiedLogics = CopiedLogics.INSTANCE;
    private final Table<Integer, Integer, Boolean> keyTable = createKeytable();

    /** ワールドのシード値. */
    public final Optional<Long> worldSeed;

    private SlimeSpawnChecker(long worldSeed)
    {
        this.worldSeed = Optional.of(worldSeed);
    }

    private SlimeSpawnChecker()
    {
        this.worldSeed = Optional.absent();
    }

    /**
     * スライムがスポーンするチャンクかを判定します.
     * 
     * @param x X座標
     * @param z Z座標
     * @return trueはスライムスポーンチャンク
     */
    public boolean isSlimeChunk(int x, int z)
    {
        if (!worldSeed.isPresent())
            return false;

        final Integer chunkX = toChunkCoord(x);
        final Integer chunkZ = toChunkCoord(z);

        Boolean slimeChunk = keyTable.get(chunkX, chunkZ);

        if (slimeChunk != null)
            return slimeChunk;

        slimeChunk = isSlimeChunk(getSlimeChunkSeed(worldSeed.get(), chunkX, chunkZ));
        keyTable.put(chunkX, chunkZ, slimeChunk);

        return slimeChunk;
    }

    /**
     * スライムがスポーン可能かを判定します.
     * 
     * @param x X座標
     * @param y Y座標
     * @param z Z座標
     * @return trueはスライムがスポーン可能
     */
    public boolean isSpawnable(int x, int y, int z)
    {
        if (isSpawnableSwampland(x, y, z) || isSpawnablePos(x, y, z))
            return copiedLogics.canSpawnAtLocation(x, y, z);

        return false;
    }

    /**
     * スポーン可能な湿地の範囲内かを判定します.
     * 
     * @see net.minecraft.entity.monster.EntitySlime#getCanSpawnHere()
     */
    private boolean isSpawnableSwampland(int x, int y, int z)
    {
        // スポーン可の高さより下
        if (y <= CONSTS.slimeSpawnLimitMinYOnSwampland)
            return false;

        // スポーン可の高さより上
        if (y >= CONSTS.slimeSpawnLimitMaxYOnSwampland)
            return false;

        // 湿地ばいーむ
        final World world = game.theWorld;
        if (world == null || world.getBiomeGenForCoords(x, z) != BiomeGenBase.swampland)
            return false;

        return copiedLogics.canSpawnByLightLevel(x, y, z, CONSTS.spawnableLightLevel);
    }

    /**
     * スポーン可能な湿地の範囲内かを判定します.
     * 
     * @see net.minecraft.entity.monster.EntitySlime#getCanSpawnHere()
     */
    private boolean isSpawnablePos(int x, int y, int z)
    {
        if (y >= CONSTS.slimeSpawnLimitMaxY)
            return false;

        return isSlimeChunk(x, z);
    }

    // ------------------

    /**
     * @see net.minecraft.entity.monster.EntitySlime#getCanSpawnHere()
     * @see net.minecraft.world.chunk.Chunk#getRandomWithSeed(long)
     */
    private static long getSlimeChunkSeed(final long worldSeed, final int chunkX, final int chunkZ)
    {
        return worldSeed
                + chunkX * chunkX * CONSTS.chunkRandomSeedX1
                + chunkX * CONSTS.chunkRandomSeedX2
                + chunkZ * chunkZ * CONSTS.chunkRandomSeedZ1
                + chunkZ * CONSTS.chunkRandomSeedZ2
                ^ CONSTS.slimeRandomSeed;
    }

    /**
     * @see net.minecraft.entity.monster.EntitySlime#getCanSpawnHere()
     */
    private static Boolean isSlimeChunk(final long slimeChunkSeed)
    {
        return Boolean.valueOf(new Random(slimeChunkSeed).nextInt(10) == 0);
    }

    // ------------------

    private static Integer toChunkCoord(final int worldCoord)
    {
        return Integer.valueOf(worldCoord >> 4);
    }

    private static Table<Integer, Integer, Boolean> createKeytable()
    {
        final Map<Integer, Map<Integer, Boolean>> backingMap = new CachedMap<>();
        final Supplier<Map<Integer, Boolean>> factory = CachedMap.supplier();

        return Tables.newCustomTable(backingMap, factory);
    }

    private static class CachedMap<K, V> extends LinkedHashMap<K, V>
    {
        @SuppressWarnings("rawtypes")
        enum SingletonSupplier implements Supplier<Map>
        {
            INSTANCE;
            @Override
            public CachedMap<Object, Object> get()
            {
                return new CachedMap<>();
            }
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        static final <M extends Map> Supplier<M> supplier()
        {
            return (Supplier<M>) SingletonSupplier.INSTANCE;
        }

        private final int maxSize;

        CachedMap()
        {
            super(CONSTS.slimeChunkCacheSize, 0.75f, true);
            maxSize = CONSTS.slimeChunkCacheSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
        {
            return size() > maxSize;
        }
    }

    private static final class Refrection
    {
        static InetSocketAddress getServerAddress() throws RuntimeException
        {
            final NetHandlerPlayClient sendQueue = getFieldValue(
                    WorldClient.class, game.theWorld, "sendQueue", CONSTS.sendQueueSrgName);

            if (sendQueue == null)
                return null;

            final NetworkManager netManager = sendQueue.getNetworkManager();

            if (netManager.getSocketAddress() instanceof InetSocketAddress)
                return (InetSocketAddress) netManager.getSocketAddress();

            LOGGER.warn("not found InetSocketAddress");
            return null;
        }

        private static <T, E> T getFieldValue(Class<? super E> clazz, E instance, String... names)
        {
            try
            {
                return ReflectionHelper.getPrivateValue(clazz, instance, names);
            }
            catch (RuntimeException ignore)
            {
                LOGGER.warn("refrection failed.", ignore);
                return null;
            }
        }
    }
}
