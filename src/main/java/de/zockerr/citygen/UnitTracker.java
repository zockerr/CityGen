package de.zockerr.citygen;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.ImmutableTriple;

public class UnitTracker {
	
	private String[][][] units;
	
	private boolean[][][] ewBridges;
	private boolean[][][] nsBridges;
	
	private boolean[][][] eHoles;
	private boolean[][][] wHoles;
	private boolean[][][] sHoles;
	private boolean[][][] nHoles;
	private boolean[][][] noneHoles;
	
	private boolean[][][] eStairs;
	private boolean[][][] wStairs;
	private boolean[][][] nStairs;
	private boolean[][][] sStairs;
	
	
	public UnitTracker(int xSize, int zSize){
		units = new String[xSize][4][zSize];
		
		ewBridges = new boolean[xSize][4][zSize];
		nsBridges = new boolean[xSize][4][zSize];
		
		eHoles = new boolean[xSize][4][zSize];
		wHoles = new boolean[xSize][4][zSize];
		sHoles = new boolean[xSize][4][zSize];
		nHoles = new boolean[xSize][4][zSize];
		noneHoles = new boolean[xSize][4][zSize];
		
		eStairs = new boolean[xSize][4][zSize];
		wStairs = new boolean[xSize][4][zSize];
		sStairs = new boolean[xSize][4][zSize];
		nStairs = new boolean[xSize][4][zSize];
		
		for(int i=0; i<xSize; i++){
			for(int j=0; j<4; j++){
				for(int k=0; k<zSize; k++){
					ewBridges[i][j][k] = false;
					nsBridges[i][j][k] = false;
					
					eHoles[i][j][k] = false;
					wHoles[i][j][k] = false;
					sHoles[i][j][k] = false;
					nHoles[i][j][k] = false;
					noneHoles[i][j][k] = false;
					
					eStairs[i][j][k] = false;
					wStairs[i][j][k] = false;
					nStairs[i][j][k] = false;
					sStairs[i][j][k] = false;
					
					units[i][j][k] = "xxx";
				}
			}
		}
	}
	
	public void putUnit(int x, int y, int z, String config){
		units[x][y][z] = config;
	}
	public void putEWBridge(int x, int y, int z, boolean value){
		ewBridges[x][y][z] = value;
	}
	
	public void putNSBridge(int x, int y, int z, boolean value){
		nsBridges[x][y][z] = true;
	}
	
	public void putEHole(int x, int y, int z, boolean value){
		eHoles[x][y][z] = true;
	}
	public void putWHole(int x, int y, int z, boolean value){
		wHoles[x][y][z] = true;
	}
	public void putSHole(int x, int y, int z, boolean value){
		sHoles[x][y][z] = true;
	}
	public void putNHole(int x, int y, int z, boolean value){
		nHoles[x][y][z] = true;
	}
	public void putNoneHole(int x, int y, int z, boolean value){
		noneHoles[x][y][z] = true;
	}
	
	public void putEStairs(int x, int y, int z, boolean value){
		eStairs[x][y][z] = true;
	}
	public void putWStairs(int x, int y, int z, boolean value){
		wStairs[x][y][z] = true;
	}
	public void putSStairs(int x, int y, int z, boolean value){
		sStairs[x][y][z] = true;
	}
	public void putNStairs(int x, int y, int z, boolean value){
		nStairs[x][y][z] = true;
	}
	
	public boolean compliesRules(int x, int y, int z, PlacementRule rule){
		HashMap<ImmutableTriple<Integer, Integer, Integer>, String> conds = rule.getUnitConditions();
		Iterator<ImmutableTriple<Integer, Integer, Integer>> i = conds.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			String cond = conds.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(!cond.equals("")){
					if(!units[testX][testY][testZ].contains(cond)){
						return false;
					}
				}else if(units[testX][testY][testZ].contains("x")){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(!cond.contains("x")){
					return false;
				}
			}
		}
		
		HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> c = rule.getEWBridgeConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != ewBridges[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getNSBridgeConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != nsBridges[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getEHoleConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != eHoles[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getWHoleConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != wHoles[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getNHoleConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != nHoles[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getSHoleConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != sHoles[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getNoneHoleConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != noneHoles[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getEStairsConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != eStairs[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getWStairsConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != wStairs[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getNStairsConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != nStairs[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		c = rule.getSStairsConditions();
		i = c.keySet().iterator();
		while(i.hasNext()){
			ImmutableTriple<Integer, Integer, Integer> relCoords = i.next();
			boolean cond = c.get(relCoords);
			int testX = x+relCoords.left;
			int testY = y+relCoords.middle;
			int testZ = z+relCoords.right;
			try{
				if(cond != sStairs[testX][testY][testZ]){
					return false;
				}
				
			}catch(ArrayIndexOutOfBoundsException e){
				if(cond == true){
					return false;
				}
			}
		}
		
		
		
		
		
		return true;
	}
	

}
