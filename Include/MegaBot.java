import gameInterface.AIInterface;
import structs.FrameData;
import structs.GameData;
import structs.CharacterData;
import structs.Key;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Random;
import java.io.IOException;
import commandcenter.CommandCenter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.StringTokenizer;

public class MegaBot implements AIInterface {
	
	Float Q[] = {1.0f,1.0f,1.0f};
	String NomesBot[] = {"BANZAI","Thunder01","DragonSurvivor"};
	CharacterData P1,P2;
	Key Acao = new Key();
	GameData Ginfo;
	FrameData Info;
	boolean Side;
	Thunder01 Thunder011 = new Thunder01();
	BANZAI BANZAI1 = new BANZAI();
	DragonSurvivor DragonSurvivor1 = new DragonSurvivor();	
	int Posi = 0; // auxiliar integer to discover the highest U();
	int numBots = 3; // number of bots, most used in FORs;
	Float alpha = Float.valueOf(0.20f);
	float d_max = 60000;  // Used in one form of Reward
	String PATH;
	int currentHp1 = 0,currentHp2 = 0;
	int AvaregeHp1 = 0, AvaregeHp2 = 0;
	int vitoria1 = 0, vitoria2 = 0;
	boolean Contabilizou = false;
	long frame1;
	long frame2 = 999999999;
	long aux = 0;
	


	
	
	public float Reward(int TypeReward, boolean arg1)
	{
		if(TypeReward == 1)
		{
			float d = Info.getRemainingTimeMilliseconds();
			System.out.println(d);
			if(Result_Match(P1,P2,arg1) == 1) // MegaBot won!!
			{
				float i = (1 - (d/d_max));
				System.out.println(i);
				return i;
			}
			else if(Result_Match(P1,P2,arg1) == -1) // MegaBot lost :(
			{
				float i = ((d/d_max) - 1);
				System.out.println(i);
				return  i;
			}
			else if(Result_Match(P1,P2,arg1) == 0) // it's a tie!
			{
				return 0;
			}
		}
		else if(TypeReward == 2)
		{
			if(Result_Match(P1,P2,arg1) == 1) // MegaBot won!!
				return 1;
			else if(Result_Match(P1,P2,arg1) == -1) // MegaBot lost :(
				return -1;
			else if(Result_Match(P1,P2,arg1) == 0) // it's a tie!
				return 0;
		}
		else if(TypeReward == 3)
		{
			return Diff_HP(P1,P2,arg1);
		}
		return -1;
	}


	
	public int Result_Match(CharacterData P1, CharacterData P2, boolean arg1)
	{
		if(arg1) // if MegaBot is P1
		{
			if(vitoria1 > vitoria2)
			{
				return 1;
			}
			else if(vitoria1 < vitoria2)
			{
				return -1;
			}
			else
				return 0;
		}
		else if(!arg1) // if MegaBot is P2
		{
			if(vitoria1 > vitoria2)
			{
				return -1;
			}
			else if(vitoria1 < vitoria2)
			{
				return 1;
			}
			else
				return 0;
		}
		return -1;
	}
	
	public int Diff_HP(CharacterData P1,CharacterData P2, boolean arg1)
	{
		if(arg1)
		{
			return (P1.hp - P2.hp); // MegaBot is P1
		}
		else if(!arg1)
		{
			return (P2.hp - P1.hp);  // MegaBot is P2
		}
		return -1;
	}

	public void Update_U_general()
	{
		Float R = Float.valueOf(Reward(1,Side));
		Float aux = Float.valueOf(alpha*(R - Q[Posi]));
		Float aux2 = Float.valueOf(Q[Posi] + alpha*aux);
		//aux2 = Float.valueOf(aux2 + Float.valueOf(alpha*aux));
		System.out.println("Value of aux2: " + aux2 + "   Value of aux: " + aux + "\n");
		Q[Posi] = Float.valueOf(aux2);
		System.out.println("Value of Q: " + Q[0] + ";" + Q[1] + ";" + Q[2] + ";\n");
		
	}
	
	public void SelecaoBot(GameData arg0, boolean arg1)
	{
		float egreedy = 0.2f;
		int i = 0;
		float a; 
		PATH = System.getProperty("user.dir");
		PATH = PATH + File.separator + "data" + File.separator + "aiData" + File.separator + "MegaBot" + File.separator + "Utilidade.txt";
		StringTokenizer token;
		if(new File(PATH).exists())
		{
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(PATH));
				String line = reader.readLine();
				while(line != null)
				{
					token = new StringTokenizer(line);
					NomesBot[i] = token.nextToken(";");
					Q[i] = Float.parseFloat(token.nextToken(";"));
					line = reader.readLine();
					if(Q[Posi] < Q[i])
					{
						System.out.println(Q[i]);
						Posi = i;
					}
					i++;
				}
				Random generator = new Random();
				a = generator.nextFloat();
				if(a <= egreedy)
				{
					i = Posi;
					while(i != Posi)
					{
						i = generator.nextInt(3);
					}
					Posi = i;
				}
			
			}
			catch(IOException ex)
			{
				System.out.println("ERRO: " + ex.getMessage());
			}
		}
		else if(!new File(PATH).exists())
		{
			File f = new File(PATH);
			try
			{
				f.createNewFile();				
				Random generator = new Random();
				Posi = generator.nextInt(3);
			}
			catch(IOException ex)
			{
				System.out.println("ERRO: "+ex.getMessage());
			}
			System.out.println(NomesBot[Posi]+": "+Q[Posi]);
		}

	}
	
	public void EscreverResultado()
	{
		int i;
		String linha;
		PATH = System.getProperty("user.dir");
		PATH = PATH + File.separator + "data" + File.separator + "aiData" + File.separator + "MegaBot" + File.separator + "Utilidade.txt";
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(PATH));
			for(i = 0; i < numBots; i++)
			{
				linha = new String(NomesBot[i] + ";" + Q[i]);
				System.out.println("Utilidade: " + Q[i]);
				writer.write(linha + "\n");
			}
			writer.close();
		}
		catch(IOException ex)
		{
			System.out.println("ERRO: " + ex.getMessage());
		}
		
	}
	
	public void GetHpOfTheMatch()
	{
		if(AvaregeHp1 > AvaregeHp2)
		{
			vitoria1++;
			Contabilizou = true;		
		}
		else if(AvaregeHp1 < AvaregeHp2)
		{
			vitoria2++;
			Contabilizou = true;
		}
		else if(AvaregeHp1 <= 0)
		{
			vitoria2++;
		}
		else if(AvaregeHp2 <= 0)
		{
			vitoria1++;
		}
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		System.out.println("Vidas: " + AvaregeHp1 + "  " + AvaregeHp2 + "\n" + "Vitorias1: " + vitoria1 + "  " + "Vitorias2: " + vitoria2 + "\n");
		Update_U_general();
		GetHpOfTheMatch();
		EscreverResultado();
		if(NomesBot[Posi] == "BANZAI")
		{
			BANZAI1.close();
		}
		else if(NomesBot[Posi] == "Thunder01")
		{
			Thunder011.close();	
		}
		else if(NomesBot[Posi] == "DragonSurvivor")
		{
			DragonSurvivor1.close();		
		}

	}

	@Override
	public String getCharacter() 
	{
		// TODO Auto-generated method stub
		if(NomesBot[Posi] == "Banzai1")
		{
			return BANZAI1.getCharacter();
		}
		else if(NomesBot[Posi] == "Thunder01")
		{
			return Thunder011.getCharacter();	
		}
		else if(NomesBot[Posi] == "DragonSurvivor")
		{
			return DragonSurvivor1.getCharacter();
		}
		return CHARACTER_ZEN;
	}

	@Override
	public void getInformation(FrameData arg0) {
		// TODO Auto-generated method stub
		if(!arg0.getEmptyFlag() && arg0.getRemainingTimeMilliseconds() > 0)
		{
			Info = new FrameData();
			this.Info = arg0;
			Thunder011.getInformation(Info);
			BANZAI1.getInformation(Info);
			DragonSurvivor1.getInformation(Info);
			AvaregeHp1 = Info.getP1().getHp();
			AvaregeHp2 = Info.getP2().getHp();
		}
	}

	@Override
	public int initialize(GameData arg0, boolean arg1) {
		// TODO Auto-generated method stub
		this.Ginfo = arg0;
		this.Side = arg1;
		
		Info = new FrameData();
		P1 = Info.getMyCharacter(Side);
		P2 = Info.getOpponentCharacter(!Side);
		Contabilizou = false;
		Thunder011.initialize(this.Ginfo,this.Side);
		BANZAI1.initialize(this.Ginfo, this.Side);
		DragonSurvivor1.initialize(Ginfo, Side);
		SelecaoBot(Ginfo,Side);
		return 0;
	}

	@Override
	public Key input() {
		// TODO Auto-generated method stub
		return Acao;
	}

	@Override
	public void processing() {
		// TODO Auto-generated method stub
		System.out.println(NomesBot[Posi]);
		if(Posi == 0)
		{
		//	System.out.println("oi1");
			BANZAI1.processing();
			Acao = BANZAI1.inputKey;
		}
		else if(Posi == 1)
		{
		//	System.out.println("oi2");
			Thunder011.processing();
			Acao = Thunder011.key;
		}
		else if(Posi == 2)
		{
		//	System.out.println("oi3");
			DragonSurvivor1.processing();
			Acao = DragonSurvivor1.inputKey;
		}
		if(!Info.getEmptyFlag() && Info.getRemainingTimeMilliseconds() > 0)
		{
			currentHp1 = Info.getP1().getHp();
			currentHp2 = Info.getP2().getHp();
			frame1 = Info.getRemainingFramesNumber();
			if(currentHp1 < AvaregeHp1 || currentHp2 < AvaregeHp2)
			{
				AvaregeHp1 = currentHp1;
				AvaregeHp2 = currentHp1;
			}
			if(frame1 == frame2)
			{
				GetHpOfTheMatch();
			}
		//	else if(frame1 < frame2)
		//	{
		//		frame2 = frame1;
		//	}
			else if(frame1 != frame2)
			{
				frame2 = frame1;
			}
			System.out.println(frame1 + "   " + frame2 + "  (" + AvaregeHp1 + AvaregeHp2 + ")\n");
		}
	}
};
