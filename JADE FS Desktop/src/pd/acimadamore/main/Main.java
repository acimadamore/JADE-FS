package pd.acimadamore.main;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import pd.acimadamore.agents.FileSharer;
import pd.acimadamore.agents.FileSharerAgent;
import pd.acimadamore.events.DesktopNotifier;
import pd.acimadamore.events.FSNotifier;
import pd.acimadamore.ui.CommandLineUI;

public class Main {

  private static final String DEFAULT_PORT = "1099";
   
  private static CommandLine         cmd;
  private static ContainerController containerController;
  private static AgentController     agentController;
  
  public static void main(String[] args) throws StaleProxyException {
    // TODO Auto-generated method stub
    parseArguments(args);

    initContainer();
    
    Scanner s = new Scanner(System.in);
    
    if(initAgent(s)){
      FSNotifier.instance = new DesktopNotifier();
      
      System.out.println("Inicializando agente...");
      while(!DesktopNotifier.isAgentInitialized())
        System.out.print(".");
      
      CommandLineUI ui = new CommandLineUI(agentController.getO2AInterface(FileSharer.class));

      ui.render();
      
      System.out.println("Finalizando Contenedor...");
      containerController.kill();
    }
  }
  
  
  private static Boolean initAgent(Scanner s){
    // If the user wants to create the Main Container BUT without File Sharing, don't create the agent is a dummy container with only the JADE Main-Container
    if(cmd.hasOption("main-container") && cmd.hasOption("no-file-sharing")){
      return false;
    }
    
    String  agentPath;
    Path    p = null;
    Boolean isValidPath = false;
    
    while(!isValidPath){
      System.out.println("Ingrese el path de la carpeta que desea compartir:");
      agentPath = s.nextLine();
      
      p = Paths.get(agentPath.trim());
      
      if(Files.exists(p) && Files.isDirectory(p) && Files.isReadable(p)){
        isValidPath = true;
      }
      else{
        System.out.println("El path ingresado no es valido! Puede que el path no exista, no sea un directorio o no sea legible");
      }
    }
       
    String agentName = "Desktop-" + String.valueOf((new Random()).nextInt(999));
    
    try {
      agentController = containerController.createNewAgent(agentName, FileSharerAgent.class.getName(), new Object[] { p.toFile() });
      agentController.start();  
    } 
    catch (StaleProxyException e) {
      System.out.println("Fallo la creación del Agente!");
      System.exit(0);
    } 
    
    
    return true;
  }
  
  private static void initContainer(){
    Profile p = new ProfileImpl();
    p.setParameter(Profile.SERVICES, "jade.core.mobility.AgentMobilityService;jade.core.event.NotificationService;jade.core.messaging.TopicManagementService");

   
    if(cmd.hasOption("main-container")){
      try{
        containerController = Runtime.instance().createMainContainer(p);
        
        System.out.println("*********************************************************************");
        System.out.println("*                     MAIN CONTAINER CREADO                         *");
        System.out.println("*********************************************************************");  
      }
      catch(Exception e){
        System.out.println("Fallo la creación del Main Container!");
        System.exit(0);
      }
      
    }
    else{
      if(!cmd.hasOption("host")){
        System.out.println("Debe proveer la Dirección IP del Main-Container al que conectarse con la opción -host(-h).");
        
        System.exit(1);
      }
      
      p.setParameter(Profile.MAIN_HOST, cmd.getOptionValue("host"));
      p.setParameter(Profile.MAIN_PORT, cmd.hasOption("port") ? cmd.getOptionValue("port") : DEFAULT_PORT);
      
      try{
        containerController = Runtime.instance().createAgentContainer(p);
        
        System.out.println("*********************************************************************");
        System.out.println("*                     AGENT CONTAINER CREADO                        *");
        System.out.println("*********************************************************************");  
      }
      catch(Exception e){
        System.out.println("Fallo la creación del Agent Container!");
        System.exit(0);
      }
      
    }
  }
  
  
  // ARGUMENTS HANDLING
  private static void parseArguments(String[] args){
    CommandLineParser parser = new DefaultParser();
    
    Options opts = getCommandLineOptions();
    
    try {
      cmd = parser.parse(opts, args);
    } 
    catch (ParseException e) {
      e.printStackTrace();
    }
    
    if(cmd.getOptions().length == 0){
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "JADE FS", opts );
      
      System.exit(0);
    }
  }
  
  private static Options getCommandLineOptions(){
    Options options = new Options();

    options.addOption(Option.builder("h")
        .longOpt("host")
        .desc("La dirección IP del Main Container al que conectarse.")
        .hasArg()
        .build());
    
    options.addOption(Option.builder("p")
        .longOpt("port")
        .desc("El puerto del Main Container al que conectarse. Default: 1099")
        .hasArg()
        .build());
    
    options.addOption(Option.builder("mc")
        .longOpt("main-container")
        .desc("Indica que se debe crear un Main Container")
        .build());
    
    options.addOption(Option.builder("nfs")
        .longOpt("no-file-sharing")
        .desc("No compartir archivos. Solo disponible si se crea un Main-Container")
        .build());
    
    
    return options;
  }

}
