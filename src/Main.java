import java.io.*;
import java.util.Scanner;

class Point
{
    double x;
    double y;
}

class Chromosome
{
    double[] coefficients;
    double fitness = 0.0;

    Chromosome(int deg)
    {
        coefficients = new double[deg+1];
    }
}

public class Main {

    static int datasets;
    static Point[] points;
    static int degree;
    static int n;
    static int population_size = 60;
    static int iterations = 5;
    static int elite_individuals = 2;
    static Chromosome[] population = new Chromosome[population_size];
    static Chromosome[] parents = new Chromosome[population_size - elite_individuals];
    static Chromosome[] offsprings = new Chromosome[population_size - elite_individuals];
    static int generation = 0;
    static int upper = 10;
    static int lower = -10;
    static double Pc = 0.6;
    static double Pm = 0.1;
    static int b = 1;
    static Chromosome[] elites = new Chromosome[elite_individuals];


    public static void Initialization()
    {
        for (int i = 0; i < population_size; i++)
        {
            population[i] = new Chromosome(degree);
            if (i >= elite_individuals)
            {
                parents[i - elite_individuals] = new Chromosome(degree);
                offsprings[i - elite_individuals] = new Chromosome(degree);
            }

            for (int j = 0; j <= degree; j++)
            {
                population[i].coefficients[j] = (20 * Math.random()) - 10;
                population[i].coefficients[j] = Math.round(100 * population[i].coefficients[j]) / 100.0;
            }
        }

        for (int i = 0; i < elite_individuals; i++)
        {
            elites[i] = new Chromosome(degree);
        }
    }

    public static void Sort(Chromosome[] c) {

        Chromosome temp = new Chromosome(degree);

        for (int i = 0; i < population_size - 1; i++) {

            int min_idx = i;
            for (int j = i + 1; j < population_size; j++)
                if (c[j].fitness < c[min_idx].fitness)
                    min_idx = j;


            temp.coefficients = c[min_idx].coefficients.clone();
            temp.fitness = c[min_idx].fitness;

            c[min_idx].coefficients = c[i].coefficients.clone();
            c[min_idx].fitness = c[i].fitness;

            c[i].coefficients = temp.coefficients.clone();
            c[i].fitness = temp.fitness;
        }
    }

    public static void Fitness()
    {
        double ycalc = 0.0;
        double total = 0.0;

        for (int i = 0; i < population_size; i++)
        {
            for (int j = 0; j < n; j++)
            {
                for (int k = 0; k <= degree; k++)
                {
                    ycalc += population[i].coefficients[k] * (Math.pow(points[j].x,k));
                }

                total += Math.pow((ycalc - points[j].y),2);
                ycalc = 0.0;
                total = Math.round(100 * total) / 100.0;
            }
            population[i].fitness = total / n;
            total = 0;
        }

        Sort(population);
    }

    public static void Selection()
    {
        int selector;
        int selector2;

        for (int i = 0; i < population_size - elite_individuals; i++)
        {
            selector = ((int) (Math.random() * (population_size - elite_individuals))) + elite_individuals;
            selector2 = ((int) (Math.random() * (population_size - elite_individuals))) + elite_individuals;

            if (population[selector].fitness < population[selector2].fitness)
            {
                parents[i].coefficients = population[selector].coefficients.clone();
            }

            else
            {
                parents[i].coefficients = population[selector2].coefficients.clone();
            }
        }
    }

    public static void Crossover()
    {
        int point = 0;
       int point2 = 0;
       int temp = 0;

       for (int i = 0; i < population_size - elite_individuals; i+=2)
       {
           double decision = Math.random();

           if (decision <= Pc)
           {
               point = (int) (Math.random() * (degree+1));
               point2 = (int) (Math.random() * (degree+1));

               if (point == 0)
                   point = 1;
               if (point2 == 0)
                   point2 = 1;

               while (point == point2)
               {
                   point2 = (int) (Math.random() * (degree+1));
                   if (point2 == 0)
                       point2 = 1;
               }

               if (point2 < point)
               {
                   temp = point;
                   point = point2;
                   point2 = temp;
               }

               System.arraycopy(parents[i].coefficients,0,offsprings[i].coefficients,0,point);
               System.arraycopy(parents[i+1].coefficients,0,offsprings[i+1].coefficients,0,point);

               System.arraycopy(parents[i+1].coefficients,point,offsprings[i].coefficients,point, point2 - point);
               System.arraycopy(parents[i].coefficients,point,offsprings[i+1].coefficients,point, point2 - point);

               System.arraycopy(parents[i].coefficients,point2,offsprings[i].coefficients,point2,degree + 1 - point2);
               System.arraycopy(parents[i+1].coefficients,point2,offsprings[i+1].coefficients,point2,degree + 1 - point2);

           }

           else
           {
               offsprings[i].coefficients = parents[i].coefficients.clone();
               offsprings[i+1].coefficients = parents[i+1].coefficients.clone();
           }
       }
    }

    public static void Mutation()
    {
        for (int i = 0; i < population_size - elite_individuals; i++)
        {
            for (int j = 0; j <= degree; j++)
            {
                double decision = Math.random();
                if (decision <= Pm)
                {
                    double Lx = offsprings[i].coefficients[j] - lower;
                    double Ux = upper - offsprings[i].coefficients[j];
                    double y;
                    double delta;

                    double decision2 = Math.random();

                    if (decision2 <= 0.5)
                        y = Lx;
                    else
                        y = Ux;

                    double r = Math.random();
                    delta = y * (1 - Math.pow(r,1 - Math.pow(((double)generation/(double)iterations),b)));
                    delta = Math.round(100 * delta) / 100.0;

                    if (y == Lx)
                        offsprings[i].coefficients[j] -= delta;
                    else
                        offsprings[i].coefficients[j] += delta;
                    offsprings[i].coefficients[j] = Math.round(100 * offsprings[i].coefficients[j]) / 100.0;
                }
            }
        }
    }

    public static void Replacement()
    {
        for (int i = elite_individuals; i < population_size; i++)
        {
            population[i].coefficients = offsprings[i - elite_individuals].coefficients.clone();
        }

        for (int i = 0; i < population_size; i++)
        {
            population[i].fitness = 0;
        }
    }

    public static void Output(int i) throws IOException {
        FileWriter fw = new FileWriter("output.txt",true);

        fw.write("In test case #" + (i+1) +"\n");
        for (int j = 0; j <= degree; j++)
        {
            fw.write("a" + j + " = " + population[0].coefficients[j] + "\n");
        }

        fw.write("MSE = " + population[0].fitness + "\n");

        fw.close();

    }

    public static void main(String[] args) throws IOException {

        FileWriter fw = new FileWriter("output.txt",false);

        fw.write("");

        fw.close();

        File file = new File("input.txt");
        Scanner input = new Scanner(file);
        datasets = input.nextInt();

        for (int i = 0; i < datasets; i++)
        {
            generation = 0;
            n = input.nextInt();
            degree = input.nextInt();
            points = new Point[n];

            for (int j = 0; j < n; j++)
            {
                points[j] = new Point();
                points[j].x = input.nextDouble();
                points[j].y = input.nextDouble();
            }

            Initialization();

            for (int j = 0; j < iterations; j++)
            {
                generation++;
                Fitness();
                Selection();
                Crossover();
                Mutation();
                Replacement();
            }
            Fitness();
            Sort(population);
            Output(i);
            generation = 0;
        }
    }
}
