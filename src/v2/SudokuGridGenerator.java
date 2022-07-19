/**
 * NOT MY CODE!
 */

package v2;

import java.util.ArrayList;
import java.util.Collections;

public class SudokuGridGenerator {
    private int[] grid;

    /**
     *Generates a valid SIZE by SIZE Sudoku grid with 1 through SIZE appearing only once in every box, row, and column
     *@return an array of size TOTAL_CELLS containing the grid
     */
    public int[] generateGrid(int size)
    {
        int totalCells = size * size;
        int sqrtSize = (int) Math.sqrt(size);
        ArrayList<Integer> arr = new ArrayList<Integer>(size);
        grid = new int[totalCells];
        for(int i = 1; i <= size; i++) arr.add(i);

        //loads all boxes with numbers 1 through SIZE
        for(int i = 0; i < totalCells; i++)
        {
            if(i% size == 0) Collections.shuffle(arr);
            int perBox = ((i / sqrtSize) % sqrtSize) * size + ((i % (size * sqrtSize)) / size) * sqrtSize + (i / (size * sqrtSize)) * (size * sqrtSize) + (i % sqrtSize);
            grid[perBox] = arr.get(i% size);
        }

        //tracks rows and columns that have been sorted
        boolean[] sorted = new boolean[totalCells];

        for(int i = 0; i < size; i++)
        {
            boolean backtrack = false;
            //0 is row, 1 is column
            for(int a = 0; a<2; a++)
            {
                //every number 1-SIZE that is encountered is registered
                boolean[] registered = new boolean[size + 1]; //index 0 will intentionally be left empty since there are only number 1-SIZE.
                int rowOrigin = i * size;
                int colOrigin = i;

                ROW_COL: for(int j = 0; j < size; j++)
                {
                    //row/column stepping - making sure numbers are only registered once and marking which cells have been sorted
                    int step = (a%2==0? rowOrigin + j: colOrigin + j* size);
                    int num = grid[step];

                    if(!registered[num]) registered[num] = true;
                    else //if duplicate in row/column
                    {
                        //box and adjacent-cell swap (BAS method)
                        //checks for either unregistered and unsorted candidates in same box,
                        //or unregistered and sorted candidates in the adjacent cells
                        for(int y = j; y >= 0; y--)
                        {
                            int scan = (a%2==0? i * size + y: i + size * y);
                            if(grid[scan] == num)
                            {
                                //box stepping
                                for(int z = (a%2==0? (i% sqrtSize + 1) * sqrtSize : 0); z < size; z++)
                                {
                                    if(a%2 == 1 && z% sqrtSize <= i% sqrtSize)
                                        continue;
                                    int boxOrigin = ((scan % size) / sqrtSize) * sqrtSize + (scan / (size * sqrtSize)) * (size * sqrtSize);
                                    int boxStep = boxOrigin + (z / sqrtSize) * size + (z % sqrtSize);
                                    int boxNum = grid[boxStep];
                                    if((!sorted[scan] && !sorted[boxStep] && !registered[boxNum])
                                            || (sorted[scan] && !registered[boxNum] && (a%2==0? boxStep% size ==scan% size : boxStep/ size ==scan/ size)))
                                    {
                                        grid[scan] = boxNum;
                                        grid[boxStep] = num;
                                        registered[boxNum] = true;
                                        continue ROW_COL;
                                    }
                                    else if(z == size - 1) //if z == SIZE  - 1, then break statement not reached: no candidates available
                                    {
                                        //Preferred adjacent swap (PAS)
                                        //Swaps x for y (preference on unregistered numbers), finds occurence of y
                                        //and swaps with z, etc. until an unregistered number has been found
                                        int searchingNo = num;

                                        //noting the location for the blindSwaps to prevent infinite loops.
                                        boolean[] blindSwapIndex = new boolean[totalCells];

                                        //loop of size SIZE  * 2 to prevent infinite loops as well. Max of SIZE  * 2 swaps are possible.
                                        //at the end of this loop, if continue or break statements are not reached, then
                                        //fail-safe is executed called Advance and Backtrack Sort (ABS) which allows the
                                        //algorithm to continue sorting the next row and column before coming back.
                                        //Somehow, this fail-safe ensures success.
                                        for(int q = 0; q < size * 2; q++)
                                        {
                                            SWAP: for(int b = 0; b <= j; b++)
                                            {
                                                int pacing = (a%2==0? rowOrigin+b: colOrigin+b* size);
                                                if(grid[pacing] == searchingNo)
                                                {
                                                    int adjacentCell = -1;
                                                    int adjacentNo = -1;
                                                    int decrement = (a%2==0? size : 1);

                                                    for(int c = 1; c < sqrtSize - (i % sqrtSize); c++)
                                                    {
                                                        adjacentCell = pacing + (a%2==0? (c + 1)* size : c + 1);

                                                        //this creates the preference for swapping with unregistered numbers
                                                        if(   (a%2==0 && adjacentCell >= totalCells)
                                                                || (a%2==1 && adjacentCell % size == 0)) adjacentCell -= decrement;
                                                        else
                                                        {
                                                            adjacentNo = grid[adjacentCell];
                                                            if(i% sqrtSize !=0
                                                                    || c!=1
                                                                    || blindSwapIndex[adjacentCell]
                                                                    || registered[adjacentNo])
                                                                adjacentCell -= decrement;
                                                        }
                                                        adjacentNo = grid[adjacentCell];

                                                        //as long as it hasn't been swapped before, swap it
                                                        if(!blindSwapIndex[adjacentCell])
                                                        {
                                                            blindSwapIndex[adjacentCell] = true;
                                                            grid[pacing] = adjacentNo;
                                                            grid[adjacentCell] = searchingNo;
                                                            searchingNo = adjacentNo;

                                                            if(!registered[adjacentNo])
                                                            {
                                                                registered[adjacentNo] = true;
                                                                continue ROW_COL;
                                                            }
                                                            break SWAP;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        //begin Advance and Backtrack Sort (ABS)
                                        backtrack = true;
                                        break ROW_COL;
                                    }
                                }
                            }
                        }
                    }
                }

                if(a%2==0)
                    for(int j = 0; j < size; j++) sorted[i* size +j] = true; //setting row as sorted
                else if(!backtrack)
                    for(int j = 0; j < size; j++) sorted[i+j* size] = true; //setting column as sorted
                else //reseting sorted cells through to the last iteration
                {
                    backtrack = false;
                    for(int j = 0; j < size; j++) sorted[i* size +j] = false;
                    for(int j = 0; j < size; j++) sorted[(i-1)* size +j] = false;
                    for(int j = 0; j < size; j++) sorted[i-1+j* size] = false;
                    i-=2;
                }
            }
        }

        if(!isPerfect()){
//            throw new RuntimeException("ERROR: Imperfect grid generated.");
            return generateGrid(size);
        }

        return grid;
    }

    /**
     *Prints a visual representation of a SIZExSIZE Sudoku grid
     */
    public static void printGrid(int[] grid)
    {
        int size = (int) Math.sqrt(grid.length);
        int totalCells = size * size;
        if(grid.length != totalCells) throw new IllegalArgumentException("The grid must be a single-dimension grid of length TOTAL_CELLS");
        for(int i = 0; i < totalCells; i++)
        {
            System.out.print(grid[i]+""+(i% size == size - 1?"\n":"\t"));
        }
    }

    /**
     *Tests an int array of length TOTAL_CELLS to see if it is a valid Sudoku grid. i.e. 1 through SIZE appearing once each in every row, column, and box
     *@return a boolean representing if the grid is valid
     */
    private boolean isPerfect()
    {
        int size = (int) Math.sqrt(grid.length);
        int totalCells = size * size;
        int sqrtSize = (int) Math.sqrt(size);
        if(grid.length != totalCells) throw new IllegalArgumentException("The grid must be a single-dimension grid of length TOTAL_CELLS");

        //tests to see if the grid is perfect

        //for every box
        for(int i = 0; i < size; i++)
        {
            boolean[] registered = new boolean[size + 1];
            registered[0] = true;
            int boxOrigin = (i * sqrtSize) % size + ((i * sqrtSize) / size) * (size * sqrtSize);
            for(int j = 0; j < size; j++)
            {
                int boxStep = boxOrigin + (j / sqrtSize) * size + (j % sqrtSize);
                int boxNum = grid[boxStep];
                registered[boxNum] = true;
            }
            for(boolean b: registered)
                if(!b) return false;
        }

        //for every row
        for(int i = 0; i < size; i++)
        {
            boolean[] registered = new boolean[size + 1];
            registered[0] = true;
            int rowOrigin = i * size;
            for(int j = 0; j < size; j++)
            {
                int rowStep = rowOrigin + j;
                int rowNum = grid[rowStep];
                registered[rowNum] = true;
            }
            for(boolean b: registered)
                if(!b) return false;
        }

        //for every column
        for(int i = 0; i < size; i++)
        {
            boolean[] registered = new boolean[size + 1];
            registered[0] = true;
            int colOrigin = i;
            for(int j = 0; j < size; j++)
            {
                int colStep = colOrigin + j* size;
                int colNum = grid[colStep];
                registered[colNum] = true;
            }
//            for (boolean b : registered) {
//                if(!b) {
//                    System.out.println(colOrigin);
//                }
//            }
            for(boolean b: registered)
                if(!b) return false;
        }

        return true;
    }

    public static void main(String[]args)
    {
        /*int trials = 0;
        while(true)
        {
            SudokuGridGenerator sudoku = new SudokuGridGenerator();
            trials++;
            //average solution time hovers around 7 microseconds (~0.000007 seconds)
            //not including time it takes for print statements, which eats up BUTT-LOADS of time and slows down the output 10-fold
            if(isPerfect(sudoku.generateGrid())) System.out.println("PERFECT GRID #" + String.format("%,d",(trials)));

        }*/
        SudokuGridGenerator sudoku = new SudokuGridGenerator();
        printGrid(sudoku.generateGrid(25));
    }
}