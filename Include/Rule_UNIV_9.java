import commandcenter.CommandCenter;
import structs.FrameData;
import structs.GameData;
import structs.Key;

public class Rule_UNIV_9 implements RuleLine {

	private int priority = 2;
	private int weight = 10;
	private boolean activated = false;
	
	private final int MAX_DURATION = 30;
	private int duration = 0;
	
	public String getStr(){
		return "Uni_ForJump("+priority+","+weight+","+activated+")";		
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
		return true;
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
		cc.commandCall("9");
		
		Key key = cc.getSkillKey();
		return key;
		
	}
}
