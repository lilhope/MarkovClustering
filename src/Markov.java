import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;

public class Markov{

	static String dataFilePath = "attweb_net.txt";
	static int numberOfEntries = 228;
	static int numberOfNodes = 180;
	static int adjMatrix[][] = new int[numberOfNodes][numberOfNodes];
	static double transMatrix[][] = new double[numberOfNodes][numberOfNodes];
	static int power = 2;
	static int inflate = 2;
	
	public static void convertToGraph(String input)
	{
		int node1;
		int node2;
		StringTokenizer tk;
		tk = new StringTokenizer(input);
		node1 = Integer.parseInt(tk.nextToken());
		node2 = Integer.parseInt(tk.nextToken());
		//System.out.println(node1);
		//System.out.println(node2);
		adjMatrix[node1][node2] = 1;
		adjMatrix[node2][node1] = 1;		
		//System.out.println("Edge between: "+ node1+" and: "+node2);
	}
	public static void readData()
	{
		String input;
		FileInputStream file_in; //file input stream
        BufferedReader data_in; //data input stream//
        try
        {
        	file_in = new FileInputStream(dataFilePath);
        	data_in = new BufferedReader(new InputStreamReader(file_in));

        	for(int i=0;i<numberOfEntries;i++)
        	{
        		input = data_in.readLine();
        		//System.out.println(input);
        		convertToGraph(input);
        	}
        }
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	static void printMatrix(int[][] matrix)
	{
		for(int i=0;i<numberOfNodes;i++)
		{
			System.out.println();
			for(int j=0;j<numberOfNodes;j++)
			{
				System.out.print(matrix[i][j]+" ");
			}
		}
		System.out.println();
	}
	
	static void printMatrix(double[][] matrix)
	{
		for(int i=0;i<numberOfNodes;i++)
		{
			System.out.println();
			for(int j=0;j<numberOfNodes;j++)
			{
				System.out.print(matrix[i][j]+" ");
			}
		}
		System.out.println();
	}
	
	static void addSelfLoop()
	{
		for(int i=0;i<numberOfNodes;i++)
		{
			for(int j=0;j<numberOfNodes;j++)
			{
				adjMatrix[i][i] = 1;
			}
		}
	}

	static void constructTransitionMatrix()
	{
		double columnSum[] = new double[numberOfNodes];
		
		for(int row=0;row<numberOfNodes;row++)
		{
			for(int col=0;col<numberOfNodes;col++)
			{
				columnSum[col] += adjMatrix[row][col];
			}
		}
		
		for(int row=0; row<numberOfNodes; row++)
		{
			for(int col=0;col<numberOfNodes; col++)
			{
				transMatrix[row][col] = (double)adjMatrix[row][col]/columnSum[col];
			} 
		}
	}
	
	static void mcl() throws IOException
	{
		//Markov Cluster Algorithm
		int iteration = 1;
		System.out.println("Iteration "+iteration);
		transMatrix = expand();
		inflate();
		iteration++;
		while(!checkConvergence()){
			System.out.println("Iteration "+iteration);
			transMatrix = expand();
			inflate();
			iteration++;
		}
		//System.out.println("Convergence Reached. The Matrix is: ");
		//printMatrix(transMatrix);
		System.out.println("The number of clusters are: "+findClusters());
	}
	
	static boolean checkConvergence(){
		double prev = -1;
		for(int j = 0;j<numberOfNodes;j++){
			for(int i = 0;i<numberOfNodes;i++){
				if(transMatrix[i][j]!=0){
					prev = transMatrix[i][j];
					break;
				}
			}
			for(int i = 0;i<numberOfNodes;i++){
				if(transMatrix[i][j]!=0){
					if(transMatrix[i][j]!=prev)
						return false;
				}
			}
		}
		return true;
	}
	static double[][] expand(){
		double[][] matrix = new double[numberOfNodes][numberOfNodes];
		int  p = power;
		while(p>1){
			for(int i = 0;i<numberOfNodes;i++){
				for(int j = 0;j<numberOfNodes;j++){
					//int sum = 0;
					for(int k = 0;k<numberOfNodes;k++){
						matrix[i][j] += (transMatrix[i][k]*transMatrix[k][j]); 
					}
					//transMatrix[i][j] = sum;
				}
			}
			p--;
		}
		return matrix;
	}
	static void inflate(){
		double[] sum = new double[numberOfNodes]; 
		for(int j = 0;j<numberOfNodes;j++){
			for(int i = 0;i<numberOfNodes;i++){
				sum[j]+=Math.pow(transMatrix[i][j], inflate);
			}
		}
		for(int j = 0;j<numberOfNodes;j++){
			for(int i = 0;i<numberOfNodes;i++){
				transMatrix[i][j] = Math.pow(transMatrix[i][j],inflate)/sum[j];
			}
		}
	}
	
	static int findClusters() throws IOException{
		File f = new File("attoutput.txt");
		if(!f.exists())
			f.createNewFile();
		FileOutputStream fs = new FileOutputStream(f);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(fs));
		int count = 0;
		for(int i = 0;i<numberOfNodes;i++){
			for(int j = 0;j<numberOfNodes;j++){
				if(transMatrix[i][j]!=0){
					count++;
					break;
				}
			}
			for(int j = 0;j<numberOfNodes;j++){
				if(transMatrix[i][j]!=0){
					out.write(Integer.toString(j)+"\t"+Integer.toString(count)+"\n");
				}
			}
		}
		out.close();
		return count;
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		readData();
		addSelfLoop();
		//printMatrix(adjMatrix);
		//System.out.println("Transition Matrix:");
		constructTransitionMatrix();
		//printMatrix(transMatrix);
		mcl();
		//printMatrix(transMatrix);
	}

}