
import gameInterface.AIInterface;
import structs.FrameData;
import structs.GameData;
import structs.Key;

import java.util.Random;

import commandcenter.CommandCenter;

public class DragonSurvivor implements AIInterface {

	// Key class for return.
	public Key inputKey;
	// is used for getting a random number.
	private Random rnd;
	private FrameData frameData;
	private GameData gd;
	private boolean player;
	private CommandCenter cc;
	
	private int qtdMovimentos;
	private String[]        callsG = {"4 _ A"  , "4 _ B"  , "A"      , "B"      , "2 _ A"   , "2 _ B"   , "6 _ A"   , "6 _ B"   , "3 _ A"    , "3 _ B"    , "2 3 6 _ A"    , "2 3 6 _ B"    , "2 3 6 _ C"    , "6 2 3 _ A"    , "6 2 3 _ B"    , "2 1 4 _ A"    , "2 1 4 _ B"    , "A"     , "B"    , "2 _ A" , "2 _ B" , "6 _ A" , "6 _ B" , "8 _ A" , "8 _ B" , "2 3 6 _ A"  , "2 3 6 _ B"  , "6 2 3 _ A"  , "6 2 3 _ B"  , "2 1 4 _ A"  , "2 1 4 _ B"};
	//private String[]  attackKEYS = {"4 _ A", "4 _ B", "6 2 3 _ A", "2 3 6 _ C", "A", "B", "6 _ A", "2 3 6 _ A", "2 3 6 _ B", "2 1 4 _ B", "6 2 3 _ B", "6 _ B", "2 1 4 _ A", "2 _ A", "3 _ A", "2 _ B", "3 _ B", "A", "B", "2 _ A", "2 _ B", "6 _ A", "6 _ B", "8 _ A", "8 _ B", "2 3 6 _ A", "2 3 6 _ B", "6 2 3 _ A", "6 2 3 _ B", "2 1 4 _ A", "2 1 4 _ B"};
	  private String[] attackNAMES = {"THROW_A", "THROW_B", "STAND_A", "STAND_B", "CROUCH_A", "CROUCH_B", "STAND_FA", "STAND_FB", "CROUCH_FA", "CROUCH_FB", "STAND_D_DF_FA", "STAND_D_DF_FB", "STAND_D_DF_FC", "STAND_F_D_DFA", "STAND_F_D_DFB", "STAND_D_DB_BA", "STAND_D_DB_BB", "AIR_A" , "AIR_B", "AIR_DA", "AIR_DB", "AIR_FA", "AIR_FB", "AIR_UA", "AIR_UB", "AIR_D_DF_FA", "AIR_D_DF_FB", "AIR_F_D_DFA", "AIR_F_D_DFB", "AIR_D_DB_BA", "AIR_D_DB_BB"};
	  ///                  Indices = {0        , 1        , 2        , 3        , 4         , 5         , 6         , 7         , 8          , 9          , 10             , 11             , 12             , 13             , 14             , 15             , 16             , 17      , 18     , 19      , 20      , 21      , 22      , 23      , 24      , 25           , 26           , 27           , 28           , 29           , 30           };
	  private int[]   payoffsLonga = {10        , 10      , 10       , 10       , 10        , 10        , 10        , 10        , 10         , 10         , 20             , 20             , 10             , 10             , 10             , 10             , 10             , 10      , 10     , 10      , 10      , 10      , 10      , 10      , 10      , 10           , 10           , 10           , 10           , 10           , 10           };
	  private int[]   payoffsMedia = {10        , 10      , 10       , 10       , 10        , 10        , 10        , 10        , 10         , 10         , 16             , 16             , 16             , 10             , 10             , 15             , 15             , 13      , 13     , 13      , 13      , 13      , 13      , 13      , 13      , 13           , 13           , 13           , 13           , 13           , 13           };
 private int[]   payoffsMediaCurta = {9         , 9       , 9        , 9        , 9         , 9         , 8         , 8         , 9          , 9          , 10             , 10             , 10             , 10             , 10             , 15             , 15             , 13      , 13     , 13      , 13      , 13      , 13      , 13      , 13      , 13           , 13           , 13           , 13           , 13           , 13           };
	  private int[]   payoffsCurta = {0         , 0       , 5        , 5        , 5         , 9         , 8         , 8         , 6          , 6          , 10             , 10             , 10             , 10             , 10             , 15             , 15             , 13      , 13     , 13      , 13      , 13      , 13      , 13      , 13      , 13           , 13           , 13           , 13           , 13           , 13           };
	private float[] payoffFinal;
	
	int i,j;
	private int memorySize;//talvez valores pequenos sejam suficientes
	private int[] movimentosPL;
	private int[] movimentosOP;
	private int[] distanciasPL;
	private int[] distanciasOP;
	
	private int[] memoriaAtual;
	private int[] memoriaAtual2;
	private int[] payoffAtual;
	
	private int[] memoriaDistanciaCurta;
	private int[] memoriaDistanciaCurta2;
	private int[] memoriaDistanciaMediaCurta;
	private int[] memoriaDistanciaMediaCurta2;
	private int[] memoriaDistanciaMedia;
	private int[] memoriaDistanciaMedia2;
	private int[] memoriaDistanciaLonga;
	private int[] memoriaDistanciaLonga2;
	private boolean adicionado; 
	
	private float[] predicoes;
	
	private int contadorMovimentosOP;
	private int predicao;
	
	
	//VARIAVEIS DO OPONENTE
	int frameFimAtaqueInimigo; 
	
	
	//VARIAVEIS DE TESTE PODEM SER RETIRADAS DEPOIS
	private String acaoAtual;
	private int acaoAtualRemainingframes;
	
	//VARIAVEIS DA SELECAO DE MOVIMENTO
	private int distanciaCurta;
	private int distanciaMedia;
	private int distanciaMediaCurta;
	private int distanciaLonga;
	private int distanciaAtual;
	
	private int distanciaCurtaInicio;
	private int distanciaMediaCurtaInicio;
	private int distanciaMediaInicio;
	private int distanciaLongaInicio;
	private int distanciaAtualInicio;
	
	@Override
	public void close() {
		// finaliza a IA
		//System.out.println("Movimentos do Oponente:"+contadorMovimentosOP);
	}

	@Override
	public String getCharacter() {
		// retorna o personagem que esta sendo usado: CHARACTER_ZEN, CHARACTER_GARNET, CHARACTER_LUD, CHARACTER_KFM
		return "CHARACTER_ZEN";
	}

	@Override
	public void getInformation(FrameData frameData) {
		// TODO coloca as informacoes deste round no arg0
		this.frameData=frameData;
		cc.setFrameData(this.frameData, player);
	}

	@Override
	public int initialize(GameData gameData, boolean playerNumber) {
		// inicializa coisas no inicio da partidas
		this.player = playerNumber;
		this.gd=gameData;
		rnd = new Random();
		
		inputKey = new Key();
		frameData = new FrameData();
		cc = new CommandCenter();
		qtdMovimentos=31;

		acaoAtual="inicio";
		acaoAtualRemainingframes=0;
		
		memorySize=3;//escolha arbitraria
		memoriaDistanciaCurta= new int[memorySize];
		memoriaDistanciaCurta2= new int[memorySize];
		memoriaDistanciaMediaCurta= new int[memorySize];
		memoriaDistanciaMediaCurta2= new int[memorySize];
		memoriaDistanciaMedia= new int[memorySize];
		memoriaDistanciaMedia2= new int[memorySize];
		memoriaDistanciaLonga= new int[memorySize];
		memoriaDistanciaLonga2= new int[memorySize];
		adicionado=false;
		movimentosPL = new int[memorySize];
		distanciasPL = new int[memorySize];
		movimentosOP = new int[memorySize];
		distanciasOP = new int[memorySize];
		predicoes = new float[qtdMovimentos+1];
		
		for(i=0;i<memorySize;i++)
		{
			movimentosPL[i]=0; 
			distanciasPL[i]=0;
			movimentosOP[i]=0;
			distanciasOP[i]=0;
			
			memoriaDistanciaCurta[i]=-1;
			memoriaDistanciaCurta2[i]=-1;
			memoriaDistanciaMediaCurta[i]=-1;
			memoriaDistanciaMediaCurta2[i]=-1;
			memoriaDistanciaMedia[i]=-1;
			memoriaDistanciaMedia2[i]=-1;
			memoriaDistanciaLonga[i]=-1;
			memoriaDistanciaLonga2[i]=-1;
			
		}
		
		payoffFinal = new float[qtdMovimentos];
		for(i=0;i<qtdMovimentos;i++)
		{
			payoffFinal[i]=0;
		}
		
		distanciaCurta=100;
		distanciaCurtaInicio=-1;
		distanciaMediaCurta=170;
		distanciaMediaCurtaInicio=100;
		distanciaMedia=305;
		distanciaMediaInicio=125;
		distanciaLonga=99999;
		distanciaLongaInicio=305;
		distanciaAtual=0;
		distanciaAtualInicio=0;
		return 0;
	}

	@Override
	public Key input() {
		// TODO Auto-generated method stub
		return inputKey;
	}

	@Override
	public void processing() {
		// processa os dados da AI
		if(!frameData.getEmptyFlag() && frameData.getRemainingTime()>0)
		{
			if (cc.getskillFlag())//testa se um skill esta sendo "inputado"(não sei como falar isso em português)
			{
				inputKey = cc.getSkillKey();//caso sim entao continua a inputar ele
			}
			else
			{	
				inputKey.empty();//limpa a inputKey(reseta/reinicia ela)
				cc.skillCancel();//limpa skillData(array que contem a sequêcia de botoes para serem apertados) e marca skillFlag como false
				
				
				if(cc.getDistanceX()<distanciaCurta)
				{
					distanciaAtual=distanciaCurta;
					distanciaAtualInicio=distanciaCurtaInicio;
					memoriaAtual=memoriaDistanciaCurta;
					memoriaAtual2=memoriaDistanciaCurta2;
					payoffAtual=payoffsCurta;
				}
				else if(cc.getDistanceX()<distanciaMediaCurta)
				{
					distanciaAtual=distanciaMediaCurta;
					distanciaAtualInicio=distanciaMediaCurtaInicio;
					memoriaAtual=memoriaDistanciaMediaCurta;
					memoriaAtual2=memoriaDistanciaMediaCurta2;
					payoffAtual=payoffsMediaCurta;
				}
				else if(cc.getDistanceX()<distanciaMedia)
				{
					distanciaAtual=distanciaMedia;
					distanciaAtualInicio=distanciaMediaInicio;
					memoriaAtual=memoriaDistanciaMediaCurta;
					memoriaAtual2=memoriaDistanciaMedia2;
					payoffAtual=payoffsMedia;
				}
				else if(cc.getDistanceX()<distanciaLonga)
				{
					distanciaAtual=distanciaLonga;
					distanciaAtualInicio=distanciaLongaInicio;
					memoriaAtual=memoriaDistanciaLonga;
					memoriaAtual2=memoriaDistanciaLonga2;
					payoffAtual=payoffsLonga;
				}
				
				for(i=0;i<qtdMovimentos;i++)
				{
					if(cc.getEnemyCharacter().getAction().name().equals(attackNAMES[i]))//oponente esta realizando um ataque
					{	
						if(acaoAtual!=cc.getEnemyCharacter().getAction().name() || (acaoAtual==cc.getEnemyCharacter().getAction().name() &&  cc.getEnemyCharacter().getRemainingFrame()>=acaoAtualRemainingframes) )//ataque comesando agora
						{
							acaoAtual=cc.getEnemyCharacter().getAction().name();
							acaoAtualRemainingframes = cc.getEnemyCharacter().getRemainingFrame();
							adicionado = false;
							for(j=0;j<memorySize;j++)
							{
								if(memoriaAtual[j]==-1)
								{
									memoriaAtual[j]=i;
									memoriaAtual2[j]=cc.getDistanceX();
									adicionado=true;
									j=memorySize+1;//saindo do loop
								}
							}
							if(adicionado==false)
							{	
								//atualizando as memorias curtas 
								for(j=0;j<memorySize-1;j++)
								{
									memoriaAtual[j]=memoriaAtual[j+1];
									memoriaAtual2[j]=memoriaAtual2[j+1];
								}
									
								memoriaAtual[memorySize-1]=i;
								memoriaAtual2[memorySize-1]=cc.getDistanceX();
								adicionado=true;
							}
							//SAINDO DO LOOP, MOVIMENTO ENCONTRADO E ADICIONADO
							i=qtdMovimentos+1;
						}
					}
				}
				
				///ESCOLHENDO O PROXIMO MOVIMENTO
				frameFimAtaqueInimigo = cc.getEnemyCharacter().getRemainingFrame();
				
				for(i=0;i<qtdMovimentos+1;i++)
					predicoes[i]=0;
				///pegando a probabilidade a priori
				for(i=0;i<memorySize;i++)
				{
					if(memoriaAtual[i]!=-1)
						predicoes[payoffAtual[memoriaAtual[i]]]+=1/(float)memorySize;
				}
				
				int maior=-1;
				
				if(distanciaAtual==distanciaMedia)
				{
					if(cc.getMyEnergy()<50)
					{
						if(predicoes[16]>0)
						{
							predicoes[10]+=predicoes[16];
							predicoes[16]=0;
						}
					}
					
				}
				for(i=0;i<qtdMovimentos;i++)
				{
					if(maior==-1)
					{
						if(predicoes[i]>0)
						{
							maior=i;
						}
					}
					else if(predicoes[i]>predicoes[maior])
					{
						maior=i;
					}
				}
				///MOVIMENTO A SER USADO EH DADO POR MAIOR
				///SETAR A KEY QUE INDICA O MOVIMENTO A SER USADO
				if(maior==-1)//retorna randomico
					cc.commandCall(callsG[rnd.nextInt(qtdMovimentos)]);
				else if(maior>=17 && !cc.getMyCharacter().getState().name().equals("AIR"))//ataque aerio
					cc.commandCall("FOR_JUMP");
				else
					cc.commandCall(callsG[maior]);

			}
		}
	}

}
