package edu.southwestern.tasks.gvgai.zelda.dungeon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.level.GraphRuleManager;
import edu.southwestern.tasks.gvgai.zelda.level.PhenotypeLoader;
import edu.southwestern.tasks.gvgai.zelda.level.ZeldaGrammar;
import edu.southwestern.tasks.gvgai.zelda.level.graph.ZeldaDungeonGraphBackBone;
import edu.southwestern.util.ClassCreation;
import edu.southwestern.util.datastructures.Graph;
import edu.southwestern.util.datastructures.GraphUtil;

public class GraphDungeon extends ZeldaDungeon<ArrayList<Double>>{

	@Override
	public Dungeon makeDungeon(ArrayList<ArrayList<Double>> phenotypes, int numRooms) {

		Graph<ZeldaGrammar> graph = null;
		try {
			
			ZeldaDungeonGraphBackBone ConstructGraph = (ZeldaDungeonGraphBackBone) ClassCreation.createObject("zeldaGraphBackBone");
			graph = ConstructGraph.getInitialGraphBackBone();
			@SuppressWarnings("unchecked")
			GraphRuleManager<ZeldaGrammar> grammar = (GraphRuleManager<ZeldaGrammar>) ClassCreation.createObject("zeldaGrammarRules");
			
			grammar.applyRules(graph);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		try {
			GraphUtil.saveGrammarGraph(graph, "data/VGLC/graph.dot");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Parameters.parameters.setBoolean("zeldaGANUsesOriginalEncoding", false);
		
		try {
			Dungeon d = DungeonUtil.convertToDungeon(graph, new PhenotypeLoader(phenotypes));
			if(Parameters.parameters.booleanParameter("makeZeldaLevelsPlayable")) 
				DungeonUtil.makeDungeonPlayable(d);
			return d;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<List<Integer>> getLevelFromLatentVector(ArrayList<Double> phenotype) {
		return null;
	}

}
