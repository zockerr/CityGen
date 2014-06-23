package de.zockerr.citygen;

import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.IWorldGenerator;

public class CityGenerator implements IWorldGenerator {

	private static HashMap<String, Boolean> generatedCities = new HashMap<String, Boolean>();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if ((random.nextDouble() * 100) > 1)
			return;
		int x = (int) (chunkX * 16 + random.nextDouble() * 16);
		int z = (int) (chunkZ * 16 + random.nextDouble() * 16);
		Logger log = LogManager.getLogger("CityGen");
		int maxCellsX = 20;
		int maxCellsZ = 20;
		int level1Height = world.getHeightValue(x, z);
		int level2Height = level1Height + 4;
		HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer = new HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>>();

		for (int i = 0; i < ((int) ((maxCellsX * 10) / 16)); i++) {
			for (int j = 0; j < ((int) ((maxCellsZ * 10) / 16)); j++) {
				if (generatedCities.get((chunkX - i) + " " + (chunkZ - j)) != null)
					return;
				if (generatedCities.get((chunkX - i) + " " + (chunkZ + j)) != null)
					return;
				if (generatedCities.get((chunkX + i) + " " + (chunkZ - j)) != null)
					return;
				if (generatedCities.get((chunkX + i) + " " + (chunkZ + j)) != null)
					return;
			}
		}
		log.log(Level.INFO,"Generating city at: " + x + " " + z);
		generatedCities.put(chunkX + " " + chunkZ, true);
		UnitTracker tracker = new UnitTracker(maxCellsX, maxCellsZ);

		// Generate level 1 units
		HashMap<String, String> units = new HashMap<String, String>();
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				String s = getRandomUnitConfig(1, random, 1);
				SchematicPlacer.addToBuffer(cityBuffer, x + i * 9, level1Height,
						z + j * 9, "citygen:schematics/basic/units/level1/" + s
								+ ".schematic");
				int a = x + i * 9;
				int b = level1Height;
				int c = z + j * 9;
				units.put(a + " " + b + " " + c, s);
				tracker.putUnit(i, 1, j, s);
			}
		}
		log.log(Level.INFO,"generated leve 1 units");

		// Place EW level 1 bridges
		HashMap<String, Boolean> ewBridges = new HashMap<String, Boolean>();
		placeEWBridges(cityBuffer, x, z, maxCellsX, maxCellsZ, level1Height, units,
				ewBridges, 1, tracker);
		log.log(Level.INFO,"generated leve 1 EWBridges");

		// Place NS level 1 bridges
		HashMap<String, Boolean> nsBridges = new HashMap<String, Boolean>();
		placeNSBridges(cityBuffer, x, z, maxCellsX, maxCellsZ, level1Height, units,
				nsBridges, 1, tracker);
		log.log(Level.INFO,"generated level 1 NSBridges");

		// Remove single units
		removeSingleUnits(random, cityBuffer, x, z, maxCellsX, maxCellsZ,
				level1Height, units, ewBridges, nsBridges, tracker, 1);
		log.log(Level.INFO,"removed level 1 single units");

		// Remove ew double units
		removeEWDoubles(random, cityBuffer, x, z, maxCellsX, maxCellsZ,
				level1Height, units, ewBridges, nsBridges, tracker, 1);
		log.log(Level.INFO,"removed level 1 EW double units");

		// Remove ns double units
		removeNSDoubles(random, cityBuffer, x, z, maxCellsX, maxCellsZ,
				level1Height, units, ewBridges, nsBridges, tracker, 1);
		log.log(Level.INFO,"removed level 1 NS double units");

		// Fill Holes: +x missing
		HashMap<String, Boolean> eHoles = new HashMap<String, Boolean>();
		fillEastHoles(cityBuffer, x, z, maxCellsX, maxCellsZ, level1Height,
				ewBridges, nsBridges, eHoles, tracker, 1);
		log.log(Level.INFO,"filled level 1 east holes");

		// Fill Holes: -x missing
		HashMap<String, Boolean> wHoles = new HashMap<String, Boolean>();
		fillWestHoles(cityBuffer, x, z, maxCellsX, maxCellsZ, level1Height,
				ewBridges, nsBridges, eHoles, wHoles, tracker, 1);
		log.log(Level.INFO,"filled level 1 west holes");

		// Fill Holes: +z missing
		HashMap<String, Boolean> sHoles = new HashMap<String, Boolean>();
		fillSouthHoles(cityBuffer, x, z, maxCellsX, maxCellsZ, level1Height,
				ewBridges, nsBridges, sHoles, tracker, 1);
		log.log(Level.INFO,"filled level 1 south holes");

		// Fill Holes: -z missing
		HashMap<String, Boolean> nHoles = new HashMap<String, Boolean>();
		fillNorthHoles(cityBuffer, x, z, maxCellsX, maxCellsZ, level1Height,
				ewBridges, nsBridges, sHoles, nHoles, tracker, 1);
		log.log(Level.INFO,"filled level 1 north holes");

		// Fill Holes: none missing
		HashMap<String, Boolean> noneHoles = new HashMap<String, Boolean>();
		fillNoneHoles(cityBuffer, x, z, maxCellsX, maxCellsZ, level1Height,
				ewBridges, nsBridges, noneHoles, tracker, 1);
		log.log(Level.INFO,"filled level 1 none holes");

		// Stairs: East(+x)
		HashMap<String, Boolean> eStairs = new HashMap<String, Boolean>();
		placeEastStairs(random, cityBuffer, x, z, maxCellsX, maxCellsZ,
				level1Height,0, ewBridges, nsBridges, wHoles, eStairs,1, tracker);
		log.log(Level.INFO,"placed level 1 east stairs");

		// Stairs: West(-x)
		HashMap<String, Boolean> wStairs = new HashMap<String, Boolean>();
		placeWestStairs(random, cityBuffer, x, z, maxCellsX, maxCellsZ,
				level1Height, ewBridges, nsBridges, eHoles, wStairs, tracker, 1);
		log.log(Level.INFO,"placed level 1 west stairs");

		// Stairs: South(+z)
		HashMap<String, Boolean> sStairs = new HashMap<String, Boolean>();
		placeSouthStairs(random, cityBuffer, x, z, maxCellsX, maxCellsZ,
				level1Height, ewBridges, nsBridges, nHoles, sStairs, tracker, 1);
		log.log(Level.INFO,"placed level 1 south stairs");

		// Stairs: North(-z)
		HashMap<String, Boolean> nStairs = new HashMap<String, Boolean>();
		placeNorthStairs(random, cityBuffer, x, z, maxCellsX, maxCellsZ,
				level1Height, ewBridges, nsBridges, sHoles, nStairs, tracker, 1);
		log.log(Level.INFO,"placed level 1 north stairs");
		
		//Start level 2
		//generate units
		for(int i = 0; i<maxCellsX; i++){
			for(int j = 0; j<maxCellsZ; j++){
				if(units.get((x+i*9)+" "+level1Height+" "+(z+j*9)).contains("u")){
					if(!nStairs.get((x+i*9)+" "+level1Height+" "+(z+j*9))&&
							!sStairs.get((x+i*9)+" "+level1Height+" "+(z+j*9))&&
							!wStairs.get((x+i*9)+" "+level1Height+" "+(z+j*9))&&
							!eStairs.get((x+i*9)+" "+level1Height+" "+(z+j*9))){
						String s = getRandomUnitConfig(2, random, 2);
						SchematicPlacer.addToBuffer(cityBuffer, x+i*9, level2Height, z+j*9, "citygen:schematics/basic/units/level2/"+s+".schematic");
						units.put((x+i*9)+" "+level2Height+" "+(z+j*9), s);
						tracker.putUnit(i, 2, j, s);
					}else{
						units.put((x+i*9)+" "+level2Height+" "+(z+j*9), "xxx");
					}
					
				}else{
					units.put((x+i*9)+" "+level2Height+" "+(z+j*9), "xxx");
				}
			}
		}
		log.log(Level.INFO,"generated level 2 units");
		
		//Place EW Bridges
		placeEWBridges(cityBuffer, x, z, maxCellsX, maxCellsZ, level2Height, units, ewBridges, 2, tracker);
		log.log(Level.INFO,"placed level 2 EW bridges");
		
		//Place NS Bridges
		placeNSBridges(cityBuffer, x, z, maxCellsX, maxCellsZ, level2Height, units,
				nsBridges, 2, tracker);
		log.log(Level.INFO,"placed level 2 NS bridges");
		
		//Fill E windows
		fillEWindows(x, z, maxCellsX, maxCellsZ, level1Height, level2Height,
				cityBuffer, units, ewBridges);
		
		//Fill W windows
		fillWWindows(x, z, maxCellsX, maxCellsZ, level1Height, level2Height,
				cityBuffer, units, ewBridges);
		
		//Fill S windows
		fillSWindows(x, z, maxCellsX, maxCellsZ, level1Height, level2Height,
				cityBuffer, nsBridges, units, tracker);
		
		//Fill N windows
		fillNWindows(x, z, maxCellsX, maxCellsZ, level1Height, level2Height,
				cityBuffer, units, nsBridges, tracker);
		
		//Place E stairs
		
		
		
		
		//Commit the buffer swap
		SchematicPlacer.placeBufferedCity(cityBuffer, world);
		log.log(Level.INFO,"committed buffer swap");
		
	}



	private void fillNWindows(
			int x,
			int z,
			int maxCellsX,
			int maxCellsZ,
			int level1Height,
			int level2Height,
			HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer,
			HashMap<String, String> units, HashMap<String, Boolean> nsBridges, UnitTracker tracker) {
		
		PlacementRule r = new PlacementRule();
		r.addNSBridgeCondition(0, -1, -1, false);
		r.addNSBridgeCondition(0, 0, -1, false);
		r.addUnitCondition(0, 0, 0, "");
		r.addEHoleCondition(-1, -1, -1, false);
		r.addWHoleCondition(0, -1, -1, false);
		
		for(int i = 0; i<maxCellsX; i++){
			for(int j = 0; j<maxCellsZ; j++){
				if(tracker.compliesRules(i, 2, j, r)){
					SchematicPlacer.addToBuffer(cityBuffer, x+i*9+1, level2Height, z+j*9, "citygen:schematics/basic/windowFill/level2/n_var0.schematic");
				}
			}
		}
	}



	private void fillSWindows(
			int x,
			int z,
			int maxCellsX,
			int maxCellsZ,
			int level1Height,
			int level2Height,
			HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer,
			HashMap<String, Boolean> nsBridges, HashMap<String, String> units, UnitTracker tracker) {
		
		PlacementRule r = new PlacementRule();
		r.addNSBridgeCondition(0, -1, 0, false);
		r.addNSBridgeCondition(0, 0, 0, false);
		r.addUnitCondition(0, 0, 0, "");
		r.addEHoleCondition(-1, -1, 0, false);
		r.addWHoleCondition(0, -1, 0, false);
		
		for(int i = 0; i<maxCellsX; i++){
			for(int j = 0; j<maxCellsZ; j++) {
				if(tracker.compliesRules(i, 2, j, r)){
					SchematicPlacer.addToBuffer(cityBuffer, x+i*9+1, level2Height, z+j*9+4, "citygen:schematics/basic/windowFill/level2/s_var0.schematic");
				}
			}
		}
	}



	private void fillWWindows(
			int x,
			int z,
			int maxCellsX,
			int maxCellsZ,
			int level1Height,
			int level2Height,
			HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer,
			HashMap<String, String> units, HashMap<String, Boolean> ewBridges) {
		for(int i = 0; i<maxCellsX; i++){
			for(int j = 0; j<maxCellsZ; j++){
				if(i==0||ewBridges.get((x+(i-1)*9)+" "+level1Height+" "+(z+j*9))==false){
					if(i==0||ewBridges.get((x+(i-1)*9)+" "+level2Height+" "+(z+j*9))==false){
						if(!units.get((x+i*9)+" "+level2Height+" "+(z+j*9)).contains("x")){
							SchematicPlacer.addToBuffer(cityBuffer, x+i*9, level2Height, z+j*9+1, "citygen:schematics/basic/windowFill/level2/w_var0.schematic");
						}
					}
				}
			}
		}
	}



	private void fillEWindows(
			int x,
			int z,
			int maxCellsX,
			int maxCellsZ,
			int level1Height,
			int level2Height,
			HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer,
			HashMap<String, String> units, HashMap<String, Boolean> ewBridges) {
		for(int i = 0; i<maxCellsX; i++){
			for(int j = 0; j<maxCellsZ; j++){
				if((i+1)==maxCellsX||ewBridges.get((x+i*9)+" "+level1Height+" "+(z+j*9))==false){
					if((i+1)==maxCellsX||ewBridges.get((x+i*9)+" "+level2Height+" "+(z+j*9))==false){
						if(!units.get((x+i*9)+" "+level2Height+" "+(z+j*9)).contains("x")){
							SchematicPlacer.addToBuffer(cityBuffer, x+i*9+4, level2Height, z+j*9+1, "citygen:schematics/basic/windowFill/level2/e_var0.schematic");
						}
					}
				}
			}
		}
	}
	
	

	private void placeNorthStairs(Random random, HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z,
			int maxCellsX, int maxCellsZ, int height,
			HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges,
			HashMap<String, Boolean> sHoles, HashMap<String, Boolean> nStairs, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if (j > 0
						&& nsBridges.get((x + i * 9) + " " + height + " "
								+ (z + (j - 1) * 9)) == true) {
					if ((j + 1) >= maxCellsZ
							|| nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + j * 9)) == false) {
						if ((i + 1) >= maxCellsX
								|| ewBridges.get((x + i * 9) + " " + height
										+ " " + (z + j * 9)) == false) {
							if (i == 0
									|| ewBridges.get((x + (i - 1) * 9) + " "
											+ height + " " + (z + j * 9)) == false) {
								if ((i + 1) >= maxCellsX
										|| j == 0
										|| sHoles.get((x + i * 9) + " "
												+ height + " "
												+ (z + (j - 1) * 9)) == false) {
									if (i == 0
											|| j == 0
											|| sHoles.get((x + (i - 1) * 9)
													+ " " + height + " "
													+ (z + (j - 1) * 9)) == false) {
										SchematicPlacer.addToBuffer(cityBuffer, x
												+ i * 9, height, z + j * 9 - 3,
												"citygen:schematics/basic/stairs/level1/n_var"
														+ random.nextInt(2)
														+ ".schematic");
										nStairs.put((x + i * 9) + " " + height
												+ " " + (z + j * 9 ), true);
										tracker.putNStairs(i, level, j, true);
									}
								}
							}
						}
					}
				}
				if (nStairs.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					nStairs.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void placeSouthStairs(Random random, HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z,
			int maxCellsX, int maxCellsZ, int height,
			HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges,
			HashMap<String, Boolean> nHoles, HashMap<String, Boolean> sStairs, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((j + 1) < maxCellsZ
						&& nsBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if (j == 0
							|| nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + (j - 1) * 9)) == false) {
						if ((i + 1) >= maxCellsX
								|| ewBridges.get((x + i * 9) + " " + height
										+ " " + (z + j * 9)) == false) {
							if (i == 0
									|| ewBridges.get((x + (i - 1) * 9) + " "
											+ height + " " + (z + j * 9)) == false) {
								if ((i + 1) >= maxCellsX
										|| nHoles.get((x + i * 9) + " "
												+ height + " " + (z + j * 9)) == false) {
									if (i == 0
											|| nHoles.get((x + (i - 1) * 9)
													+ " " + height + " "
													+ (z + j * 9)) == false) {
										SchematicPlacer.addToBuffer(cityBuffer, x
												+ i * 9, height, z + j * 9,
												"citygen:schematics/basic/stairs/level1/s_var"
														+ random.nextInt(2)
														+ ".schematic");
										sStairs.put((x + i * 9) + " " + height
												+ " " + (z + j * 9), true);
										tracker.putSStairs(i, level, j, true);
									}
								}
							}
						}
					}
				}
				if (sStairs.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					sStairs.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void placeWestStairs(Random random, HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z,
			int maxCellsX, int maxCellsZ, int height,
			HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges,
			HashMap<String, Boolean> eHoles, HashMap<String, Boolean> wStairs, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if (i > 0
						&& ewBridges.get((x + (i - 1) * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if ((i + 1) >= maxCellsX
							|| ewBridges.get((x + i * 9) + " " + height + " "
									+ (z + j * 9)) == false) {
						if ((j + 1) >= maxCellsZ
								|| nsBridges.get((x + i * 9) + " " + height
										+ " " + (z + j * 9)) == false) {
							if (j == 0
									|| nsBridges.get((x + i * 9) + " " + height
											+ " " + (z + (j - 1) * 9)) == false) {
								if ((i == 0)
										|| (j + 1) > maxCellsX
										|| eHoles.get((x + (i - 1) * 9) + " "
												+ height + " " + (z + j * 9)) == false) {
									if ((i == 0)
											|| (j == 0)
											|| eHoles.get((x + (i - 1) * 9)
													+ " " + height + " "
													+ (z + (j - 1) * 9)) == false) {
										SchematicPlacer.addToBuffer(cityBuffer, x
												+ i * 9 - 3, height, z + j * 9,
												"citygen:schematics/basic/stairs/level1/w_var"
														+ random.nextInt(2)
														+ ".schematic");
										wStairs.put((x + i * 9) + " " + height
												+ " " + (z + j * 9), true);
										tracker.putWStairs(i, level, j, true);
									}
								}
							}
						}
					}
				}
				if (wStairs.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					wStairs.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void placeEastStairs(Random random, HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z,
			int maxCellsX, int maxCellsZ, int height, int belowHeight,
			HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges,
			HashMap<String, Boolean> wHoles, HashMap<String, Boolean> eStairs, int level, UnitTracker tracker) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((i + 1) < maxCellsX
						&& ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if (j == 0
							|| nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + (j - 1) * 9)) == false) {
						if ((j + 1) >= maxCellsZ
								|| nsBridges.get((x + i * 9) + " " + height
										+ " " + (z + j * 9)) == false) {
							if (i == 0
									|| ewBridges.get((x + (i - 1) * 9) + " "
											+ height + " " + (z + j * 9)) == false) {
								if (level>1||(i + 1) >= maxCellsX
										|| wHoles.get((x + i * 9) + " "
												+ height + " " + (z + j * 9)) == false) {
									if (level>1||(j - 1) < 0
											|| wHoles.get((x + i * 9) + " "
													+ height + " "
													+ (z + (j - 1) * 9)) == false) {
										if(level == 1||(i>0)&&ewBridges.get((x+(i-1)*9)+" "+belowHeight+" "+(z+j*9))==true){
											SchematicPlacer.addToBuffer(cityBuffer, x
												+ i * 9, height, z + j * 9,
												"citygen:schematics/basic/stairs/level"+level+"/e_var"
														+ random.nextInt(2)
														+ ".schematic");
											eStairs.put((x + i * 9) + " " + height
													+ " " + (z + j * 9), true);
											tracker.putEStairs(i, level, j, true);
										}
										
									}
								}
							}
						}
					}
				}
				if (eStairs.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					eStairs.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void fillNoneHoles(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z, int maxCellsX,
			int maxCellsZ, int height, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges,
			HashMap<String, Boolean> noneHoles, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((i + 1) < maxCellsX
						&& ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if ((j + 1) < maxCellsZ
							&& nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + j * 9)) == true) {
						if (ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + (j + 1) * 9)) == true) {
							if (nsBridges.get((x + (i + 1) * 9) + " " + height
									+ " " + (z + j * 9)) == true) {
								SchematicPlacer
										.addToBuffer(cityBuffer, x + i * 9 + 4,
												height, z + j * 9 + 4,
												"citygen:schematics/basic/holeFill/level1/none_var0.schematic");
								noneHoles.put((x + i * 9) + " " + height + " "
										+ (z + j * 9), true);
								tracker.putNoneHole(i, level, j, true);
							}
						}
					}
				}
				if (noneHoles.get((x + i * 9) + " " + height + " "
						+ (z + j * 9)) == null) {
					noneHoles.put((x + i * 9) + " " + height + " "
							+ (z + j * 9), false);
				}
			}
		}
	}

	private void fillNorthHoles(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z, int maxCellsX,
			int maxCellsZ, int height, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges,
			HashMap<String, Boolean> sHoles, HashMap<String, Boolean> nHoles, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((j + 1) < maxCellsZ
						&& nsBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if ((i + 1) < maxCellsX
							&& ewBridges.get((x + i * 9) + " " + height + " "
									+ (z + (j + 1) * 9)) == true) {
						if (nsBridges.get((x + (i + 1) * 9) + " " + height
								+ " " + (z + j * 9)) == true) {
							if (ewBridges.get((x + i * 9) + " " + height + " "
									+ (z + j * 9)) == false) {
								if (j == 0
										|| sHoles.get((x + i * 9) + " "
												+ height + " "
												+ (z + (j - 1) * 9)) == false) {
									SchematicPlacer.addToBuffer(cityBuffer, x + i * 9
													+ 4, height, z + j * 9,
													"citygen:schematics/basic/holeFill/level1/n_var0.schematic");
									nHoles.put((x + i * 9) + " " + height + " "
											+ (z + j * 9), true);
									tracker.putNHole(i, level, j, true);
								}
							}
						}
					}
				}
				if (nHoles.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					nHoles.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void fillSouthHoles(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z, int maxCellsX,
			int maxCellsZ, int height, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges, HashMap<String, Boolean> sHoles, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((i + 1) < maxCellsX
						&& ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if ((j + 1) < maxCellsZ
							&& nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + j * 9)) == true) {
						if (nsBridges.get((x + (i + 1) * 9) + " " + height
								+ " " + (z + j * 9)) == true) {
							if (ewBridges.get((x + i * 9) + " " + height + " "
									+ (z + (j + 1) * 9)) == false) {
								SchematicPlacer
										.addToBuffer(cityBuffer, (x + i * 9 + 4),
												height, (z + j * 9 + 4),
												"citygen:schematics/basic/holeFill/level1/s_var0.schematic");
								sHoles.put((x + i * 9) + " " + height + " "
										+ (z + j * 9), true);
								tracker.putSHole(i, level, j, true);
							}
						}
					}
				}
				if (sHoles.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					sHoles.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void fillWestHoles(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z, int maxCellsX,
			int maxCellsZ, int height, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges,
			HashMap<String, Boolean> eHoles, HashMap<String, Boolean> wHoles, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((i + 1) < maxCellsX
						&& ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if ((j + 1) < maxCellsZ
							&& nsBridges.get((x + (i + 1) * 9) + " " + height
									+ " " + (z + j * 9)) == true) {
						if (ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + (j + 1) * 9)) == true) {
							if (nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + j * 9)) == false) {
								if (i == 0
										|| eHoles.get((x + (i - 1) * 9) + " "
												+ height + " " + (z + j * 9)) == false) {
									SchematicPlacer
											.addToBuffer(cityBuffer, x + i * 9,
													height, z + j * 9 + 4,
													"citygen:schematics/basic/holeFill/level1/w_var0.schematic");
									wHoles.put((x + i * 9) + " " + height + " "
											+ (z + j * 9), true);
									tracker.putWHole(i, level, j, true);
								}
							}
						}
					}
				}

				if (wHoles.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					wHoles.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void fillEastHoles(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z, int maxCellsX,
			int maxCellsZ, int height, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges, HashMap<String, Boolean> eHoles, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((i + 1) < maxCellsX
						&& ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if ((j + 1) < maxCellsZ
							&& nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + j * 9)) == true) {
						if (ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + (j + 1) * 9)) == true) {
							if (nsBridges.get((x + (i + 1) * 9) + " " + height
									+ " " + (z + j * 9)) == false) {
								SchematicPlacer
										.addToBuffer(cityBuffer, x + i * 9 + 4,
												height, z + j * 9 + 4,
												"citygen:schematics/basic/holeFill/level1/e_var0.schematic");
								eHoles.put((x + i * 9) + " " + height + " "
										+ (z + j * 9), true);
								tracker.putEHole(i, level, j, true);
							}
						}
					}
				}
				if (eHoles.get((x + i * 9) + " " + height + " " + (z + j * 9)) == null) {
					eHoles.put((x + i * 9) + " " + height + " " + (z + j * 9),
							false);
				}
			}
		}
	}

	private void removeNSDoubles(Random random, HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z,
			int maxCellsX, int maxCellsZ, int height,
			HashMap<String, String> units, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if (j < maxCellsZ
						&& nsBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) {
					if ((j + 1) >= maxCellsZ
							|| nsBridges.get((x + i * 9) + " " + height + " "
									+ (z + (j + 1) * 9)) == false) {
						if ((j - 1) < 0
								|| nsBridges.get((x + i * 9) + " " + height
										+ " " + (z + (j - 1) * 9)) == false) {
							if ((i + 1) >= maxCellsX
									|| ewBridges.get((x + i * 9) + " " + height
											+ " " + (z + j * 9)) == false) {
								if ((i - 1) < 0
										|| ewBridges.get((x + (i - 1) * 9)
												+ " " + height + " "
												+ (z + j * 9)) == false) {
									if ((j + 1) >= maxCellsZ
											|| i >= maxCellsX
											|| ewBridges.get((x + i * 9) + " "
													+ height + " "
													+ (z + (j + 1) * 9)) == false) {
										if ((j + 1) >= maxCellsZ
												|| (i - 1) < 0
												|| ewBridges
														.get((x + (i - 1) * 9)
																+ " "
																+ height
																+ " "
																+ (z + (j + 1) * 9)) == false) {
											int r = (int) (random.nextDouble() * 6);
											int var;
											if (r > 1) {
												var = 1;
											} else {
												var = 0;
											}
											SchematicPlacer.addToBuffer(
													cityBuffer, x + i * 9, height, z
															+ j * 9,
													"citygen:schematics/basic/removedUnits/level1/var"
															+ var
															+ ".schematic");
											units.remove((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9));
											units.put((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9), "xxx");
											tracker.putUnit(i, level, j, "xxx");

											r = (int) (random.nextDouble() * 6);
											if (r > 1) {
												var = 1;
											} else {
												var = 0;
											}
											SchematicPlacer.addToBuffer(
													cityBuffer, x + i * 9, height, z
															+ (j + 1) * 9,
													"citygen:schematics/basic/removedUnits/level1/var"
															+ var
															+ ".schematic");
											units.remove((x + i * 9) + " "
													+ height + " "
													+ (z + (j + 1) * 9));
											units.put((x + i * 9) + " "
													+ height + " "
													+ (z + (j + 1) * 9), "xxx");
											tracker.putUnit(i, level, j+1, "xxx");

											r = (int) (random.nextDouble() * 6);
											if (r > 1) {
												var = 1;
											} else {
												var = 0;
											}
											SchematicPlacer.addToBuffer(
													cityBuffer, x + i * 9, height, z
															+ j * 9 + 4,
													"citygen:schematics/basic/removedUnits/level1/var"
															+ var
															+ ".schematic");
											nsBridges.remove((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9));
											nsBridges.put((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9), false);
											tracker.putNSBridge(i, level, j, false);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void removeEWDoubles(Random random, HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z,
			int maxCellsX, int maxCellsZ, int height,
			HashMap<String, String> units, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if (i < maxCellsX
						&& ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == true) { // Unit to be tested
															// has a connection
															// to east
					if ((i - 1) < 0
							|| ewBridges.get((x + (i - 1) * 9) + " " + height
									+ " " + (z + j * 9)) == false) { // Unit
																		// has
																		// no
																		// connection
																		// to
																		// west
						if ((i + 1) >= maxCellsX
								|| ewBridges.get((x + (i + 1) * 9) + " "
										+ height + " " + (z + j * 9)) == false) { // Unit
																					// to
																					// the
																					// east
																					// has
																					// no
																					// east
																					// connection
							if ((j >= maxCellsZ)
									|| nsBridges.get((x + i * 9) + " " + height
											+ " " + (z + j * 9)) == false) { // Unit
																				// has
																				// no
																				// north
																				// connection
								if ((j == 0)
										|| nsBridges.get((x + i * 9) + " "
												+ height + " "
												+ (z + (j - 1) * 9)) == false) { // Unit
																					// has
																					// no
																					// south
																					// connection
									if ((j >= maxCellsZ)
											|| ((i + 1 > maxCellsX))
											|| nsBridges.get((x + (i + 1) * 9)
													+ " " + height + " "
													+ (z + j * 9)) == false) { // Unit
																				// to
																				// east
																				// has
																				// no
																				// north
										if (((j - 1) < 0)
												|| ((i + 1 > maxCellsX))
												|| nsBridges
														.get((x + (i + 1) * 9)
																+ " "
																+ height
																+ " "
																+ (z + (j - 1) * 9)) == false) { // Unit
																									// to
																									// east
																									// has
																									// no
																									// south
											int r = (int) (random.nextDouble() * 6);
											int var;
											if (r > 1) {
												var = 1;
											} else {
												var = 0;
											}
											SchematicPlacer.addToBuffer(
													cityBuffer, x + i * 9, height, z
															+ j * 9,
													"citygen:schematics/basic/removedUnits/level1/var"
															+ var
															+ ".schematic");
											units.remove((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9));
											units.put((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9), "xxx");
											tracker.putUnit(i, level, j, "xxx");

											r = (int) (random.nextDouble() * 6);
											if (r > 1) {
												var = 1;
											} else {
												var = 0;
											}
											SchematicPlacer.addToBuffer(
													cityBuffer, x + (i + 1) * 9,
													height, z + j * 9,
													"citygen:schematics/basic/removedUnits/level1/var"
															+ var
															+ ".schematic");
											units.remove((x + (i + 1) * 9)
													+ " " + height + " "
													+ (z + j * 9));
											units.put((x + (i + 1) * 9) + " "
													+ height + " "
													+ (z + j * 9), "xxx");
											tracker.putUnit(i+1, level, j, "xxx");

											r = (int) (random.nextDouble() * 6);
											if (r > 1) {
												var = 1;
											} else {
												var = 0;
											}
											SchematicPlacer.addToBuffer(
													cityBuffer, x + i * 9 + 4,
													height, z + j * 9,
													"citygen:schematics/basic/removedUnits/level1/var"
															+ var
															+ ".schematic");
											ewBridges.remove((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9));
											ewBridges.put((x + i * 9) + " "
													+ height + " "
													+ (z + j * 9), false);
											tracker.putEWBridge(i, level, j, false);

										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void removeSingleUnits(Random random, HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z,
			int maxCellsX, int maxCellsZ, int height,
			HashMap<String, String> units, HashMap<String, Boolean> ewBridges,
			HashMap<String, Boolean> nsBridges, UnitTracker tracker, int level) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if ((i >= maxCellsX)
						|| ewBridges.get((x + i * 9) + " " + height + " "
								+ (z + j * 9)) == false) {
					if ((i - 1) < 0
							|| ewBridges.get((x + (i - 1) * 9) + " " + height
									+ " " + (z + j * 9)) == false) {
						if ((j >= maxCellsZ)
								|| nsBridges.get((x + i * 9) + " " + height
										+ " " + (z + j * 9)) == false) {
							if ((j - 1) < 0
									|| nsBridges.get((x + i * 9) + " " + height
											+ " " + (z + (j - 1) * 9)) == false) {
								int r = (int) (random.nextDouble() * 6);
								int var;
								if (r > 1) {
									var = 1;
								} else {
									var = 0;
								}
								SchematicPlacer.addToBuffer(cityBuffer,
										x + i * 9, height, z + j * 9,
										"citygen:schematics/basic/removedUnits/level1/var"
												+ var + ".schematic");
								units.remove((x + i * 9) + " " + height + " "
										+ (z + j * 9));
								units.put((x + i * 9) + " " + height + " "
										+ (z + j * 9), "xxx");
								tracker.putUnit(i, level, j, "xxx");
							}
						}
					}
				}
			}
		}
	}

	private void placeNSBridges(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z, int maxCellsX,
			int maxCellsZ, int height, HashMap<String, String> units,
			HashMap<String, Boolean> nsBridges, int level, UnitTracker tracker) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				if (units.get((x + i * 9) + " " + height + " " + (z + j * 9))
						.contains("s")) {
					if ((j + 1 < maxCellsZ)
							&& units.get(
									(x + i * 9) + " " + height + " "
											+ (z + (j + 1) * 9)).contains("n")) {
						SchematicPlacer
								.addToBuffer(cityBuffer, x + i * 9, height, z + j
										* 9 + 4,
										"citygen:schematics/basic/bridge/level"+level+"/ns_var0.schematic");
						int a = x + i * 9;
						int b = height;
						int c = z + j * 9;
						nsBridges.put(a + " " + b + " " + c, true);
						tracker.putNSBridge(i, level, j, true);
					} else {
						int a = x + i * 9;
						int b = height;
						int c = z + j * 9;
						nsBridges.put(a + " " + b + " " + c, false);
					}

				} else {
					int a = x + i * 9;
					int b = height;
					int c = z + j * 9;
					nsBridges.put(a + " " + b + " " + c, false);
				}
			}
		}
	}

	private void placeEWBridges(HashMap<ImmutableTriple<Integer, Integer, Integer>, ImmutablePair<Block, Byte>> cityBuffer, int x, int z, int maxCellsX,
			int maxCellsZ, int height, HashMap<String, String> units,
			HashMap<String, Boolean> ewBridges, int level, UnitTracker tracker) {
		for (int i = 0; i < maxCellsX; i++) {
			for (int j = 0; j < maxCellsZ; j++) {
				int d = x + i * 9;
				int e = height;
				int f = z + j * 9;
				if (units.get(d + " " + e + " " + f).contains("e")) {
					d = x + (i + 1) * 9;
					if ((i + 1 < maxCellsX)
							&& units.get(d + " " + e + " " + f).contains("w")) {
						SchematicPlacer
								.addToBuffer(cityBuffer, x + i * 9 + 4, height, z
										+ j * 9,
										"citygen:schematics/basic/bridge/level"+level+"/ew_var0.schematic");
						int a = x + i * 9;
						int b = height;
						int c = z + j * 9;
						ewBridges.put(a + " " + b + " " + c, true);
						tracker.putEWBridge(i, level, j, true);
					} else {
						int a = x + i * 9;
						int b = height;
						int c = z + j * 9;
						ewBridges.put(a + " " + b + " " + c, false);
					}

				} else {
					int a = x + i * 9;
					int b = height;
					int c = z + j * 9;
					ewBridges.put(a + " " + b + " " + c, false);
				}
			}
		}
	}

	private static String getRandomUnitConfig(int level, Random random,
			int variations) {
		String s = "";

		int r = random.nextInt(9);
		if(level == 1){
			switch (r) {
				case 0:
					s = "nsew";
					break;
				case 1:
					s = "sew";
					break;
				case 2:
					s = "new";
					break;
				case 3:
					s = "nsw";
					break;
				case 4:
					s = "nse";
					break;
				case 5:
					s = "ne";
					break;
				case 6:
					s = "nw";
					break;
				case 7:
					s = "se";
					break;
				case 8:
					s = "sw";
					break;
			}
		}else if(level == 2){
			s = "nsew";
		}
		
		
		r = random.nextInt(3);
		switch(r){
			case 0:
			case 1:
				s = s+"u";
				break;
		}
		r = random.nextInt(variations);
		s = s+"_var"+r;
		

		return s;
	}

}
