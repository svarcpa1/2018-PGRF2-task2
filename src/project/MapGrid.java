package project;

public class MapGrid {

    private int[][] mapGrid = new int [16][16];

    public MapGrid(){

    }

    /**
     * getting map grid (array)
     * @return array mapGrid
     */
    public int[][] getMapGrid() {
        return mapGrid;
    }

    /**
     * setting value to specific field
     * @param r row
     * @param c column
     * @param i value
     */
    public void setField(int r, int c, int i){
        mapGrid[r][c] = i;
    }

    /**
     * Method returns value by field
     * @param r row of field
     * @param c column of field
     * @return value on field position [r][c]
     */
    public int getField(int r, int c){
        return mapGrid[r][c];
    }

    /**
     * 0 = wall, 1 = not used, 2 = end, 3 = free/possibility of trap
     */
    public void setUpMap (){
        mapGrid = new int[][]{  {0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
                                {0, 2, 0, 1, 0, 0, 3, 3, 3, 2, 0, 2, 3, 0, 1, 1},
                                {0, 3, 0, 0, 0, 3, 3, 3, 3, 3, 0, 3, 3, 0, 1, 1},
                                {0, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 3, 0, 1, 1},
                                {0, 3, 3, 3, 3, 3, 3, 0, 3, 3, 3, 3, 3, 0, 1, 1},
                                {0, 0, 0, 0, 0, 3, 3, 0, 3, 0, 0, 0, 3, 0, 1, 1},
                                {1, 1, 1, 1, 0, 3, 3, 0, 3, 3, 3, 0, 3, 0, 1, 1},
                                {0, 0, 0, 0, 0, 3, 3, 0, 3, 0, 3, 0, 3, 0, 1, 1},
                                {0, 3, 3, 3, 3, 3, 3, 3, 3, 0, 3, 3, 3, 0, 1, 1},
                                {0, 2, 3, 3, 3, 3, 3, 0, 0, 0, 3, 0, 3, 0, 0, 0},
                                {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 3, 0, 0, 0, 2, 0},
                                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 3, 3, 3, 3, 3, 0},
                                {1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0},
                                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                                {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
        };

        //transpose
        for(int i = 0; i < mapGrid.length; i++) {
            for(int j = i+1; j < mapGrid.length; j++) {
                int temp = mapGrid[i][j];
                mapGrid[i][j] = mapGrid[j][i];
                mapGrid[j][i] = temp;
            }
        }
    }
}
