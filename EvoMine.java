/* 
 * Jack Wines and Sam Orfield
 * Running this will start evolution
 * compile with:
 * javac MineSweeper.java -cp ../thisFolder
 * javac EvoMine
 * Run with:
 * java: Evomine
 */
import org.jenetics.BitChromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.BitGene;
import org.jenetics.IntegerGene;
import org.jenetics.Genotype;
import org.jenetics.Chromosome;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionStatistics;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;
import org.jenetics.stat.DoubleMomentStatistics;
import org.jenetics.stat.IntMomentStatistics;
import java.util.Scanner;

public class EvoMine {
private static final int NUMGAMES = 100;
private static final int BOARDSIZE = 15;
private static final int NUMMINES = 35;
private static final int NUMPATTERNS = 100;
private static final int FITNESSTYPE = 1;



    /*
     * Takes a square, converts it to an int.
     * int is -1 if bomb
     * int is 10 if flag
     */
    private static int squareToInt(Square sq)  {
        if(sq == null)  {
            return -1;
        }  else if(sq.flagged)  {
            return 9;
        }  else if(!sq.shown)  {
            return -1;
        }  else  {
            return sq.value;
        }
    }
    /*
     * takes a game, takes coords, returns the index and match score of the best pattern.
     * match score is the edit distance of one integer string to another
     * where one edit is classiied as +- 1 to one index
     */
    private static int getPatternMatchScore(MineSweeper game, int x, int y, int[] arrPattern, int startingIndex)  {
        int deviationSoFar = 0;
        int arrPatternIndex = startingIndex;

        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                if(i == 0 && j == 0)  {
                    continue;
                }
                int actualSquareValue = squareToInt(game.get(x, y));
                int predictedSquareValue = arrPattern[arrPatternIndex];
                deviationSoFar += Math.abs(actualSquareValue - predictedSquareValue);
                arrPatternIndex++;
            }
        }
        return deviationSoFar;

    }

    /*
     * Given a game, selects the best pattern to use for a given pair
     * returns both the best pattern index and its index.
     */
    private static int[] selectPattern(MineSweeper game, int x, int y, int[] arrPattern )  {
       
        int minPatternIndex = -1;
        int minPatternScore = 900;
        for(int i = 0; i < NUMPATTERNS; i++)  {
            int patternIndex = i * 9;
            int patternScore = getPatternMatchScore(game, x, y, arrPattern, patternIndex);
            if(patternScore < minPatternScore)  {
                minPatternIndex = patternIndex;
                minPatternScore = patternScore;
            }
        }
        int[] results = {minPatternIndex, minPatternScore};
        return results;
    }

    /*
     * Given a chromosome pattern, plays the game.
     * @return the fitness
     */
    private static MineSweeper playGame(int[] chromoPattern)  {
        MineSweeper game = new MineSweeper(BOARDSIZE, NUMMINES);
        boolean hitBomb = false;
        for(int k = 0; k < 70 && !hitBomb; k++)  {
            int bestX = -1;
            int bestY = -1;
            int minPattern = -1;
            int minPatternScore = 1000;
            for(int i = 0; i < BOARDSIZE; i++)  {
                for(int j = 0; j < BOARDSIZE; j++)  {
                   
                    if(game.get(i,j).flagged
                    || game.get(i,j).shown)  {
                        // System.out.println("skip?" + game.get(i,j));
                        continue;
                    } else  {
                        // System.out.println("noskip");
                    }
                   
                    int[] patternData = selectPattern(game, i, j, chromoPattern);
                    //System.out.println(patternData[1] + ", " + patternData[0]);    
                    //System.err.println("" + patternData[1]);
                    if(patternData[1] < minPatternScore)  {
                    //System.out.println(patternData[1] + ", " + patternData[0]);    
                        
                        minPatternScore = patternData[1];
                        minPattern = patternData[0];
                        bestX = i;
                        bestY = j;
                    }
                }
            }
            if(FITNESSTYPE != 1 && chromoPattern[minPattern + 8] >= 5)  {
                game.flag(bestX, bestY);
            } else  {
                //System.out.println("x: " + bestX + "y: " + bestY);
                hitBomb = game.peek(bestX, bestY) == -1;
            }
        }
        /*
        game.printBoard(false);
        System.out.println(bestX);
        System.out.println(bestY);
        System.out.println(minPattern + ", minPatternIndex");
        System.out.println(minPatternScore + ", minPatternScore");
        */
            return game;
    }

    /*
     * The eval function, takes a genetic thing,
     * outputs a fitness
     */
    private static Integer evalMaybePrint(Genotype<IntegerGene> gt, int numEvalGames, boolean printGames) {
        int[] chromoPattern = new int[9 * NUMPATTERNS];
        int i = 0;
        for(IntegerGene num : gt.getChromosome())  {
            chromoPattern[i] = num.intValue();
            i++;
        }

        int sumFitnesses = 0;

        for(int j = 0; j < numEvalGames; j++)  {
            MineSweeper game = playGame(chromoPattern);
            switch(FITNESSTYPE)  {
                case 0:
                    sumFitnesses += game.fitnessCalc();
                    break;
                case 1:
                    sumFitnesses += game.numRevealed();
                    break;
            }
            if(printGames)  {
                game.printBoard(false);
            }
        }

        return sumFitnesses;
    }

    private static Integer eval(Genotype<IntegerGene> gt)  {
        return evalMaybePrint(gt, NUMGAMES, false);
    }

    public static void main(String[] args) {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<IntegerGene>> gtf =
            Genotype.of(IntegerChromosome.of(-1,9, 9 * NUMPATTERNS));

        // 3.) Create the execution environment.
        Engine<IntegerGene, Integer> engine = Engine
            .builder(EvoMine::eval, gtf)
            .build();
        
        final EvolutionStatistics<Integer, DoubleMomentStatistics> statistics = EvolutionStatistics.ofNumber();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<IntegerGene> result = engine.stream()
            .limit(20)
            .peek(statistics)
            .collect(EvolutionResult.toBestGenotype());

        System.out.println(statistics);
        System.out.println("top result: " + result);

        Scanner in = new Scanner(System.in);
        boolean showThreeGames = true;
        while(showThreeGames)  {
            evalMaybePrint(result, 3, true);
            System.out.print("Show three more games?");
            showThreeGames = in.nextBoolean();
        }
    }
}
