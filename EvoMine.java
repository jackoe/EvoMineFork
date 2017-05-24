import org.jenetics.BitChromosome;
import org.jenetics.IntegerChromosome;
import org.jenetics.BitGene;
import org.jenetics.IntegerGene;
import org.jenetics.Genotype;
import org.jenetics.Chromosome;
import org.jenetics.engine.Engine;
import org.jenetics.engine.EvolutionResult;
import org.jenetics.util.Factory;

public class EvoMine {
    
    private static int squareToInt(Square sq)  {
        if(sq == null || !sq.shown)  {
            return -1;
        }  else if(sq.flagged)  {
            return 10;
        }  else  {
            return sq.value;
        }
    }
    
    private static int getPatternMatchScore(MineSweeper game, int x, int y, int[] arrPattern, int startingIndex)  {
        int deviationSoFar = 0;
        int arrPatternIndex = startingIndex - 1;

        for(int i = -1; i < 2; i++)  {
            for(int j = -1; j < 2; j++)  {
                arrPatternIndex++;
                if(i == 0 && j == 0)  {
                    continue;
                }
                int actualSquareValue = squareToInt(game.get(x, y));
                int predictedSquareValue = arrPattern[arrPatternIndex];
                deviationSoFar += Math.abs(actualSquareValue - predictedSquareValue);
            }
        }
        return deviationSoFar;

    }

    private static int[] selectPattern(MineSweeper game, int x, int y, int[] arrPattern )  {
        int numPatterns = arrPattern.length / 9;
        int maxPatternIndex = -1;
        int maxPatternScore = -900;
        for(int i = 0; i < numPatterns; i++)  {
            int patternIndex = i * 9;
            int patternScore = getPatternMatchScore(game, x, y, arrPattern, patternIndex);
            if(patternScore > maxPatternScore)  {
                maxPatternIndex = patternIndex;
                maxPatternScore = patternScore;
            }
        }
        int[] results = {maxPatternIndex, maxPatternScore};
        return results;
    }

    private static int playGame(int[] chromoPattern)  {
        MineSweeper game = new MineSweeper(8, 10);
        boolean hitBomb = false;
        int bestX = -1;
        int bestY = -1;
        int bestPattern = -1;
        int bestPatternScore = 1000;
        for(int k = 0; k < 70 && !hitBomb; k++)  {
            for(int i = 0; i < 8; i++)  {
                for(int j = 0; j < 8; j++)  {
                    int[] patternData = selectPattern(game, i, j, chromoPattern);
                    //System.out.println(patternData[1] + ", " + patternData[0]);
                    if(patternData[1] > bestPatternScore)  {
                        bestPatternScore = patternData[1];
                        bestPattern = patternData[0];
                    }
                }
            }
            if(chromoPattern[bestPattern + 8] >= 5)  {
                game.flag(bestX, bestY);
            } else  {
                hitBomb = game.peek(bestX, bestY) == -1;
            }
        }
        //System.out.println(bestX);
        //System.out.println(bestY);

        return game.fitnessCalc();
    }

    // 2.) Definition of the fitness function.
    private static Integer eval(Genotype<IntegerGene> gt) {
        int[] chromoPattern = new int[90];
        int i = 0;
        for(IntegerGene num : gt.getChromosome())  {
            chromoPattern[i] = num.intValue();
            i++;
        }

        int numGames = 10;
        int sumFitnesses = 0;

        for(int j = 0; j < numGames; j++)  {
            sumFitnesses += playGame(chromoPattern);
        }

        return sumFitnesses;
    }

    public static void main(String[] args) {
        // 1.) Define the genotype (factory) suitable
        //     for the problem.
        Factory<Genotype<IntegerGene>> gtf =
            Genotype.of(IntegerChromosome.of(-1,10, 9 * 10));

        // 3.) Create the execution environment.
        Engine<IntegerGene, Integer> engine = Engine
            .builder(EvoMine::eval, gtf)
            .build();

        // 4.) Start the execution (evolution) and
        //     collect the result.
        Genotype<IntegerGene> result = engine.stream()
            .limit(50)
            .collect(EvolutionResult.toBestGenotype());

        System.out.println("Hello World:\n" + result);
    }
}
