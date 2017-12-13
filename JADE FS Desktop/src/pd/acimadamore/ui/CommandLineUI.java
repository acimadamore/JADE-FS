package pd.acimadamore.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import jade.core.AID;
import pd.acimadamore.agents.FileSharer;
import pd.acimadamore.ontologies.SharedDirectory;
import pd.acimadamore.ontologies.SharedFile;

public class CommandLineUI {

  private FileSharer fs;
  
  public CommandLineUI(FileSharer fs){
    this.fs = fs;
  }
  
  public void render(){
    
    Boolean keepRendering = true;
    
    this.clearTerminal();
    
    @SuppressWarnings("resource")
    Scanner s = new Scanner(System.in);
    
    while(keepRendering){
      
      this.renderUI();
    
      if(s.hasNextInt()){
        
        this.clearTerminal();
        
        int option = s.nextInt();
        
        switch(option){
          case(1):
            this.printLocalDirectory();
            break;
          case(2):
            this.printRemoteDirectories();
            break;
          case(3):
            this.downloadFile(s);
            break;
          case(4):
            keepRendering = false;
            break;
          default:
            System.out.println("Seleccione una de las opciones: 1, 2, 3, 4.");
        } 
      }
    }
     
      
  }
  
  private void renderUI(){
    System.out.println("***********************************************************");
    System.out.println("*                    FILE SHARE APP                       *");
    System.out.println("***********************************************************");
    System.out.println("*                                                         *");
    System.out.println("*   1) Mostrar mis archivos compartidos.                  *");
    System.out.println("*                                                         *");
    System.out.println("*   2) Mostrar archivos remotos.                          *");
    System.out.println("*                                                         *");
    System.out.println("*   3) Descargar archivo.                                 *");
    System.out.println("*                                                         *");
    System.out.println("*   4) Salir.                                             *");
    System.out.println("*                                                         *");
    System.out.println("***********************************************************");
    System.out.println("");
  }
  
  private void clearTerminal(){
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
  

  private void printLocalDirectory(){
    System.out.println("");
    System.out.println("");
    System.out.println("Mis archivos compartidos: ");
    System.out.println("");
    System.out.println(this.fs.getSharedDirectory());
  }
  
  private void printRemoteDirectories(){
    System.out.println("");
    System.out.println("");
    
    if(!this.fs.getRemoteDirectories().isEmpty()){
      for (Map.Entry<AID, SharedDirectory> entry : this.fs.getRemoteDirectories().entrySet())
      {
        System.out.println("Archivos del agente: " + entry.getKey().getLocalName());
        System.out.println(entry.getValue());
      }
    }
    else{
      System.out.println("No hay archivos compartidos por otros agentes");
    }
     
  }
  
  private void downloadFile(Scanner s){
    
    HashMap<AID, SharedDirectory> sd = this.fs.getRemoteDirectories();
    int i = 0;
    
    AID             selectedAgent;
    SharedDirectory selectedDirectory;
    SharedFile      selectedFile;
    
    if(!sd.isEmpty()){
      
      // Select agent
      System.out.println("Indique el agente del que quiere descargar el archivo");
      System.out.println("");
      
      
      for(AID aid : sd.keySet()){
        System.out.println(String.valueOf(i) + " - " + aid.getLocalName());
        i++;
      }
      
      if(s.hasNextInt())   
        i = s.nextInt();
      
      if(i < 0 || i > sd.keySet().size()){
        System.out.println("Opción seleccionada no permitida.");
        return;
      }
      
      selectedAgent = (AID) sd.keySet().toArray()[i];
           
      selectedDirectory = sd.get(selectedAgent);
      
      // Selecct file
      System.out.println("Indique el archivo que quiere descargar de " + selectedAgent.getLocalName());
      System.out.println("");
      
      i= 0;
      for(SharedFile sf : selectedDirectory.getFiles()){
        System.out.println(String.valueOf(i) + " - " + sf.getName());
        i++;
      }
      
      if(s.hasNextInt())   
        i = s.nextInt();
      
      if(i < 0 || i > selectedDirectory.getFiles().size()){
        System.out.println("Opción seleccionada no permitida.");
        return;
      }
      
      selectedFile = selectedDirectory.getFiles().get(i);
      
      System.out.println("Descargando archivo " + selectedFile.getName() + " de " + selectedAgent.getLocalName() + "...");
      
      this.fs.downloadRemoteFile(selectedDirectory, selectedFile);
    }
    else{
      System.out.println("No hay otros agentes compartiendo directorios!");
    }
  }
  
}
