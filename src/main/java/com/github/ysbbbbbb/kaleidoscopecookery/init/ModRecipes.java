package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.ChoppingBoardRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.PotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, KaleidoscopeCookery.MOD_ID);

    public static RegistryObject<RecipeSerializer<?>> POT_SERIALIZER = RECIPE_SERIALIZERS.register("pot", PotRecipeSerializer::new);
    public static RegistryObject<RecipeSerializer<?>> CHOPPING_BOARD_SERIALIZER = RECIPE_SERIALIZERS.register("chopping_board", ChoppingBoardRecipeSerializer::new);
    public static RegistryObject<RecipeSerializer<?>> STOCKPOT_SERIALIZER = RECIPE_SERIALIZERS.register("stockpot", StockpotRecipeSerializer::new);

    public static RecipeType<PotRecipe> POT_RECIPE;
    public static RecipeType<ChoppingBoardRecipe> CHOPPING_BOARD_RECIPE;
    public static RecipeType<StockpotRecipe> STOCKPOT_RECIPE;

    @SubscribeEvent
    public static void register(RegisterEvent evt) {
        if (evt.getRegistryKey().equals(Registries.RECIPE_SERIALIZER)) {
            POT_RECIPE = RecipeType.simple(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "pot"));
            CHOPPING_BOARD_RECIPE = RecipeType.simple(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "chopping_board"));
            STOCKPOT_RECIPE = RecipeType.simple(new ResourceLocation(KaleidoscopeCookery.MOD_ID, "stockpot"));
        }
    }
}
