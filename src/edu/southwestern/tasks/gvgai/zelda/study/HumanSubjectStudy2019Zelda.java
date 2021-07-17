package edu.southwestern.tasks.gvgai.zelda.study;

import java.io.File;
import java.io.FileNotFoundException;
//import java.util.LinkedList;
//import java.util.List;

import edu.southwestern.MMNEAT.MMNEAT;
import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.dungeon.Dungeon;
import edu.southwestern.tasks.gvgai.zelda.dungeon.DungeonUtil;
import edu.southwestern.tasks.gvgai.zelda.dungeon.LoadOriginalDungeon;
import edu.southwestern.tasks.gvgai.zelda.level.GraphRuleManager;
import edu.southwestern.tasks.gvgai.zelda.level.LevelLoader;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.graph.ZeldaDungeonGraphBackBone;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.GraphUtil;
import edu.southwestern.util.random.RandomNumbers;
import me.jakerg.rougelike.RougelikeApp;

/**
 * Launches Zelda/Rogue dungeon play sessions for the 2019 human subject study comparing dungeons from
 * the original Legend of Zelda to ones using Zelda rooms with a graph grammar dungeon, and dungeons
 * using ZeldaGAN rooms with a graph grammar.
 * 
 * @author Jake Gutierrez
 */
public class HumanSubjectStudy2019Zelda {
	public enum Type {TUTORIAL, ORIGINAL, GENERATED_DUNGEON}

	public static String dungeonType;
	public static String subjectDir;
	
	public static void runTrial(Type type) {
		Dungeon dungeonToPlay = null;
		// Save in the experiment's subject directory
		subjectDir = "batch/Experiments-2020-CEC-ZeldaGAN/Subject-" + 
	            String.valueOf(Parameters.parameters.integerParameter("randomSeed")) + 
	            "/";

		Parameters.parameters.setBoolean("zeldaHelpScreenEnabled", true);
		int seed = Parameters.parameters.integerParameter("randomSeed");	
		RandomNumbers.reset(seed);
		
		if(type.equals(Type.ORIGINAL)) {
			dungeonType = "Original";
			// This is all of the levels, but the range of complexity across them is too much. We stick to the first few for simplicity.
			//String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", "tloz4_1_flip", "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip"};
			// Levels 1, 2, and 4. Level 3 is skipped because its layout is (inconveniently) a swastika, which could be offensive.
			//String[] names = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz4_1_flip"};
			// Now we are only using level 4, because it is the level that introduces the raft item, and we wanted to introduce this in each of our dungeons as well.
			String[] names = new String[] {"tloz4_1_flip"};
			String dungeonName = names[seed % names.length];
			try {
				dungeonToPlay = LoadOriginalDungeon.loadOriginalDungeon(dungeonName);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else if(type.equals(Type.GENERATED_DUNGEON)) {

			try {
				ZeldaDungeonGraphBackBone ConstructGraph = (ZeldaDungeonGraphBackBone) ClassCreation.createObject("zeldaGraphBackBone");
				Graph<ZeldaGrammar> graph = ConstructGraph.getInitialGraphBackBone();
				@SuppressWarnings("unchecked")
				GraphRuleManager<ZeldaGrammar> grammar = (GraphRuleManager<ZeldaGrammar>) ClassCreation.createObject("zeldaGrammarRules");
				

				grammar.applyRules(graph);
				
				LevelLoader loader = (LevelLoader) ClassCreation.createObject("zeldaLevelLoader");
				dungeonType = loader.getClass().getSimpleName();
				
				if(Parameters.parameters != null && Parameters.parameters.booleanParameter("rogueLikeDebugMode"))
					GraphUtil.saveGrammarGraph(graph, subjectDir + "DungeonGraph_" + dungeonType + ".dot");
				
				dungeonToPlay = DungeonUtil.recursiveGenerateDungeon(graph, loader);
				
				//System.out.println("IS THIS NULL??: "+dungeonToPlay);
				
				DungeonUtil.makeDungeonPlayable(dungeonToPlay);
				dungeonToPlay.markReachableRooms();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
//			System.out.println("waiting");
//			MiscUtil.waitForReadStringAndEnterKeyPress();
			
		} else if(type.equals(Type.TUTORIAL)) {
			System.out.println("\n\n\nTutorial not supported yet.");
			System.exit(1);
		} else {
			throw new IllegalArgumentException("Type : " + type + " unrecognized");
		}
		
		System.out.println("Play dungeon");
		try {
			if(Parameters.parameters != null && Parameters.parameters.booleanParameter("rogueLikeDebugMode"))
				DungeonUtil.viewDungeon(dungeonToPlay);
			RougelikeApp.PD.storeDungeonData(dungeonToPlay);
			RougelikeApp.startDungeon(dungeonToPlay, false);
			File dir = new File("ZeldaStudy2019");
			if(!dir.exists())
				dir.mkdir(); // Should only happen the first time the code is run
			RougelikeApp.saveParticipantData();
			dungeonToPlay.saveToJson(subjectDir + dungeonType  + "_dungeon.json");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws FileNotFoundException, NoSuchMethodException {
		// TODO Auto-generated method stub
		
		// zeldaType: original/generated/tutorial
		// zeldaLevelLoader: edu.southwestern.tasks.gvgai.zelda.level.GANLoader
		//                   edu.southwestern.tasks.gvgai.zelda.level.OriginalLoader
		
		
		MMNEAT.main("zeldaType:generated randomSeed:7 zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.OriginalLoader rogueLikeDebugMode:true zeldaGraphBackBone:edu.southwestern.tasks.gvgai.zelda.level.graph.TwoTriforceBugGraph zeldaGrammarRules:edu.southwestern.tasks.gvgai.zelda.level.MoreInterestingGraphGrammarRules firstSoftLockedRoomHasRaft:false".split(" "));
		//MMNEAT.main("zeldaType:generated randomSeed:0 zeldaLevelLoader:edu.southwestern.tasks.gvgai.zelda.level.GANLoader".split(" "));
	}

}
