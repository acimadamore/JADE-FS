package pd.acimadamore.behaviours;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import jade.core.behaviours.OneShotBehaviour;
import pd.acimadamore.agents.FileSharerAgent;
import pd.acimadamore.events.FSEvent;

/*
 * This behaviour is part of the downloading process.
 * Only intended to be used after the FileReader behaviour when the agent has returned to its original Container. 
 * It Writers the file that the FileReader previously readed.
 *  
 * */
public class FileWriter extends OneShotBehaviour {

  private static final long serialVersionUID = 1L;

  private File   file;
  private byte[] fileData;
  
  public FileWriter(File file, byte[] fileData){
    this.file     = file;
    this.fileData = fileData;
  }
  
  @Override
  public void action() { 
    
    this.writeFile();
    
    this.myAgent().notify(FSEvent.FILE_DOWNLOADED);
  }
  
  private void writeFile(){
    try{
      FileOutputStream output = new FileOutputStream(this.file.getAbsolutePath(), true);
      output.write(fileData);
      output.close();
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private FileSharerAgent myAgent(){
    return (FileSharerAgent) this.myAgent;
  }

}
