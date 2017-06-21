import commandcenter.CommandCenter;
import structs.FrameData;
import structs.GameData;
import structs.Key;

public class Rule_MIDDLE_3K implements RuleLine {

	private int priority = 3;
	private int weight = 10;
	private boolean activated = false;
	
	private final int MAX_DURATION = 150;
	private int duration = 0;
	private boolean enemyJump = false;
	
	public String getStr(){
		return "Mid_3K_AntiAir("+priority+","+weight+","+activated+")";		
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
		return (CNSTs.MIDDLE_DIST_MAX >= distX && distX >= CNSTs.MIDDLE_DIST_MIN);
	}

	@Override
	public void preset(GameData gd, FrameData fd, CommandCenter cc) {
		duration = 0;
		enemyJump = false;
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
		if(cc.getDistanceY() > 10){enemyJump = true;}
		if(enemyJump){
			if(cc.getMyEnergy()>=50){
				cc.commandCall("6 2 3 _ B");
			} else {
				cc.commandCall("6 2 3 _ A");
			}
		}
		else{
			cc.commandCall("3 _ B");			
		}
		Key key = cc.getSkillKey();
		return key;
		
	}
}
