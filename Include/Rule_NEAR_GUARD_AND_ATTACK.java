import commandcenter.CommandCenter;
import enumerate.Action;
import structs.FrameData;
import structs.GameData;
import structs.Key;
import structs.MotionData;

public class Rule_NEAR_GUARD_AND_ATTACK implements RuleLine {

	private int priority = 3;
	private int weight = 10;
	private boolean activated = false;
	
	private final int MAX_DURATION = 240;
	private int duration = 0;
	
	private GameData gd;
	private boolean counterMode = true;
	private int startDuration_counter = 0;
	
	public String getStr(){
		return "Near_GUARD_ATTACK("+priority+","+weight+","+activated+")";		
	}

	

	@Override
	public int getWeight() {
		return weight;
	}

	@Override
	public void setWeight(int w) {
		this.weight = w;
	}

	@Override
	public int getPriority() {
		return priority;
	}


	@Override
	public boolean getActivated() {
		return activated;
	}

	@Override
	public void setActivated(boolean activated_) {
		this.activated = activated_;
	}


	@Override
	public boolean checkCondition(GameData gd, FrameData fd, CommandCenter cc) {
		// TODO Auto-generated method stub
		int distX =  cc.getDistanceX();
		return (CNSTs.NEAR_DIST_MAX >= distX);
	}

	@Override
	public void preset(GameData gd, FrameData fd, CommandCenter cc) {
		duration = 0;
		this.gd = gd;
		cc.skillCancel();
	}
	
	@Override
	public boolean hasActionEnded(GameData gd, FrameData fd, CommandCenter cc) {
		// TODO Auto-generated method stub
		if(duration==MAX_DURATION){
			return true;			
		}	
		return false;
	}

	@Override
	public Key exeAction(CommandCenter cc) {
		duration++;
		if(cc.getSkillFlag()){
			return cc.getSkillKey();
		}
		
		if(!counterMode){//I.e. Guard mode
			if(cc.getEnemyY() < cc.getMyY()){
				cc.commandCall("4");
			}else{
				cc.commandCall("1");
			}
			if(doesEnemyPerformAttackAction(cc) && cc.getDistanceX() <= 140){
				counterMode = true;
				startDuration_counter = duration;
			}
		}
		if(counterMode){ // Not "else{}" statement here. "Else" would cause a bug here. 
			if(cc.getDistanceX() <= 80){
				cc.commandCall("2 _ A");						
			}
			else if(15 <= cc.getDistanceX() && cc.getDistanceX() <= 140){
				cc.commandCall("3 _ B");		
			} 
			else{
				if(cc.getEnemyY() < cc.getMyY()){
					cc.commandCall("4");
				}else{
					cc.commandCall("1");
				}
			}
			if(duration - startDuration_counter >= 80){
				counterMode = false;
			}
		}
		Key key = cc.getSkillKey();
		if(!key.A && !key.B){
			cc.getSkillKey();
		}
		return key;
		
	}
	
	private boolean doesEnemyPerformAttackAction(CommandCenter cc){
		Action oppAct = cc.getEnemyCharacter().getAction();		
		MotionData oppMotion = new MotionData();
		if(BANZAI.PLAYER_NUMBER){
			oppMotion = gd.getPlayerTwoMotion().elementAt(oppAct.ordinal());
		}else{
			oppMotion = gd.getPlayerOneMotion().elementAt(oppAct.ordinal());
		}
		
		String motionName = oppMotion.getMotionName();
		if(motionName.endsWith("A")||motionName.endsWith("B")){
			//System.out.println(motionName);
			return true;
		}
				
		return false;
	
	}
}
