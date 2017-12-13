package pd.acimadamore.behaviours;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;

import jade.core.behaviours.OneShotBehaviour;

import pd.acimadamore.agents.FileSharerAgent;

/*
 * This behaviour is part of the downloading process.
 * Only intended to be used when the agent is at the container where the file is located. 
 * It Reads the file and moves the agent back to its original location.
 *  
 * */
public class FileReader extends OneShotBehaviour {

  private static final long serialVersionUID = 1L;
  
  // Max file size: 100MB
  private static final int  maxFileSize      = 1048576;  
  
  private File path;
  private String filename;
  
  public FileReader(File path, String filename){
    this.path     = path;
    this.filename = filename;
  }
  
  @Override
  public void action() {
    
    File fileToDownload = this.searchFile(this.path, this.filename);
    
    if(fileToDownload != null){
      this.myAgent().remoteFileFound();
      this.myAgent().setRemoteFileData(this.readFile(fileToDownload));
    }
    else{
      this.myAgent().remoteFileNotFound();
    }
    
    this.myAgent().returnToOriginalLocation();
  }
  
  private byte[] readFile(File file){
    FileInputStream fstream = null;
    
    byte[] readedFile = new byte[FileReader.maxFileSize];
    int    readed     = 0;
    
    try {
      
      fstream = new FileInputStream(file.getAbsolutePath());
      
      readed  = fstream.read(readedFile, 0, fstream.available());
      
      fstream.close();
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    
    return Arrays.copyOf(readedFile, readed);
  }
  
  private FileSharerAgent myAgent(){
    return (FileSharerAgent) this.myAgent;
  }
  
  private File searchFile(File path, String filename){
    for(File f : path.listFiles()){
      
      if(f.getName().equals(filename)){
        return f;
      }
      
      if(f.isDirectory()){
        File fileFounded = searchFile(f, filename);
          
        if (fileFounded != null){
          return fileFounded;
        }
      }
    }
    
    return null;
  }

}
