import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
/**
 * @author Trey Starshak
 * The HuffmanCompressor Class represents an object that encrypts a file based on an input file
 */
public class HuffmanCompressor {
	
	/**
	 * The HuffmanNode class represents nodes in a HuffmanTree
	 */
	private static class HuffmanNode {
		
		//HuffmanNode fields
		private Character inChar;
		private double frequency; 
		private HuffmanNode left;
		private HuffmanNode right;
		
		//HuffmanNode constructor
		public HuffmanNode(Character inChar, double frequency, HuffmanNode left, HuffmanNode right) {
			this.left = left;
			this.right = right;
			this.frequency = frequency;
			this.inChar = inChar;
		}
	}
	
	//HuffmanCompressor Fields
	private HuffmanNode root;
	//I used ArrayList for the random access and O(1) and O(n) run times associated with ArrayList methods
	private ArrayList<HuffmanNode> list;
	private ArrayList<HuffmanNode> freqList;
	private ArrayList<String> binList;
	
	//HuffmanCompressor constructor
	public HuffmanCompressor() {
		root = new HuffmanNode(null, 0, null, null);
		list = new ArrayList<HuffmanNode>(1);
		freqList = new ArrayList<HuffmanNode>(1);
		binList = new ArrayList<String>(1);
	}
	
	//HuffmanCompressor Getter/setter methods
	public HuffmanNode getRoot() {
		return this.root;
	}
	
	public void setRoot(HuffmanNode root) {
		this.root = root;
	}
	
	public ArrayList<HuffmanNode> getList() {
		return this.list;
	}
	
	public ArrayList<HuffmanNode> getFreqList() {
		return this.freqList;
	}
	
	public ArrayList<String> getBinList() {
		return this.binList;
	}
	
	
	/**
	 * HuffmanCompressor main method
	 * @param args the args passed to the main method
	 */
	public static void main(String[] args) {
		try {
			huffmanEncoder(args[0], args[1], args[2]);
		} catch (NullPointerException e) {
			if(args.length < 2) 
				System.out.println("More Input Expected");
			else
				huffmanEncoder(args[0], args[1], "Output");
		}
	}
	
	/**
	 * Makes calls to scan a file, make a tree, and encode the result. 
	 * Prints and returns the outcome of the operation
	 * @param inputFileName the name of the file to compress
	 * @param encodingFileName the name of the file used for encoding
	 * @param outputFileName the name of the file that stores the result
	 * @return the outcome of the solution
	 */
	public static String huffmanEncoder(String inputFileName, String encodingFileName, String outputFileName) {
		HuffmanCompressor encodingCompressor = new HuffmanCompressor();
		try {
			encodingCompressor.scanFile(encodingFileName);
			encodingCompressor.createTree();
			encodingCompressor.encodeToFile(inputFileName, outputFileName);
		} catch (IOException e) {
			System.out.println("Error: IOException. Check file names/paths");
			return "Input File Error: IOException";
		} catch (NullPointerException e2) {
			System.out.println("Error: NullPointerException");
			return "Input File Error: Null Pointer";
		}
		System.out.println("File Encoded");
		return "File Encoded";
	}
	
	/**
	 * Scans a file and computes how often characters appear
	 * @param filename the name of the file to generate frequencies for
	 * @throws IOException
	 */
	public void scanFile(String filename) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String string = reader.readLine();
		while(string != null) {
			//Reads each character of a line of the file
			for(int i = 0; i < string.length(); i++) {
				if(string.charAt(i) >= ' ' && string.charAt(i) < '~') {
					HuffmanNode node = searchChar(string.charAt(i));
					//If character is stored with by a node, increments frequency. Otherwise, creates a new node
					if(node != null) 
						node.frequency++;
					else 
						getList().add(new HuffmanNode(string.charAt(i), 1, null, null));
				}
			}
			string = reader.readLine();
		}
		reader.close();
	}
	
	/**
	 * Finds the huffmanNode from a list with given character
	 * @param c the character to search for
	 * @return the huffmannode with specified character, if it exists. Otherwise returns null
	 */
	public HuffmanNode searchChar(Character c) {
		if(getList() != null) 
			for(HuffmanNode i: getList()) {
				if(i.inChar == c) 
					return i;
			}
		return null;
	}
	
	/**
	 * Constructs a Huffman Tree from a list of nodes
	 * @return the huffmanNode that is the root of the tree;
	 */
	public HuffmanNode createTree() {
		HuffmanNode node = null;
		//MergeNodes removes the minimum node as it is found.  When the last node has been used, the list is empty
		while(getList().size() > 0) {
			node = mergeNodes();
		}
		setRoot(node);
		//ASCII values ' ' to '~'
		for(int i = 32; i < 127; i++)
			traverseNodes(node,(char)i, "");
		return node;
	}
	
	/**
	 * Creates a new HuffmanNode from the nodes with minimum frequencies
	 * @return the new HuffmanNode created
	 */
	public HuffmanNode mergeNodes() {
		HuffmanNode nodeL = popMin();
		HuffmanNode nodeR = popMin();
		//If only one of the nodes exists, it is the root of the tree
		if(nodeL == null || nodeR == null) {
			if(nodeL == null) {
				return nodeR;
			}
			else {
				return nodeL;
			}
		}
		else {
			HuffmanNode node = new HuffmanNode(null, nodeL.frequency + nodeR.frequency, nodeL, nodeR);
			getList().add(node);
			return node;
		}	
	}
	

	
	/**
	 * Finds and removes the HuffmanNode with minimum frequency in a list, if it exists
	 * @return HuffmanNode the minimum node
	 */
	public HuffmanNode popMin() {
		if(getList().size() > 0) {
			HuffmanNode min = getList().get(0);
			int index = 0;
			int minIndex = 0;
			for(HuffmanNode i: getList()) {
				if(i.frequency <= min.frequency) {
					min = i;
					minIndex = index;
				}
				index++;
			} 
			getList().remove(minIndex);
			return min;
		}
		else 
			return null;
	}
	
	/**
	 * searches for node with given inChar.  Also computes the 1 and 0 representation of the node and Prints the node, character, and representation
	 * Also stores these values in the arrayLists
	 * Traverse via in-order traversal
	 * @param node the node to search for
	 * @param c the character to search for
	 * @param binary the string of 1's and 0's representing this node's encoding
	 * @return binary the representation of 1's and 0's encoding this node
	 */
	public String traverseNodes(HuffmanNode node, Character c, String binary) {
		if(node != null) {
			//Recursively searches the left subchild and appends 0 to its binary representation
			traverseNodes(node.left, c, binary + "0");
			if(node.inChar == c) {
				System.out.println(node.inChar + ": " + node.frequency + ": " + binary);
				getFreqList().add(node);
				getBinList().add(binary);
				return binary;
			}
			//Recursively searches the right subchild and appendss 1 to its binary representation 
			traverseNodes(node.right, c, binary + "1");
			return binary + "";
		}
		else
			return binary;
	}
	
	/**
	 * Returns the index of node with given c from arrayList freqList
	 * @param c tbe cbaracter to search
	 * @return the index of the character
	 */
	public Integer parse(Character c) {
		int index = 0;;
		for(HuffmanNode i: getFreqList()) {
			if(c == i.inChar) 
				return index;
			index++;
		}
		return null;
	}
	
	/**
	 * Opens a file if it exists, otherwise creates the file. For each character in the file, writes it's bit representation to the output file
	 * Computes and prints the file size of the original and final files and their compression ratio 
	 * @param input the input file to read
	 * @param output the output file to write to
	 * @throws IOException
	 */
	public void encodeToFile(String input, String output) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(input));
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		String string = reader.readLine();
		//The size in bits of the original file
		double originalSize = 0;
		//The size in bits of the final file
		double finalSize = 0;
		//reads and writes each line
		while(string != null) {
			//Reads and writes for each character in a line
			for(int i = 0; i < string.length(); i++) {
				Integer temp = parse(string.charAt(i));
				//If the character has an encoding value, writes it's value to the output and sums the length of its value
				if(temp != null) {
					writer.write(getBinList().get(temp));
					finalSize += getBinList().get(temp).length();
				}
				else
					writer.append(string.charAt(i));
				//I assume a character is represented by 8 bits
				originalSize += 8;
			}
			writer.flush();
			string = reader.readLine();
			writer.newLine();
		}
		//Prints out encryption data and closes readers and writers
		System.out.println("Original File Size: "+ originalSize + " bits.");
		System.out.println("Final File Size: " + finalSize + " bits.");
		System.out.println("Difference: " + (originalSize - finalSize) + " bits.");
		System.out.println("Compression Ratio: " + originalSize/finalSize);
		reader.close();
		writer.close();
	}
}
