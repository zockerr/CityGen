package de.zockerr.citygen;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SimpleResource;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class SchematicPlacer {
	
	public static void placeSchematic(World world, int x, int y, int z, String schematicLocation){
		ResourceLocation location = new ResourceLocation(schematicLocation);
		Minecraft mc = Minecraft.getMinecraft();
		try {
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(new BufferedInputStream(mc.getResourceManager().getResource(location).getInputStream()));
			int width = nbt.getInteger("Width");
			int height = nbt.getInteger("Height");
			int length = nbt.getInteger("Length");
			
			byte[] blocks = nbt.getByteArray("Blocks");
			byte[] meta = nbt.getByteArray("Data");
			int arrayCounter = 0;			
			for(int i = 0; i<height; i++){
				for(int j = 0; j<length; j++){
					for(int k = 0; k<width; k++){
						world.setBlock(x+k, y+i, z+j, Block.getBlockById(blocks[arrayCounter]), meta[arrayCounter], 2);
						arrayCounter++;
					}
				}
			}
			for(int i = 0; i<height; i++){
				for(int j = 0; j<length; j++){
					for(int k = 0; k<width; k++){
						world.markBlockForUpdate(x+k, y+i, z+j);
					}
				}
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void addToBuffer(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> buffer, int x, int y, int z, String schematicLocation){
		ResourceLocation location = new ResourceLocation(schematicLocation);
		Minecraft mc = Minecraft.getMinecraft();
		try {
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(new BufferedInputStream(mc.getResourceManager().getResource(location).getInputStream()));
			int width = nbt.getInteger("Width");
			int height = nbt.getInteger("Height");
			int length = nbt.getInteger("Length");
			
			byte[] blocks = nbt.getByteArray("Blocks");
			byte[] meta = nbt.getByteArray("Data");
			int arrayCounter = 0;			
			for(int i = 0; i<height; i++){
				for(int j = 0; j<length; j++){
					for(int k = 0; k<width; k++){
						buffer.put(new ImmutableTriple<Integer, Integer, Integer>(x+k, y+i, z+j), 
								new ImmutablePair<Block, Byte>(Block.getBlockById(blocks[arrayCounter]), meta[arrayCounter]));
						//world.setBlock(x+k, y+i, z+j, Block.getBlockById(blocks[arrayCounter]), meta[arrayCounter], 2);
						arrayCounter++;
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void placeBufferedCity(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> buffer, World world){
		Iterator<ImmutableTriple<Integer, Integer, Integer>> iterator = buffer.keySet().iterator();
		while(iterator.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> t = iterator.next();
			ImmutablePair<Block, Byte> p = buffer.get(t);
			world.setBlock(t.left, t.middle, t.right, p.left, p.right, 2);
		}
		while(iterator.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> t = iterator.next();
			world.markBlockForUpdate(t.left, t.middle, t.right);
		}
		
	}

}
