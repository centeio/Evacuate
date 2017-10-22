package world;

import jason.environment.grid.GridWorldModel;

public class RoomModel extends GridWorldModel {
	
	public static final int DOOR  = 16;
	
	// singleton pattern
    protected static RoomModel model = null;

    synchronized public static RoomModel create(int w, int h, int nbAgs) {
        if (model == null) {
            model = new RoomModel(w, h, nbAgs);
        }
        
        //Walls
        model.add(RoomModel.OBSTACLE, 0, 0);
        model.add(RoomModel.OBSTACLE, 0, 1);
        model.add(RoomModel.OBSTACLE, 0, 2);
        model.add(RoomModel.OBSTACLE, 0, 3);
        model.add(RoomModel.OBSTACLE, 0, 4);
        model.add(RoomModel.OBSTACLE, 0, 5);
        model.add(RoomModel.OBSTACLE, 0, 6);
        model.add(RoomModel.OBSTACLE, 0, 7);
        model.add(RoomModel.OBSTACLE, 0, 8);
        model.add(RoomModel.OBSTACLE, 0, 9);
        model.add(RoomModel.OBSTACLE, 0, 10);
        model.add(RoomModel.OBSTACLE, 0, 11);
        model.add(RoomModel.OBSTACLE, 0, 12);
        model.add(RoomModel.OBSTACLE, 0, 13);
        model.add(RoomModel.OBSTACLE, 0, 14);
        model.add(RoomModel.OBSTACLE, 0, 15);
        model.add(RoomModel.OBSTACLE, 0, 16);
        model.add(RoomModel.OBSTACLE, 0, 17);
        model.add(RoomModel.OBSTACLE, 0, 18);
        model.add(RoomModel.OBSTACLE, 0, 19);
        
        model.add(RoomModel.OBSTACLE, 29, 0);
        model.add(RoomModel.OBSTACLE, 29, 1);
        model.add(RoomModel.OBSTACLE, 29, 2);
        model.add(RoomModel.OBSTACLE, 29, 3);
        model.add(RoomModel.OBSTACLE, 29, 4);
        model.add(RoomModel.OBSTACLE, 29, 5);
        model.add(RoomModel.OBSTACLE, 29, 6);
        model.add(RoomModel.OBSTACLE, 29, 7);
        model.add(RoomModel.OBSTACLE, 29, 8);
        model.add(RoomModel.OBSTACLE, 29, 9);
        model.add(RoomModel.OBSTACLE, 29, 10);
        model.add(RoomModel.OBSTACLE, 29, 11);
        model.add(RoomModel.OBSTACLE, 29, 12);
        model.add(RoomModel.OBSTACLE, 29, 13);
        model.add(RoomModel.OBSTACLE, 29, 14);
        model.add(RoomModel.OBSTACLE, 29, 15);
        model.add(RoomModel.OBSTACLE, 29, 16);
        model.add(RoomModel.OBSTACLE, 29, 17);
        model.add(RoomModel.OBSTACLE, 29, 18);
        model.add(RoomModel.OBSTACLE, 29, 19);
        
        model.add(RoomModel.OBSTACLE, 1, 0);
        model.add(RoomModel.OBSTACLE, 2, 0);
        model.add(RoomModel.OBSTACLE, 3, 0);
        model.add(RoomModel.OBSTACLE, 4, 0);
        model.add(RoomModel.OBSTACLE, 5, 0);
        model.add(RoomModel.OBSTACLE, 6, 0);
        model.add(RoomModel.OBSTACLE, 7, 0);
        model.add(RoomModel.OBSTACLE, 8, 0);
        model.add(RoomModel.OBSTACLE, 9, 0);
        model.add(RoomModel.OBSTACLE, 10, 0);
        model.add(RoomModel.OBSTACLE, 11, 0);
        model.add(RoomModel.OBSTACLE, 12, 0);
        model.add(RoomModel.OBSTACLE, 13, 0);
        model.add(RoomModel.OBSTACLE, 14, 0);
        model.add(RoomModel.OBSTACLE, 15, 0);
        model.add(RoomModel.OBSTACLE, 16, 0);
        model.add(RoomModel.OBSTACLE, 17, 0);
        model.add(RoomModel.OBSTACLE, 18, 0);
        model.add(RoomModel.OBSTACLE, 19, 0);
        model.add(RoomModel.OBSTACLE, 20, 0);
        model.add(RoomModel.OBSTACLE, 21, 0);
        model.add(RoomModel.OBSTACLE, 22, 0);
        model.add(RoomModel.OBSTACLE, 23, 0);
        model.add(RoomModel.OBSTACLE, 24, 0);
        model.add(RoomModel.OBSTACLE, 25, 0);
        model.add(RoomModel.OBSTACLE, 26, 0);
        model.add(RoomModel.DOOR, 27, 0);
        model.add(RoomModel.OBSTACLE, 28, 0);
        
        model.add(RoomModel.OBSTACLE, 1, 19);
        model.add(RoomModel.OBSTACLE, 2, 19);
        model.add(RoomModel.OBSTACLE, 3, 19);
        model.add(RoomModel.OBSTACLE, 4, 19);
        model.add(RoomModel.OBSTACLE, 5, 19);
        model.add(RoomModel.OBSTACLE, 6, 19);
        model.add(RoomModel.OBSTACLE, 7, 19);
        model.add(RoomModel.OBSTACLE, 8, 19);
        model.add(RoomModel.OBSTACLE, 9, 19);
        model.add(RoomModel.OBSTACLE, 10, 19);
        model.add(RoomModel.OBSTACLE, 11, 19);
        model.add(RoomModel.OBSTACLE, 12, 19);
        model.add(RoomModel.OBSTACLE, 13, 19);
        model.add(RoomModel.OBSTACLE, 14, 19);
        model.add(RoomModel.OBSTACLE, 15, 19);
        model.add(RoomModel.OBSTACLE, 16, 19);
        model.add(RoomModel.OBSTACLE, 17, 19);
        model.add(RoomModel.OBSTACLE, 18, 19);
        model.add(RoomModel.OBSTACLE, 19, 19);
        model.add(RoomModel.OBSTACLE, 20, 19);
        model.add(RoomModel.OBSTACLE, 21, 19);
        model.add(RoomModel.OBSTACLE, 22, 19);
        model.add(RoomModel.OBSTACLE, 23, 19);
        model.add(RoomModel.OBSTACLE, 24, 19);
        model.add(RoomModel.OBSTACLE, 25, 19);
        model.add(RoomModel.OBSTACLE, 26, 19);
        model.add(RoomModel.OBSTACLE, 27, 19);
        model.add(RoomModel.OBSTACLE, 28, 19);
        
        
        return model;
    }

	private RoomModel(int w, int h, int nAgs) {
		super(w, h, nAgs);
	}

}
