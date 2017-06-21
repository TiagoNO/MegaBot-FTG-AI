import structs.FrameData;
import structs.GameData;
import structs.Key;
import structs.MotionData;
import gameInterface.AIInterface;
import commandcenter.CommandCenter;
import enumerate.Action;
import structs.CharacterData;
import enumerate.State;


/**
 * Main class of our fighter based on the Dynamic Scripting technique.
 *    [Ref. Pieter S, et al, "Adaptive game AI with dynamic scripting."]
 *  
 * Other classes... 
 *  ScriptHandler.java -> The main handler of Dynamic Scripting method.
 *  RuleLine.java      -> Interface which is used in "rule-base" in dynamic scripting technique. 
 *  Rule_XXXX.java     -> Classes that implement the [RuleLine] interface.
 *  CNSTs.java         -> Static constants for control actions. *  
 *  
 */


public class BANZAI implements AIInterface {
	
	private static final int CLOCK_FRAMES = 300; // 60 frames/sec.
	private static int frameCounter = 0;
	public static boolean PLAYER_NUMBER;
	
	public Key inputKey;
	FrameData frameData;
	CommandCenter cc;
	boolean temp;
	CharacterData chData;
	boolean p;
	GameData gd;

	@Override
	public void close() {
	}

	@Override
	public String getCharacter() {
		return CHARACTER_ZEN;
	}

	@Override
	public void getInformation(FrameData frameData) {
		this.frameData = frameData;
		cc.setFrameData(this.frameData, BANZAI.PLAYER_NUMBER);
	}

	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		BANZAI.PLAYER_NUMBER = playerNumber;
		this.inputKey = new Key();
		cc = new CommandCenter();
		gd = gameData;
		p = playerNumber;
		frameData = new FrameData();
		inputKey = new Key();	
		
		ScriptHandler.genScript();
		
		return 0;
	}

	@Override
	public Key input() {
		return inputKey;
	}

	@Override
	public void processing() {
		frameCounter++;
		
		if(!frameData.getEmptyFlag() && frameData.getRemainingTimeMilliseconds() > 0)
		{
			if(frameCounter%CLOCK_FRAMES == 0){
				//System.out.println("time"+frameData.getRemainingTime());
				int fitness = calcFitness();
				ScriptHandler.weightAdjust(fitness);
				ScriptHandler.genScript();
				cc.skillCancel();
								
			}
			
			inputKey = ScriptHandler.exeScript(gd, frameData, cc);		
		}
	}

	
	private int myHP_stored = 0; 
	private int opHP_stored = 0; 
	private int calcFitness(){
		if(frameData==null || frameData.getMyCharacter(BANZAI.PLAYER_NUMBER)==null){
			return 0;
		}
		int currentMyHP = frameData.getMyCharacter(BANZAI.PLAYER_NUMBER).getHp();
		int currentOpHP = frameData.getOpponentCharacter(BANZAI.PLAYER_NUMBER).getHp();
		//System.out.println("HP_info:"currentMyHP+","+myHP_stored+","+currentOpHP+","+opHP_stored);
		if(currentMyHP > myHP_stored || currentOpHP > opHP_stored ){
			myHP_stored = currentMyHP;
			opHP_stored = currentOpHP;				
			return 0;
		}
		int fitness = (currentMyHP - myHP_stored) - (currentOpHP - opHP_stored);
		myHP_stored = currentMyHP;
		opHP_stored = currentOpHP;
		return fitness;		
	}

}
