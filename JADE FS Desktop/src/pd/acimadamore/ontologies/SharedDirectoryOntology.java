package pd.acimadamore.ontologies;

import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;

import pd.acimadamore.ontologies.SharedFile;
import pd.acimadamore.ontologies.SharedDirectory;

public class SharedDirectoryOntology extends BeanOntology {

  private static final long serialVersionUID = 1L;
  
  private static Ontology instance = new SharedDirectoryOntology("shared-directory-ontology");
  
  public static Ontology getInstance(){
    return instance;
  }
  
  private SharedDirectoryOntology(String name){
    
    super(name);
      
    try{
      add(SharedFile.class);
      add(SharedDirectory.class);
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }
  
}
