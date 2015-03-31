package org.renci.main;

import java.io.File;

public class ArgHandler {
		boolean logFileSet,segFileSet,outFileSet,paramFileSet,Debug;
		File logFile,segFile,outFile,paramFile;
		String[] arguments;
		int numProc;
		
		public ArgHandler(String[] args){
			arguments = args;
		}
		public void setArguments(){
			int i = 0;
			while(i<arguments.length){
				if(arguments[i].equalsIgnoreCase("-l")){
					setLogFile(arguments[++i]);
					
				}
				else if(arguments[i].equalsIgnoreCase("-p")){
					setParamFile(arguments[++i]);
				
				}
				else if(arguments[i].equalsIgnoreCase("-s")){
					setSegFile(arguments[++i]);
					
				}
				else if(arguments[i].equalsIgnoreCase("-o")){
					setOutFile(arguments[++i]);
										
				}
				else if(arguments[i].equalsIgnoreCase("-proc")){
					setNumProcs(arguments[++i]);
				}
				else{
					System.out.println("Warning: illegal argument passed");
					break;
				}
				i++;
			}
			parmFileCheck();
		}
		private void parmFileCheck() {
			if(!paramFileSet){
				System.out.println("A param File must be used");
				//add usage out text first 
				System.exit(0);
			}
			
		}
		private void setOutFile(String aFile) {//needs to be writable
			//actually the base file name so need to be worked out 
			outFile = new File(aFile);
			outFileSet = true;
		}
		public File getOutFile(){
			assert outFileSet;
			return outFile;
		}
		private void setSegFile(String aFile) {
			segFile = new File(aFile);
			segFileSet = true;
		}
		public File getSegFile(){
			//assert segFileSet;
			return segFile;
		}
		private void setParamFile(String aFile) {
			paramFile = new File(aFile);
			paramFileSet = true;
		}
		public File getParamFile(){
			assert paramFileSet;
			return paramFile;
		}
		private void setLogFile(String aFile) {//needs to be writable
			logFile = new File(aFile);
			logFileSet = true;
		}
		public File getLogFile(){
			assert logFileSet;
			return logFile;
		}
		private void setNumProcs(String aNum){
			numProc = Integer.parseInt(aNum);
		}
		public int getNumProcs(){
			if(numProc >0 )
				return numProc;
			else //if this has not been set 
				return 1;
		}
}
