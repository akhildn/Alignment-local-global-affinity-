import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

class MaxIndex {
	int xIndex;
	int yIndex;
	public MaxIndex(int x, int y) {
		this.xIndex = x;
		this.yIndex = y;
	}
}

public class Sample {

	public static void main(String args[]) throws IOException {
		
		/*double matchScore = 1;
		double misMatchScore = -1;
		double indelScore = -1;
		double gapInit = -3;
		double gapExt = -0.5;*/
		
		String filename = args[0];
		String algorithm = args[1];
		double matchScore = Double.parseDouble(args[2]);
		double misMatchScore = Double.parseDouble(args[3]);
		double indelScore = Double.parseDouble(args[4]);
		double gapInit = Double.parseDouble(args[5]);
		double gapExt = Double.parseDouble(args[6]);
		
		//System.out.println(filename);
		String horizontal = ""; // vertical
		String vertical = ""; // horizontal
		
		BufferedReader br = null;
		int linenum = 0;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line;

			while ((line = br.readLine()) != null) {
				linenum++;
				if(linenum == 1)
					horizontal = line;
				else if(linenum == 2)
					vertical = line;
				else
					break;
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		char[] vArray = horizontal.toCharArray();
		char[] wArray = vertical.toCharArray();
		
		int vLength = horizontal.length() + 1 ;
		int wLength = vertical.length() + 1;
		
		double[][] scoreMatrix = new double[vLength][wLength];
		char[][] backMatrix = new char[vLength][wLength];
		ArrayList<MaxIndex> maxIndices;
		double maxScore;
		if(algorithm.equalsIgnoreCase("local")){
			System.out.println("-------LOCAL ALIGNMENT--------------");
			maxScore = localAlignment(scoreMatrix, backMatrix, vArray, wArray
									, matchScore, misMatchScore, indelScore);		
			printScoreMatrix(scoreMatrix);		
			//printBackTrackmatrix(backMatrix);		
			maxIndices = findMaxIndices(scoreMatrix, maxScore);		
			localAlignmentBackTrack(vArray, wArray, scoreMatrix, backMatrix, maxScore, maxIndices);
		} else if(algorithm.equalsIgnoreCase("global")) {
			System.out.println("-------GLOBAL ALIGNMENT--------------");
			maxScore = globalAlignment(scoreMatrix, backMatrix, vArray, wArray
					, matchScore, misMatchScore, indelScore);
			printScoreMatrix(scoreMatrix);		
			//printBackTrackmatrix(backMatrix);		
			maxIndices = findMaxIndices(scoreMatrix, maxScore);
			globalAlignmentBackTrack(vArray, wArray, scoreMatrix, backMatrix, maxScore, maxIndices);
			
		} else if(algorithm.equalsIgnoreCase("affine")) {
			System.out.println("-------AFFINE ALIGNMENT--------------");
		    maxScore = affineAlignment(scoreMatrix, backMatrix, vArray, wArray
					, matchScore, misMatchScore, gapInit, gapExt);
					
			printScoreMatrix(scoreMatrix);		
			//printBackTrackmatrix(backMatrix);		
			maxIndices = findMaxIndices(scoreMatrix, maxScore);
			globalAlignmentBackTrack(vArray, wArray, scoreMatrix, backMatrix, maxScore, maxIndices);
			
		} else {
			System.out.println("Not valid Algorithm");
		}
		
		
	}

	private static ArrayList<MaxIndex> findMaxIndices(double[][] scoreMatrix, double maxScore) {
		ArrayList<MaxIndex> maxIndices = new ArrayList<>();
		for(int i=0; i < scoreMatrix.length; i++) {
			for(int j =0; j < scoreMatrix[i].length; j++) {
				if(maxScore == scoreMatrix[i][j]) {
					maxIndices.add(new MaxIndex(i, j));
				}
			}
		}
		return maxIndices;
	}

	private static void printBackTrackmatrix(char[][] backMatrix) {
		System.out.println("-------back track-------");
		for(int i = 0; i < backMatrix.length; i++) {
			for(int j = 0; j < backMatrix[i].length; j++) {
				System.out.print(backMatrix[i][j] + "\t");
			}
			System.out.println("");
		}
		System.out.println("--------------");
	}

	private static void printScoreMatrix(double[][] scoreMatrix) {
		System.out.println("--------------");
		for(int i = 0; i < scoreMatrix.length; i++) {
			for(int j = 0; j < scoreMatrix[i].length; j++) {
				System.out.print(scoreMatrix[i][j] + "\t");
			}
			System.out.println("");
		}
		System.out.println("--------------");
	}

	private static void localAlignmentBackTrack(char[] vArray, char[] wArray, double[][] scoreMatrix,
			char[][] backMatrix, double maxScore, ArrayList<MaxIndex> maxIndices) {
		int sequenceCount = 0;
		System.out.println("Max Score:" + maxScore);
		for(MaxIndex maxIndex : maxIndices) {
			StringBuffer horizontalSeq = new StringBuffer();
			StringBuffer verticalSeq = new StringBuffer();
			
			int i = maxIndex.xIndex;
			int j = maxIndex.yIndex;
			
			while(scoreMatrix[i][j] > 0) {
				
				
				if(backMatrix[i][j] == 'D') {
					horizontalSeq.append(vArray[i - 1]);
					verticalSeq.append(wArray[j - 1]);
					i = i - 1;
					j = j - 1;
					
				}
				
				if(backMatrix[i][j] == 'B') {	
					horizontalSeq.append('-');
					verticalSeq.append(wArray[j - 1]);
					j = j - 1;
					
				}
				
				if(backMatrix[i][j] == 'S') {
					horizontalSeq.append(vArray[i - 1]);
					verticalSeq.append('-');
					i = i - 1;					
				}
				
				if(i < 0 || j < 0)
					break;
				
			}
			
			/*if(i == 0)
				horizontalSeq.append("-");
			else if (i > 0)
				horizontalSeq.append(vArray[i - 1]);
			
			if(j == 0)
				verticalSeq.append("-");
			else if (j > 0)
			verticalSeq.append(wArray[j - 1]);*/
			
			System.out.println("Sequence-" + sequenceCount);
			System.out.println(horizontalSeq.reverse());
			System.out.println(verticalSeq.reverse());
			sequenceCount++;
		
		}
	}
	
	public static double localAlignment(double[][] scoreMatrix
			, char[][] backMatrix
			, char[] vArray
			, char[] wArray
			, double matchScore, double misMatchScore
			, double indelScore) {
		
		
		double maxScore = Integer.MIN_VALUE;
		
		/*
		 * Initialize the score matrix with all zeros
		 */
		for(int vIndex = 0; vIndex < scoreMatrix.length; vIndex++) {
			for(int wIndex = 0; wIndex < scoreMatrix[vIndex].length; wIndex++) {
				scoreMatrix[vIndex][wIndex] = 0;
			}
		}
		
		/*
		 * Initialize the backtrack matrix to blank
		 */
		for(int vIndex = 0; vIndex < backMatrix.length; vIndex++) {
			for(int wIndex = 0; wIndex < backMatrix[vIndex].length; wIndex++) {
				backMatrix[vIndex][wIndex] = ' ';
			}
		}
		
		
		/*
		 * Start scoring the matrix for V against W
		 */
		for(int vIndex = 1; vIndex < scoreMatrix.length; vIndex++) {
			for(int wIndex = 1; wIndex < scoreMatrix[vIndex].length; wIndex++) {
				/*
				 * Get current values.
				 */
				double sideValue = scoreMatrix[vIndex-1][wIndex];
				double bottomValue = scoreMatrix[vIndex][wIndex-1];
				double diagValue = scoreMatrix[vIndex-1][wIndex-1];
				
				boolean isMatch = vArray[vIndex - 1] == wArray[wIndex - 1];
				diagValue = diagValue + ( isMatch ? matchScore : misMatchScore);
				sideValue = sideValue + indelScore;
				bottomValue = bottomValue + indelScore;
				
				/*
				 * Convert negative values to zero
				 */
				
				diagValue = Math.max(0, diagValue);
				sideValue = Math.max(0, sideValue);
				bottomValue = Math.max(0, bottomValue);
				
				
				if(bottomValue >= Math.max(sideValue, diagValue)) {
					backMatrix[vIndex][wIndex] = 'B';
					scoreMatrix[vIndex][wIndex] = bottomValue;
				}
				if(sideValue >= Math.max(bottomValue, diagValue)) {
					backMatrix[vIndex][wIndex] = 'S';
					scoreMatrix[vIndex][wIndex] = sideValue;
				}
				if(diagValue >= Math.max(sideValue, bottomValue)) {
					
					backMatrix[vIndex][wIndex] = 'D';					
					scoreMatrix[vIndex][wIndex] = diagValue;
				}
				
				maxScore = Math.max(maxScore, scoreMatrix[vIndex][wIndex]);
			}
		}
		
		
		return maxScore;
	}
	
	public static double globalAlignment(double[][] scoreMatrix
			, char[][] backMatrix
			, char[] vArray
			, char[] wArray
			, double matchScore, double misMatchScore
			, double gap) {
		
		
		double maxScore = Integer.MIN_VALUE;
		
		/*
		 * Initialize the score matrix with gap values
		 */
		
		for(int vIndex = 0; vIndex < scoreMatrix.length; vIndex++) {
			scoreMatrix[vIndex][0] = gap * vIndex;
		}
		
		for(int wIndex = 0; wIndex < scoreMatrix[0].length; wIndex++) {
			scoreMatrix[0][wIndex] = gap * wIndex;
		}
		
		/*
		 * Initialize the backtrack matrix to blank
		 */
		for(int vIndex = 0; vIndex < backMatrix.length; vIndex++) {
			for(int wIndex = 0; wIndex < backMatrix[vIndex].length; wIndex++) {
				backMatrix[vIndex][wIndex] = ' ';
			}
		}
		
		
		/*
		 * Start scoring the matrix for V against W
		 */
		for(int vIndex = 1; vIndex < scoreMatrix.length; vIndex++) {
			for(int wIndex = 1; wIndex < scoreMatrix[vIndex].length; wIndex++) {
				/*
				 * Get current values.
				 */
				double topValue = scoreMatrix[vIndex-1][wIndex];
				double sideValue = scoreMatrix[vIndex][wIndex-1];
				double diagValue = scoreMatrix[vIndex-1][wIndex-1];
				
				boolean isMatch = vArray[vIndex - 1] == wArray[wIndex - 1];
				diagValue = diagValue + ( isMatch ? matchScore : misMatchScore);
				topValue = topValue + gap;
				sideValue = sideValue + gap;
				
				
				if(sideValue >= Math.max(topValue, diagValue)) {
					backMatrix[vIndex][wIndex] = 'S';
					scoreMatrix[vIndex][wIndex] = sideValue;
				}
				if(topValue >= Math.max(sideValue, diagValue)) {
					backMatrix[vIndex][wIndex] = 'B';
					scoreMatrix[vIndex][wIndex] = topValue;
				}
				if(diagValue >= Math.max(topValue, sideValue)) {
					
					backMatrix[vIndex][wIndex] = 'D';					
					scoreMatrix[vIndex][wIndex] = diagValue;
				}
				
				maxScore = Math.max(maxScore, scoreMatrix[vIndex][wIndex]);
			}
		}
		
		
		return maxScore;
	}

	public static double affineAlignment(double[][] scoreMatrix
			, char[][] backMatrix
			, char[] vArray
			, char[] wArray
			, double matchScore, double misMatchScore
			, double gapInit
			, double gapExt ) {
		
		
		double maxScore = Integer.MIN_VALUE;
		int gapCount = 0;
		/*
		 * Initialize the score matrix with gap values
		 */
		scoreMatrix[0][0] = 0;
		for(int vIndex = 1; vIndex < scoreMatrix.length; vIndex++) {
			scoreMatrix[vIndex][0] = gapInit + (gapExt * gapCount);
			gapCount++;
		}
		
		gapCount = 0;
		for(int wIndex = 1; wIndex < scoreMatrix[0].length; wIndex++) {
			scoreMatrix[0][wIndex] = gapInit + (gapExt * gapCount);
			gapCount++;
		}
		
		/*
		 * Initialize the backtrack matrix to blank
		 */
		for(int vIndex = 0; vIndex < backMatrix.length; vIndex++) {
			for(int wIndex = 0; wIndex < backMatrix[vIndex].length; wIndex++) {
				backMatrix[vIndex][wIndex] = ' ';
			}
		}
		
		gapCount = 0;
		/*
		 * Start scoring the matrix for V against W
		 */
		for(int vIndex = 1; vIndex < scoreMatrix.length; vIndex++) {
			for(int wIndex = 1; wIndex < scoreMatrix[vIndex].length; wIndex++) {
				/*
				 * Get current values.
				 */
				double topValue = scoreMatrix[vIndex-1][wIndex];
				double sideValue = scoreMatrix[vIndex][wIndex-1];
				double diagValue = scoreMatrix[vIndex-1][wIndex-1];
				
				boolean isMatch = vArray[vIndex - 1] == wArray[wIndex - 1];
				diagValue = diagValue + ( isMatch ? matchScore : misMatchScore);
				if(gapCount == 0){
					topValue = topValue + gapInit;
					sideValue = sideValue + gapInit;
				} else {
					topValue = topValue  + (gapCount * gapExt);
					sideValue = sideValue  + (gapCount * gapExt);
				}
				
				
				if(sideValue >= Math.max(topValue, diagValue)) {
					backMatrix[vIndex][wIndex] = 'S';
					scoreMatrix[vIndex][wIndex] = sideValue;
				}
				if(topValue >= Math.max(sideValue, diagValue)) {
					backMatrix[vIndex][wIndex] = 'B';
					scoreMatrix[vIndex][wIndex] = topValue;
				}
				if(diagValue >= Math.max(topValue, sideValue)) {
					
					backMatrix[vIndex][wIndex] = 'D';					
					scoreMatrix[vIndex][wIndex] = diagValue;
				}
				
				if(backMatrix[vIndex][wIndex] != 'D')
					gapCount++;
				else
					gapCount = 0;
				
				maxScore = Math.max(maxScore, scoreMatrix[vIndex][wIndex]);
			}
		}
		
		
		return maxScore;
	}

	
	private static void globalAlignmentBackTrack(char[] vArray, char[] wArray, double[][] scoreMatrix,
			char[][] backMatrix, double maxScore, ArrayList<MaxIndex> maxIndices) {
		int sequenceCount = 1;
		System.out.println("Max Score:" + maxScore);
		for(MaxIndex maxIndex : maxIndices) {
			StringBuffer horizontalSeq = new StringBuffer();
			StringBuffer verticalSeq = new StringBuffer();
			
			int i = maxIndex.xIndex;
			int j = maxIndex.yIndex;
			
			while(i >= 0 && j >= 0) {
				
				
				if(backMatrix[i][j] == 'D') {
					horizontalSeq.append(vArray[i - 1]);
					verticalSeq.append(wArray[j - 1]);
					i = i - 1;
					j = j - 1;
					
				}
				
				if(backMatrix[i][j] == 'B') {	
					horizontalSeq.append('-');
					verticalSeq.append(wArray[j - 1]);
					j = j - 1;
					
				}
				
				if(backMatrix[i][j] == 'S') {
					horizontalSeq.append(vArray[i - 1]);
					verticalSeq.append('-');
					i = i - 1;					
				}
				
				if(backMatrix[i][j] == ' ') {
					if(i-1 >= 0)
						horizontalSeq.append(vArray[i - 1]);
					else
						horizontalSeq.append('-');
					
					if(j-1 >= 0)
						verticalSeq.append(wArray[j - 1]);
					else
						verticalSeq.append('-');
					
					break;
				}
				
				if(i < 0 || j < 0 )
					break;
				
			}
			
			/*if(i == 0)
				horizontalSeq.append("-");
			else if (i > 0)
				horizontalSeq.append(vArray[i - 1]);
			
			if(j == 0)
				verticalSeq.append("-");
			else if (j > 0)
			verticalSeq.append(wArray[j - 1]);*/
			
			System.out.println("Sequence " + sequenceCount);
			System.out.println(horizontalSeq.reverse());
			System.out.println(verticalSeq.reverse());
			sequenceCount++;
		}
	}

}


