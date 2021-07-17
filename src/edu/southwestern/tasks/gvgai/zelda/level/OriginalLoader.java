package edu.southwestern.tasks.gvgai.zelda.level;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import edu.southwestern.parameters.Parameters;
import edu.southwestern.tasks.gvgai.zelda.ZeldaVGLCUtil;
import me.jakerg.rougelike.Ladder;
import me.jakerg.rougelike.Tile;

public class OriginalLoader implements LevelLoader {

	Set<List<List<Integer>>> levels;
	
	public OriginalLoader() {
		Parameters.parameters.setBoolean("zeldaGANUsesOriginalEncoding", false);
		levels = new HashSet<>();
		String[] dungeonsToLoad = new String[] {"tloz1_1_flip", "tloz2_1_flip", "tloz3_1_flip", "tloz4_1_flip", "tloz5_1_flip", "tloz6_1_flip", "tloz7_1_flip", "tloz8_1_flip"};
		loadLevels(dungeonsToLoad);
		
	}
	
	
	private void loadLevels(String[] dungeonsToLoad) {
		Path filePath = new File("data/VGLC/Zelda/Processed").toPath();
		for(String folder : dungeonsToLoad) {
			File dir = filePath.resolve(folder).toFile();
			for(File entry : dir.listFiles()) {
				loadOneLevel(entry);
			}
		}
		
	}

	private void loadOneLevel(File file) {
		String[] levelString = new String[11];
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			int i = 0;
			while(scanner.hasNextLine())
				levelString[i++] = scanner.nextLine();
				
			List<List<Integer>> levelInt = ZeldaVGLCUtil.convertZeldaLevelVGLCtoRoomAsList(levelString);
			removeJunk(levelInt);
			levels.add(levelInt);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	// This switch case purposefully does nothing for certain tile types
	@SuppressWarnings("incomplete-switch")
	private void removeJunk(List<List<Integer>> levelInt) {
		for(int y = 0; y < levelInt.size(); y++) {
			for(int x = 0; x < levelInt.get(y).size(); x++) {
				Integer num = levelInt.get(y).get(x);
				Tile tile = Tile.findNum(num);
				switch(tile) {
				case DOOR:
				case LOCKED_DOOR:
					num = Tile.WALL.getNum();
					break;
				case TRIFORCE:
					num = Tile.FLOOR.getNum();
					break;
				}
				if(tile.isMovable())
					num = Tile.WALL.getNum();
				if(num == Ladder.INT_CODE) // Ladder item
					num = Tile.FLOOR.getNum();
				levelInt.get(y).set(x, num);
					
			}
		}
	}


	@Override
	public List<List<List<Integer>>> getLevels() {
		return new ArrayList<>(levels);
	}

}
