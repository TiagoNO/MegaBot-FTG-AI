import java.awt.event.InputMethodListener;
import java.rmi.activation.Activatable;

import commandcenter.CommandCenter;
import structs.FrameData;
import structs.GameData;
import structs.Key;

public class Rule_SampleA implements RuleLine {

	private int priority = 5;
	private int weight = 10;
	private boolean activated = false;
	
	private final int MAX_DURATION = 120;
	private int duration = 0;
	
	public String getStr(){
		return "ruleA("+priority+","+weight+","+activated+")";		
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
		if(duration == 1){
			cc.commandCall("4 5 4 4 4 4");
		}
		if(duration == 20){
			cc.commandCall("B");
		}// TODO Auto-generated method stub
		if(duration == 80){
			cc.commandCall("7");
		}// TODO Auto-generated method stub
		Key key = cc.getSkillKey();
		//System.out.println("a"+key.A);
		return key;
		
	}

	
}
