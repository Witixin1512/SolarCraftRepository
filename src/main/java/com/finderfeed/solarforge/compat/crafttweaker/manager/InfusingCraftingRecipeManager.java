package com.finderfeed.solarforge.compat.crafttweaker.manager;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.CraftTweakerConstants;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.finderfeed.solarforge.SolarForge;
import com.finderfeed.solarforge.compat.crafttweaker.CraftTweakerUtilities;
import com.finderfeed.solarforge.recipe_types.infusing_crafting.InfusingCraftingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.*;

/**
 * @docParam this <recipetype:solarforge:infusing_crafting>
 */
@ZenRegister
@ZenCodeType.Name("mods.solarforge.InfusingCraftingManager")
@Document("mods/SolarForge/InfusingCraftingManager")
public class InfusingCraftingRecipeManager implements IRecipeManager<InfusingCraftingRecipe> {


    /**
     * Gets the recipe type for the registry to remove from.
     *
     * @return IRecipeType of this registry.
     */
    @Override
    public RecipeType<InfusingCraftingRecipe> getRecipeType() {
        return SolarForge.INFUSING_CRAFTING_RECIPE_TYPE;
    }

    /**
     * Adds a recipe to the InfusingCraftingManager
     *
     * @param name The recipe name
     * @param output The {@link IItemStack} the recipe should output
     * @param inputs The inputs necessary for the recipe to craft
     * @param processingTime The amount of time the recipe should process for
     * @param fragment The fragment used in the recipe.
     */
    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack output, IItemStack[][] inputs, int processingTime, String fragment){
        name = fixRecipeName(name);
        ResourceLocation location = new ResourceLocation(CraftTweakerConstants.MOD_ID, name);
        List<IItemStack> stackList =  CraftTweakerUtilities.flatten(inputs);
        String[] patterns = CraftTweakerUtilities.getPattern(stackList, 9, "Inputs must be a 3x3 Two-Dimensional Array!");
        InfusingCraftingRecipe recipe = new InfusingCraftingRecipe(location, patterns, CraftTweakerUtilities.getInputMaps(stackList, patterns, 3), output.getInternal(), processingTime, output.getAmount(), fragment);
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe));
    }


}
