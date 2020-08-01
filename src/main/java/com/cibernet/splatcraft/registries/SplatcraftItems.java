package com.cibernet.splatcraft.registries;

import com.cibernet.splatcraft.Splatcraft;
import com.cibernet.splatcraft.items.*;
import com.cibernet.splatcraft.util.SplatcraftArmorMaterial;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

import static com.cibernet.splatcraft.registries.SplatcraftItemGroups.*;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftItems
{
	public static final List<Item> weapons = new ArrayList<>();
	public static final ArrayList<Item> inkColoredItems = new ArrayList<>();
	
	//Armor Materials
	public static final IArmorMaterial INK_CLOTH = new SplatcraftArmorMaterial("ink_cloth", SoundEvents.ITEM_ARMOR_EQUIP_LEATHER);
	
	//Shooters
	public static final ShooterItem splattershot = new ShooterItem("splattershot", 1.05f, 0.65f, 12f, 4, 8f, 0.9f);
	public static final ShooterItem tentatekSplattershot = new ShooterItem("tentatek_splattershot", splattershot);
	public static final ShooterItem wasabiSplattershot = new ShooterItem("wasabi_splattershot", splattershot);
	public static final ShooterItem splattershotJr = new ShooterItem("splattershot_jr", 1f, 0.35f, 13.5f, 4, 6.5f, 0.5f);
	public static final ShooterItem aerosprayMG = new ShooterItem("aerospray_mg", 1.3f, 0.35f, 26f, 2, 4.8f, 0.5f);
	public static final ShooterItem getAerosprayRG = new ShooterItem("aerospray_rg", aerosprayMG);
	public static final ShooterItem gal52 = new ShooterItem("52_gal", 1.2f, 0.68f, 16f, 9, 10.4f, 1.3f);
	public static final ShooterItem gal52Deco = new ShooterItem("52_gal_deco", gal52);
	public static final ShooterItem gal96 = new ShooterItem("96_gal", 1.3f, 0.75f, 12.5f, 11, 12.4f, 2.5f);
	public static final ShooterItem gal96Deco = new ShooterItem("96_gal_deco", gal96);
	
	//Rollers
	
	//Chargers
	
	//Dualies
	
	//Sloshers
	
	//Ink Tanks
	
	//Vanity
	public static final Item inkClothHelmet = new ColoredArmorItem("ink_cloth_helmet", INK_CLOTH, EquipmentSlotType.HEAD);
	public static final Item inkClothChestplate = new ColoredArmorItem("ink_cloth_chestplate", INK_CLOTH, EquipmentSlotType.CHEST);
	public static final Item inkClothLeggings = new ColoredArmorItem("ink_cloth_leggings", INK_CLOTH, EquipmentSlotType.LEGS);
	public static final Item inkClothBoots = new ColoredArmorItem("ink_cloth_boots", INK_CLOTH, EquipmentSlotType.FEET);
	
	//Materials
	public static final Item sardinium = new Item(new Item.Properties().group(GROUP_GENERAL)).setRegistryName("sardinium");
	public static final Item sardiniumBlock = new BlockItem(SplatcraftBlocks.sardiniumBlock).setRegistryName("sardinium_block");
	public static final Item sardiniumOre = new BlockItem(SplatcraftBlocks.sardiniumOre).setRegistryName("sardinium_ore");
	public static final Item powerEgg = new Item(new Item.Properties().group(GROUP_GENERAL)).setRegistryName("power_egg");
	public static final Item powerEggCan = new PowerEggCanItem("power_egg_can");
	public static final Item powerEggBlock = new BlockItem(SplatcraftBlocks.powerEggBlock).setRegistryName("power_egg_block");
	public static final Item emptyInkwell = new BlockItem(SplatcraftBlocks.emptyInkwell).setRegistryName("empty_inkwell");
	
	//Remotes
	
	//Map Items
	public static final Item grate = new BlockItem(SplatcraftBlocks.grate).setRegistryName("grate");
	public static final Item barrierBar = new BlockItem(SplatcraftBlocks.barrierBar).setRegistryName("barrier_bar");
	public static final Item inkwell = new InkwellItem().setRegistryName("inkwell");
	
	//Misc
	
	@SubscribeEvent
	public static void itemInit(final RegistryEvent.Register<Item> event)
	{
		IForgeRegistry<Item> registry = event.getRegistry();
		
		for(Item item : weapons)
			registry.register(item);
		
		registry.register(inkClothHelmet);
		registry.register(inkClothChestplate);
		registry.register(inkClothLeggings);
		registry.register(inkClothBoots);
		
		registry.register(sardinium);
		registry.register(sardiniumBlock);
		registry.register(sardiniumOre);
		registry.register(powerEgg);
		registry.register(powerEggCan);
		registry.register(powerEggBlock);
		registry.register(emptyInkwell);
		
		registry.register(grate);
		registry.register(barrierBar);
		registry.register(inkwell);
		
		registry.register(new net.minecraft.item.BlockItem(Blocks.IRON_BARS, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName("minecraft","iron_bars"));
		
	}
}
