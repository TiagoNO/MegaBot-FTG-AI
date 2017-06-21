import commandcenter.CommandCenter;
import structs.FrameData;
import structs.GameData;
import structs.Key;

public class Rule_MIDDLE_JumpAtk implements RuleLine {

	private int priority = 3;
	private int weight = 10;
	private boolean activated = false;
	
	private final int MAX_DURATION = 200;
	private int duration = 0;
	
	public String getStr(){
		return "Mid_JumpAtk("+priority+","+weight+","+activated+")";		
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
		if(duration < 15){
			if(Math.abs(cc.getEnemyCharacter().getSpeedX()) > 3){
				cc.commandCall("8 8 8 8 8 8 8 8 8 8");		
			}else {
				cc.commandCall("9 9 9 9 9 9 9 9 9 9");						
			}
		}
		else{
			if(cc.getMyEnergy()>=5){
				cc.commandCall("2 _ B");
				if(duration >= 150) {duration -= 50;}
			} else {
				cc.commandCall("6 _ B");
			}
		}
		Key key = cc.getSkillKey();
		return key;
		
	}
}