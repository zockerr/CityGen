package de.zockerr.citygen;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid = CityGen.MODID, version = CityGen.VERSION)
public class CityGen {
	
	public static final String MODID = "citygen";
	public static final String VERSION = "1.0.0";
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		CityGenerator generator = new CityGenerator();
		GameRegistry.registerWorldGenerator(generator, 10);
		
		
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event){
		System.out.println("CityGen Loaded!");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		
	}
	

}
