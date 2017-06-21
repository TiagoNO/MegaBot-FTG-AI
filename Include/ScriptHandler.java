import java.util.ArrayList;
import java.util.Random;

import commandcenter.CommandCenter;
import structs.FrameData;
import structs.GameData;
import structs.Key;

/**
 * [Dynamic Scripting]
 * See: Pieter S, Marc P, Ida S and Eric P. "Adaptive game AI with dynamic scripting,"
 * 		Mach Learn 2006, Vol.63, pp.217-248, 2006. 
 * 			-- Especially, section 3 is helpful to understand this method.
 */
public class ScriptHandler {
	
	//For DEBUG. 
	private static boolean PRINT_FLAG = false;
	
	private static final int SCRIPT_SIZE = 8;
	//private static final int SCRIPT_SIZE = 2; //For DEBUG.
	private static final int TRY_MAX = 50;
	
	private static final int MIN_WEIGHT = 2;
	private static final int MAX_WEIGHT = 100;
	
	private static final Random rnd = new Random();
	private static RuleLine rule_running = null;

	
	/**
	 *  "Script" (see below) controls the actions that the AI-fighter performs.
	 *  And, the "script" consists of rules('RuleLine' in this code) that are stored in "ruleBase."
	 *  
	 *  "Script" varies dynamically during the fight process, while, 
	 *  "RuleBase" never.
	 */
	private static ArrayList<RuleLine> script = new ArrayList<RuleLine>();
	
	private static RuleLine[] ruleBase = new RuleLine[]{
			new Rule_UNIV_3B(),
			new Rule_UNIV_9(),
			new Rule_UNIV_Guard(),
			new Rule_FAR_669(),
			new Rule_FAR_777(),
			new Rule_FAR_Proj(),
			new Rule_MIDDLE_3K(),
			new Rule_MIDDLE_Assult(),
			new Rule_MIDDLE_JumpAtk(),
			new Rule_MIDDLE_Throw(),
			new Rule_NEAR_2AA3B(),
			new Rule_NEAR_Throw(),
			new Rule_NEAR_JK(),
			new Rule_NEAR_BackJ(),
			new Rule_NEAR_Special(),
			new Rule_NEAR_GUARD_AND_ATTACK(),
			
	};

	//Store rule in local variables.
	public static Key exeScript(GameData gd, FrameData fd, CommandCenter cc){
		
		if(rule_running==null){
			for(int priority = 5; priority >= 1; priority--){
				for(int i = 0; i < script.size(); i++){
					//printScript();
					//System.out.println("i"+i);
					RuleLine rule = script.get(i);
					if(rule.getPriority()!= priority){ continue; }
					if(rule.checkCondition(gd, fd, cc)){
						rule.setActivated(true);
						rule.preset(gd, fd, cc);
						rule_running = rule;
						break;
					}			
				}
				
				if(rule_running != null){
					break;
				}
			}
			
		}
		
		if(rule_running.hasActionEnded(gd, fd, cc)){
			rule_running = null;
			Key key = exeScript(gd,fd,cc);
			return key;
		}
		
		if(rule_running!=null){
			Key key = rule_running.exeAction(cc);
			return key;
		}else{
			//No rule is executed. 
			cc.commandCall("1");
			return cc.getSkillKey();
		}
	}
	
	
	public static void genScript(){
		clearScript();
		rule_running = null;
		
		int sumWeights = 0;
		for(int i =0; i < ruleBase.length; i++){
			sumWeights += ruleBase[i].getWeight();
		}
		
		//Repeated roulette wheel selection. 
		for(int i_scr = 0; i_scr < SCRIPT_SIZE; i_scr++){

			int try_n = 0;
			boolean lineAdded = false;
			while (try_n < TRY_MAX && !lineAdded){
				//Pick out a rule randomly from the rulebase. 
				int fraction = rnd.nextInt(sumWeights);
				int selected = 0;
				int sum_tmp = 0;
				for(int j_rule = 0; j_rule < ruleBase.length; j_rule++){
					sum_tmp += ruleBase[j_rule].getWeight();
					if(fraction < sum_tmp){
						selected = j_rule;
						break;
					}
				}
				
				//If the rule is included in the script already, try again.
				if(script.contains(ruleBase[selected])){
					try_n++;
					continue;
				}
				script.add(ruleBase[selected]);
				lineAdded = true;		
			}		
		}		
		//DEBUG.
		if(PRINT_FLAG){
			ScriptHandler.printRuleBase();
			ScriptHandler.printScript();
		}
	}
	
	
	public static void weightAdjust(int fitness){
		int active = 0;
		for(int i = 0; i < ruleBase.length; i++){
			if(ruleBase[i].getActivated()){
				active++;
			}	
		}
		if(active==0){return;}
		
		int nonActive = script.size() - active;
		int nonSelected = ruleBase.length - script.size(); //The rules, not in the script, but in rule-base.

		int fitness_activated = (4*fitness)/5; 		//Activated Rule in the script
		int fitness_others = (fitness)/5; 			//Non_Activated Rule in the script
		
		int adjustment_active = calculateAdjust(fitness_activated);
		int adjustment_nonActive = calculateAdjust(fitness_others);
		int compensation = - ((active*adjustment_active + nonActive * adjustment_nonActive)/nonSelected);
		int remainder = 0;
		//System.out.println(adjustment_active+" "+adjustment_nonActive+"" + compensation);
		//Credit assignment
		for(int i_rule = 0; i_rule < ruleBase.length; i_rule++){
			RuleLine rule = ruleBase[i_rule];

			if(rule.getActivated()){
				rule.setWeight(rule.getWeight() + adjustment_active);
				if(rule.getWeight() > MAX_WEIGHT ){
					remainder += rule.getWeight() - MAX_WEIGHT;
					rule.setWeight(MAX_WEIGHT);
				}
				if(rule.getWeight() < MIN_WEIGHT){
					remainder += rule.getWeight() - MIN_WEIGHT;
					rule.setWeight(MIN_WEIGHT);
				}
			}else if(script.contains(rule)){
				rule.setWeight(rule.getWeight() + adjustment_nonActive);
				if(rule.getWeight() > MAX_WEIGHT ){
					remainder += rule.getWeight() - MAX_WEIGHT;
					rule.setWeight(MAX_WEIGHT);
				}
				if(rule.getWeight() < MIN_WEIGHT){
					remainder += rule.getWeight() - MIN_WEIGHT;
					rule.setWeight(MIN_WEIGHT);
				}
			}else{
				rule.setWeight(rule.getWeight() + compensation);
				if(rule.getWeight() > MAX_WEIGHT ){
					remainder += rule.getWeight() - MAX_WEIGHT;
					rule.setWeight(MAX_WEIGHT);
				}
				if(rule.getWeight() < MIN_WEIGHT){
					remainder += rule.getWeight() - MIN_WEIGHT;
					rule.setWeight(MIN_WEIGHT);
				}
			}			
		}
		//Distribute Remainder.
		int index_rule = 0;
		if(remainder>0){
			while(remainder > 0){
				RuleLine rule = ruleBase[index_rule];
				if(rule.getWeight() < MAX_WEIGHT){
					rule.setWeight(rule.getWeight()+1);
					remainder--;
				}
				index_rule = (index_rule+1) % ruleBase.length;			
			}	
		}else if(remainder < 0){
			while(remainder < 0){
				RuleLine rule = ruleBase[index_rule];
				if(rule.getWeight() > MIN_WEIGHT){
					rule.setWeight(rule.getWeight()-1);
					remainder++;
				}
				index_rule = (index_rule+1) % ruleBase.length;			
			}	
		}
	}

	private static void clearScript(){
		for(int i = 0; i < script.size(); i++){
			script.get(i).setActivated(false);
		}
		script.clear();	
	}
	
	private static int calculateAdjust(int fitness){
		return Math.max(Math.min(fitness,100), -100);
	}
	
	public static void printScript(){
		System.out.print("SCRIPT[");
		for(int i = 0; i<script.size(); i++){
			System.out.print(script.get(i).getStr());			
		}
		System.out.println("]");
	}

	public static void printRuleBase(){
		System.out.print("RuleBase[");
		for(int i = 0; i<ruleBase.length; i++){
			System.out.print(ruleBase[i].getStr());			
		}
		System.out.println("]");
	}

	
}
