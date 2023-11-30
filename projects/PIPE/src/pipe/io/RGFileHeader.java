/*
 * Created on 15-Jul-2005
 */
package pipe.io;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Nadeem
 *
 * This class is used to read/write a fileheader for
 * a Reachability Graph file.
 */
public class RGFileHeader {
	private int signature; // Will be used to check the correct file type
	private int numstates;
	private int statearraysize;
	private int numtransitions;
	private int transitionrecordsize;
	private long offsettotransitions;
	

	/**
	 * Sets up a Reachability Graph File Header Object ready
	 * for writing.
	 */
	public RGFileHeader(int ns, int ss, int nt, int trs, long offset) {
		signature = 8271; // ASCII code for 'R' 'G'
		numstates = ns;
		statearraysize = ss;
		numtransitions = nt;
		transitionrecordsize = trs;
		offsettotransitions = offset;
	}
	
	public RGFileHeader(RandomAccessFile input) throws IncorrectFileFormatException, IOException{
		this(0,0,0,0,0);
		read(input);
	}
	
	/**
	 * Sets up a blank Reachability Graph File Header Object
	 */
	public RGFileHeader(){
		this(0,0,0,0,0);
	}
	
	public void write(RandomAccessFile outputfile) throws IOException{
		outputfile.writeInt(signature);
		outputfile.writeInt(numstates);
		outputfile.writeInt(statearraysize);
		outputfile.writeInt(numtransitions);
		outputfile.writeInt(transitionrecordsize);
		outputfile.writeLong(offsettotransitions);
	}
	
	public void read(RandomAccessFile inputfile) throws IOException, IncorrectFileFormatException{
		signature = inputfile.readInt();
		
		// Check the specified file is an RG File
		if(signature != 8271)
			throw new IncorrectFileFormatException("RG File");
		
		numstates = inputfile.readInt();
		statearraysize = inputfile.readInt();
		numtransitions = inputfile.readInt();
		transitionrecordsize = inputfile.readInt();
		offsettotransitions = inputfile.readLong();
	}

	public int getNumStates(){
		return numstates;
	}
	
	public int getStateArraySize(){
		return statearraysize;
	}
	
	public int getNumTransitions(){
		return numtransitions;
	}
	
	public int getTransitionRecordSize(){
		return transitionrecordsize;
	}
	
	public long getOffsetToTransitions(){
		return offsettotransitions;
	}
}
