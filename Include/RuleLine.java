import structs.FrameData;
import structs.GameData;
import structs.Key;
import commandcenter.CommandCenter;

public interface RuleLine {

	int getWeight();
	void setWeight(int w);
	int getPriority();
	boolean getActivated();
	void setActivated(boolean activated);
	boolean checkCondition(GameData gd, FrameData fd, CommandCenter cc);
	
	void preset(GameData gd, FrameData fd, CommandCenter cc);
	Key exeAction(CommandCenter cc); //Return true while the rule is running. 
	boolean hasActionEnded(GameData gd, FrameData fd, CommandCenter cc);
	
	String getStr();
	
}
