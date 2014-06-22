package de.zockerr.citygen;

import java.util.HashMap;

import org.apache.commons.lang3.tuple.ImmutableTriple;

public class PlacementRule {
	int x,y,z;
	
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, String> unitConditions;
	
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> ewBridgeConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> nsBridgeConditions;
	
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> eHoleConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> wHoleConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> sHoleConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> nHoleConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> noneHoleConditions;
	
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> eStairsConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> wStairsConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> sStairsConditions;
	private HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> nStairsConditions;
	
	
	public PlacementRule(){
		
		unitConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, String>();
		
		ewBridgeConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		nsBridgeConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		
		eHoleConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		wHoleConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		sHoleConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		nHoleConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		noneHoleConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		
		eStairsConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		wStairsConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		sStairsConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		nStairsConditions = new HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean>();
		
	}
	
	public void addUnitCondition(int x, int y, int z, String cond){
		unitConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addEWBridgeCondition(int relX, int relY, int relZ, boolean cond){
		ewBridgeConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addNSBridgeCondition(int x, int y, int z, boolean cond){
		nsBridgeConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addEHoleCondition(int x, int y, int z, boolean cond){
		eHoleConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addWHoleCondition(int x, int y, int z, boolean cond){
		wHoleConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addSHoleCondition(int x, int y, int z, boolean cond){
		sHoleConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addNHoleCondition(int x, int y, int z, boolean cond){
		nHoleConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addNoneHoleCondition(int x, int y, int z, boolean cond){
		noneHoleConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addEStairsCondition(int x, int y, int z, boolean cond){
		eStairsConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addWStairsCondition(int x, int y, int z, boolean cond){
		wStairsConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addNStairsCondition(int x, int y, int z, boolean cond){
		nStairsConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public void addSStairsCondition(int x, int y, int z, boolean cond){
		sStairsConditions.put(new ImmutableTriple<Integer, Integer, Integer>(x, y, z), cond);
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, String> getUnitConditions(){
		return unitConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getEWBridgeConditions(){
		return ewBridgeConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getNSBridgeConditions(){
		return nsBridgeConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getEHoleConditions(){
		return eHoleConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getWHoleConditions(){
		return wHoleConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getSHoleConditions(){
		return sHoleConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getNHoleConditions(){
		return nHoleConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getNoneHoleConditions(){
		return noneHoleConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getWStairsConditions(){
		return wStairsConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getSStairsConditions(){
		return sStairsConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getNStairsConditions(){
		return nStairsConditions;
	}
	
	public HashMap<ImmutableTriple<Integer, Integer, Integer>, Boolean> getEStairsConditions(){
		return eStairsConditions;
	}
	

}
