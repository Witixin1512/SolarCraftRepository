package com.finderfeed.solarforge.compat.crafttweaker;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.finderfeed.solarforge.registries.blocks.BlocksRegistry;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

import java.util.*;
import java.util.function.Supplier;

public class CraftTweakerUtilities {

    private static final char[] CHAR_ARRAY = "ABCDEFGHIJ".toCharArray();

    public static final Map<String, Character> map = new HashMap<>();

    public static String[] getPattern(List<IItemStack> inputs, int maxLength, String errorMessage){
        List<String> pattern = new ArrayList<>();
        Map<Item, String> map = new HashMap<>();
        if (inputs.size() != maxLength){
            throw new IllegalArgumentException(errorMessage);
        }
        String construct = "";
        for (int i = 0; i < inputs.size(); i++){
            final IItemStack stack = inputs.get(i);
            if (stack.isEmpty()){
                construct = construct.concat(" ");
            }
            else {
                if (map.containsKey(stack.getInternal().getItem())){
                    construct = construct.concat(map.get(stack.getInternal().getItem()));
                }
                else {
                    String s = (String.valueOf(CHAR_ARRAY[i]));
                    construct = construct.concat(s);
                    map.put(stack.getInternal().getItem(), s);
                }

            }
            if (resetConstructor(i, maxLength)){
                pattern.add(construct);
                construct = "";
            }
        }
        return pattern.stream().toArray(String[]::new);
    }
    public static Map<Character, Item> getInputItemMap(List<IItemStack> flattenedList, String[] patterns){
        Map<Character, Item> toReturn = new HashMap<>();
        if (flattenedList.size() != 9) {
            throw new IllegalArgumentException("Invalid Map configuration!");
        }
        for (int i = 0; i < flattenedList.size(); i++){
            Character toAnalyze = String.join("", patterns).toCharArray()[i];
            IItemStack stack = flattenedList.get(i);
            toReturn.put(toAnalyze, stack.getInternal().getItem());
        }
        return toReturn;
    }
    public static Map<Character, Ingredient> getInputIngredientMap(List<IItemStack> flattenedList, String[] patterns){
        Map<Character, Ingredient> toReturn = new HashMap<>();
        if (flattenedList.size() != 13) {
            throw new IllegalArgumentException("Invalid Map configuration!");
        }
        for (int i = 0; i < flattenedList.size(); i++){
            Character toAnalyze = String.join("", patterns).toCharArray()[i];
            IItemStack stack = flattenedList.get(i);
            toReturn.put(toAnalyze, Ingredient.of(stack.getInternal()));
        }
        return toReturn;
    }
    public static <T> List<T> flatten (T[][] inputs){
        List<T> toReturn = new ArrayList<>();
        for (T[] arrayStack : inputs){
            toReturn.addAll(Arrays.asList(arrayStack));
        }
        return toReturn;
    }

    public static boolean resetConstructor(int i, int maxLength){
        return ((maxLength == 9 && (i + 1) % 3 == 0)) || ((maxLength == 13 && (i == 2 || i == 4 || i == 7 || i == 9 || i == 12)));
    }

    public static void setupCatalystsMap(){
        map.put("solarforge:tera_rune_block", 'T');
        map.put("solarforge:zeta_rune_block", 'Z');
        map.put("solarforge:kelda_rune_block", 'K');
        map.put("solarforge:urba_rune_block", 'R');
        map.put("solarforge:fira_rune_block", 'F');
        map.put("solarforge:ardo_rune_block", 'A');
        map.put("solarforge:ultima_rune_block", 'U');
        map.put("solarforge:giro_rune_block", 'G');
    }

    public static Map<String, Character> getMap(){
        return map;
    }

    public static String readCatalysts(Block[][] catalysts){
        if (catalysts[0].length != 3){
            throw new IllegalArgumentException("Catalysts must be a 4x3 Two dimensional Array with blocks! Valid blocks will be parsed and invalid ones will be read as nothing special!");
        }
        String toReturn = "";
        List<Block> flattened = (List<Block>) flatten(catalysts);
        for (Block block : flattened){
            if (map.containsKey(block.getRegistryName().toString())){
                toReturn = toReturn.concat(String.valueOf(map.get(block.getRegistryName().toString())));
            }
            else {
                toReturn += " ";
            }
        }
        return toReturn;
    }
}
