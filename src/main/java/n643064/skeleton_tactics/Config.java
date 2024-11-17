package n643064.skeleton_tactics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class Config
{
    private record InnerConfig
            (
                    HashMap<String, Entry> entities
            ) {}

    private record Entry(String melee, String ranged, double swapDistance, boolean meleeFollowAfterLosingLineOfSight, double rangedSpeedMod, int rangedAttackInterval, float rangedRadiusSqrt, double meleeSpeedMod, boolean dontShootShields) {}
    public record CachedEntry(Item melee, Item ranged, double distance, boolean meleeFollowAfterLosingLineOfSight, double rangedSpeedMod, int rangedAttackInterval, float rangedRadiusSqrt, double meleeSpeedMod, boolean dontShootShields)
    {
        private static CachedEntry fromEntry(@NotNull Entry e)
        {
            return new CachedEntry
                    (
                            BuiltInRegistries.ITEM.get(ResourceLocation.parse(e.melee)),
                            BuiltInRegistries.ITEM.get(ResourceLocation.parse(e.ranged)),
                            e.swapDistance,
                            e.meleeFollowAfterLosingLineOfSight,
                            e.rangedSpeedMod,
                            e.rangedAttackInterval,
                            e.rangedRadiusSqrt,
                            e.meleeSpeedMod,
                            e.dontShootShields
                    );
        }
    }

    private static final CachedEntry DEFAULT = new CachedEntry(Items.IRON_SWORD, Items.BOW, 5d, true, 1.0, 20, 15.0F, 1.1, true);

    private static InnerConfig CONFIG = new InnerConfig(new HashMap<>());
    static
    {
        CONFIG.entities.put("minecraft:skeleton", new Entry("minecraft:iron_axe", "minecraft:bow", 4.5d, true, 1.2, 20, 15.0F, 1.2, true));
        CONFIG.entities.put("minecraft:wither_skeleton", new Entry("minecraft:stone_axe", "minecraft:bow", 7d, true, 1.0, 10, 20.0F, 1.1, true));
        CONFIG.entities.put("minecraft:stray", new Entry("minecraft:golden_axe", "minecraft:bow", 4.5d, true, 1.0, 20, 15.0F, 1.1, true));

    }

    public static HashMap<EntityType<?>, CachedEntry> map = new HashMap<>();

    @NotNull
    public static CachedEntry getForEntity(EntityType<?> entityType)
    {
        return map.getOrDefault(entityType, DEFAULT);
    }

    static boolean mapPopulated;
    static void onLoad(final LevelEvent.Load event)
    {
        if (mapPopulated) return;
        for (String s : CONFIG.entities.keySet())
        {
            final Entry e = CONFIG.entities.get(s);
            map.put(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(s)), CachedEntry.fromEntry(e));
        }
        mapPopulated = true;
    }

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();
    static final String CONFIG_PATH = "config" + File.separator + "skeleton_tactics.json";

    public static void create() throws IOException
    {
        Path p = Path.of("config");
        if (Files.exists(p))
        {
            if (Files.isDirectory(p))
            {
                FileWriter writer = new FileWriter(CONFIG_PATH);
                writer.write(GSON.toJson(CONFIG));
                writer.flush();
                writer.close();
            }
        } else
        {
            Files.createDirectory(p);
            create();
        }
    }

    public static void read() throws IOException
    {
        FileReader reader = new FileReader(CONFIG_PATH);
        CONFIG = GSON.fromJson(reader, InnerConfig.class);
        reader.close();
    }

    public static void setup()
    {
        try
        {
            if (Files.exists(Path.of(CONFIG_PATH)))
            {
                read();
            } else
            {
                create();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
