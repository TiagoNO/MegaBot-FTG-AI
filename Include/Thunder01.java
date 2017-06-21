import java.util.LinkedList;
import java.util.Vector;

import commandcenter.CommandCenter;
import enumerate.Action;
import enumerate.State;
import gameInterface.AIInterface;
import simulator.Simulator;
import structs.CharacterData;
import structs.FrameData;
import structs.GameData;
import structs.Key;
import structs.MotionData;

/**
 * EMCTS: Extended MCTS(Incorporate Motion of Oppornent to MCT and Consider Energy)
 * ZEN: Machete plus EMCTS
 * GARNET: EMCTS
 * LUD:EMCTS (Consider Distance)
 *
 * @author Eita Aoki
 */
public class Thunder01 implements AIInterface {

  private Simulator simulator;
  public Key key;
  int teste;
  private CommandCenter commandCenter;
  private boolean playerNumber;
  private GameData gameData;

  private FrameData frameData;

  private FrameData simulatorAheadFrameData;

  private LinkedList<Action> myActions;

  private LinkedList<Action> oppActions;

  private CharacterData myCharacter;

  private CharacterData oppCharacter;

  private static final int FRAME_AHEAD = 14;

  private Vector<MotionData> myMotion;

  private Vector<MotionData> oppMotion;

  private Action[] actionAir;

  private Action[] actionGround;

  private Action spSkill;

  private ExtendedNode rootNode;

  public static final boolean DEBUG_MODE = false;

  private CharacterName charName;

  @Override
  public void close() {}

  @Override
  public String getCharacter() {
    return CHARACTER_ZEN;
  }

  @Override
  public void getInformation(FrameData frameData) {
    this.frameData = frameData;
    this.commandCenter.setFrameData(this.frameData, playerNumber);

    if (playerNumber) {
      myCharacter = frameData.getP1();
      oppCharacter = frameData.getP2();
    } else {
      myCharacter = frameData.getP2();
      oppCharacter = frameData.getP1();
    }
  }

  @Override
  public int initialize(GameData gameData, boolean playerNumber) {

    this.playerNumber = playerNumber;
    this.gameData = gameData;

    this.key = new Key();
    this.frameData = new FrameData();
    this.commandCenter = new CommandCenter();

    this.myActions = new LinkedList<Action>();
    this.oppActions = new LinkedList<Action>();

    simulator = gameData.getSimulator();
    System.out.println("thunder:"+gameData.getMyName(playerNumber));
    actionAir =
        new Action[] {Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DA, Action.AIR_DB,
            Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA,
            Action.AIR_D_DF_FB, Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA,
            Action.AIR_D_DB_BB};
    actionGround =
        new Action[] {Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH,
            Action.JUMP, Action.FOR_JUMP, Action.BACK_JUMP, Action.STAND_GUARD,
            Action.CROUCH_GUARD, Action.THROW_A, Action.THROW_B, Action.STAND_A, Action.STAND_B,
            Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA, Action.STAND_FB, Action.CROUCH_FA,
            Action.CROUCH_FB, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB, Action.STAND_F_D_DFA,
            Action.STAND_F_D_DFB, Action.STAND_D_DB_BB};
    spSkill = Action.STAND_D_DF_FC;

    myMotion = this.playerNumber ? gameData.getPlayerOneMotion() : gameData.getPlayerTwoMotion();
    oppMotion = this.playerNumber ? gameData.getPlayerTwoMotion() : gameData.getPlayerOneMotion();


    String tmpcharname=this.gameData.getMyName(this.playerNumber);
    if(tmpcharname.equals(CHARACTER_ZEN))charName=CharacterName.ZEN;
    else if(tmpcharname.equals(CHARACTER_GARNET))charName=CharacterName.GARNET;
    else if(tmpcharname.equals(CHARACTER_LUD))charName=CharacterName.LUD;
    else charName=CharacterName.OTHER;
    teste = 0;
    return 0;
  }

  @Override
  public Key input() {
    return key;
  }

  private void mctsProcessing(){
	  rootNode =
	            new ExtendedNode(charName,myCharacter.getEnergy(),oppCharacter.getEnergy(), simulatorAheadFrameData, null, myActions, oppActions, gameData, playerNumber,
	                commandCenter);
	        rootNode.createNode();

	        Action bestAction = rootNode.mcts(); 


	        commandCenter.commandCall(bestAction.name()); 
  }
  private void zenProcessing(){
	  FrameData tmpFrameData = simulator.simulate(frameData, this.playerNumber, null, null, 17);
	  CommandCenter cc=this.commandCenter;
	  cc.setFrameData(tmpFrameData, playerNumber);
		int distance = cc.getDistanceX();
		int energy = frameData.getMyCharacter(playerNumber).getEnergy();
		CharacterData my = cc.getMyCharacter();
		CharacterData opp = cc.getEnemyCharacter();
		int xDifference = my.left - opp.left;

	  if ((opp.energy >= 300) && ((my.hp - opp.hp) <= 300))
			cc.commandCall("FOR_JUMP _B B B");
			// if the opp has 300 of energy, it is dangerous, so better jump!!
			// if the health difference is high we are dominating so we are fearless :)
		else if (!my.state.equals(State.AIR) && !my.state.equals(State.DOWN)) { //if not in air
			if ((distance > 150)) {
				cc.commandCall("FOR_JUMP"); //If its too far, then jump to get closer fast
			}
			else if (energy >= 300)
				cc.commandCall("STAND_D_DF_FC"); //High energy projectile
			else if ((distance > 100) && (energy >= 50))
				cc.commandCall("STAND_D_DB_BB"); //Perform a slide kick
			else if (opp.state.equals(State.AIR)) //if enemy on Air
				cc.commandCall("STAND_F_D_DFA"); //Perform a big punch
			else if (distance > 100)
				this.mctsProcessing();
				//cc.commandCall("6 6 6"); // Perform a quick dash to get closer
			else
				this.mctsProcessing();
				//cc.commandCall("B"); //Perform a kick in all other cases, introduces randomness
		}
		else if ((distance <= 150) && (my.state.equals(State.AIR) || my.state.equals(State.DOWN))
				&& (((gameData.getStageXMax() - my.left)>=200) || (xDifference > 0))
				&& ((my.left >=200) || xDifference < 0)) { //Conditions to handle game corners
			if (energy >= 5)
				this.mctsProcessing();
				//cc.commandCall("AIR_DB"); // Perform air down kick when in air
			else
				this.mctsProcessing();
				//cc.commandCall("B"); //Perform a kick in all other cases, introduces randomness
		}
		else
			this.mctsProcessing();
			//cc.commandCall("B"); //Perform a kick in all other cases, introduces randomness
  }

  private void garnetProcessing(){
	  this.mctsProcessing();
  }


  private boolean printnameflag=true;
  @Override
  public void processing() {
	  if (canProcessing()) {
		  teste = 1;
		  if (commandCenter.getSkillFlag()) {
			  key = commandCenter.getSkillKey();
		  } else {
			  key.empty();
			  commandCenter.skillCancel();

			  mctsPrepare(); 

			  if(charName==CharacterName.ZEN){
				  zenProcessing();
				  if(printnameflag)System.out.println("zenProcessing");
			  }else if(charName==CharacterName.GARNET){
				  garnetProcessing();
				  if(printnameflag)System.out.println("garnetProcessing");
			  }
			  else{
				  if(printnameflag)System.out.println("elseProcessing");
				  mctsProcessing();
			  }
			  printnameflag=false;

		  }
	  }
  }

  public boolean canProcessing() {
    return !frameData.getEmptyFlag() && frameData.getRemainingTimeMilliseconds() > 0;
  }

  public void mctsPrepare() {
    simulatorAheadFrameData = simulator.simulate(frameData, playerNumber, null, null, FRAME_AHEAD);

    myCharacter = playerNumber ? simulatorAheadFrameData.getP1() : simulatorAheadFrameData.getP2();
    oppCharacter = playerNumber ? simulatorAheadFrameData.getP2() : simulatorAheadFrameData.getP1();

    setMyAction();
    setOppAction();
  }

  public void setMyAction() {
    myActions.clear();

    int energy = myCharacter.getEnergy();

    if (myCharacter.getState() == State.AIR) {
      for (int i = 0; i < actionAir.length; i++) {
        if (Math.abs(myMotion.elementAt(Action.valueOf(actionAir[i].name()).ordinal())
            .getAttackStartAddEnergy()) <= energy) {
          myActions.add(actionAir[i]);
        }
      }
    } else {
      if (Math.abs(myMotion.elementAt(Action.valueOf(spSkill.name()).ordinal())
          .getAttackStartAddEnergy()) <= energy) {
        myActions.add(spSkill);
      }

      for (int i = 0; i < actionGround.length; i++) {
        if (Math.abs(myMotion.elementAt(Action.valueOf(actionGround[i].name()).ordinal())
            .getAttackStartAddEnergy()) <= energy) {
          myActions.add(actionGround[i]);
        }
      }
    }

  }

  public void setOppAction() {
    oppActions.clear();

    int energy = oppCharacter.getEnergy();

    if (oppCharacter.getState() == State.AIR) {
      for (int i = 0; i < actionAir.length; i++) {
        if (Math.abs(oppMotion.elementAt(Action.valueOf(actionAir[i].name()).ordinal())
            .getAttackStartAddEnergy()) <= energy) {
          oppActions.add(actionAir[i]);
        }
      }
    } else {
      if (Math.abs(oppMotion.elementAt(Action.valueOf(spSkill.name()).ordinal())
          .getAttackStartAddEnergy()) <= energy) {
        oppActions.add(spSkill);
      }

      for (int i = 0; i < actionGround.length; i++) {
        if (Math.abs(oppMotion.elementAt(Action.valueOf(actionGround[i].name()).ordinal())
            .getAttackStartAddEnergy()) <= energy) {
          oppActions.add(actionGround[i]);
        }
      }
    }
  }
}
